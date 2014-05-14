package cz.vity.freerapid.core;

import cz.vity.freerapid.core.application.GlobalEDTExceptionHandler;
import cz.vity.freerapid.core.application.ListItemsConvertor;
import cz.vity.freerapid.core.tasks.CheckForNewVersionTask;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.LookAndFeels;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.net.ProxySelector;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;


/**
 * Hlavni trida aplikace
 *
 * @author Vity
 */
public class MainApp extends SingleXFrameApplication {

    private ManagerDirector director;
    static boolean debug = false;
    private TrayIconSupport trayIconSupport = null;
    private AppPrefs appPrefs;

    private boolean minimizeOnStart = false;

//    private static Logger logger = null;


    @Override
    protected void initialize(String[] args) {
        if (checkInvalidPath()) return;

        final CmdLine line = new CmdLine(this);

        final List<String> fileList = line.processCommandLine(args);
        try {
            final SplashScreen splash = SplashScreen.getSplashScreen();
            if (splash != null && line.isNosplash()) {
                splash.close();
            }
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }


        try {
            LogUtils.initLogging((debug) ? Consts.LOGDEBUG : Consts.LOGDEFAULT);//logovani nejdrive
        } catch (Exception e) {
            java.util.logging.Logger logger = getLogger();
            logger.log(Level.SEVERE, e.getMessage());
        }

        minimizeOnStart = line.isMinimize();

        final String vm = System.getProperty("java.vm.vendor", "");
        if (vm.toLowerCase(Locale.ENGLISH).contains("gcj")) {
            getLogger().log(Level.SEVERE, "Not using Sun Java" + vm);
            exitWithErrorMessage(String.format("You are not using Sun Java, but %s. See readme.txt for minimal requirements to run FreeRapid Downloader.", vm));
        }

        final Map<String, String> map = line.getProperties();
        if (Utils.isWindows() && (new java.io.File("C:/Program files/Eset").exists() || new java.io.File("D:/Program files/Eset").exists())) {
            if (!map.containsKey(FWProp.ONEINSTANCE)) {
                getLogger().info("Detecting ESET - disabling OneInstance functionality");
                map.put(FWProp.ONEINSTANCE, "false");
            }
        }

        try {
            this.appPrefs = new AppPrefs(this.getContext(), map, line.isResetOptions());
        } catch (IllegalStateException e) {
            getLogger().log(Level.SEVERE, e.getMessage());
            exitWithErrorMessage("Fatal Error - not all required libraries are available.\nYou probably didn't extract the zip file properly.\nYou have to have /lib directory with all libraries in the FreeRapid directory.\nExiting.");
        }

        checkBugs();


        System.getProperties().put("arguments", args);
        //if (System.getProperty("mrj.version") != null)
        System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(AppPrefs.getProperty("apple.laf.useScreenMenuBar", true)));

        if (OneInstanceClient.checkInstance(fileList, appPrefs, getContext())) {
            this.exit();
            return;
        }

        this.getContext().getResourceMap();
        ResourceConverter.register(new ListItemsConvertor());

        this.getContext().getTaskMonitor().setAutoUpdateForegroundTask(false);

        Lng.loadLangProperties();

        LookAndFeels.getInstance().loadLookAndFeelSettings();//inicializace LaFu, musi to byt pred vznikem hlavniho panelu
        //Swinger.initLaF(); //inicializace LaFu, musi to byt pred vznikem hlavniho panelu

