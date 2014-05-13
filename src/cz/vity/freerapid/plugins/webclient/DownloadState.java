package cz.vity.freerapid.plugins.webclient;

import java.util.EnumSet;

/**
 * @author Vity
 */
public enum DownloadState {
    PAUSED, QUEUED, GETTING, WAITING, DOWNLOADING, ERROR, CANCELLED, COMPLETED, DELETED;

    public static boolean isProcessState(DownloadState s) {
        return s == WAITING || s == DOWNLOADING || s == GETTING;
    }

    public static EnumSet<DownloadState> pauseEnabledStates = EnumSet.of(DownloadState.ERROR, DownloadState.GETTING, DownloadState.QUEUED, DownloadState.WAITING);

    public static EnumSet<DownloadState> resumeEnabledStates = EnumSet.of(DownloadState.ERROR, DownloadState.CANCELLED, DownloadState.PAUSED);

    public static EnumSet<DownloadState> cancelEnabledStates = EnumSet.of(DownloadState.COMPLETED, DownloadState.ERROR, DownloadState.DOWNLOADING, DownloadState.GETTING, DownloadState.WAITING, DownloadState.PAUSED);

    public static EnumSet<DownloadState> forceEnabledStates = EnumSet.of(DownloadState.QUEUED, DownloadState.PAUSED, DownloadState.CANCELLED);

    public static EnumSet<DownloadState> completedStates = EnumSet.of(DownloadState.COMPLETED);
}
