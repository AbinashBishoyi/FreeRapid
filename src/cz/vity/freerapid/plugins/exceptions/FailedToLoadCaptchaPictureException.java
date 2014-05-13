package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class FailedToLoadCaptchaPictureException extends ErrorDuringDownloadingException {

    public FailedToLoadCaptchaPictureException() {
        super("Failed to load captcha picture");
    }

    public FailedToLoadCaptchaPictureException(String message) {
        super(message);
    }

    public FailedToLoadCaptchaPictureException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedToLoadCaptchaPictureException(Throwable cause) {
        super(cause);
    }
}
