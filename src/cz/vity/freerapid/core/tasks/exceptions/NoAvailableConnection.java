package cz.vity.freerapid.core.tasks.exceptions;

/**
 * @author Ladislav Vitasek
 */
public class NoAvailableConnection extends Exception {
    public NoAvailableConnection() {
        super();
    }

    public NoAvailableConnection(String message) {
        super(message);
    }

    public NoAvailableConnection(String message, Throwable cause) {
        super(message, cause);
    }

    public NoAvailableConnection(Throwable cause) {
        super(cause);
    }
}
