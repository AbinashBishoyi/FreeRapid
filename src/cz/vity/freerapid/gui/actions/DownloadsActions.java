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
        priorities[DOWNLOADING.ordinal()] = 0;
        priorities[GETTING.ordinal()] = 10;
        priorities[TESTING.ordinal()] = 20;
        priorities[WAITING.ordinal()] = 30;
        priorities[QUEUED.ordinal()] = 40;
        priorities[SLEEPING.ordinal()] = 50;
        priorities[ERROR.ordinal()] = 60;
        priorities[HOLD_ON.ordinal()] = 70;
        priorities[PAUSED.ordinal()] = 80;
        priorities[DISABLED.ordinal()] = 90;
        priorities[SKIPPED.ordinal()] = 100;
        priorities[CANCELLED.ordinal()] = 110;
        priorities[COMPLETED.ordinal()] = 120;
        priorities[DELETED.ordinal()] = 1000;
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
    public static EnumSet<DownloadState> resumeEnabledStates = EnumSet.of(ERROR, SLEEPING, CANCELLED, SKIPPED, HOLD_ON, PAUSED, DISABLED);
    /**
     * set of states in which user can press cancel button
     */
    public static EnumSet<DownloadState> cancelEnabledStates = EnumSet.of(COMPLETED, ERROR, SLEEPING, DOWNLOADING, GETTING, WAITING, HOLD_ON, PAUSED, DISABLED, TESTING, SKIPPED);
    /**
     * set of states in which user can press force download action
     */
    public static EnumSet<DownloadState> forceEnabledStates = EnumSet.of(ERROR, SLEEPING, QUEUED, HOLD_ON, PAUSED, CANCELLED, SKIPPED, DISABLED);
    /**
     * set of states in which user can press validate links download action
     */
    public static EnumSet<DownloadState> recheckExistingStates = EnumSet.of(ERROR, QUEUED, HOLD_ON, PAUSED, CANCELLED, SKIPPED, DISABLED);
    /**
     * states those indicates that file is completed
     */
    public static EnumSet<DownloadState> completedStates = EnumSet.of(COMPLETED);

    /**
     * states those indicates that file is being downloaded
     */
    public static EnumSet<DownloadState> processStates = EnumSet.of(WAITING, DOWNLOADING, GETTING, TESTING);


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
