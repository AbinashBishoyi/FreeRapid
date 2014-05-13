package cz.vity.freerapid.plugins.webclient;

/**
 * @author Vity
 */
public enum DownloadState {
    PAUSED, QUEUED, GETTING, WAITING, DOWNLOADING, ERROR, CANCELLED, COMPLETED, DELETED;

    public static boolean isProcessState(DownloadState s) {
        return s == WAITING || s == DOWNLOADING || s == GETTING;
    }
}
