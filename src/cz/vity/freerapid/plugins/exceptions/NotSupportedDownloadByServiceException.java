package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class NotSupportedDownloadByServiceException extends ErrorDuringDownloadingException {

    public NotSupportedDownloadByServiceException() {
        super();
    }

    public NotSupportedDownloadByServiceException(String message) {
        super(message);
    }
}