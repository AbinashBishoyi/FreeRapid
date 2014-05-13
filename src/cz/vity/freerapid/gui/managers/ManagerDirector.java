package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.swing.TextComponentContextMenuListener;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;

/**
 * Sprava a vytvoreni hlavniho panelu
 *
 * @author Vity
 */
public class ManagerDirector {
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
    private ContentManager dockingManager;
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
        mainFrame = ((MainApp) context.getApplication()).getMainFrame();

        this.rootContainer = new JPanel();
        this.rootContainer.setPreferredSize(new Dimension(700, 550));

        this.clientManager = new ClientManager();

        this.fileHistoryManager = new FileHistoryManager(context);

        this.pluginsManager = new PluginsManager(context);

        this.fileTypeIconProvider = new FileTypeIconProvider(context);

        this.inputDataManager = new DataManager(this, context);

        this.dockingManager = new ContentManager(context, this);
        this.dockingManager.getContentPanel();

        this.menuManager = new MenuManager(context);

        rootContainer.setLayout(new BorderLayout());


        this.inputDataManager.initProcessManager();

        rootContainer.add(getToolbarManager().getComponent(), BorderLayout.NORTH);
        rootContainer.add(getDockingManager().getComponent(), BorderLayout.CENTER);
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

    public ContentManager getDockingManager() {
        return dockingManager;
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
}
