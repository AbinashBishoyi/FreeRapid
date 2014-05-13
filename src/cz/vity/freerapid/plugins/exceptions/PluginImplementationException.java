package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class PluginImplementationException extends ErrorDuringDownloadingException {
    public PluginImplementationException(String message) {
        super(message);
    }
}