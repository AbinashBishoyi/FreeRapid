package cz.vity.freerapid.sandbox;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

/**
 * @author Vity
 */
public class Test3 {
    private final static Logger logger = Logger.getLogger(Test3.class.getName());
    private String referer = "";
    private String asString;
    private HttpClient client;
    private int redirect;

    public Test3() {
        HostConfiguration configuration = new HostConfiguration();
        final Integer port = new Integer(8081);
        if (port != -1) {
            logger.config("Running proxy on localhost:" + port);
            configuration.setProxy("localhost", port);
        }
        //configuration.setHost(HOMEPAGE);
        this.client = new HttpClient();
        client.setHostConfiguration(configuration);
        client.getParams().setCookiePolicy(CookiePolicy.BROWSER_COMPATIBILITY);
        // Get initial state object

        HttpState initialState = new HttpState();
        initialState.purgeExpiredCookies();
        client.setState(initialState);
    }

    private static void sleep(int seconds) {
        try {
            logger.info("Going to sleep on " + (seconds) + " seconds");
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultsForMethod(HttpMethod method) {
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

    private PostMethod getPostMethod(final String uri) {
        final PostMethod m = new PostMethod(uri);
        setDefaultsForMethod(m);
        return m;
    }


    public static void main(String[] args) throws IOException {
        new Test3().start();
    }

    private void start() throws IOException {
        final String file = "http://rapidshare.com/files/134794960/PC-W_09_08.rar.html";
        final GetMethod getMethod = getGetMethod(file);
        if (makeRequest(getMethod) == HttpStatus.SC_OK) {
            Matcher matcher = Pattern.compile("form id=\"ff\" action=\"([^\"]*)\"", Pattern.MULTILINE).matcher(asString);
            if (!matcher.find())
                return;
            String s = matcher.group(1);
            logger.info("Found File URL - " + s);
            setReferer(file);
            final PostMethod postMethod = getPostMethod(s);
            postMethod.addParameter("dl.start", "Free");
            if (makeRequest(postMethod) == HttpStatus.SC_OK) {
                matcher = Pattern.compile("var c=([0-9]*);", Pattern.MULTILINE).matcher(asString);
                if (!matcher.find())
                    return;
                s = matcher.group(1);
                int seconds = new Integer(s);
                matcher = Pattern.compile("form name=\"dlf\" action=\"([^\"]*)\"", Pattern.MULTILINE).matcher(asString);
                if (matcher.find()) {
                    s = matcher.group(1);
                    logger.info("Download URL: " + s);
                    sleep(seconds + 1);
                    final PostMethod method = getPostMethod(s);
                    method.addParameter("mirror", "on");
                    makeRequest(method);
                }
            }
        }
    }

    private int makeRequest(HttpMethod method) throws IOException {
        toString(method);
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
            } else {
                final Header contentType = method.getResponseHeader("Content-Type");
                if (contentType != null) {
                    if ("application/octet-stream".equals(contentType.getValue())) {
                        final Header contentLength = method.getResponseHeader("Content-Length");
                        int length = new Integer(contentLength.getValue());
                        System.out.println("length = " + length);
                        final File tempFile = File.createTempFile("prefix", "suffix");
                        saveToFile(tempFile, method.getResponseBodyAsStream());
                    } else
                        this.asString = method.getResponseBodyAsString();
                } else
                    this.asString = method.getResponseBodyAsString();
            }
            logger.info("asString = " + asString);

        }
        method.releaseConnection();
        return statuscode;
    }

    private void saveToFile(File tempFile, InputStream inputStream) {
        final byte[] buffer = new byte[100000];
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(tempFile);
            int len;
            int counter = 0;
            while ((len = inputStream.read(buffer)) != -1) {
                fileOutputStream.write(buffer, 0, len);
                counter += len;
            }
            System.out.println("counter = " + counter);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null)
                    inputStream.close();
                if (fileOutputStream != null)
                    fileOutputStream.close();
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error during saving file", e);
            }
        }

    }

    private boolean isRedirect(int statuscode) {
        return (statuscode == HttpStatus.SC_MOVED_TEMPORARILY) ||
                (statuscode == HttpStatus.SC_MOVED_PERMANENTLY) ||
                (statuscode == HttpStatus.SC_SEE_OTHER) ||
                (statuscode == HttpStatus.SC_TEMPORARY_REDIRECT);
    }

    private GetMethod getGetMethod(final String uri) {
        final GetMethod m = new GetMethod(uri);
        setDefaultsForMethod(m);
        return m;
    }


    private void toString(HttpMethod method) {
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

    private String inflate(InputStream in) throws IOException {
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


}
