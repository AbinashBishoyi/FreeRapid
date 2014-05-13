package cz.vity.freerapid.plugins.webclient.interfaces;

import java.io.InputStream;

/**
 * Interface that represents Task of downloading file - its downloading process.
 *
 * @author Vity
 */
public interface HttpFileDownloadTask {
    /**
     * Returns file that is given to be downloaded
     *
     * @return file for downloading
     */
    HttpFile getDownloadFile();

    /**
     * Client associated with current file - its HTTP connections are used to grab a file
     *
     * @return actual instance of HttpDownloadClient
     */
    HttpDownloadClient getClient();

    /**
     * Method that handles direct saving file onto physical disc.
     * file download state is set to COMPLETED automatically
     *
     * @param inputStream - Http response stream, which contains data to be saved on the disk - should not be null
     * @throws Exception Error during writing or if inputStream is null
     */
    public void saveToFile(InputStream inputStream) throws Exception;

    /**
     * Sets file download state to WAITING.
     * Stops download thread for given time.
     *
     * @param seconds time to sleep
     * @throws InterruptedException if the thread was interrupted during sleeping
     */
    void sleep(int seconds) throws InterruptedException;

    /**
     * Checks whether current downloading process has been stopped
     *
     * @return true if downloading of the file was interrupted
     */
    boolean isTerminated();
}
