package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.FailedToLoadCaptchaPictureException;

import java.awt.image.BufferedImage;
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

    @Deprecated
    BufferedImage getCaptchaImage(final String url) throws FailedToLoadCaptchaPictureException;

    @Deprecated
    String askForCaptcha(BufferedImage image) throws Exception;

    @Deprecated
    String getCaptcha(final String url) throws FailedToLoadCaptchaPictureException;
}
