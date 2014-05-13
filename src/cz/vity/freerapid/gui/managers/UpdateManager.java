package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.tasks.CheckPluginUpdateTask;
import cz.vity.freerapid.core.tasks.DownloadNewPluginsTask;
import cz.vity.freerapid.gui.dialogs.UpdateDialog;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.xmlimport.ver1.Plugin;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class UpdateManager {
    private final static Logger logger = Logger.getLogger(UpdateManager.class.getName());

    private final ManagerDirector director;
    private final ApplicationContext context;

    public UpdateManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
    }

    public void checkUpdate() {
        final CheckPluginUpdateTask pluginUpdateTask = new CheckPluginUpdateTask(director, context);
        pluginUpdateTask.addTaskListener(new TaskListener.Adapter<List<Plugin>, Void>() {
            public void succeeded(TaskEvent<List<Plugin>> event) {
                final List<Plugin> result = event.getValue();
                if (result != null && !result.isEmpty()) {

                    showUpdateDialog(result);
                }
            }
        });
        context.getTaskService().execute(pluginUpdateTask);
    }

    private void showUpdateDialog(List<Plugin> result) {
        final UpdateDialog dialog = new UpdateDialog(this.director.getMainFrame(), context, this.director, result);
        final MainApp app = (MainApp) context.getApplication();
        app.prepareDialog(dialog, true);
    }

    public void downloadUpdate(List<Plugin> pluginList) {
        final List<DownloadFile> fileList = new LinkedList<DownloadFile>();
        for (Plugin plugin : pluginList) {
            try {
                fileList.add(getDownloadFileInstance(plugin));
            } catch (MalformedURLException e) {
                LogUtils.processException(logger, e);
            }
        }
        downloadPlugins(fileList);
    }

    public DownloadFile getDownloadFileInstance(final Plugin plugin) throws MalformedURLException {
        final DownloadFile downloadFile = new DownloadFile();
        downloadFile.setFileUrl(new URL(plugin.getUrl()));
        downloadFile.setFileName(plugin.getFilename());
        downloadFile.setState(DownloadState.PAUSED);
        downloadFile.setErrorAttemptsCount(0);
        downloadFile.setFileSize(plugin.getFilesize());
        return downloadFile;
    }

    private void downloadPlugins(List<DownloadFile> fileList) {
        context.getTaskService().execute(new DownloadNewPluginsTask(director, context, fileList));
    }

}
