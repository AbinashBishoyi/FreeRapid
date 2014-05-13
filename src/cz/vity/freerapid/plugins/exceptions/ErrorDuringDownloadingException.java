package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class ErrorDuringDownloadingException extends Exception {

    public ErrorDuringDownloadingException() {
        super();
    }

    public ErrorDuringDownloadingException(String message) {
        super(message);
    }

    public ErrorDuringDownloadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorDuringDownloadingException(Throwable cause) {
        super(cause);
    }
}
