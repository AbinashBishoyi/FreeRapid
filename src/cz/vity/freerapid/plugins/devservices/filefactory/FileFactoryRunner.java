package cz.vity.freerapid.plugins.devservices.filefactory;

import cz.vity.freerapid.plugins.exceptions.CaptchaEntryInputMismatchException;
import cz.vity.freerapid.plugins.exceptions.InvalidURLOrServiceProblemException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.exceptions.YouHaveToWaitException;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.HttpFile;
import cz.vity.freerapid.plugins.webclient.HttpFileDownloader;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
class FileFactoryRunner {
    private final static Logger logger = Logger.getLogger(FileFactoryRunner.class.getName());
    private HttpDownloadClient client;
    private HttpFileDownloader downloader;
    private static final String HTTP_FILEFACTORY_COM = "http://www.filefactory.com";

    private int deep;
    private HttpFile httpFile;
    private String iframeContent;
    private static final String VERIFICATION_WAS_INCORRECT = "the verification code you entered was incorrect";


    private enum Step {
        INIT, FRAME, DOWNLOAD, FINISHED
    }


    private Step step;
    private String initURL;
    private String iframeURL;
    private String finalDownloadURL;

    FileFactoryRunner() {
        deep = 2;
    }

    public void run(HttpFileDownloader downloader) throws Exception {
        this.downloader = downloader;
        httpFile = downloader.getDownloadFile();
        client = downloader.getClient();
        step = Step.INIT;
        initURL = httpFile.getFileUrl().toString();
        logger.info("Starting download in TASK " + initURL);
        iframeContent = iframeURL = "";
        httpFile.setState(DownloadState.GETTING);
        while (step != Step.FINISHED) {
            switch (step) {
                case INIT:
                    mainStep(initURL);
                    break;
                case FRAME:
                    iframeStep(iframeURL);
                    break;
                case DOWNLOAD:
                    downloadStep(finalDownloadURL);
                    break;
                case FINISHED:
                    return;
                default:
                    assert false;
            }
        }

    }

    private void downloadStep(String finalDownloadURL) throws Exception {
        saveFileOnURL(finalDownloadURL);
    }

    private void iframeStep(String iframeURL) throws Exception {
        logger.info("IFrame url " + iframeURL);
        if (stepCaptcha(iframeContent))
            return;
        if (checkRestart(iframeContent))
            return;
        if (checkDownload(iframeContent))
            return;
        throw new PluginImplementationException("Unrecognized content of page");
    }

    private boolean checkDownload(String iframeContent) {
        if (iframeContent.contains("Click here to begin your")) {
            Matcher matcher = Pattern.compile("top\" href=\"(.*?)\"><img src", Pattern.MULTILINE).matcher(iframeContent);
            if (matcher.find()) {
                this.finalDownloadURL = matcher.group(1);
                step = Step.DOWNLOAD;
                return true;
            }
        }
        return false;
    }

    private void mainStep(String fileURL) throws Exception {
        if (--deep <= 0)
            throw new InvalidURLOrServiceProblemException("Something is very weird");
        final GetMethod getMethod = client.getGetMethod(fileURL);
        getMethod.setFollowRedirects(true);
        if (client.makeRequest(getMethod) == HttpStatus.SC_OK) {
            String contentAsString = client.getContentAsString();
            Matcher matcher = Pattern.compile("Size: ([0-9-\\.]*) MB", Pattern.MULTILINE).matcher(contentAsString);
            if (!matcher.find()) {
                throw new InvalidURLOrServiceProblemException("Invalid URL or unindentified service");
            }
            String s = matcher.group(1);
            httpFile.setFileSize((long) Float.parseFloat(s) * 1024 * 1024);
            //href="/dlf/f/a3f880/b/2/h/dd8f6f6df8ec3d79aef99536de9aab06/j/0/n/KOW_-_Monica_divx_002" id="basicLink"
            matcher = Pattern.compile("href=\"(.*?)\" id=\"basicLink\"", Pattern.MULTILINE).matcher(client.getContentAsString());
            if (matcher.find()) {
                s = matcher.group(1);
                logger.info("Found File URL - " + s);
                if (downloader.isTerminated())
                    throw new InterruptedException();
                final String basicLinkURL = HTTP_FILEFACTORY_COM + s;
                GetMethod method = client.getGetMethod(basicLinkURL);
                if (client.makeRequest(method) == HttpStatus.SC_OK) {
                    contentAsString = client.getContentAsString();
                    logger.info(contentAsString);
                    matcher = Pattern.compile("<iframe src=\"(.*?)\"", Pattern.MULTILINE).matcher(contentAsString);
                    if (matcher.find()) {
                        client.setReferer(basicLinkURL);
                        s = matcher.group(1);
                        iframeURL = replaceEntities(s);
                        method = client.getGetMethod(HTTP_FILEFACTORY_COM + iframeURL);
                        if (client.makeRequest(method) != HttpStatus.SC_OK)
                            throw new PluginImplementationException("IFrame with captcha not found");
                        iframeContent = client.getContentAsString();//iframes content
                        step = Step.FRAME;
                    } else throw new PluginImplementationException("IFrame with captcha not found");
                } else throw new PluginImplementationException("Retrieving page with free download failed");
            } else throw new PluginImplementationException("Basic link not found found");

        } else
            throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");
    }

