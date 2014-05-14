package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.BuildMethodException;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.hoster.CaptchaSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.*;
import cz.vity.freerapid.plugins.webclient.rtmp.RtmpClient;
import cz.vity.freerapid.plugins.webclient.rtmp.RtmpSession;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.plugins.webclient.utils.PlugUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.Cookie;
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
     * Referer
     */

    private String referer;

    /**
     * default encoding for page
     */
    private String encoding = "UTF-8";

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
        logger.info("Starting 'run' for file " + fileURL);
    }

    /**
     * {@inheritDoc}
     */
    public void runCheck() throws Exception {
        if (!initialized)
            throw new IllegalStateException("Cannot run runCheck method. Runner was not initialized via init method");
        logger.info("Starting 'runCheck' for file " + fileURL);
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
     * @return true if file was successfully downloaded, false otherwise - file was not found, only string content is available
     * @throws Exception when connection/writing to file failed
     */
    protected boolean tryDownloadAndSaveFile(HttpMethod method) throws Exception {
        logger.info("Download link URI: " + method.getURI().toString());
        httpFile.setState(DownloadState.GETTING);
        logger.info("Making final request for file");
        try {
            final InputStream inputStream = client.makeFinalRequestForFile(method, httpFile, true);
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
     * Method uses given RtmpSession parameter to connect to the server and tries to download.<br />
     * Download state of HttpFile is updated automatically - sets <code>DownloadState.GETTING</code> and then <code>DownloadState.DOWNLOADING</code>.
     * The HttpClient parameter <code>noContentLengthAvailable</code> is also set.
     *
     * @param rtmpSession RtmpSession to use for downloading
     * @return true if file was successfully downloaded, false otherwise
     * @throws Exception if something goes horribly wrong
     * @see RtmpSession
     */
    protected boolean tryDownloadAndSaveFile(final RtmpSession rtmpSession) throws Exception {
        httpFile.setState(DownloadState.GETTING);
        logger.info("Starting RTMP download");

        httpFile.getProperties().remove(DownloadClient.START_POSITION);
        httpFile.getProperties().remove(DownloadClient.SUPPOSE_TO_DOWNLOAD);

        final String fn = httpFile.getFileName();
        if (fn == null || fn.isEmpty())
            throw new IOException("No defined file name");
        httpFile.setFileName(HttpUtils.replaceInvalidCharsForFileSystem(PlugUtils.unescapeHtml(fn), "_"));

        client.getHTTPClient().getParams().getBooleanParameter("noContentLengthAvailable", true);

        RtmpClient rtmpClient = null;
        try {
            rtmpClient = new RtmpClient(rtmpSession);
            rtmpClient.connect();

            InputStream in = rtmpClient.getStream();

            if (in != null) {
                logger.info("Saving to file");
                downloadTask.saveToFile(in);
                return true;
            } else {
                logger.info("Saving file failed");
                return false;
            }
        } catch (Throwable e) {
            throw new PluginImplementationException("RTMP error (" + e.toString() + ")", e);
        } finally {
            if (rtmpClient != null) {
                try {
                    //no need to specifically close any streams, this method handles that too
                    rtmpClient.disconnect();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
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
        referer = method.getURI().toString();
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
        referer = method.getURI().toString();
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

    /**
     * Returns new instance of MethodBuilder, which is used to create GET and POST method.<br>
     * The default content for parsing is taken from the last HTTP response. <br />If you need to use your own content for creating method, make your own instance of MethodBuilder.
     *
     * @return new instance of MethodBuilder
     * @throws BuildMethodException if something goes wrong
     * @since 0.82
     */
    protected MethodBuilder getMethodBuilder() throws BuildMethodException {
        return new MethodBuilder(client).setBaseURL(getBaseURL()).setReferer(referer).setEncoding(encoding);
    }

    /**
     * Returns new instance of MethodBuilder, which is used to create GET and POST method.<br>
     * The default content for parsing is taken from the last HTTP response. <br />If you need to use your own content for creating method, make your own instance of MethodBuilder.
     *
     * @param content specific content
     * @return new instance of MethodBuilder
     * @throws BuildMethodException if something goes wrong
     * @since 0.82
     */
    protected MethodBuilder getMethodBuilder(String content) throws BuildMethodException {
        return new MethodBuilder(content, client).setBaseURL(getBaseURL()).setReferer(referer);
    }

    /**
     * Sets a cookie to the current session.
     *
     * @param cookie cookie to add
     * @since 0.82
     */
    protected void addCookie(Cookie cookie) {
        client.getHTTPClient().getState().addCookie(cookie);
    }

    /**
     * Gets the cookies of the current session.
     *
     * @return cookies of the current session
     * @since 0.84
     */
    protected Cookie[] getCookies() {
        return client.getHTTPClient().getState().getCookies();
    }

    /**
     * Gets a cookie by name from the current session.
     *
     * @param name name of cookie to look for
     * @return cookie with given name, or null if not found
     * @since 0.84
     */
    protected Cookie getCookieByName(final String name) {
        for (final Cookie cookie : getCookies()) {
            if (cookie.getName().equalsIgnoreCase(name)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * Gets a cookie by value from the current session.
     *
     * @param value value of cookie to look for
     * @return cookie with given value, or null if not found
     * @since 0.84
     */
    protected Cookie getCookieByValue(final String value) {
        for (final Cookie cookie : getCookies()) {
            if (cookie.getValue().equalsIgnoreCase(value)) {
                return cookie;
            }
        }
        return null;
    }

    /**
     * Returns base url for the site
     *
     * @return base url - null by default
     */
    protected String getBaseURL() {
        return null;
    }

    /**
     * Sets client parameter for handling requests
     *
     * @param parameterName  name of parameter
     * @param parameterValue parameter's value
     */
    protected void setClientParameter(String parameterName, Object parameterValue) {
        client.getHTTPClient().getParams().setParameter(parameterName, parameterValue);
    }

    /**
     * Sets encoding for the web page - encoding must be sets manually, it's not determined automatically<br />
     * UTF-8 encoding is set by default
     *
     * @param encoding encoding name - like Windows-1250, ISO-8859-1
     * @since 0.83
     */
    protected void setPageEncoding(String encoding) {
        this.encoding = encoding;
        setClientParameter("pageCharset", encoding);
        client.getHTTPClient().getParams().setHttpElementCharset(encoding);
    }

}
