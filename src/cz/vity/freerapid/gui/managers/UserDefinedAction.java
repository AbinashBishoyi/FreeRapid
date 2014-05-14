package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * User defined external action executed on defined event 
 * @author Vity
 * @since 0.85
 */
public class UserDefinedAction extends AbstractEventAction {
    private String script;
    private boolean waitForScriptEnd;
    private final static Logger logger = Logger.getLogger(UserDefinedAction.class.getName());


    public UserDefinedAction() {
        super();
    }

    public String getScript() {
        return script;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public boolean isWaitForScriptEnd() {
        return waitForScriptEnd;
    }

    public void setWaitForScriptEnd(boolean waitForScriptEnd) {
        this.waitForScriptEnd = waitForScriptEnd;
    }

    @Override
    public boolean run(ApplicationContext context) throws Exception {
        boolean wasError = false;
        if (script != null) {
            final String[] cmd = script.split("\n");
            for (String s : cmd) {
                if (s.trim().isEmpty()) {
                    continue;
                }
                try {
                    final Process process = Runtime.getRuntime().exec(s);
                    if (waitForScriptEnd) {
                        final int i = process.waitFor();
                        if (i != 0) {
                            //TODO 18n
                            throw new IOException("Invalid return code for command: " + s);
                        }
                    }
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                    if (isInformAboutError()) {
                        throw e;
                    }
                    wasError = true;
                }
            }

        }
        return !wasError;
    }
}
