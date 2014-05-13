package cz.vity.freerapid.gui.managers.interfaces;

import javax.swing.*;

/**
 * @author Vity
 */
public interface IInformedTab {
    public JComponent getComponent();

    public Icon getIcon();

    public String getTip();

    public String getTabName();

    public void activate();

    public void deactivate();

    public String getID();

}