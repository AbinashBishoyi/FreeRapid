package cz.vity.freerapid.plugins.webclient.interfaces;

import java.io.InputStream;

/**
 * @author Vity
 */
public interface HttpFileDownloader {
    HttpFile getDownloadFile();

    HttpDownloadClient getClient();

    public void saveToFile(InputStream inputStream) throws Exception;

    void sleep(int seconds) throws InterruptedException;

    boolean isTerminated();
}
