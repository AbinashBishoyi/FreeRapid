package cz.vity.freerapid.plugins.exceptions;

/**
 * Error during downloading process
 *
 * @author Vity
 */
public class ErrorDuringDownloadingException extends Exception {

    /**
     * Constructs a new ErrorDuringDownloadingException.
     */
    public ErrorDuringDownloadingException() {
        super();
    }

    /**
     * Constructor - creates a new ErrorDuringDownloadingException instance.
     *
     * @param message exception message
     */
    public ErrorDuringDownloadingException(String message) {
        super(message);
    }

    /**
     * Constructor - creates a new ErrorDuringDownloadingException instance.
     *
     * @param message exception message
     * @param cause   exception that caused this exception
     */
    public ErrorDuringDownloadingException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor - creates a new ErrorDuringDownloadingException instance.
     *
     * @param cause exception that caused this exception
     */
    public ErrorDuringDownloadingException(Throwable cause) {
        super(cause);
    }

}
