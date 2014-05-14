package cz.vity.freerapid.plugins.exceptions;

/**
 * File on the given URL is not available anymore
 *
 * @author Vity
 */
public class URLNotAvailableAnymoreException extends NotRecoverableDownloadException {

    public URLNotAvailableAnymoreException() {
        super();
    }

    public URLNotAvailableAnymoreException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @see NotRecoverableDownloadException#NotRecoverableDownloadException(String)
     */
    public URLNotAvailableAnymoreException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "URLNotAvailableAnymore";
    }
}
