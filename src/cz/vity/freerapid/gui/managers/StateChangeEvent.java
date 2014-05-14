package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.plugins.webclient.DownloadState;

import javax.swing.event.ChangeEvent;

/**
 * @author Vity
 */
public class StateChangeEvent extends ChangeEvent {

    private DownloadState oldState;
    private DownloadState newState;

    /**
     * Constructs a ChangeEvent object.
     *
     * @param source  the Object that is the source of the event
     *                (typically <code>this</code>)
     * @param oldState
     * @param newState
     */
    public StateChangeEvent(Object source, DownloadState oldState, DownloadState newState) {
        super(source);
        this.oldState = oldState;
        this.newState = newState;
    }

    public DownloadState getOldState() {
        return oldState;
    }

    public DownloadState getNewState() {
        return newState;
    }
}
