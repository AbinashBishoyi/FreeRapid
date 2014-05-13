package cz.vity.freerapid.plugins.exceptions;

/**
 * Plugin does not understand response from the server.<br>
 * It usually means that server's web pages were changed.
 *
 * @author Vity
 */
public class PluginImplementationException extends NotRecoverableDownloadException {

    /**
     * Constructor
     */
    public PluginImplementationException() {
    }

    /**
     * @see NotRecoverableDownloadException#NotRecoverableDownloadException(String)
     */
    public PluginImplementationException(String message) {
        super(message);
    }

    @Override
    public String getLocalizedMessage() {
        return "PluginImplementationException";
    }
}