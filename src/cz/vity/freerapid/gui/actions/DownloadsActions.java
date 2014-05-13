package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.plugins.webclient.DownloadState;
import static cz.vity.freerapid.plugins.webclient.DownloadState.*;

import java.util.EnumSet;

/**
 * @author Ladislav Vitasek
 */
final public class DownloadsActions {

    /**
     * Private constructor
     */
    private DownloadsActions() {
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
     * set of states in which user can press validate links download action
     */
    public static EnumSet<DownloadState> recheckExistingStates = EnumSet.of(ERROR, QUEUED, PAUSED, CANCELLED, DISABLED);
    /**
     * states those indicates that file is completed
     */
    public static EnumSet<DownloadState> completedStates = EnumSet.of(COMPLETED);

    /**
     * Checks whether state download state means "working"
     *
     * @param s given state
     * @return true if state is in working phase
     */
    public static boolean isProcessState(DownloadState s) {
        return s == WAITING || s == DOWNLOADING || s == GETTING || s == TESTING;
    }
}
