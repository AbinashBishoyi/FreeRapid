package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.application.GlobalEDTExceptionHandler;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Clipboard Monitoring je aktivni pouze, pokud je aplikace aktivni.
 * Pri precvakavani oken dochazi k tomu, ze activeWindow je na jednu chvili null,
 * proto se to kontroluje jeste v cyklu.
 *
 * @author Vity
 */
public class ClipboardMonitorManager extends Thread implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ClipboardMonitorManager.class.getName());

    private ApplicationContext context;
    private final ManagerDirector managerDirector;
    private boolean threadSuspended;

    private final static String URL_LIST_MIME_TYPE = "application/x-java-url; class=java.net.URL";

    private DataFlavor urlFlavor;
    private volatile Object currentClipboardData;

    public ClipboardMonitorManager(ApplicationContext context, ManagerDirector managerDirector) {
        this.context = context;
        this.managerDirector = managerDirector;
        this.setPriority(Thread.MIN_PRIORITY);

        synchronized (this) {
            threadSuspended = true;
        }
    }


    @Override
    public void run() {
        this.setName("ClipboardMonitorManager");

        this.setUncaughtExceptionHandler(new GlobalEDTExceptionHandler());

        final Clipboard clipboard = context.getClipboard();

        boolean urlFlavorAvailable;
        while (!interrupted()) {
            try {
                synchronized (this) {
                    logger.fine("Clipboard test for sleeping");
                    while (threadSuspended) {
                        wait();
                    }
                }
                Thread.sleep(700);
            } catch (InterruptedException e) {
                //ignore
            }

            try {

                if (clipboard.getAvailableDataFlavors().length < 10) { //dirty hack
                    continue;
                }

                final boolean stFlavorAvailable = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);

                urlFlavorAvailable = !stFlavorAvailable && clipboard.isDataFlavorAvailable(urlFlavor);
                if (stFlavorAvailable || urlFlavorAvailable) {

                    try {
                        final Transferable contents = clipboard.getContents(this);

                        final Object data = (stFlavorAvailable) ?
                                contents.getTransferData(DataFlavor.stringFlavor) : contents.getTransferData(urlFlavor);
                        if (!currentClipboardData.equals(data)) {
                            currentClipboardData = data;

                            paste();

                        }
                    } catch (Exception e) {
                        //ignore
                    }
                }
            } catch (IllegalStateException e) {
                //ignore
                //  usually java.lang.IllegalStateException: cannot open system clipboard 
                // LogUtils.processException(logger, e);
            } catch (ArrayIndexOutOfBoundsException e) {
                LogUtils.processException(logger, e);
            } catch (NullPointerException e) {
                LogUtils.processException(logger, e);
            } catch (AbstractMethodError e) {
                LogUtils.processException(logger, e);
            }


        }
        logger.info("ClipboardMonitorManager was interrupted");
    }

//    private static boolean isApplicationActive() {
//        return KeyboardFocusManager.getCurrentKeyboardFocusManager().getActiveWindow() != null;
//    }

    /**
     * Init of this manager. Should be called just once.
     */
    void initManager() {
        currentClipboardData = "";
        try {
            this.urlFlavor = new DataFlavor(URL_LIST_MIME_TYPE);
        } catch (ClassNotFoundException e) {
            LogUtils.processException(logger, e);
        }

        updateThreadSleep();

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.CLIPBOARD_MONITORING.equals(evt.getKey()))
                    updateThreadSleep();
            }
        });

        KeyboardFocusManager.getCurrentKeyboardFocusManager().addPropertyChangeListener("activeWindow", new PropertyChangeListener() {

            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getOldValue() != null && evt.getNewValue() == null) {
                    copyClipboard(); //aby zkopirovane uvnitr aplikace se ihned neobjevilo jako zkopirovane externe
                }
                updateThreadSleep();
            }
        });


    }

    private boolean isEnabled() {
        //return AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT) && !isApplicationActive();
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

    private void copyClipboard() {

        final Clipboard clipboard = context.getClipboard();
        try {
            final boolean stFlavorAvailable = clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor);
            final boolean urlFlavorAvailable = !stFlavorAvailable && clipboard.isDataFlavorAvailable(urlFlavor);
            if (stFlavorAvailable || urlFlavorAvailable) {

                final Transferable contents = clipboard.getContents(this);

                try {
                    currentClipboardData = (stFlavorAvailable) ?
                            contents.getTransferData(DataFlavor.stringFlavor) : contents.getTransferData(urlFlavor);
                } catch (UnsupportedFlavorException e) {
                    //ignore
                } catch (IOException e) {
                    //ignore
                } catch (ArrayIndexOutOfBoundsException e) {
                    LogUtils.processException(logger, e);
                }
            }
        } catch (IllegalStateException e) {
            //ignore
        } catch (AbstractMethodError e) {
            LogUtils.processException(logger, e);
        }

    }

}
