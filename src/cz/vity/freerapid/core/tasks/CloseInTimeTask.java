package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Application;

import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class CloseInTimeTask extends CoreTask<Void, Void> {
    private final static Logger logger = Logger.getLogger(CloseInTimeTask.class.getName());


    public CloseInTimeTask(Application application) {
        super(application);
        MainApp app = (MainApp) application;
        this.setUserCanCancel(true);
        this.setInputBlocker(new ScreenltInputBlocker(this, BlockingScope.APPLICATION, app.getMainFrame(), null));
        Swinger.bringToFront(app.getMainFrame());
    }

    protected Void doInBackground() throws Exception {
        final int seconds = AppPrefs.getProperty(UserProp.CLOSE_APPLICATION_CONFIRM_WAITTIME, 30);
        for (int i = seconds; i > 0; i--) {
            if (isCancelled())
                break;
            message((i > 1) ? "closeAppMessageN" : "closeAppMessage1", i);
            Thread.sleep(1000);
        }

        return null;
    }

    @Override
    protected void succeeded(Void result) {
        getApplication().exit();
    }

    @Override
    protected void failed(Throwable cause) {
        if (!(cause instanceof InterruptedException)) {
            super.failed(cause);
            LogUtils.processException(logger, cause);
        }
    }

    public void sleep(int seconds) throws InterruptedException {
        logger.info("Going to sleep on " + (seconds) + " seconds");
    }

}
