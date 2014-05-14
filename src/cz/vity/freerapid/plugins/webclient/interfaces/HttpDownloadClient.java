package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;

/**
 * Interface that represents HTTP robot - a client - for browsing pages.
 * Robot emulates WebBrowser.
 *
 * @author Vity
 */
public interface HttpDownloadClient {
    /**
     * Initialization of client with connection settings that should be used for creating http connection
     *
     * @param settings internet connection settings
     */
    void initClient(ConnectionSettings settings);

    /**
     * Returns setuble object for creation POST HTTP request
     *
     * @param uri URI for given HTTP request
     * @return new instance of PostMethod with default settings; referer is already set
     * @see org.apache.commons.httpclient.methods.PostMethod PostMethod
     */
    PostMethod getPostMethod(String uri);

    /**
     * Returns setuble object for creation GET HTTP request
     *
     * @param uri URI for given HTTP request
     * @return new instance of GetMethod with default settings
     * @see org.apache.commons.httpclient.methods.GetMethod GetMethod
     */
    GetMethod getGetMethod(String uri);

    /**
     * Return value of HTTP referer from the last HTTP request
     *
     * @return actual value of referer parsed from HTTP response header
     */
    String getReferer();

    /**
     * Sets referer value for the next HTTP request
     * This method should be called before getGetMethod or getPostMethod
     *
     * @param referer URI for the next request made by method makeRequest
     */
    void setReferer(String referer);

    /**
     * Runs HTTP request to get file specified in file parameter.
     * According to HTTP response filename and file size attributes are updated.
     *
     * @param method        a descendant of HttpMethod - PostMethod or GetMethod
     * @param file          the file that is downloaded
     * @param allowRedirect allow redirect flag
     * @return beginning of the stream for reading or null if there was no file stream in response
     * @throws IOException error I/O
     * @see org.apache.commons.httpclient.HttpClient#executeMethod(org.apache.commons.httpclient.HttpMethod)
     */
    InputStream makeFinalRequestForFile(HttpMethod method, HttpFile file, boolean allowRedirect) throws IOException;

    /**
     * Runs simple direct HTTP request to get a file - eg. CAPTCHA picture<br/>
     * Redirection is off
     *
     * @param method a descendant of HttpMethod - PostMethod or GetMethod
     * @return beginning of the stream for reading or null if there was no file stream in HTTP response
     * @throws IOException error I/O
     * @see org.apache.commons.httpclient.HttpClient#executeMethod(org.apache.commons.httpclient.HttpMethod)
     */
    InputStream makeRequestForFile(HttpMethod method) throws IOException;

    /**
     * Runs simple HTTP request with optional redirect.
     *
     * @param method        a descendant of HttpMethod - PostMethod or GetMethod
     * @param allowRedirect allow redirect flag
     * @return HTTP result code
     * @throws IOException error I/O
     * @see org.apache.commons.httpclient.HttpClient#executeMethod(org.apache.commons.httpclient.HttpMethod)
     */
    int makeRequest(HttpMethod method, boolean allowRedirect) throws IOException;

    /**
     * Getter
     *
     * @return Returns actual internet connection settings used by HTTP client
     */
    ConnectionSettings getSettings();

    /**
     * Method returns direct access to instance of HttpClient
     *
     * @return actual used HttpClient instance
     * @see org.apache.commons.httpclient.HttpClient
     */
    HttpClient getHTTPClient();

    /**
     * Method returns string content (web page - server response), which was gotten from the last GET or POST request
     *
     * @return webpage content, If request returned a file stream then this method empty string.
     */
    String getContentAsString();

    /**
     * Sets timeout for connection.
     *
     * @param timeout new timeout in milliseconds
     * @since 0.85
     */
    void setConnectionTimeOut(int timeout);
}
