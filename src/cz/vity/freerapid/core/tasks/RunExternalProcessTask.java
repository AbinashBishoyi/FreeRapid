package cz.vity.freerapid.core.tasks;

import org.jdesktop.application.Application;

/**
 * @author ntoskrnl
 */
public class RunExternalProcessTask extends CoreTask<Void, Void> {

    public RunExternalProcessTask(Application application) {
        super(application);
    }

    @Override
    public Void doInBackground() throws Exception {
        final String command = "test.exe";//TODO
        final Process process = Runtime.getRuntime().exec(command);
        try {
            process.waitFor();
        } catch (InterruptedException e) {
            //ignore
        }
        return null;
    }

}
