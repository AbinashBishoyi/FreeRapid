package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class FileTransferFailedException extends ErrorDuringDownloadingException {
    public FileTransferFailedException(String s) {
        super(s);
    }
}
