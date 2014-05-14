package cz.vity.freerapid.gui.managers;

import java.util.EventListener;

/**
 * @author Vity
 */
public interface FileStateChangeListener extends EventListener{
    public void stateChanged(StateChangeEvent event);    
}
