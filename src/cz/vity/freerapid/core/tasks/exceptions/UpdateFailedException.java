package cz.vity.freerapid.core.tasks.exceptions;

/**
 * @author Ladislav Vitasek
 */
public class UpdateFailedException extends Exception {
    public UpdateFailedException() {
        super();
    }

    public UpdateFailedException(String message) {
        super(message);
    }

    public UpdateFailedException(String message, Throwable cause) {
        super(message, cause);
    }

    public UpdateFailedException(Throwable cause) {
        super(cause);
    }
}
