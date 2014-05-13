package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class InvalidURLOrServiceProblemException extends ErrorDuringDownloadingException {
    /**
     * @see ErrorDuringDownloadingException#ErrorDuringDownloadingException(String)
     */
    public InvalidURLOrServiceProblemException(String message) {
        super(message);
    }
}
