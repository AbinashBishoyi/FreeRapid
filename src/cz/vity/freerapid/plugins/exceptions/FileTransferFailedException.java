package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class FileTransferFailedException extends NotRecoverableDownloadException {
    /**
     * Constructor - creates a new FileTransferFailedException instance.
     *
     * @param s
     */
    public FileTransferFailedException(String s) {
        super(s);
    }
}