        super.initialize(args);


    }

    private boolean checkInvalidPath() {
        final String path = Utils.getAppPath();//Utils pouzivaji AppPrefs i logovani
        int index = path.indexOf('+');
        if (index == -1)
            index = path.indexOf("!/");
        if (index == -1)
            index = path.indexOf("!\\");

        if (index > 0 || path.endsWith("!")) {
            final String msg = String.format("This application cannot be started on the path containing '+' or '!' characters ('%s'...)\nPlease move FRD's folder to another place.\nSorry for inconvenience. Exiting.", path.substring(0, index + 1));
            JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
            getLogger().severe(msg);
            System.exit(-1);
            return true;
        }
        return false;
    }

    private java.util.logging.Logger getLogger() {
        return java.util.logging.Logger.getLogger(MainApp.class.getName());
    }

    private void checkBugs() {
        final String jvm = System.getProperty("java.version");
        getLogger().info("You are running FRD with JVM version = " + System.getProperty("java.version") + " - (min 1.6.0_07 is required)");
        if (jvm.equals("1.6.0_0") || System.getProperty("java.version").equals("1.6.0-beta")) {
            exitWithErrorMessage("errorInvalidJRE");
        }
    }

    private void exitWithErrorMessage(final String s, final Object... args) {
        getLogger().severe(s);
        Swinger.showErrorMessage(this.getContext().getResourceMap(), s, args);
        System.exit(-1);
    }


    @Override
    protected void startup() {
        super.startup();
        director = new ManagerDirector(getContext());
        director.initComponents();
        UIStringsManager.load(this.getContext().getResourceManager());
        initMainFrame();
        this.addExitListener(new MainAppExitListener());
        //this.getContext().getLocalStorage().load()
        final JFrame mainFrame = getMainFrame();

        show(mainFrame);
        getTrayIconSupport().setVisibleByDefault();
        setGlobalEDTExceptionHandler();

        if (minimizeOnStart)
            Swinger.minimize(mainFrame);
    }

    private void initMainFrame() {
        ProxySelector.setDefault(null);
        final JFrame frame = getMainFrame();
        frame.setVisible(false);
        if (AppPrefs.getProperty(FWProp.DECORATED_FRAMES, false)) {
            JFrame.setDefaultLookAndFeelDecorated(true);
            frame.setUndecorated(true);
            frame.getRootPane().setWindowDecorationStyle(JRootPane.FRAME);
            LookAndFeels.updateWindowUI(frame);
            JDialog.setDefaultLookAndFeelDecorated(true);
        }

        frame.setJMenuBar(director.getMenuManager().getMenuBar());
        frame.setContentPane(director.getComponent());
        final ComponentOrientation componentOrientation = ComponentOrientation.getOrientation(Locale.getDefault());
        frame.getJMenuBar().applyComponentOrientation(componentOrientation);
        frame.getContentPane().applyComponentOrientation(componentOrientation);
        frame.setMinimumSize(new Dimension(30, 30));
        frame.pack();

        if (AppPrefs.getProperty(FWProp.NEW_VERSION, true) && !debug)
            startCheckNewVersion();
    }


    private void setGlobalEDTExceptionHandler() {
        final GlobalEDTExceptionHandler eh = new GlobalEDTExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(eh);
        Thread.currentThread().setUncaughtExceptionHandler(eh);
    }

//    @Override
//    protected void injectSessionProperties() {
//        super.injectSessionProperties();
//        SessionStorage storage = getContext().getSessionStorage();
//        storage.putProperty(JXStatusBar.class, new StorageProperties.XStatusBarProperty());
//        storage.putProperty(JToolBar.class, new StorageProperties.JToolbarProperty());
//        storage.putProperty(JXMultiSplitPane.class, new StorageProperties.XMultipleSplitPaneProperty());
//        new StorageProperties().registerPersistenceDelegates();
//    }

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
        //apple stuff
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FreeRapid Downloader");
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
            if (AppPrefs.getProperty(FWProp.MINIMIZE_ON_CLOSE, FWProp.MINIMIZE_ON_CLOSE_DEFAULT) && event instanceof WindowEvent) {
                Swinger.minimize(MainApp.this.getMainFrame());
                return false;
            }
            return true;
        }

        public void willExit(EventObject event) {
            appPrefs.store();
        }
    }

    public TrayIconSupport getTrayIconSupport() {
        if (trayIconSupport == null) {
            trayIconSupport = new TrayIconSupport();
        }
        return trayIconSupport;
    }

    public AppPrefs getAppPrefs() {
        return appPrefs;
    }
}
