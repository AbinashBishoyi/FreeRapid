package cz.vity.freerapid.plugins.webclient.hoster;

import cz.vity.freerapid.plugins.exceptions.FailedToLoadCaptchaPictureException;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import org.apache.commons.httpclient.methods.GetMethod;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Ladislav Vitasek
 */
public class CaptchaSupport {
    private final HttpDownloadClient client;
    private final DialogSupport dialogSupport;

    public CaptchaSupport(HttpDownloadClient client, DialogSupport dialogSupport) {
        this.client = client;
        this.dialogSupport = dialogSupport;
    }

    public String askForCaptcha(final BufferedImage image) throws Exception {
        return dialogSupport.askForCaptcha(image);
    }

    public String getCaptcha(final String url) throws FailedToLoadCaptchaPictureException {
        try {
            return dialogSupport.askForCaptcha(getCaptchaImage(url));
        } catch (FailedToLoadCaptchaPictureException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToLoadCaptchaPictureException(e);
        }
    }

    public BufferedImage getCaptchaImage(final String url) throws FailedToLoadCaptchaPictureException {
        final GetMethod getMethod = client.getGetMethod(url);
        try {
            InputStream stream = client.makeRequestForFile(getMethod);
            if (stream == null)
                throw new FailedToLoadCaptchaPictureException();
            return loadCaptcha(stream);
        } catch (FailedToLoadCaptchaPictureException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToLoadCaptchaPictureException(e);
        }

    }

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
