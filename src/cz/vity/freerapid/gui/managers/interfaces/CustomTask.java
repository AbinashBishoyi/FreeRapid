package cz.vity.freerapid.gui.managers.interfaces;

import cz.vity.freerapid.gui.managers.ManagerDirector;

/**
 * @author Vity
 */
public interface CustomTask {
    public String getHumanReadableName();
    public void setEnabled(boolean enabled);
    public boolean isEnabled();
    public void register(ManagerDirector director);
    public void unregister(ManagerDirector director);
}
