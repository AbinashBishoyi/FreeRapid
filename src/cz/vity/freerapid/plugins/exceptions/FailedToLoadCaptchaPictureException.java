package cz.vity.freerapid.plugins.exceptions;

/**
 * Failed to load CAPTCHA picture
 *
 * @author Vity
 */
public class FailedToLoadCaptchaPictureException extends ErrorDuringDownloadingException {

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException()
     */
    public FailedToLoadCaptchaPictureException() {
        super("Failed to load captcha picture");
    }

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(String)
     */
    public FailedToLoadCaptchaPictureException(String message) {
        super(message);
    }

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(String, Throwable)
     */
    public FailedToLoadCaptchaPictureException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(Throwable)
     */
    public FailedToLoadCaptchaPictureException(Throwable cause) {
        super(cause);
    }
}
