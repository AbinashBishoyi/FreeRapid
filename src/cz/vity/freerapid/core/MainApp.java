package cz.vity.freerapid.core;

import cz.vity.freerapid.core.application.GlobalEDTExceptionHandler;
import cz.vity.freerapid.core.application.ListItemsConvertor;
import cz.vity.freerapid.core.tasks.CheckForNewVersionTask;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.LookAndFeels;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceConverter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.ProxySelector;
import java.util.EventObject;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Hlavni trida aplikace
 *
 * @author Vity
 */
public class MainApp extends SingleXFrameApplication {

    public static final int BUILD_REQUEST = 14;
    public static final int PLUGINS_VERSION = 12;
    static boolean debug = false;
    private ManagerDirector director;
    private TrayIconSupport trayIconSupport = null;
    private AppPrefs appPrefs;
    private boolean minimizeOnStart = false;

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
            getLogger().log(Level.SEVERE, e.getMessage());
        }

        minimizeOnStart = line.isMinimize();

        final String vm = System.getProperty("java.vm.vendor", "");
        if (vm.toLowerCase(Locale.ENGLISH).contains("gcj")) {
            getLogger().log(Level.SEVERE, "Not using Sun Java" + vm);
            exitWithErrorMessage(String.format("You are not using Sun Java, but %s. See readme.txt for minimal requirements to run FreeRapid Downloader.", vm));
        }

        final Map<String, String> map = line.getProperties();
        if (Utils.isWindows() && (new File("C:/Program files/Eset").exists() || new File("D:/Program files/Eset").exists())) {
            if (!map.containsKey(FWProp.ONEINSTANCE)) {
                getLogger().warning("Detected ESET - disabling OneInstance functionality");
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

        System.setProperty("apple.laf.useScreenMenuBar", String.valueOf(AppPrefs.getProperty("apple.laf.useScreenMenuBar", true)));

        System.setProperty("objectdb.conf", new File(Utils.getAppPath(), "objectdb.conf").getAbsolutePath());

        SystemCommanderFactory.getInstance().getSystemCommanderInstance(getContext());//trigger initialization

        if (OneInstanceClient.checkInstance(fileList, appPrefs, getContext())) {
            this.exit();
            return;
        }

        Lng.loadLangProperties();

        this.getContext().getResourceMap();
        ResourceConverter.register(new ListItemsConvertor());

        this.getContext().getTaskMonitor().setAutoUpdateForegroundTask(false);


        LookAndFeels.getInstance().loadLookAndFeelSettings();//inicializace LaFu, musi to byt pred vznikem hlavniho panelu

        super.initialize(args);
    }

    @Override
    protected void ready() {
        director.guiIsReady();
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
            System.exit(1);
            return true;
        }
        return false;
    }

    private Logger getLogger() {
        return Logger.getLogger(MainApp.class.getName());
    }

    private void checkBugs() {
        final String jvm = System.getProperty("java.version");
        getLogger().info("You are running FRD with JVM version = " + System.getProperty("java.home") + "  " +System.getProperty("java.version") + " - (min 1.6.0_07 is required)");
        if ("1.6.0_0".equals(jvm) || "1.6.0-beta".equals(System.getProperty("java.version"))) {
            exitWithErrorMessage("errorInvalidJRE");
        }
    }

    private void exitWithErrorMessage(final String s, final Object... args) {
        getLogger().severe(s);
        Swinger.showErrorMessage(this.getContext().getResourceMap(), s, args);
        System.exit(1);
    }

    @Override
    protected void startup() {
        super.startup();
        director = new ManagerDirector(getContext());
        director.initComponents();
        UIStringsManager.load(this.getContext().getResourceManager());
        initMainFrame();
        this.addExitListener(new MainAppExitListener());
        final JFrame mainFrame = getMainFrame();

        show(mainFrame);
        getTrayIconSupport().setVisibleByDefault();
        setGlobalEDTExceptionHandler();

        if (minimizeOnStart) {
            Swinger.minimize(mainFrame);
        }

        showDelayedRequestPaypalDialog();
    }

    private void showDelayedRequestPaypalDialog() {
        final Thread appThread = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(20000);
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            paypalRequest();
                        }
                    });
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        };
        appThread.setPriority(Thread.MIN_PRIORITY);
        appThread.start();
    }

    private void paypalRequest() {
        if (AppPrefs.getProperty(UserProp.SHOW_PAYPAL_REQUEST, BUILD_REQUEST - 1) != BUILD_REQUEST) {
            AppPrefs.storeProperty(UserProp.SHOW_PAYPAL_REQUEST, BUILD_REQUEST);
            int res = Swinger.getChoiceYesNo(this.getContext().getResourceMap().getString("paypalSupportAction.Action.shortDescription"));
            if (res == Swinger.RESULT_YES) {
                Browser.openBrowser(AppPrefs.getProperty(UserProp.PAYPAL, UserProp.PAYPAL_DEFAULT));
            }
        }
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

        if (AppPrefs.getProperty(FWProp.NEW_VERSION, true))
            startCheckNewVersion();
    }

    private void setGlobalEDTExceptionHandler() {
        final GlobalEDTExceptionHandler eh = new GlobalEDTExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(eh);
        Thread.currentThread().setUncaughtExceptionHandler(eh);
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
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "FreeRapid Downloader");
        Application.launch(MainApp.class, args);
    }

    public static ApplicationContext getAContext() {
        return Application.getInstance(MainApp.class).getContext();
    }

    private void startCheckNewVersion() {
        final Thread appThread = new Thread() {
            @Override
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
        @Override
        public boolean canExit(EventObject event) {
            if (AppPrefs.getProperty(FWProp.MINIMIZE_ON_CLOSE, FWProp.MINIMIZE_ON_CLOSE_DEFAULT) && event instanceof WindowEvent) {
                MainApp.this.getMainFrame().setVisible(false);
                return false;
            }
            return true;
        }

        @Override
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
