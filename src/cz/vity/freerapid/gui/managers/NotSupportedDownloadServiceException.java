package cz.vity.freerapid.gui.managers;

/**
 * @author Ladislav Vitasek
 */
public class NotSupportedDownloadServiceException extends Exception {

    public NotSupportedDownloadServiceException() {
    }

    public NotSupportedDownloadServiceException(String message) {
        super(message);
    }
}