    private String replaceEntities(String s) {
        s = s.replaceAll("\\&amp;", "&");
        return s;
    }

    private boolean stepCaptcha(String contentAsString) throws Exception {
        if (contentAsString.contains("Please enter the following code") || contentAsString.contains(VERIFICATION_WAS_INCORRECT)) {
            //src="/securimage/securimage_show.php?f=a3f880&amp;h=eda55e0920a7371c4983ec8e19f3de88"
            Matcher matcher = Pattern.compile("src=\"(/securi[^\"]*)\"", Pattern.MULTILINE).matcher(contentAsString);
            if (matcher.find()) {
                String s = replaceEntities(matcher.group(1));
                final String captcha = downloader.getCaptcha(HTTP_FILEFACTORY_COM + s);
                if (captcha == null) {
                    throw new CaptchaEntryInputMismatchException();
                } else {
                    client.setReferer(this.iframeURL);
                    String f = getParameter("f", contentAsString);
                    String h = getParameter("h", contentAsString);
                    String b = getParameter("b", contentAsString);

                    final PostMethod postMethod = client.getPostMethod(HTTP_FILEFACTORY_COM + "/check/?" + "f=" + f + "&b=" + b + "&h=" + h);
                    postMethod.addParameter("f", f);
                    postMethod.addParameter("h", h);
                    postMethod.addParameter("b", b);
                    postMethod.addParameter("captcha", captcha);

                    if (client.makeRequest(postMethod) == HttpStatus.SC_OK) {
                        iframeContent = client.getContentAsString();
                        return true;
                    }
                }
            } else throw new PluginImplementationException("Captcha picture was not found");
        }
        return false;
    }

    private void saveFileOnURL(String finalFileURL) throws Exception {
        HttpMethod getMethod = client.getGetMethod(finalFileURL);
        try {
            getMethod.setFollowRedirects(true);
            final InputStream inputStream = client.makeFinalRequestForFile(getMethod, downloader.getDownloadFile());
            if (inputStream != null) {
                downloader.saveToFile(inputStream);
                step = Step.FINISHED;
            } else {
                if (checkRestart(client.getContentAsString())) return;
                if (checkLimit(client.getContentAsString())) {
                    return;
                }
                logger.info(client.getContentAsString());
                throw new IOException("File input stream is empty.");
            }
        } finally {
            getMethod.abort();
            getMethod.releaseConnection();
        }
    }

    private boolean checkLimit(String contentAsString) throws YouHaveToWaitException {
        if (contentAsString.contains("for free users.  Please wait")) {
            Matcher matcher = Pattern.compile("for free users.  Please wait ([0-9]*?) minutes", Pattern.MULTILINE).matcher(contentAsString);
            if (matcher.find()) {
                throw new YouHaveToWaitException("Limit for free users reached", Integer.parseInt(matcher.group(1)) * 60);
            }
        }
        return false;
    }

    private boolean checkRestart(String contentAsString) throws Exception {
        if (contentAsString.contains("the allowed time to enter a code")) {
            Matcher matcher = Pattern.compile("href=\"([^\"]*)\" target=\"_top\">restart", Pattern.MULTILINE).matcher(contentAsString);
            if (!matcher.find())
                throw new PluginImplementationException("Couldn't find restart URL");
            step = Step.INIT;
            initURL = matcher.group(1);
            return true;
        }
        return false;
    }

    private String getParameter(String s, String contentAsString) throws PluginImplementationException {
        Matcher matcher = Pattern.compile("name=\"" + s + "\" value=\"([^\"]*)\"", Pattern.MULTILINE).matcher(contentAsString);
        if (matcher.find()) {
            return matcher.group(1);
        } else
            throw new PluginImplementationException("Parameter " + s + " was not found");
    }


}