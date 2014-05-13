package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Ladislav Vitasek
 */
public class InvalidUsernameOrPassword extends ErrorDuringDownloadingException {
    public InvalidUsernameOrPassword() {
        super();
    }

    public InvalidUsernameOrPassword(String message) {
        super(message);
    }

    public InvalidUsernameOrPassword(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidUsernameOrPassword(Throwable cause) {
        super(cause);
    }
}
