package cz.vity.freerapid.gui.actions;


import com.jgoodies.common.collect.ArrayListModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.ExportLinksTask;
import cz.vity.freerapid.core.tasks.ImportLinksTask;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.gui.dialogs.NewLinksDialog;
import cz.vity.freerapid.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.gui.managers.TaskServiceManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.container.ContainerPlugin;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.os.OSCommand;
import cz.vity.freerapid.utilities.os.SystemCommander;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.*;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.logging.Logger;

/**
 * FileActions = Menu soubory
 *
 * @author Vity
 */
@ProxyActions({"select-all", "copy", "cut", "paste", "delete"})
public class FileActions extends AbstractBean {
    private final static Logger logger = Logger.getLogger(FileActions.class.getName());
    private final static String ID_CONTAINER = "container";

    private final MainApp app;
    private final PluginsManager pluginsManager;
    private final DataManager dataManager;
    private final TaskServiceManager taskServiceManager;
    private final ContentPanel contentPanel;

    private NewLinksDialog dialog;
    private long restart;
    private boolean restartHookInstalled;

    public FileActions(ApplicationContext context) {
        app = (MainApp) context.getApplication();
        pluginsManager = app.getManagerDirector().getPluginsManager();
        dataManager = app.getManagerDirector().getDataManager();
        taskServiceManager = app.getManagerDirector().getTaskServiceManager();
        contentPanel = app.getManagerDirector().getContentManager().getContentPanel();
        restartHookInstalled = false;
    }

    @SuppressWarnings({"unchecked"})
    @Action
    public void addNewLinksAction(ActionEvent event) {
        final ManagerDirector managerDirector = app.getManagerDirector();
        List<URL> urlList = null;
        final boolean showing = dialog != null;
        if (event.getSource() instanceof List) {
            urlList = (List<URL>) event.getSource();
            if (urlList.isEmpty())
                return;

            if (!showing) {
                synchronized (dataManager.getLock()) {//overeni, jestli uz tam ty pastovane nejsou na seznamu
                    final ArrayListModel<DownloadFile> files = dataManager.getDownloadFiles();
                    int counterFound = 0;
                    for (DownloadFile file : files) {
                        final URL urlAddress = file.getFileUrl();
                        if (urlList.contains(urlAddress))
                            ++counterFound;
                    }
                    if (counterFound == urlList.size())
                        return;
                }
            }
        }

        if (!showing)
            dialog = new NewLinksDialog(managerDirector, app.getMainFrame());

        if (urlList != null) {

            final boolean activate = AppPrefs.getProperty(UserProp.BRING_TO_FRONT_WHEN_PASTED, UserProp.BRING_TO_FRONT_WHEN_PASTED_DEFAULT);
            if (activate) {
                Swinger.bringToFront(app.getMainFrame(), activate);
            } else {
                if (!showing)
                    Swinger.bringToFront(app.getMainFrame(), activate);
                else Toolkit.getDefaultToolkit().beep();
            }
            final List<URL> urlList1 = urlList;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null) {
                        dialog.setURLs(urlList1);
                    } else {
                        logger.warning("Dialog is null");
                    }
                }
            });
        }
        if (!showing) {
            app.prepareDialog(dialog, true);
            if (dialog.getModalResult() == NewLinksDialog.RESULT_OK) {
                final List<DownloadFile> files = dialog.getDownloadFiles();
                if (!files.isEmpty()) {
                    dataManager.addToList(files);
                    final boolean notPaused = !dialog.isStartPaused();
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            if (notPaused)
                                dataManager.addToQueue(files);
                            managerDirector.getContentManager().getContentPanel().selectAdded(files);
                        }
                    });
                }
            }
            dialog = null;
        }
    }

    @Action
    public void restartApplication() {
        installShutdownHook();
        this.restart = System.currentTimeMillis();
        app.exit();
    }

    private void installShutdownHook() {
        if (restartHookInstalled)
            return;
        final SystemCommander commander = SystemCommanderFactory.getInstance().getSystemCommanderInstance(app.getContext());
        if (!commander.isSupported(OSCommand.RESTART_APPLICATION)) {
            Swinger.showErrorMessage(app.getContext().getResourceMap(), "systemCommandNotSupported", OSCommand.RESTART_APPLICATION.toString().toLowerCase());
            return;
        }

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - restart < 4000) {
                    commander.shutDown(OSCommand.RESTART_APPLICATION, false);
                }
            }
        });
        Runtime.getRuntime().addShutdownHook(thread);
        restartHookInstalled = true;
    }

    @Action
    public void importLinksAction(ActionEvent e) {
        importLinks();
    }

    @Action
    public void exportSelectedLinksAction(ActionEvent e) {
        exportLinks(dataManager.getSelectionToList(contentPanel.getSelectedRows()));
    }

    @Action
    public void exportAllLinksAction(ActionEvent e) {
        exportLinks(dataManager.getActualDownloadFiles());
    }

    public void importLinks() {
        final ContainerPlugin plugin = getContainerPlugin();
        if (plugin != null) {
            final File[] files = OpenSaveDialogFactory.getInstance(app.getContext()).getImportLinks(plugin.getSupportedFiles());
            if (files != null && files.length > 0) {
                final Task task = new ImportLinksTask(app, plugin, files);
                taskServiceManager.runTask(TaskServiceManager.WORK_WITH_FILE_SERVICE, task);
            }
        }
    }

    public void exportLinks(final List<DownloadFile> list) {
        if (!list.isEmpty()) {
            final ContainerPlugin plugin = getContainerPlugin();
            if (plugin != null) {
                final File destination = OpenSaveDialogFactory.getInstance(app.getContext()).getExportLinks(plugin.getSupportedFiles());
                if (destination != null) {
                    final Task task = new ExportLinksTask(app, plugin, list, destination);
                    taskServiceManager.runTask(TaskServiceManager.WORK_WITH_FILE_SERVICE, task);
                }
            }
        }
    }

    private ContainerPlugin getContainerPlugin() {
        if (!pluginsManager.isPluginDisabled(ID_CONTAINER)) {
            try {
                return (ContainerPlugin) pluginsManager.getPluginManager().getPlugin(ID_CONTAINER);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            }
        }
        return null;
    }

}
