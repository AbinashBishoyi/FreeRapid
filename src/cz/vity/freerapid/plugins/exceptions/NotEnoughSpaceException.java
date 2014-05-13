package cz.vity.freerapid.plugins.exceptions;

/**
 * Not enough space on the disk exception.
 *
 * @author Vity
 */
public class NotEnoughSpaceException extends ErrorDuringDownloadingException {
    /**
     * Constructs a new ErrorDuringDownloadingException.
     */
    public NotEnoughSpaceException() {
        super();
    }

    @Override
    public String getLocalizedMessage() {
        return "NotEnoughSpaceException";
    }
}
