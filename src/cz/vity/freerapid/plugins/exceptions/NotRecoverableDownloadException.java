package cz.vity.freerapid.plugins.exceptions;

/**
 * File downloading cannot continue. <br>
 * Represents parent for group of other exceptions.
 *
 * @author Ladislav Vitasek
 */
public class NotRecoverableDownloadException extends ErrorDuringDownloadingException {
    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException()
     */
    public NotRecoverableDownloadException() {
        super();
    }

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(String)
     */
    public NotRecoverableDownloadException(String message) {
        super(message);
    }

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(String, Throwable)
     */
    public NotRecoverableDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(Throwable)
     */
    public NotRecoverableDownloadException(Throwable cause) {
        super(cause);
    }
}
