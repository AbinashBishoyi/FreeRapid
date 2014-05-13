package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class NotEnoughSpaceException extends ErrorDuringDownloadingException {
    public NotEnoughSpaceException() {
        super("Not enough space on target disk");
    }
}
