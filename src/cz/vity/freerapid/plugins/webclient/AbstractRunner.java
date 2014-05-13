package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.*;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * Parent class for all "Runners" - implementation of plugins.
 *
 * @author Ladislav Vitasek
 */
public abstract class AbstractRunner implements PluginRunner {
    /**
     * Field logger
     */
    private final static Logger logger = Logger.getLogger(AbstractRunner.class.getName());

    /**
     * Field client - to access internet
     */
    protected HttpDownloadClient client;

    /**
     * reference to plugin class description
     */
    private ShareDownloadService pluginService;
    /**
     * Task that manages download
     */
    protected HttpFileDownloadTask downloadTask;

    /**
     * file that is being downloaded
     */
    protected HttpFile httpFile;

    /**
     * Field fileURL
     */
    protected String fileURL;

    /**
     * support loading CAPTCHA from the web
     */
    private CaptchaSupport captchaSupport;

    /**
     * property if runner was already initialized
     */
    private boolean initialized = false;

    /**
     * Constructs a new AbstractRunner.
     */
    public AbstractRunner() {

    }

    /**
     * {@inheritDoc}
     */
    public void init(ShareDownloadService service, HttpFileDownloadTask downloadTask) {
        this.pluginService = service;
        this.downloadTask = downloadTask;
        this.client = downloadTask.getClient();
        this.httpFile = downloadTask.getDownloadFile();
        this.fileURL = httpFile.getFileUrl().toString();
        initialized = true;
    }

    /**
     * {@inheritDoc}
     */
    public void run() throws Exception {
        if (!initialized)
            throw new IllegalStateException("Cannot run Run method. Runner was not initialized via init method");
        logger.info("Starting download 'run' for file:" + fileURL);
    }

    /**
     * {@inheritDoc}
     */
    public void runCheck() throws Exception {
        if (!initialized)
            throw new IllegalStateException("Cannot run runCheck method. Runner was not initialized via init method");
        logger.info("Starting download 'runCheck' " + fileURL);
    }

    /**
     * Access to class that help to manage loading and showing CAPTCHA to the user
     *
     * @return instance of CaptchaSupport
     * @see cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport
     */
    protected CaptchaSupport getCaptchaSupport() {
        if (this.captchaSupport == null) {
            return captchaSupport = new CaptchaSupport(client, getDialogSupport());
        } else return captchaSupport;
    }

    /**
     * Help method to get support for showing simple dialogs to user during downloading or for user options
     *
     * @return instance to DialogSupport
     * @see cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport
     */
    protected DialogSupport getDialogSupport() {
        return pluginService.getPluginContext().getDialogSupport();
    }


    /**
     * Method uses given method parameter to connect to the server and tries to download.<br />
     * Method updates download state of HttpFile automatically - sets <code>DownloadState.GETTING</code> and then <code>DownloadState.DOWNLOADING</code>
     *
     * @param method HttpMethod - its URL should be a link to the file
     * @return true if file was sucessfuly downloaded, false otherwise - file was not found, only string content is available
     * @throws Exception when connection/writing to file failed
     */
    protected boolean tryDownloadAndSaveFile(HttpMethod method) throws Exception {
        httpFile.setState(DownloadState.GETTING);
        logger.info("Making final request for file");
        try {
            final InputStream inputStream = client.makeFinalRequestForFile(method, httpFile);
            if (inputStream != null) {
                logger.info("Saving to file");
                downloadTask.saveToFile(inputStream);
                return true;
            } else {
                logger.info("Saving file failed");
                return false;
            }
        } finally {
            method.abort();
            method.releaseConnection();
        }
    }

    /**
     * Getter for property 'pluginService'.
     *
     * @return Value for property 'pluginService'.
     */
    public ShareDownloadService getPluginService() {
        return pluginService;
    }

    /**
     * Method makeRequest does simple request to server via given HTTP method <br />
     * Autoredirect is off.
     *
     * @param method HTTP method
     * @return boolean true if request finished with HTTP status code 200
     * @throws IOException when there was error during getting response from server
     * @see org.apache.commons.httpclient.HttpStatus#SC_OK
     */
    protected boolean makeRequest(HttpMethod method) throws IOException {
        return client.makeRequest(method, false) == HttpStatus.SC_OK;
    }

    /**
     * Method makeRequest does simple request to server via given HTTP method.<br />
     * Autoredirect is on.
     *
     * @param method HTTP method
     * @return boolean true if request finished with HTTP status code 200
     * @throws IOException when there was error during getting response from server
     * @see org.apache.commons.httpclient.HttpStatus#SC_OK
     */
    protected boolean makeRedirectedRequest(HttpMethod method) throws IOException {
        return client.makeRequest(method, true) == HttpStatus.SC_OK;
    }

    /**
     * Help method for easier calling client.getPostMethod in Runner
     *
     * @param uri URI for connecting via this POST method
     * @return new instance of Http PostMethod
     * @see cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient#getPostMethod(String)
     */
    protected PostMethod getPostMethod(String uri) {
        return client.getPostMethod(uri);
    }

    /**
     * Help method for easier calling client.getGetMethod in Runner
     *
     * @param uri URI for connecting via this GET method
     * @return new instance of Http GetMethod
     * @see cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient#getGetMethod(String)
     */
    protected GetMethod getGetMethod(String uri) {
        return client.getGetMethod(uri);
    }

    /**
     * Returns last response from server as string
     *
     * @return text response from server
     */
    protected String getContentAsString() {
        return client.getContentAsString();
    }


    /**
     * Method returns Matcher for given regular expression and content of the web page that was get from the last request
     *
     * @param regexp regular expression that is used for parsing content
     * @return Matcher new instance of Matcher
     */
    protected Matcher getMatcherAgainstContent(final String regexp) {
        return PlugUtils.matcher(regexp, client.getContentAsString());
    }
}
