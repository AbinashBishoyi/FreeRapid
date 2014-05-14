package cz.vity.freerapid.gui.managers.interfaces;

import cz.vity.freerapid.gui.managers.StateChangeEvent;

import java.util.EventListener;

/**
 * @author Vity
 */
public interface FileStateChangeListener extends EventListener{
    public void stateChanged(StateChangeEvent event);
}
