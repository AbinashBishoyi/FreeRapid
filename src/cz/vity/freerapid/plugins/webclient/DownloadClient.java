package cz.vity.freerapid.plugins.webclient;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpClientParams;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import java.net.URLDecoder;

/**
 * @author Vity
 */
final public class DownloadClient implements HttpDownloadClient {
    private final static Logger logger = Logger.getLogger(DownloadClient.class.getName());

    protected HttpClient client;
    protected String referer = "";
    protected String asString;
    private int redirect;
    private volatile ConnectionSettings settings;


    public DownloadClient() {
        this.client = new HttpClient();
    }

    public void initClient(final ConnectionSettings settings) {
        this.settings = settings;
        final HttpClientParams clientParams = client.getParams();
        clientParams.setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        clientParams.setHttpElementCharset("UTF-8");
        this.client.setHttpConnectionManager(new SimpleHttpConnectionManager(true));

        HttpState initialState = new HttpState();
        if (settings.isProxySet()) {
            HostConfiguration configuration = new HostConfiguration();
            configuration.setProxy(settings.getProxyURL(), settings.getProxyPort());
            client.setHostConfiguration(configuration);
            if (settings.getUserName() != null)
                initialState.setProxyCredentials(AuthScope.ANY, new NTCredentials(settings.getUserName(), settings.getPassword(), "", ""));
        } else client.setHostConfiguration(new HostConfiguration());

        clientParams.setBooleanParameter(HttpClientParams.ALLOW_CIRCULAR_REDIRECTS, true);
        // Get initial state object

        client.setState(initialState);
    }

    private boolean hasAuthentification() {
        if (settings == null)
            throw new IllegalStateException("Client not initialized");
        return settings.isProxySet() && settings.getUserName() != null;
    }

    protected void setDefaultsForMethod(HttpMethod method) {
        method.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-GB; rv:1.9.0.1) Gecko/2008070208 Firefox/3.0.1");
        method.setRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        method.setRequestHeader("Accept-Language", "cs,en-us;q=0.7,en;q=0.3");
        method.setRequestHeader("Accept-Charset", "windows-1250,utf-8;q=0.7,*;q=0.7");
        //method.setRequestHeader("Accept-Charset", "utf-8, windows-1250;q=0.7,*;q=0.7");
        method.setRequestHeader("Accept-Encoding", "gzip,deflate");
        method.setRequestHeader("Keep-Alive", "30");
        if (referer.length() > 0)
            method.setRequestHeader("Referer", referer);
        method.setFollowRedirects(false);
    }

    public PostMethod getPostMethod(final String uri) {
        final PostMethod m = new PostMethod(uri);
        setDefaultsForMethod(m);
        m.setDoAuthentication(hasAuthentification());
        return m;
    }


    public String getReferer() {
        return referer;
    }


    public InputStream makeFinalRequestForFile(HttpMethod method, HttpFile file) throws IOException {
        toString(method);
        client.executeMethod(method);

        final int statuscode = method.getStatusCode();

        if (statuscode != HttpStatus.SC_OK) { //selhalo pripojeni
            logger.warning("Loading file failed - invalid HTTP return status code:" + statuscode);
            updateAsString(method);
            return null;
        }

        boolean isStream = true;
        final Header contentType = method.getResponseHeader("Content-Type");
        if (contentType == null) {
            isStream = false;
            logger.warning("No Content-Type!");
        } else {
            final String value = contentType.getValue();
            boolean isImage = value.startsWith("image/");
            if (!value.startsWith("application/") && !isImage) {
                isStream = false;
                logger.warning("Suspicious Content-Type:" + contentType.getValue());
            } else {
                final Header contentLength = method.getResponseHeader("Content-Length");
                if (contentLength == null) {
                    isStream = false;
                    logger.warning("No Content-Length in header");
                } else
                    file.setFileSize(Long.valueOf(contentLength.getValue()));
            }
        }
        final String fileName = getFileName(method);
        if (fileName != null) {
            file.setFileName(fileName);
        }

        if (isStream) {
            return method.getResponseBodyAsStream();
        } else {
            logger.warning("Loading file failed");
            updateAsString(method);
        }
        return null;
    }

    private String getFileName(HttpMethod method) {

        final Header disposition = method.getResponseHeader("Content-Disposition");
        if (disposition != null && disposition.getValue().toLowerCase().contains("attachment")) {
            final String value = disposition.getValue();
            String str = "filename=";
            final String lowercased = value.toLowerCase();
            int index = lowercased.lastIndexOf(str);
            if (index >= 0) {
                String s = value.substring(index + str.length());
                if (s.startsWith("\"") && s.endsWith("\""))
                    s = s.substring(1, s.length() - 1);
                // napr. pro xtraupload je jeste treba dekodovat
                if (s.matches(".*%[0-9A-Fa-f]+.*"))
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.warning("Unsupported encoding");
                    }
                return s;
            } else {
                //test na buggove Content-Disposition
                str = "filename\\*=UTF-8''";
                index = lowercased.lastIndexOf(str);
                if (index >= 0) {
                    final String s = value.substring(index + str.length());
                    if (!s.isEmpty())
                        try {
                            return URLDecoder.decode(s,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                          logger.warning("Unsupported encoding");
                        }
                } else {
                    logger.warning("File name was not found in:" + value);
                }
            }
        }
        return null;
    }

    public InputStream makeRequestForFile(HttpMethod method) throws IOException {
        toString(method);
        client.executeMethod(method);

        int statuscode = method.getStatusCode();

        if (statuscode == HttpStatus.SC_OK) {
            final Header contentType = method.getResponseHeader("Content-Type");
            if (contentType == null) {
                logger.warning("No Content-Type!");
            } else {
                final String contentTypeValue = contentType.getValue();
                if (!contentTypeValue.startsWith("application/") && !contentTypeValue.startsWith("image/")) {
                    logger.warning("Suspicious Content-Type:" + contentTypeValue);
                }
            }

            Header hce = method.getResponseHeader("Content-Encoding");
            if (null != hce) {
                if ("gzip".equals(hce.getValue())) {
                    logger.info("Found gzip Stream");
                    return new GZIPInputStream(method.getResponseBodyAsStream());
                } else {
                    //better hope this never happens
                }
            }
            return method.getResponseBodyAsStream();
        } else {
            logger.warning("Loading file failed");
            updateAsString(method);
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
            updateAsString(method);
        }
        // logger.info("asString = " + asString);

        method.releaseConnection();
        return statuscode;
    }

    private void updateAsString(HttpMethod method) throws IOException {
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
            this.asString = streamToString(method.getResponseBodyAsStream());
    }

    private String streamToString(final InputStream in) {
        BufferedReader in2 = null;
        StringWriter sw = new StringWriter();
        char[] buffer = new char[4000];
        try {
            in2 = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            int x;
            while ((x = in2.read(buffer)) != -1) {
                sw.write(buffer, 0, x);
            }
            return sw.toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error during reading content of page", e);
        } finally {
            if (in2 != null) {
                try {
                    in2.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
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
        m.setDoAuthentication(hasAuthentification());
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

    public String getContentAsString() {
        return asString;
    }
}
