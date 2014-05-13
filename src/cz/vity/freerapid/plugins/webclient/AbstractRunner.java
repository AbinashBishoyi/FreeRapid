package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginRunner;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;

/**
 * @author Ladislav Vitasek
 */
public abstract class AbstractRunner implements PluginRunner {
    private final static Logger logger = Logger.getLogger(AbstractRunner.class.getName());

    protected HttpDownloadClient client;

    private ShareDownloadService pluginService;
    protected HttpFileDownloader downloader;

    protected HttpFile httpFile;

    protected String fileURL;

    private CaptchaSupport captchaSupport;

    public AbstractRunner() {

    }

    public void init(ShareDownloadService service, HttpFileDownloader downloader) {
        this.pluginService = service;
        this.downloader = downloader;
        this.client = downloader.getClient();
        this.httpFile = downloader.getDownloadFile();
        this.fileURL = httpFile.getFileUrl().toString();
    }

    public void run(HttpFileDownloader downloader) throws Exception {
        logger.info("Starting download 'run'" + fileURL);
    }

    public void runCheck(HttpFileDownloader downloader) throws Exception {
        logger.info("Starting download 'runCheck' " + fileURL);
    }

    protected CaptchaSupport getCaptchaSupport() {
        if (this.captchaSupport == null) {
            return captchaSupport = new CaptchaSupport(client, getDialogSupport());
        } else return captchaSupport;
    }

    protected DialogSupport getDialogSupport() {
        return pluginService.getPluginContext().getDialogSupport();
    }

    protected boolean tryDownload(HttpMethod method) throws Exception {
        httpFile.setState(DownloadState.GETTING);
        logger.info("Making final request for file");
        try {
            final InputStream inputStream = client.makeFinalRequestForFile(method, httpFile);
            if (inputStream != null) {
                logger.info("Saving to file");
                downloader.saveToFile(inputStream);
                return true;
            } else {
                return false;
            }
        } finally {
            method.abort();
            method.releaseConnection();
        }
    }

    public ShareDownloadService getPluginService() {
        return pluginService;
    }

    protected boolean makeRequest(HttpMethod method) throws IOException {
        return client.makeRequest(method) == HttpStatus.SC_OK;
    }

    protected PostMethod getPostMethod(String uri) {
        return client.getPostMethod(uri);
    }

    protected GetMethod getGetMethod(String uri) {
        return client.getGetMethod(uri);
    }

    protected String getContentAsString() {
        return client.getContentAsString();
    }

    protected Matcher matcherRequest(final String regexp) {
        return PlugUtils.matcher(regexp, client.getContentAsString());
    }
}
