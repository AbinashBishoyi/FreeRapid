package cz.vity.freerapid.gui.managers;

/**
 * @author Vity
 */
interface CustomTask {
    public String getHumanReadableName();
    public void setEnabled(boolean enabled);
    public boolean isEnabled();
    public void register(ManagerDirector director);
    public void unregister(ManagerDirector director);
}
