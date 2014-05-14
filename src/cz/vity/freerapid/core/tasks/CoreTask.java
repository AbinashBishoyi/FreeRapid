package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.application.GlobalEDTExceptionHandler;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.Task;

import javax.swing.*;

/**
 * @author Vity
 */
public abstract class CoreTask<T, V> extends Task<T, V> {

    public CoreTask(Application application) {
        super(application);
        //setDefaultInputBlocker();
    }

    public ResourceMap getTaskResourceMap() {
        return super.getResourceMap();
    }

//    public void postMessage(String s, Object args) {
//        message(s, args);
//    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
        handleRuntimeException(cause);
    }

    protected JFrame getMainFrame() {
        return ((SingleFrameApplication) getApplication()).getMainFrame();
    }

    /**
     * Metoda overi, zda se jedna o Runtime
     *
     * @param cause
     * @return
     */
    protected boolean handleRuntimeException(Throwable cause) {
        if (cause instanceof RuntimeException) {
            GlobalEDTExceptionHandler exceptionHandler = new GlobalEDTExceptionHandler();
            exceptionHandler.uncaughtException(Thread.currentThread(), cause);
            return true;
        }
        return false;
    }


    protected void setTaskToForeground() {
        getApplication().getContext().getTaskMonitor().setForegroundTask(this);
    }

}
