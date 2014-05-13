package cz.vity.freerapid.plugins.webclient;

import java.util.EnumSet;

/**
 * Describes all possible download states
 *
 * @author Vity
 */
public enum DownloadState {
    /**
     * file is in paused state
     */
    PAUSED,
    /**
     * file is queued in the front for downloading
     */
    QUEUED,
    /**
     * state is indicating that downloading process is waiting for response from the server or writing to disk
     */
    GETTING,
    /**
     * process is in "downloading state" and waits for other link to continue
     */
    WAITING,
    /**
     * indicates "WAIT!" state - download is not available for given time
     */
    SLEEPING,
    /**
     * file is being downloaded
     */
    DOWNLOADING,
    /**
     * downloading/or checking ended with an error
     */
    ERROR,
    /**
     * user cancelled download of this file
     */
    CANCELLED,
    /**
     * file is sucessfully downloaded
     */
    COMPLETED,
    /**
     * file was deleted from the queue
     */
    DELETED,
    /**
     * state that indicates that download cannot continue because its plugin is disabled
     */
    DISABLED,
    /**
     * running test check
     */
    TESTING;

    /**
     * Checks whether state download state means "working"
     *
     * @param s given state
     * @return true if state is in working phase
     */
    public static boolean isProcessState(DownloadState s) {
        return s == WAITING || s == DOWNLOADING || s == GETTING || s == TESTING;
    }

    /**
     * set of states in which user can press pause button
     */
    public static EnumSet<DownloadState> pauseEnabledStates = EnumSet.of(ERROR, SLEEPING, GETTING, QUEUED, WAITING, DISABLED, TESTING);

    /**
     * set of states in which user can press resume button
     */
    public static EnumSet<DownloadState> resumeEnabledStates = EnumSet.of(ERROR, SLEEPING, CANCELLED, PAUSED, DISABLED);

    /**
     * set of states in which user can press cancel button
     */
    public static EnumSet<DownloadState> cancelEnabledStates = EnumSet.of(COMPLETED, ERROR, SLEEPING, DOWNLOADING, GETTING, WAITING, PAUSED, DISABLED, TESTING);

    /**
     * set of states in which user can press force download action
     */
    public static EnumSet<DownloadState> forceEnabledStates = EnumSet.of(ERROR, SLEEPING, QUEUED, PAUSED, CANCELLED, DISABLED);

    /**
     * states those indicates that file is completed
     */
    public static EnumSet<DownloadState> completedStates = EnumSet.of(COMPLETED);
}
