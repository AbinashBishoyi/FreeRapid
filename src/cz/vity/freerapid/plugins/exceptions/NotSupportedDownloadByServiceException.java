package cz.vity.freerapid.plugins.exceptions;

/**
 * Service/plugin does not support given URL for downloading
 *
 * @author Vity
 */
public class NotSupportedDownloadByServiceException extends NotRecoverableDownloadException {

    /**
     * Constructor - creates a new NotSupportedDownloadByServiceException instance.
     */
    public NotSupportedDownloadByServiceException() {
        super();
    }

    /**
     * Constructor - creates a new NotSupportedDownloadByServiceException instance.
     *
     * @param message
     */
    public NotSupportedDownloadByServiceException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return super.getLocalizedMessage();
    }
}