package cz.vity.freerapid.plugins.exceptions;

/**
 * Problem with connection to the service
 *
 * @author Vity
 */
public class ServiceConnectionProblemException extends ErrorDuringDownloadingException {
    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(String)
     */
    public ServiceConnectionProblemException(String message) {
        super(message);
    }

    public ServiceConnectionProblemException() {
        super("ServiceConnectionProblemException");
    }

    public ServiceConnectionProblemException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServiceConnectionProblemException(Throwable cause) {
        super(cause);
    }

}