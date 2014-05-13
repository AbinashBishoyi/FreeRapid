package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Ladislav Vitasek
 */
public class NotRecoverableDownloadException extends ErrorDuringDownloadingException {
    public NotRecoverableDownloadException() {
        super();
    }

    public NotRecoverableDownloadException(String message) {
        super(message);
    }

    public NotRecoverableDownloadException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotRecoverableDownloadException(Throwable cause) {
        super(cause);
    }
}
