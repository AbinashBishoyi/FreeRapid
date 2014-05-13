package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class ClipboardMonitorManager extends Thread implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ClipboardMonitorManager.class.getName());

    private ApplicationContext context;
    private final ManagerDirector managerDirector;
    private boolean threadSuspended;

    private final static String URL_LIST_MIME_TYPE = "application/x-java-url; class=java.net.URL";

    private DataFlavor urlFlavor;


    public ClipboardMonitorManager(ApplicationContext context, ManagerDirector managerDirector) {
        this.context = context;
        this.managerDirector = managerDirector;
        this.setPriority(Thread.MIN_PRIORITY);
//        final MainApp app = (MainApp) context.getApplication();
//        mainFrame = app.getMainFrame();
        synchronized (this) {
            threadSuspended = true;
        }

        updateThreadSleep();

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.CLIPBOARD_MONITORING.equals(evt.getKey()))
                    updateThreadSleep();
            }
        });
    }


    @Override
    public void run() {
        this.setName("ClipboardMonitorManager");

        final Clipboard clipboard = context.getClipboard();

        init();

        Object currentStringData = "";

        boolean urlFlavorAvailable;
        while (!interrupted()) {
            try {
                synchronized (this) {
                    logger.info("Clipboard test for sleeping");
                    while (threadSuspended) {
                        wait();
                    }
                }
                Thread.sleep(750);
            } catch (InterruptedException e) {
                //ignore
            }

//            final Window window = KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow();
//            System.out.println("activeWindow = " + window);
            try {
                final boolean stFlavorAvailable = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
                urlFlavorAvailable = !stFlavorAvailable && clipboard.isDataFlavorAvailable(urlFlavor);
                if (stFlavorAvailable || urlFlavorAvailable) {

                    final Transferable contents = clipboard.getContents(this);

                    try {
                        final Object data = (stFlavorAvailable) ?
                                contents.getTransferData(DataFlavor.stringFlavor) : contents.getTransferData(urlFlavor);
                        if (!currentStringData.equals(data)) {
                            currentStringData = data;
                            if (!isApplicationActive())
                                paste();
                        }
                    } catch (UnsupportedFlavorException e) {
                        //ignore
                    } catch (IOException e) {
                        //ignore
                    }
                }
            } catch (IllegalStateException e) {
                //ignore
            }

        }
        logger.info("ClipboardMonitorManager was interrupted");
    }

    private boolean isApplicationActive() {
        final Frame[] frames = Frame.getFrames();
        boolean active = false;
        for (Frame frame : frames) {
            if (frame.isActive()) {
                active = true;
                break;
            }
        }
        return active;
    }

    private void init() {
        try {
            this.urlFlavor = new DataFlavor(URL_LIST_MIME_TYPE);
        } catch (ClassNotFoundException e) {
            LogUtils.processException(logger, e);
        }
    }

    private boolean isEnabled() {
        return AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT);
    }

    private void updateThreadSleep() {
        final boolean b = isEnabled();
        final boolean oldValue;
        final boolean newValue;
        synchronized (this) {
            oldValue = threadSuspended;
            threadSuspended = !b;
            newValue = threadSuspended;
        }

        if (b) {
            if (oldValue != newValue) {
                if (!this.isAlive())
                    this.start();
                else wakeUp();
            }
        }
    }

    private void paste() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                managerDirector.getContentManager().getContentPanel().paste();
            }
        });
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        logger.info("Lost Clipboard ownership");
    }

    private void wakeUp() {
        synchronized (this) {
            this.notify();
        }
    }

}
