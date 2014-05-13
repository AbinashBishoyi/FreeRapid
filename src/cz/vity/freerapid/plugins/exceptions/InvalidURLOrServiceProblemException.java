package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class InvalidURLOrServiceProblemException extends ErrorDuringDownloadingException {
    public InvalidURLOrServiceProblemException(String message) {
        super(message);
    }
}
