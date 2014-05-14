package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.tasks.SpeedRegulator;
import cz.vity.freerapid.swing.TextComponentContextMenuListener;
import org.jdesktop.application.ApplicationContext;

import javax.imageio.ImageIO;
import javax.imageio.spi.ServiceRegistry;
import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.Map;
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

    static {
        // Fix for JDK 6 bug ICO vs WBMP
        try {
            final Field theRegistry = ImageIO.class.getDeclaredField("theRegistry");
            theRegistry.setAccessible(true);
            final Field categoryMap = ServiceRegistry.class.getDeclaredField("categoryMap");
            categoryMap.setAccessible(true);
            final Map categoryMapObj = (Map) categoryMap.get(theRegistry.get(null));
            for (final Object value : categoryMapObj.values()) {
                final Field map = value.getClass().getDeclaredField("map");
                map.setAccessible(true);
                final Map mapObj = (Map) map.get(value);
                for (final Object key : mapObj.keySet()) {
                    if (((Class) key).getName().startsWith("com.sun.imageio.plugins.wbmp.")) {
                        mapObj.remove(key);
                        logger.info("Successfully removed " + key);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to remove WBMP SPI, problems may occur when reading ICO files", e);
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
        this.rootContainer.setPreferredSize(new Dimension(700, 550));

        taskServiceManager = new TaskServiceManager(context);
        this.clientManager = new ClientManager(this);

        this.fileHistoryManager = new FileHistoryManager(this, context);


        this.pluginsManager = new PluginsManager(context, this);

        this.searchManager = new SearchManager(context, this);

        this.updateManager = new UpdateManager(this, context);

        this.fileTypeIconProvider = new FileTypeIconProvider(context);

        this.inputDataManager = new DataManager(this, context);

        this.contentManager = new ContentManager(context, this);
        this.contentManager.getContentPanel();

        this.menuManager = new MenuManager(context, this);

        rootContainer.setLayout(new BorderLayout());


        this.clipboardMonitorManager = new ClipboardMonitorManager(context, this);

        this.inputDataManager.initProcessManager();

        linkStoreManager = new LinkStoreManager(this, context);

        this.searchManager.loadSearchData();

        rootContainer.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        rootContainer.add(getContentManager().getComponent(), BorderLayout.CENTER);
        rootContainer.add(getStatusBarManager().getStatusBar(), BorderLayout.SOUTH);

        //male popmenu pro jtextcomponenty
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
}
