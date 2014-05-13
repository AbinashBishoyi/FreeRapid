package cz.vity.freerapid.model;

/**
 * @author Ladislav Vitasek
 */
public enum DownloadState {
    PAUSED, QUEUED, GETTING, WAITING, DOWNLOADING, ERROR, CANCELLED, COMPLETED, DELETED;

    public static boolean isProcessState(DownloadState s) {
        return s == WAITING || s == DOWNLOADING || s == GETTING;
    }
}
