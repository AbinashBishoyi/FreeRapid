package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.CheckPluginUpdateTask;
import cz.vity.freerapid.core.tasks.DownloadNewPluginsTask;
import cz.vity.freerapid.gui.dialogs.UpdateDialog;
import cz.vity.freerapid.gui.dialogs.WrappedPluginData;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.xmlimport.ver1.Plugin;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Ladislav Vitasek
 */
public class UpdateManager {
    private final static Logger logger = Logger.getLogger(UpdateManager.class.getName());

    private final ManagerDirector director;
    private final ApplicationContext context;
    private Set<String> updatedPluginsCode = new HashSet<String>();

    private Timer timer;

    public UpdateManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        initListeners();
        initUpdateTimer();
    }

    private void initListeners() {
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                final String key = evt.getKey();
                if (UserProp.PLUGIN_UPDATE_CHECK_INTERVAL.equals(key) || UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK.equals(key)) {
                    initUpdateTimer();
                }
            }
        });
    }

    private void initUpdateTimer() {
        final int interval = AppPrefs.getProperty(UserProp.PLUGIN_UPDATE_CHECK_INTERVAL, UserProp.PLUGIN_UPDATE_CHECK_INTERVAL_DEFAULT);

        long lastTimestamp = AppPrefs.getProperty(UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK, 0L);
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(lastTimestamp);
        instance.add(Calendar.HOUR_OF_DAY, interval);

        final Calendar currentDateTime = Calendar.getInstance();

        if (lastTimestamp == 0 || currentDateTime.after(instance)) {
            currentDateTime.add(Calendar.SECOND, 25);
            instance = currentDateTime; //calendar switch off
        } else if (lastTimestamp < 0) {
            currentDateTime.add(Calendar.SECOND, 5 * 60);
            instance = currentDateTime; //calendar switch off
        }

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("UpdateTimer");
        logger.info(String.format("Rescheduling plugins update check to %1$ta %1$tb %1$td %1$tT", instance.getTime()));
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                if (checkForUpdates())
                    checkUpdate(true);
            }
        }, instance.getTime(), interval * 1000 * 3600);
    }

    private boolean checkForUpdates() {
        return AppPrefs.getProperty(UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY, UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY_DEFAULT);
    }

    public void checkUpdate(final boolean quiet) {

        final CheckPluginUpdateTask pluginUpdateTask = new CheckPluginUpdateTask(director, context, quiet);
        pluginUpdateTask.addTaskListener(new TaskListener.Adapter<List<Plugin>, Void>() {
            public void succeeded(TaskEvent<List<Plugin>> event) {
                updateDetected(event.getValue(), quiet);
            }
        });
        context.getTaskService().execute(pluginUpdateTask);
    }

    private void updateDetected(List<Plugin> availablePlugins, boolean quiet) {
        final int method = AppPrefs.getProperty(UserProp.PLUGIN_UPDATE_METHOD, UserProp.PLUGIN_UPDATE_METHOD_DEFAULT);
        final List<WrappedPluginData> datas;
        if (method == UserProp.PLUGIN_UPDATE_METHOD_AUTO)
            datas = generateUpdateData(availablePlugins, false);
        else
            datas = generateUpdateData(availablePlugins, true);
        if (datas.isEmpty()) {
            if (!quiet) {
                Swinger.showInformationDialog(context.getResourceMap().getString("updatesNotFoundMessage"));
            }
            return;
        }

        final int result;
        if (AppPrefs.getProperty(UserProp.CONFIRM_UPDATING_PLUGINS, UserProp.CONFIRM_UPDATING_PLUGINS_DEFAULT)) {
            result = Swinger.showOptionDialog(context.getResourceMap(), JOptionPane.QUESTION_MESSAGE, "informationMessage", "updatesFoundMessage", new String[]{"updateWithDetails", "updateNowButton", "updateCancel"});
        } else result = AppPrefs.getProperty(UserProp.PLUGIN_UPDATE_METHOD, UserProp.PLUGIN_UPDATE_METHOD_DEFAULT);
        switch (result) {
            case UserProp.PLUGIN_UPDATE_METHOD_DIALOG:
                showUpdateDialog(datas);
                break;
            case UserProp.PLUGIN_UPDATE_METHOD_AUTO:
                downloadUpdate(datas);
                break;
            default:
                break;
        }
    }

    private void showUpdateDialog(List<WrappedPluginData> result) {
        final UpdateDialog dialog = new UpdateDialog(this.director.getMainFrame(), this.director);
        dialog.initData(result);
        final MainApp app = (MainApp) context.getApplication();
        app.prepareDialog(dialog, true);
    }

    public void downloadUpdate(List<WrappedPluginData> pluginList) {
        final Task task = getDownloadPluginsTask(pluginList);
        if (task != null)
            executeUpdateTask(task);
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

    public Task getDownloadPluginsTask(final List<WrappedPluginData> wrappedList) {

        final List<DownloadFile> fileList = new LinkedList<DownloadFile>();
        for (WrappedPluginData data : wrappedList) {
            if (data.isSelected() && !updatedPluginsCode.contains(getUniqueId(data.getID(), data.getVersion()))) {
                final DownloadFile httpFile = data.getHttpFile();
                if (httpFile.getState() != DownloadState.COMPLETED)
                    fileList.add(data.getHttpFile());
            }
        }

        if (fileList.isEmpty())
            return null;
        final DownloadNewPluginsTask newPluginsTask = new DownloadNewPluginsTask(director, context, fileList);

        newPluginsTask.addTaskListener(new TaskListener.Adapter<Void, Long>() {

            @Override
            public void succeeded(TaskEvent<Void> event) {
                super.succeeded(event);
            }

            @Override
            public void finished(TaskEvent<Void> event) {
                for (WrappedPluginData data : wrappedList) {
                    if (data.getHttpFile().getState() == DownloadState.COMPLETED) {
                        updatedPluginsCode.add(getUniqueId(data.getID(), data.getVersion()));
                    }
                }
            }
        });

        return newPluginsTask;
    }

    public void executeUpdateTask(Task task) {
        context.getTaskService().execute(task);
    }

    private List<WrappedPluginData> generateUpdateData(List<Plugin> list, boolean selectAll) {
        final PluginsManager pluginsManager = director.getPluginsManager();
        final boolean downloadNotExisting = AppPrefs.getProperty(UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS_DEFAULT);
        List<WrappedPluginData> result = new LinkedList<WrappedPluginData>();
        for (Plugin plugin : list) {
            final String id = plugin.getId();
            if (updatedPluginsCode.contains(getUniqueId(id, plugin.getVersion())))
                continue;
            final boolean isNew = !pluginsManager.hasPlugin(id);
            if (!isNew) {
                final PluginMetaData data = pluginsManager.getPluginMetadata(id);
                if (!data.isUpdatesEnabled())
                    continue;
            }
            final DownloadFile httpFile;
            final boolean checked = downloadNotExisting || !isNew;
            if (!checked && !selectAll)
                continue;
            try {
                httpFile = getDownloadFileInstance(plugin);
                final WrappedPluginData pluginData = new WrappedPluginData(checked, httpFile, plugin);
                pluginData.setNew(isNew);
                result.add(pluginData);
            } catch (MalformedURLException e) {
                //ignore this malformed file
                LogUtils.processException(logger, e);
            }
        }
        return result;
    }

    private String getUniqueId(String id, String version) {
        return id + '@' + version;
    }

}
