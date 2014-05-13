package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;

import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author Vity
 */
class PluginDevDownloadTask implements HttpFileDownloadTask {
    /**
     * Field logger instance
     */
    private final static Logger logger = Logger.getLogger(PluginDevDownloadTask.class.getName());

    /**
     * Field file - file that is being downloaded
     */
    private HttpFile file;
    /**
     * download client/robot that is used for downloading
     */
    private DownloadClient downloadClient;


    /**
     * Constructor
     *
     * @param file     file that is being downloaded
     * @param settings internet connection settings
     */
    PluginDevDownloadTask(HttpFile file, ConnectionSettings settings) {
        this.file = file;
        downloadClient = new DownloadClient();
        downloadClient.initClient(settings);
    }

    /**
     * Returns file that is given to be downloaded
     *
     * @return file for downloading
     */
    public HttpFile getDownloadFile() {
        return file;
    }

    /**
     * Client associated with current file - its HTTP connections are used to grab a file
     *
     * @return actual instance of HttpDownloadClient
     */
    public HttpDownloadClient getClient() {
        return downloadClient;
    }

    /**
     * Method that handles direct saving file onto physical disc.
     * file download state is set to COMPLETED automatically
     *
     * @param inputStream - Http response stream, which contains data to be saved on the disk - should not be null
     * @throws Exception Error during writing or if inputStream is null
     */
    public void saveToFile(InputStream inputStream) throws Exception {
        logger.info("Simulating saving file from stream");
        sleep(1);
        logger.info("File succesfully saved");
    }

    /**
     * Sets file download state to WAITING.
     * Stops download thread for given time.
     *
     * @param seconds time to sleep
     * @throws InterruptedException if the thread was interrupted during sleeping
     */
    public void sleep(int seconds) throws InterruptedException {
        file.setState(DownloadState.WAITING);

        logger.info("Going to sleep on " + (seconds) + " seconds");
        for (int i = seconds; i > 0; i--) {
            if (isTerminated())
                break;
            Thread.sleep(1000);
        }

    }

    /**
     * Checks whether current downloading process has been stopped
     *
     * @return true if downloading of the file was interrupted
     */
    public boolean isTerminated() {
        return false;
    }

}

