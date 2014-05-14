package cz.vity.freerapid.plugins.webclient;

import org.apache.commons.httpclient.HttpMethod;

import java.io.InputStream;

/**
 * Parent class for all "Runners" - implementation of plugins.
 *
 * @author Ladislav Vitasek
 */
public abstract class AbstractRunner extends AbstractHttpRunner {

    /**
     * Constructs a new AbstractRunner.
     */
    public AbstractRunner() {
        super();
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


}
