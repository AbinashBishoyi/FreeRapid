package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class FileTransferFailedException extends NotRecoverableDownloadException {
    public FileTransferFailedException(String s) {
        super(s);
    }
}
