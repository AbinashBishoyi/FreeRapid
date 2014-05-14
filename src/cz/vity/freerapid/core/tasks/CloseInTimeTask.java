package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.os.OSCommand;
import cz.vity.freerapid.utilities.os.SystemCommander;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.Application;

import java.util.logging.Logger;

/**
 * @author Vity
 */
final public class CloseInTimeTask extends CoreTask<Void, Void> {
    private final static Logger logger = Logger.getLogger(CloseInTimeTask.class.getName());
    private DataManager dataManager;


    public CloseInTimeTask(Application application) {
        super(application);
        MainApp app = (MainApp) application;
        this.setUserCanCancel(true);
        this.setInputBlocker(new ScreenInputBlocker(this, BlockingScope.APPLICATION, app.getMainFrame(), null));
        Swinger.bringToFront(app.getMainFrame(), true);
        dataManager = app.getManagerDirector().getDataManager();
    }

    protected Void doInBackground() throws Exception {
        final int seconds = AppPrefs.getProperty(UserProp.CLOSE_APPLICATION_CONFIRM_WAITTIME, 30);
        for (int i = seconds; i > 0; i--) {
            if (isCancelled())
                break;
            message((i > 1) ? "closeAppMessageN" : "closeAppMessage1", i);
            Thread.sleep(1000);
            if (!dataManager.checkAllComplete()) {
                //   cancel(true);
                throw new InterruptedException();
            }
        }

        return null;
    }

    @Override
    protected void succeeded(Void taskResult) {
        final int property = AppPrefs.getProperty(UserProp.AUTOSHUTDOWN, UserProp.AUTOSHUTDOWN_DEFAULT);
        if (property == UserProp.AUTOSHUTDOWN_CLOSE) {
            getApplication().exit();
            return;
        } else if (property == UserProp.AUTOSHUTDOWN_DISABLED)
            return;
        final SystemCommander utils = SystemCommanderFactory.getInstance().getSystemCommanderInstance(getContext());
        final boolean force = AppPrefs.getProperty(UserProp.AUTOSHUTDOWN_FORCE, UserProp.AUTOSHUTDOWN_FORCE_DEFAULT);
        final boolean renew = AppPrefs.getProperty(UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED, UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED_DEFAULT);
        if (renew) {
            AppPrefs.storeProperty(UserProp.AUTOSHUTDOWN, UserProp.AUTOSHUTDOWN_DISABLED);
        }
        boolean result = true;
        switch (property) {
            case UserProp.AUTOSHUTDOWN_HIBERNATE:
                result = utils.shutDown(OSCommand.HIBERNATE, force);
                break;
            case UserProp.AUTOSHUTDOWN_REBOOT:
                result = utils.shutDown(OSCommand.REBOOT, force);
                break;
            case UserProp.AUTOSHUTDOWN_SHUTDOWN:
                result = utils.shutDown(OSCommand.SHUTDOWN, force);
                break;
            case UserProp.AUTOSHUTDOWN_STANDBY:
                result = utils.shutDown(OSCommand.STANDBY, force);
                break;
            default:
                break;
        }
        if (!result) {
            Swinger.showErrorMessage(getResourceMap(), "shutdownActionFailed");
        } else {
            if (property == UserProp.AUTOSHUTDOWN_SHUTDOWN) {
                getApplication().exit();
            }
        }
    }

    @Override
    protected void failed(Throwable cause) {
        if (!(cause instanceof InterruptedException)) {
            super.failed(cause);
            LogUtils.processException(logger, cause);
        }
    }


    @Override
    protected void cancelled() {
        super.cancelled();
        AppPrefs.storeProperty(UserProp.AUTOSHUTDOWN, UserProp.AUTOSHUTDOWN_DISABLED);
    }

    public void sleep(int seconds) throws InterruptedException {
        logger.info("Going to sleep on " + (seconds) + " seconds");
    }


}
