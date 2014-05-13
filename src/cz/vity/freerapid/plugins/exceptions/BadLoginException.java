package cz.vity.freerapid.plugins.exceptions;

/**
 * Invalid user name/password for accessing privileged area
 *
 * @author Ladislav Vitasek
 */
public class BadLoginException extends NotRecoverableDownloadException {
    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     */
    public BadLoginException() {
        super("InvalidUsernameOrPassword");
    }

    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     *
     * @param message
     */
    public BadLoginException(String message) {
        super(message);
    }

    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     *
     * @param message
     * @param cause
     */
    public BadLoginException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     *
     * @param cause
     */
    public BadLoginException(Throwable cause) {
        super(cause);
    }
}
