package cz.vity.freerapid.plugins.exceptions;

/**
 * Service requires correct user input to continue downloading.
 *
 * @author Vity
 */
public class CaptchaEntryInputMismatchException extends NotRecoverableDownloadException {

    /**
     * Constructs a new CaptchaEntryInputMismatchException.
     */
    public CaptchaEntryInputMismatchException() {
        super();
    }

    /**
     * @see NotRecoverableDownloadException#NotRecoverableDownloadException(String)
     */
    public CaptchaEntryInputMismatchException(String message) {
        super(message);
    }

    /**
     * @see NotRecoverableDownloadException#NotRecoverableDownloadException(String, Throwable)
     */
    public CaptchaEntryInputMismatchException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see NotRecoverableDownloadException#NotRecoverableDownloadException(Throwable)
     */
    public CaptchaEntryInputMismatchException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        return "CaptchaEntryInputMismatch";
    }
}