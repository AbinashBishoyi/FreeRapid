package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.tasks.SpeedRegulator;
import cz.vity.freerapid.swing.TextComponentContextMenuListener;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sprava a vytvoreni hlavniho panelu
 *
 * @author Vity
 */
public class ManagerDirector {
    /**
     * logger instance
     */
    private final static Logger logger = Logger.getLogger(ManagerDirector.class.getName());

    /**
     * context frameworku
     */
    private final ApplicationContext context;
    /**
     * manazer pres menu
     */
    private MenuManager menuManager;
    /**
     * statusbar...
     */
    private StatusBarManager statusBarManager;
    /**
     * spravce toolbaru
     */
    private ToolbarManager toolbarManager;
    /**
     * spravce obrazku
     */
    private ContentManager contentManager;
    /**
     * hlavni container okna
     */
    private Container rootContainer;
    /**
     * odkaz na hlavni okno
     */
    private JFrame mainFrame;
    /**
     * manazer pres data
     */
    private DataManager inputDataManager;

    private FileTypeIconProvider fileTypeIconProvider;

    private ClientManager clientManager;

    private PluginsManager pluginsManager;

    private FileHistoryManager fileHistoryManager;
    private ClipboardMonitorManager clipboardMonitorManager;
    private TaskServiceManager taskServiceManager;
    private UpdateManager updateManager;
    private SpeedRegulator speedRegulator;
    private LinkStoreManager linkStoreManager;

    private SearchManager searchManager;
    private SystemManager systemManager;
    private CountDownLatch countDownLatch = new CountDownLatch(1); //only one purpose barrier simulation
    private DatabaseManager databaseManager;

    static {
        // Fix for JDK 6 bug ICO vs WBMP
        // http://bugs.sun.com/view_bug.do?bug_id=5101862
        try {
            final javax.imageio.spi.IIORegistry registry = javax.imageio.spi.IIORegistry.getDefaultInstance();
            final Object spi = registry.getServiceProviderByClass(Class.forName("com.sun.imageio.plugins.wbmp.WBMPImageReaderSpi"));
            registry.deregisterServiceProvider(spi);
        } catch (final Throwable e) {
            logger.log(Level.WARNING, "Failed to remove WBMP SPI, problems may occur when reading ICO files", e);
        }
    }

    /**
     * Konstruktor
     *
     * @param context context frameworku
     */
    public ManagerDirector(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Inicializace komponent - manazeru
     */
    public void initComponents() {
        logger.info("Starting version " + Consts.APPVERSION);
        mainFrame = ((MainApp) context.getApplication()).getMainFrame();


        this.rootContainer = new JPanel();

        //if we don't name it , sometimes it generates this:
        //mainTable/JViewport0/contentScrollPane/contentPanel/JPanel1/null.layeredPane/JXRootPane0/mainFrame
        // and sometimes this
        //mainTable/JViewport0/contentScrollPane/contentPanel/JPanel2/null.layeredPane/JXRootPane0/mainFrame

        this.rootContainer.setName("JPanel2");
        this.rootContainer.setPreferredSize(new Dimension(700, 550));

        taskServiceManager = new TaskServiceManager(context);
        this.clientManager = new ClientManager(this);


        this.databaseManager = new DatabaseManager(this);

        this.fileHistoryManager = new FileHistoryManager(this, context);


        this.pluginsManager = new PluginsManager(context, this, countDownLatch);

        this.searchManager = new SearchManager(context, this);

        this.updateManager = new UpdateManager(ManagerDirector.this, context);

        this.fileTypeIconProvider = new FileTypeIconProvider(context);

        this.inputDataManager = new DataManager(this, context);

        this.contentManager = new ContentManager(context, this);
        this.contentManager.getContentPanel();

        this.menuManager = new MenuManager(context, this);

        rootContainer.setLayout(new BorderLayout());


        this.clipboardMonitorManager = new ClipboardMonitorManager(context, this);

        this.inputDataManager.initProcessManagerInstance();
        this.systemManager = new SystemManager(ManagerDirector.this, context);


        rootContainer.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        rootContainer.add(getContentManager().getComponent(), BorderLayout.CENTER);
        rootContainer.add(getStatusBarManager().getStatusBar(), BorderLayout.SOUTH);

        //male popmenu pro jtextcomponenty

    }

    /**
     * Init when GUI application is ready, it's called just once
     *
     * @see org.jdesktop.application.Application#ready()
     */
    public void guiIsReady() {
        //initialization of managers
        try {
            countDownLatch.await(); //wait for finishing loading threads
        } catch (InterruptedException e) {
            //ignore
        }
        inputDataManager.initProcessManagerQueue(); //loads file list from file, fills main table
        this.systemManager.initManager();

        linkStoreManager = new LinkStoreManager(ManagerDirector.this, context);
        searchManager.loadSearchData();
        toolbarManager.initManager();
        clipboardMonitorManager.initManager();
        updateManager.initManager();
        Toolkit.getDefaultToolkit().addAWTEventListener(new TextComponentContextMenuListener(), AWTEvent.MOUSE_EVENT_MASK);
    }


    private StatusBarManager getStatusBarManager() {
        if (statusBarManager == null)
            statusBarManager = new StatusBarManager(this, context);
        return statusBarManager;
    }

    public Container getComponent() {
        return rootContainer;
    }


    public ToolbarManager getToolbarManager() {
        if (toolbarManager == null)
            toolbarManager = new ToolbarManager(this, context);
        return toolbarManager;

    }

    public MenuManager getMenuManager() {
        return menuManager;
    }

    public Container getRootContainer() {
        return rootContainer;
    }

    public JFrame getMainFrame() {
        return mainFrame;
    }

    public ContentManager getContentManager() {
        return contentManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public ApplicationContext getContext() {
        return context;
    }

    public DataManager getDataManager() {
        return inputDataManager;
    }

    public FileTypeIconProvider getFileTypeIconProvider() {
        return fileTypeIconProvider;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }

    public PluginsManager getPluginsManager() {
        return pluginsManager;
    }

    public FileHistoryManager getFileHistoryManager() {
        return fileHistoryManager;
    }

    public ClipboardMonitorManager getClipboardMonitorManager() {
        return clipboardMonitorManager;
    }

    public TaskServiceManager getTaskServiceManager() {
        return taskServiceManager;
    }

    public UpdateManager getUpdateManager() {
        return updateManager;
    }

    public SpeedRegulator getSpeedRegulator() {
        if (speedRegulator == null) {
            speedRegulator = new SpeedRegulator();
        }
        return speedRegulator;
    }

    public SearchManager getSearchManager() {
        return searchManager;
    }

    public SystemManager getSystemManager() {
        return systemManager;
    }

}
