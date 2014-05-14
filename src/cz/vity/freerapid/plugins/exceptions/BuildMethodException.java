package cz.vity.freerapid.plugins.exceptions;

/**
 * Exception used by MethodBuilder to inform about fatal exception during building HTTP method from the content of website.
 *
 * @author Vity
 */
public class BuildMethodException extends PluginImplementationException {
    /**
     * Constructor
     */
    public BuildMethodException() {
        super();
    }

    /**
     * Constructor
     *
     * @param message error message
     */
    public BuildMethodException(String message) {
        super(message);
    }
}
