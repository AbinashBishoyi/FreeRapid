package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.QuietMode;
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
import org.java.plugin.registry.Version;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private Timer timer;
    private AtomicBoolean updating = new AtomicBoolean(false);


    public UpdateManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
    }

    void initManager() {
        if (AppPrefs.getProperty(UserProp.BLIND_MODE, UserProp.BLIND_MODE_DEFAULT)) {
            AppPrefs.storeProperty(UserProp.PLUGIN_UPDATE_METHOD, UserProp.PLUGIN_UPDATE_METHOD_AUTO);
        }
        initListeners();
        initUpdateTimer();
    }

    private void initListeners() {
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                final String key = evt.getKey();
                if (UserProp.PLUGIN_UPDATE_CHECK_INTERVAL.equals(key) || UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK.equals(key)) {
                    initUpdateTimer();
                }
            }
        });
    }

    private void initUpdateTimer() {
        final int interval = Math.max(AppPrefs.getProperty(UserProp.PLUGIN_UPDATE_CHECK_INTERVAL, UserProp.PLUGIN_UPDATE_CHECK_INTERVAL_DEFAULT), 4);
        long lastTimestamp = AppPrefs.getProperty(UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK, 0L);

        final Calendar currentDateTime = Calendar.getInstance();
        Calendar scheduleTime;
        if (lastTimestamp < 0) {//error connection
            currentDateTime.add(Calendar.HOUR_OF_DAY, 1);
            scheduleTime = currentDateTime;
        } else {
            scheduleTime = Calendar.getInstance();
            scheduleTime.setTimeInMillis(lastTimestamp);
            scheduleTime.add(Calendar.HOUR_OF_DAY, interval);
            if (lastTimestamp == 0 || currentDateTime.after(scheduleTime)) {
                currentDateTime.add(Calendar.SECOND, 17);
                scheduleTime = currentDateTime; //calendar switch off
            }
        }

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer("UpdateTimer");
        logger.info(String.format("Rescheduling plugins update check to %1$ta %1$tb %1$td %1$tT", scheduleTime.getTime()));
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (checkForUpdates())
                    checkUpdate(true);
            }
        }, scheduleTime.getTime(), interval * 1000 * 3600L);
    }

    private boolean checkForUpdates() {
        return AppPrefs.getProperty(UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY, UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY_DEFAULT);
    }

    public void checkUpdate(final boolean quiet) {
        final CheckPluginUpdateTask pluginUpdateTask = new CheckPluginUpdateTask(director, context, quiet);
        pluginUpdateTask.addTaskListener(new TaskListener.Adapter<List<Plugin>, Void>() {
            @Override
            public void succeeded(final TaskEvent<List<Plugin>> event) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (!updating.getAndSet(true)) {
                            updateDetected(event.getValue(), quiet);
                            updating.set(false);
                        } else {
                            logger.info("I don't know when this happens, but it happens");
                        }
                    }
                });
            }
        });
        context.getTaskService().execute(pluginUpdateTask);
    }

    private void updateDetected(List<Plugin> availablePlugins, boolean quiet) {
        int method;
        if (!quiet) {
            //called from menu
            method = UserProp.PLUGIN_UPDATE_METHOD_DIALOG;
        } else
            method = AppPrefs.getProperty(UserProp.PLUGIN_UPDATE_METHOD, UserProp.PLUGIN_UPDATE_METHOD_DEFAULT);
        final List<WrappedPluginData> datas;
        if (method == UserProp.PLUGIN_UPDATE_METHOD_AUTO || method == UserProp.PLUGIN_UPDATE_METHOD_QUIET)
            datas = generateUpdateData(availablePlugins, false);
        else
            datas = generateUpdateData(availablePlugins, true);
        if (datas.isEmpty()) {
            if (!quiet) {
                Swinger.showInformationDialog(context.getResourceMap().getString("updatesNotFoundMessage"));
            }
            return;
        }

        if (method == UserProp.PLUGIN_UPDATE_ASK_FOR_METHOD) {
            final boolean bringToFront = !QuietMode.getInstance().isActive() || !QuietMode.getInstance().isDialogsDisabled();
            if (!bringToFront) {
                QuietMode.getInstance().playUserInteractionRequiredSound();
            }
            final int res = Swinger.showOptionDialog(context.getResourceMap(), bringToFront, JOptionPane.QUESTION_MESSAGE, "informationMessage", "updatesFoundMessage", new String[]{"updateWithDetails", "updateNowButton", "updateCancel"});
            if (res == 0)
                method = UserProp.PLUGIN_UPDATE_METHOD_DIALOG;
            else if (res == 1)
                method = UserProp.PLUGIN_UPDATE_METHOD_AUTO;
            else method = -1;
        }
        switch (method) {
            case UserProp.PLUGIN_UPDATE_METHOD_DIALOG:
                showUpdateDialog(datas, false);
                break;
            case UserProp.PLUGIN_UPDATE_METHOD_AUTO:
                showUpdateDialog(datas, true);
                break;
            case UserProp.PLUGIN_UPDATE_METHOD_QUIET:
                downloadUpdate(datas, quiet);
                break;
            default:
                break;
        }
    }

    private void showUpdateDialog(List<WrappedPluginData> result, boolean startAutomatically) {
        final UpdateDialog dialog = new UpdateDialog(this.director.getMainFrame(), this.director);
        dialog.initData(result);
        final MainApp app = (MainApp) context.getApplication();
        if (startAutomatically) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    dialog.okBtnAction();
                }
            });
        }
        app.prepareDialog(dialog, true);
    }

    private void downloadUpdate(List<WrappedPluginData> pluginList, boolean quiet) {
        final Task task = getDownloadPluginsTask(pluginList, quiet);
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

    public Task getDownloadPluginsTask(final List<WrappedPluginData> wrappedList, boolean quiet) {
        final List<WrappedPluginData> fileList = new LinkedList<WrappedPluginData>();
        for (WrappedPluginData data : wrappedList) {
            if (data.isSelected()) {
                final DownloadFile httpFile = data.getHttpFile();
                if (httpFile.getState() != DownloadState.COMPLETED)
                    fileList.add(data);
            }
        }
        if (fileList.isEmpty())
            return null;
        return new DownloadNewPluginsTask(director, context, fileList, quiet);
    }

    public void executeUpdateTask(Task task) {
        context.getTaskService().execute(task);
    }

    private List<WrappedPluginData> generateUpdateData(List<Plugin> list, boolean selectAll) {
        final PluginsManager pluginsManager = director.getPluginsManager();
        final boolean downloadNotExisting = AppPrefs.getProperty(UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS_DEFAULT);
        List<WrappedPluginData> result = new LinkedList<WrappedPluginData>();
        Set<String> supportedPluginsIdByServer = new HashSet<String>(list.size());
        for (Plugin plugin : list) {
            final String id = plugin.getId();
            supportedPluginsIdByServer.add(id);
            final Version newVersion = Version.parse(plugin.getVersion());
            plugin.setVersion(newVersion.toString());

            final boolean isNew = !pluginsManager.hasPlugin(id);
            if (!isNew) {
                final Version oldVersion = Version.parse(pluginsManager.getPluginMetadata(id).getVersion());
                logger.info("id = " + id + "  oldVersion = " + oldVersion + "  newVersion = " + newVersion);
                //is newer or newer was replaced with an older version again == versions are being ignored
                if (newVersion.equals(oldVersion))
                    continue;
                final PluginMetaData data = pluginsManager.getPluginMetadata(id);
                logger.info("found new plugin with id =" + id);
                if (!data.isUpdatesEnabled()) {
                    logger.info("It's disabled to download new plugins, ignoring " + id);
                    continue;
                }
            }

            final DownloadFile httpFile;
            final boolean checked = downloadNotExisting || !isNew;
            if (!checked && !selectAll)
                continue;
            try {
                httpFile = getDownloadFileInstance(plugin);
                final WrappedPluginData pluginData = new WrappedPluginData(checked, httpFile, plugin);
                pluginData.setNew(isNew);
                if (!isNew) {
                    pluginData.setPluginInUse(pluginsManager.isPluginInUseForUpdates(id));
                }
                result.add(pluginData);
            } catch (MalformedURLException e) {
                //ignore this malformed file
                LogUtils.processException(logger, e);
            }
        }

        final boolean removeNotSupportedPLugins = AppPrefs.getProperty(UserProp.REMOVE_NOT_SUPPORTED_PLUGINS, UserProp.REMOVE_NOT_SUPPORTED_PLUGINS_DEFAULT);
        if (removeNotSupportedPLugins) {
            final List<PluginMetaData> dataList = pluginsManager.getSupportedPlugins();
            for (PluginMetaData data : dataList) {
                //plugin will be deleted
                if (!supportedPluginsIdByServer.contains(data.getId())) {
                    final Plugin plugin = new Plugin();
                    plugin.setId(data.getId());
                    plugin.setVersion(data.getVersion());
                    plugin.setVendor(data.getVendor());
                    plugin.setServices(data.getServices());
                    plugin.setUrl(data.getWWW());
                    plugin.setFilename("xxxx.frp");
                    plugin.setFilesize(0);
                    plugin.setPremium(String.valueOf(data.isPremium()));
                    final DownloadFile httpFile = new DownloadFile();
                    httpFile.setState(DownloadState.QUEUED);
                    final WrappedPluginData pluginData = new WrappedPluginData(true, httpFile, plugin);
                    pluginData.setToBeDeleted(true);
                    result.add(pluginData);
                }
            }
        }
        return result;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    private static String getUniqueId(String id, String version) {
        return id + '@' + version;
    }

}
