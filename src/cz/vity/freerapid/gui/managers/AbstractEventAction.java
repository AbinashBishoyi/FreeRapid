package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.managers.interfaces.EventAction;
import org.jdesktop.application.ApplicationContext;

/**
 * @author Vity
 */
public class AbstractEventAction implements EventAction {

    private boolean informAboutError;
    private boolean enabled;
    private String name;
    private boolean runInTask;

    public AbstractEventAction() {
        runInTask = false;
        enabled = true;
        informAboutError = false;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isInformAboutError() {
        return informAboutError;
    }

    public boolean isRunInTask() {
        return runInTask;
    }

    public void setRunInTask(boolean runInTask) {
        this.runInTask = runInTask;
    }

    public void setInformAboutError(boolean informAboutError) {
        this.informAboutError = informAboutError;
    }

    public boolean run(ApplicationContext context) throws Exception {
        return false;
    }
}
