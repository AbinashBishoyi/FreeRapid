package cz.vity.freerapid.plugins.exceptions;

/**
 * File on the given URL is not available anymore
 *
 * @author Vity
 */
public class URLNotAvailableAnymoreException extends NotRecoverableDownloadException {
    /**
     * @see NotRecoverableDownloadException#NotRecoverableDownloadException(String)
     */
    public URLNotAvailableAnymoreException(String message) {
        super(message);
    }
}
