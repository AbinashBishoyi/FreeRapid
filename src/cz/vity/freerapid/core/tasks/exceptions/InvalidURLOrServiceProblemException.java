package cz.vity.freerapid.core.tasks.exceptions;

/**
 * @author Ladislav Vitasek
 */
public class InvalidURLOrServiceProblemException extends Exception {
    public InvalidURLOrServiceProblemException(String message) {
        super(message);
    }
}
