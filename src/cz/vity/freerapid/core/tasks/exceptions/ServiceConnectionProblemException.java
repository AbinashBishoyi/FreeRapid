package cz.vity.freerapid.core.tasks.exceptions;

/**
 * @author Ladislav Vitasek
 */
public class ServiceConnectionProblemException extends Exception {
    public ServiceConnectionProblemException(String message) {
        super(message);
    }
}