package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class PluginImplementationException extends NotRecoverableDownloadException {
    public PluginImplementationException(String message) {
        super(message);
    }
}