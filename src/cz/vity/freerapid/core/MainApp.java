package cz.vity.freerapid.core;

import cz.vity.freerapid.core.application.GlobalEDTExceptionHandler;
import cz.vity.freerapid.core.application.ListItemsConvertor;
import cz.vity.freerapid.core.tasks.CheckForNewVersionTask;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.LookAndFeels;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceConverter;

import javax.swing.*;
import java.util.EventObject;


/**
 * Hlavni trida aplikace
 *
 * @author Vity
 */
public class MainApp extends SingleXFrameApplication {

    private ManagerDirector director;
    static boolean debug = false;
    private TrayIconSupport trayIconSupport = null;

//    private static Logger logger = null;


    @Override
    protected void initialize(String[] args) {
        new CmdLine(this).processCommandLine(args);

        LogUtils.initLogging(debug);//logovani nejdrive    

        LookAndFeels.getInstance().loadLookAndFeelSettings();//inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        //Swinger.initLaF(); //inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        super.initialize(args);

        ResourceConverter.register(new ListItemsConvertor());
        this.getContext().getTaskMonitor().setAutoUpdateForegroundTask(false);
    }

    @Override
    protected void startup() {
        director = new ManagerDirector(getContext());
        director.initComponents();
        initMainFrame();
        this.addExitListener(new MainAppExitListener());
        //this.getContext().getLocalStorage().load()
        show(getMainFrame());
        getTrayIconSupport().setVisibleByDefault();
        setGlobalEDTExceptionHandler();
    }

    private void initMainFrame() {
        final JFrame frame = getMainFrame();
        frame.setJMenuBar(director.getMenuManager().getMenuBar());
        frame.setContentPane(director.getComponent());
        frame.pack();

        if (AppPrefs.getProperty(FWProp.NEW_VERSION, true) && !debug)
            startCheckNewVersion();
    }


    private void setGlobalEDTExceptionHandler() {
        final GlobalEDTExceptionHandler eh = new GlobalEDTExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(eh);
        Thread.currentThread().setUncaughtExceptionHandler(eh);
    }

    @Override
    protected void injectSessionProperties() {
        super.injectSessionProperties();
//        SessionStorage storage = getContext().getSessionStorage();
//        storage.putProperty(JXStatusBar.class, new StorageProperties.XStatusBarProperty());
//        storage.putProperty(JToolBar.class, new StorageProperties.JToolbarProperty());
//        storage.putProperty(JXMultiSplitPane.class, new StorageProperties.XMultipleSplitPaneProperty());
//        new StorageProperties().registerPersistenceDelegates();
    }

    /**
     * Vraci komponentu hlavniho panelu obsahujici dalsi komponenty
     *
     * @return hlavni panel
     */
    public ManagerDirector getManagerDirector() {
        assert director != null; //calling getMainPanel before finished initialization
        return director;
    }

    /**
     * Hlavni spousteci metoda programu
     *
     * @param args vstupni parametry pro program
     */
    public static void main(String[] args) {
        //zde prijde overovani vstupnich pridavnych parametru
        Application.launch(MainApp.class, args); //spusteni
    }

    public static ApplicationContext getAContext() {
        return Application.getInstance(MainApp.class).getContext();
    }


    private void startCheckNewVersion() {

        final Thread appThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(30000);
                    MainApp.this.getContext().getTaskService().execute(new CheckForNewVersionTask(false));
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        };
        appThread.setPriority(Thread.MIN_PRIORITY);
        appThread.start();
    }


    /**
     * Exit listener. Pri ukoncovani provede ulozeni uzivatelskych properties.
     */
    private class MainAppExitListener implements Application.ExitListener {

        public boolean canExit(EventObject event) {
            return true;
        }

        public void willExit(EventObject event) {
            AppPrefs.store();
        }
    }

    public TrayIconSupport getTrayIconSupport() {
        if (trayIconSupport == null) {
            trayIconSupport = new TrayIconSupport();
        }
        return trayIconSupport;
    }


}
