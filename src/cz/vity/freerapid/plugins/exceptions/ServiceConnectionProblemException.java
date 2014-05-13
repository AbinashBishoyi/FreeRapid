package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class ServiceConnectionProblemException extends ErrorDuringDownloadingException {
    public ServiceConnectionProblemException(String message) {
        super(message);
    }
}