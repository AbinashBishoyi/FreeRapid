package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class URLNotAvailableAnymoreException extends ErrorDuringDownloadingException {
    public URLNotAvailableAnymoreException(String message) {
        super(message);
    }
}
