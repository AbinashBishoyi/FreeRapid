package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class CaptchaEntryInputMismatchException extends ErrorDuringDownloadingException {

    public CaptchaEntryInputMismatchException() {
        super("Captcha Entry Input Mismatch");
    }

    public CaptchaEntryInputMismatchException(String message) {
        super(message);
    }

    public CaptchaEntryInputMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaEntryInputMismatchException(Throwable cause) {
        super(cause);
    }
}