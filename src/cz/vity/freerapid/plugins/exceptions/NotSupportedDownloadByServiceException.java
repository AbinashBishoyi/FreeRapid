package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class NotSupportedDownloadByServiceException extends NotRecoverableDownloadException {

    public NotSupportedDownloadByServiceException() {
        super();
    }

    public NotSupportedDownloadByServiceException(String message) {
        super(message);
    }
}