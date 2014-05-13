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
}