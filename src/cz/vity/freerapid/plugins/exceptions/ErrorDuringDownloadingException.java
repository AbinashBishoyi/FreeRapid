package cz.vity.freerapid.plugins.exceptions;

/**
 * @author Vity
 */
public class ErrorDuringDownloadingException extends Exception {
    private DownloadErrorType downloadErrorType;


    public ErrorDuringDownloadingException() {
        super();
    }

    public ErrorDuringDownloadingException(String message) {
        super(message);
    }

    public ErrorDuringDownloadingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorDuringDownloadingException(Throwable cause) {
        super(cause);
    }

    public DownloadErrorType getDownloadErrorType() {
        return downloadErrorType;
    }

    public void setDownloadErrorType(DownloadErrorType downloadErrorType) {
        this.downloadErrorType = downloadErrorType;
    }
}
