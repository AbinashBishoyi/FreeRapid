package cz.vity.freerapid.plugins.webclient.hoster;

import cz.vity.freerapid.plugins.exceptions.FailedToLoadCaptchaPictureException;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class that help to manage working with CAPTCHA in plugins.
 *
 * @author Ladislav Vitasek
 */
public class CaptchaSupport {
    /**
     * Field client
     */
    private final HttpDownloadClient client;
    /**
     * Field dialogSupport
     */
    private final DialogSupport dialogSupport;

    /**
     * Constructor
     *
     * @param client        current HTTP connection manager
     * @param dialogSupport support for showing simple dialogs to user
     */
    public CaptchaSupport(HttpDownloadClient client, DialogSupport dialogSupport) {
        this.client = client;
        this.dialogSupport = dialogSupport;
    }

    /**
     * Shows default simple input dialog for getting input from user.
     *
     * @param image CAPTCHA image to show user
     * @return returns string given from user - returns null if user cancelled dialog; method can return empty string
     * @throws Exception if something went wrong - thread interruption
     * @see cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport#askForCaptcha(java.awt.image.BufferedImage)
     */
    public String askForCaptcha(final BufferedImage image) throws Exception {
        return dialogSupport.askForCaptcha(image);
    }


    /**
     * Shows default simple input dialog for getting input from user.
     * Method loads CAPTCHA image and show it to user.
     *
     * @param url CAPTCHA image url
     * @return returns string given from user - returns null if user cancelled dialog; method can return empty string
     * @throws FailedToLoadCaptchaPictureException
     *          if http operation failed to load remote image
     */
    public String getCaptcha(final String url) throws FailedToLoadCaptchaPictureException {
        try {
            return dialogSupport.askForCaptcha(getCaptchaImage(url));
        } catch (FailedToLoadCaptchaPictureException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToLoadCaptchaPictureException(e);
        }
    }

    /**
     * Method downloads image from given URL. Usually used to load CAPTCHA image for CAPTCHA recognition (OCR).
     *
     * @param url CAPTCHA image url
     * @return returns instance of loaded image
     * @throws FailedToLoadCaptchaPictureException
     *          if http operation failed to load remote image
     */
    public BufferedImage getCaptchaImage(final String url) throws FailedToLoadCaptchaPictureException {
        try {
            return loadCaptcha(loadStream(url));
        } catch (FailedToLoadCaptchaPictureException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToLoadCaptchaPictureException(e);
        }

    }

    private InputStream loadStream(String url) throws IOException, FailedToLoadCaptchaPictureException {
        InputStream stream = client.makeRequestForFile(client.getGetMethod(url));
        if (stream == null)
            throw new FailedToLoadCaptchaPictureException();
        return stream;
    }

    /**
     * Method creates image from given data input stream.
     *
     * @param inputStream data input stream
     * @return instance of loaded image
     * @throws FailedToLoadCaptchaPictureException
     *                              if reading from input stream failed to load image
     * @throws NullPointerException if given input stream is null
     */
    public BufferedImage loadCaptcha(InputStream inputStream) throws FailedToLoadCaptchaPictureException {
        if (inputStream == null)
            throw new NullPointerException("InputStreamForCaptchaIsNull");
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new FailedToLoadCaptchaPictureException("ReadingCaptchaPictureFailed", e);
        }
    }

}
