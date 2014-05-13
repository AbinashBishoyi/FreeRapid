package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class NotSupportedDownloadServiceException extends RuntimeException {

    public NotSupportedDownloadServiceException() {
    }

    public NotSupportedDownloadServiceException(String message) {
        super(message);
    }
}
