package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.plugins.webclient.DownloadState;

import java.util.EnumSet;

import static cz.vity.freerapid.plugins.webclient.DownloadState.*;

/**
 * @author Ladislav Vitasek
 */
final public class DownloadsActions {

    private final static int priorities[] = new int[DownloadState.values().length];

    static {
        priorities[DownloadState.DOWNLOADING.ordinal()] = 0;
        priorities[DownloadState.GETTING.ordinal()] = 10;
        priorities[DownloadState.TESTING.ordinal()] = 20;
        priorities[DownloadState.WAITING.ordinal()] = 30;
        priorities[DownloadState.QUEUED.ordinal()] = 40;
        priorities[DownloadState.SLEEPING.ordinal()] = 50;
        priorities[DownloadState.ERROR.ordinal()] = 60;
        priorities[DownloadState.PAUSED.ordinal()] = 70;
        priorities[DownloadState.DISABLED.ordinal()] = 80;
        priorities[DownloadState.SKIPPED.ordinal()] = 90;
        priorities[DownloadState.CANCELLED.ordinal()] = 100;
        priorities[DownloadState.COMPLETED.ordinal()] = 110;
        priorities[DownloadState.DELETED.ordinal()] = 1000;
    }

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
    public static EnumSet<DownloadState> resumeEnabledStates = EnumSet.of(ERROR, SLEEPING, CANCELLED, SKIPPED, PAUSED, DISABLED);
    /**
     * set of states in which user can press cancel button
     */
    public static EnumSet<DownloadState> cancelEnabledStates = EnumSet.of(COMPLETED, ERROR, SLEEPING, DOWNLOADING, GETTING, WAITING, PAUSED, DISABLED, TESTING, SKIPPED);
    /**
     * set of states in which user can press force download action
     */
    public static EnumSet<DownloadState> forceEnabledStates = EnumSet.of(ERROR, SLEEPING, QUEUED, PAUSED, CANCELLED, SKIPPED, DISABLED);
    /**
     * set of states in which user can press validate links download action
     */
    public static EnumSet<DownloadState> recheckExistingStates = EnumSet.of(ERROR, QUEUED, PAUSED, CANCELLED, SKIPPED, DISABLED);
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

    public static int getPriorityForState(DownloadState s) {
        return priorities[s.ordinal()];
    }
}
