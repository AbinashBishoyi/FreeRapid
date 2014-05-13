package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.model.DownloadFile;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * @author Ladislav Vitasek
 */
public class DownloadClient {
    private final static Logger logger = Logger.getLogger(DownloadClient.class.getName());

    protected HttpClient client;
    protected String referer = "";
    protected String asString;
    private int redirect;
    private final ConnectionSettings settings;

    public DownloadClient(ConnectionSettings settings) {
        this.settings = settings;
        initClient();
    }

    public void initClient() {
        this.client = new HttpClient();
        if (settings.isProxySet()) {
            HostConfiguration configuration = new HostConfiguration();
            configuration.setProxy(settings.getProxyURL(), settings.getProxyPort());
            client.setHostConfiguration(configuration);
        }

        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // Get initial state object

        HttpState initialState = new HttpState();
        initialState.purgeExpiredCookies();
        client.setState(initialState);
    }

    protected void setDefaultsForMethod(HttpMethod method) {
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
        method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        method.setRequestHeader("Accept-Language", "cs,en-us;q=0.7,en;q=0.3");
        method.setRequestHeader("Accept-Charset", "windows-1250,utf-8;q=0.7,*;q=0.7");
        method.setRequestHeader("Accept-Encoding", "gzip,deflate");
        method.setRequestHeader("Keep-Alive", "30");
        if (referer.length() > 0)
            method.setRequestHeader("Referer", referer);
        method.setFollowRedirects(false);
    }

    public PostMethod getPostMethod(final String uri) {
        final PostMethod m = new PostMethod(uri);
        setDefaultsForMethod(m);
        return m;
    }


    public String getReferer() {
        return referer;
    }


    public InputStream makeFinalRequestForFile(HttpMethod method, DownloadFile file) throws IOException {
        toString(method);
        client.executeMethod(method);

        int statuscode = method.getStatusCode();

        if (statuscode == HttpStatus.SC_OK) {
            final Header contentType = method.getResponseHeader("Content-Type");
            if (contentType != null) {
                if ("application/octet-stream".equals(contentType.getValue())) {
                    final Header contentLength = method.getResponseHeader("Content-Length");
                    file.setFileSize(new Long(contentLength.getValue()));
                    //Content-Disposition: Attachment; filename=Private_Triple_X_01.pdf
                    final Header disposition = method.getResponseHeader("Content-Disposition");
                    if (disposition != null) {
                        final String value = disposition.getValue();
                        final String str = "filename=";
                        final int index = value.indexOf(str);
                        if (index >= 0) {
                            file.setFileName(value.substring(index + str.length()));
                        }
                    }
                    return method.getResponseBodyAsStream();
                }
            }
        }
        return null;
    }

    public int makeRequest(HttpMethod method) throws IOException {
        //toString(method);
        client.executeMethod(method);

        int statuscode = method.getStatusCode();

        if (statuscode == HttpStatus.SC_INTERNAL_SERVER_ERROR || statuscode == HttpStatus.SC_FORBIDDEN) {//bezpecnost
            logger.severe("Status code je 500");
        } else if (statuscode >= HttpStatus.SC_INTERNAL_SERVER_ERROR) {
            logger.severe("Status code > 500:" + statuscode);
        }


        final boolean isRedirect = isRedirect(statuscode);
        if (isRedirect && redirect != 1) {
            redirect = 1;
            Header header = method.getResponseHeader("location");
            if (header != null) {
                String newuri = header.getValue();
                if ((newuri == null) || ("".equals(newuri))) {
                    newuri = "/";
                }
                logger.info("Redirect target: " + newuri);
                setReferer(newuri);
                GetMethod redirect = getGetMethod(newuri);
                final int i = makeRequest(redirect);
                logger.info("Redirect: " + redirect.getStatusLine().toString());
                // release any connection resources used by the method
                return i;
            } else {
                logger.info("Invalid redirect");
                System.exit(1);
            }
        } else {
            redirect = 0;
            Header hce = method.getResponseHeader("Content-Encoding");
            asString = "";
            if (null != hce) {
                if ("gzip".equals(hce.getValue())) {
                    logger.info("Extracting GZIP");
                    asString = inflate(method.getResponseBodyAsStream());
                } else {
                    //better hope this never happens
                }
            } else
                this.asString = method.getResponseBodyAsString();
        }
        // logger.info("asString = " + asString);

        method.releaseConnection();
        return statuscode;
    }


    protected boolean isRedirect(int statuscode) {
        return (statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                (statuscode == HttpStatus.SC_SEE_OTHER) ||
                (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT);
    }

    public GetMethod getGetMethod(final String uri) {
        final GetMethod m = new GetMethod(uri);
        setDefaultsForMethod(m);
        return m;
    }


    protected void toString(HttpMethod method) {
        logger.info("===============HTTP METHOD===============");
        final String path = method.getPath();
        logger.info("path = " + path);
        final Header[] headers = method.getRequestHeaders();
        StringBuilder builder = new StringBuilder();
        for (Header header : headers) {
            if (header != null)
                builder.append(header.toString());
        }
        logger.info("header = \n" + builder.toString().trim());
        if (method instanceof PostMethod) {
            PostMethod postMethod = (PostMethod) method;
            builder = new StringBuilder();
            final NameValuePair[] parameters = postMethod.getParameters();
            for (NameValuePair pair : parameters) {
                builder.append(pair.getName()).append("=").append(pair.getValue()).append("\n");
            }
        }
        logger.info("post parameters: \n" + builder.toString().trim());
        logger.info("query string = " + method.getQueryString());
        logger.info("=========================================");
    }

    protected String inflate(InputStream in) throws IOException {
        byte[] buffer = new byte[4000];
        int b;
        GZIPInputStream gin = new GZIPInputStream(in);
        StringBuilder builder = new StringBuilder();
        while (true) {
            b = gin.read(buffer);
            if (b == -1)
                break;
            builder.append(new String(buffer, 0, b, "UTF-8"));
        }
        return builder.toString();
    }

    public void setReferer(String referer) {
        logger.info("Setting referer to " + referer);
        this.referer = referer;
    }

    public ConnectionSettings getSettings() {
        return settings;
    }


    public HttpClient getHTTPClient() {
        return client;
    }
}
