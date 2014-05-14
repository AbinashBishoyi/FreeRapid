package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.CoreTask;
import cz.vity.freerapid.gui.managers.interfaces.FileStateChangeListener;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import jlibs.core.lang.RuntimeUtil;
import org.jdesktop.application.Application;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class ReconnectTask extends AbstractCustomTask implements FileStateChangeListener {
    private ManagerDirector director;
    private final static Logger logger = Logger.getLogger(ReconnectTask.class.getName());

    public ReconnectTask() {
    }

    @Override
    public void register(ManagerDirector director) {
        this.director = director;
        director.getDataManager().addFileStateChangedListener(this);
    }

    @Override
    public void unregister(ManagerDirector director) {
        director.getDataManager().removeFileStateChangedListener(this);
        this.director = null;
    }

    @Override
    public void stateChanged(StateChangeEvent event) {
        if (event.getNewState() == DownloadState.COMPLETED) {
            final boolean b = AppPrefs.getProperty(UserProp.RECONNECT_SCRIPT_ENABLED, UserProp.RECONNECT_SCRIPT_ENABLED_DEFAULT);
            if (!b) {
                return;
            }
            final String script = AppPrefs.getProperty(UserProp.RECONNECT_SCRIPT, null);
            if (script == null)
                return;
            final DataManager dataManager = director.getDataManager();
            if (dataManager.isDownloading()) {
                director.getDataManager().setDownloadFilesState(DownloadState.QUEUED, DownloadState.HOLD_ON);
            } else {
                //run script - problem - sync on AWT thread

                final ScriptTask scriptTask = new ScriptTask(director.getContext().getApplication());
                scriptTask.addTaskListener(new TaskListener.Adapter<Void, Void>() {

                    @Override
                    public void doInBackground(TaskEvent<Void> event) {
                        super.doInBackground(event);
                        director.getDataManager().setDownloadFilesState(DownloadState.QUEUED, DownloadState.HOLD_ON);
                    }

                    @Override
                    public void finished(TaskEvent<Void> event) {
                        director.getDataManager().setDownloadFilesState(DownloadState.HOLD_ON, DownloadState.QUEUED);
                    }
                });
                director.getTaskServiceManager().runTask(TaskServiceManager.WORK_WITH_FILE_SERVICE, scriptTask);
            }
        }
    }

    private class ScriptTask extends CoreTask<Void, Void> {

        public ScriptTask(Application application) {
            super(application);
        }

        @Override
        protected Void doInBackground() throws Exception {
            final String script = AppPrefs.getProperty(UserProp.RECONNECT_SCRIPT, null);
            if (script == null) {
                return null;
            }
            final String s = RuntimeUtil.runCommand(script);
            logger.info("Script output:" + s);
            return null;
        }

        @Override
        protected void failed(Throwable cause) {
            logger.log(Level.SEVERE, "Running script failed", cause);
        }
    }

}
