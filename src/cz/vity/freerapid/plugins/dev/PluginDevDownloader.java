package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.exceptions.FailedToLoadCaptchaPictureException;
import cz.vity.freerapid.plugins.webclient.*;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.logging.Logger;

/**
 * @author Vity
 */
class PluginDevDownloader implements HttpFileDownloader {
    private final static Logger logger = Logger.getLogger(PluginDevDownloader.class.getName());

    private HttpFile file;
    private DownloadClient downloadClient;


    PluginDevDownloader(HttpFile file, ConnectionSettings settings) {
        this.file = file;
        downloadClient = new DownloadClient();
        downloadClient.initClient(settings);
    }

    public HttpFile getDownloadFile() {
        return file;
    }

    public HttpDownloadClient getClient() {
        return downloadClient;
    }

    public void saveToFile(InputStream inputStream) throws Exception {
        logger.info("Simulating saving file from stream");
        sleep(1);
        logger.info("File succesfully saved");
    }

    public void sleep(int seconds) throws InterruptedException {
        file.setState(DownloadState.WAITING);

        logger.info("Going to sleep on " + (seconds) + " seconds");
        for (int i = seconds; i > 0; i--) {
            if (isTerminated())
                break;
            Thread.sleep(1000);
        }

    }

    public String askForCaptcha(BufferedImage image) throws Exception {
        return null;
    }

    public String getCaptcha(String url) throws FailedToLoadCaptchaPictureException {
        return null;
    }

    public boolean isTerminated() {
        return false;
    }

    public BufferedImage getCaptchaImage(String url) throws FailedToLoadCaptchaPictureException {
        return null;
    }
}

