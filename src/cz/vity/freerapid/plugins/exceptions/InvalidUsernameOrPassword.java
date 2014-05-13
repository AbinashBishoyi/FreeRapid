package cz.vity.freerapid.plugins.exceptions;

/**
 * Invalid user name/password for accessing privileged area
 *
 * @author Ladislav Vitasek
 */
public class InvalidUsernameOrPassword extends NotRecoverableDownloadException {
    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     */
    public InvalidUsernameOrPassword() {
        super();
    }

    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     *
     * @param message
     */
    public InvalidUsernameOrPassword(String message) {
        super(message);
    }

    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     *
     * @param message
     * @param cause
     */
    public InvalidUsernameOrPassword(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor - creates a new InvalidUsernameOrPassword instance.
     *
     * @param cause
     */
    public InvalidUsernameOrPassword(Throwable cause) {
        super(cause);
    }
}
