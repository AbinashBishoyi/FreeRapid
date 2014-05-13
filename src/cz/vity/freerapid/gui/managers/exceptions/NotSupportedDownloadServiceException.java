package cz.vity.freerapid.gui.managers.exceptions;

/**
 * @author Vity
 */
public class NotSupportedDownloadServiceException extends RuntimeException {

    public NotSupportedDownloadServiceException() {
    }

    public NotSupportedDownloadServiceException(String message) {
        super(message);
    }

    public NotSupportedDownloadServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotSupportedDownloadServiceException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getLocalizedMessage() {
        return "NotSupportedDownloadByServiceException";
    }
}
