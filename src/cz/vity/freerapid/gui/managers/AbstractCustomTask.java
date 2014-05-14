package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.managers.interfaces.CustomTask;

/**
 * @author Vity
 */
public abstract class AbstractCustomTask implements CustomTask {
    protected boolean enabled = true;

    public AbstractCustomTask() {

    }

    @Override
    public String getHumanReadableName() {
        return "System Task";
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

}
