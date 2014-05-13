package cz.vity.freerapid.plugins.webclient;

import java.util.EnumSet;

/**
 * @author Vity
 */
public enum DownloadState {
    PAUSED, QUEUED, GETTING, WAITING, SLEEPING, DOWNLOADING, ERROR, CANCELLED, COMPLETED, DELETED, DISABLED;

    public static boolean isProcessState(DownloadState s) {
        return s == WAITING || s == DOWNLOADING || s == GETTING;
    }

    public static EnumSet<DownloadState> pauseEnabledStates = EnumSet.of(ERROR, SLEEPING, GETTING, QUEUED, WAITING, DISABLED);

    public static EnumSet<DownloadState> resumeEnabledStates = EnumSet.of(ERROR, SLEEPING, CANCELLED, PAUSED, DISABLED);

    public static EnumSet<DownloadState> cancelEnabledStates = EnumSet.of(COMPLETED, ERROR, SLEEPING, DOWNLOADING, GETTING, WAITING, PAUSED, DISABLED);

    public static EnumSet<DownloadState> forceEnabledStates = EnumSet.of(ERROR, SLEEPING, QUEUED, PAUSED, CANCELLED, DISABLED);

    public static EnumSet<DownloadState> completedStates = EnumSet.of(COMPLETED);
}
