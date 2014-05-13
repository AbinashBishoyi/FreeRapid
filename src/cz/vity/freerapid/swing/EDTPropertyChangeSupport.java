package cz.vity.freerapid.swing;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeSupport;

/**
 * @author Vity
 */
public class EDTPropertyChangeSupport extends PropertyChangeSupport {
    public EDTPropertyChangeSupport(Object source) {
        super(source);
    }

    public void firePropertyChange(final PropertyChangeEvent e) {
        if (SwingUtilities.isEventDispatchThread()) {
            super.firePropertyChange(e);
        } else {
            Runnable doFirePropertyChange = new Runnable() {
                public void run() {
                    firePropertyChange(e);
                }
            };
            SwingUtilities.invokeLater(doFirePropertyChange);
        }
    }
}