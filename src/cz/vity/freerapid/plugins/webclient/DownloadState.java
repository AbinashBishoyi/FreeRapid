package cz.vity.freerapid.plugins.webclient;

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
     * file is waiting for other downloads (for "run when current downloads complete" actions) or external process to complete
     */
    HOLD_ON,
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
     * downloading or checking ended with an error
     */
    ERROR,
    /**
     * user cancelled download of this file
     */
    CANCELLED,
    /**
     * user skipped download of this file
     */
    SKIPPED,
    /**
     * file was successfully downloaded
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
    TESTING

}
