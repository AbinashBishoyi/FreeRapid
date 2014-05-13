package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class URLNotAvailableAnymoreException extends NotRecoverableDownloadException {
    public URLNotAvailableAnymoreException(String message) {
        super(message);
    }
}
