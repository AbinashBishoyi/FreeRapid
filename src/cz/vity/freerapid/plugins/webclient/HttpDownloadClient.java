package cz.vity.freerapid.plugins.webclient;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Vity
 */
public interface HttpDownloadClient {
    void initClient(ConnectionSettings settings);

    PostMethod getPostMethod(String uri);

    GetMethod getGetMethod(String uri);

    String getReferer();

    void setReferer(String referer);

    InputStream makeFinalRequestForFile(HttpMethod method, HttpFile file) throws IOException;

    InputStream makeRequestForFile(HttpMethod method) throws IOException;

    int makeRequest(HttpMethod method) throws IOException;

    ConnectionSettings getSettings();

    HttpClient getHTTPClient();

    String getContentAsString();

}
