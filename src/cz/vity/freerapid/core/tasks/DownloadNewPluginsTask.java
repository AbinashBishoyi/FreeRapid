package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.exceptions.NoAvailableConnection;
import cz.vity.freerapid.core.tasks.exceptions.UpdateFailedException;
import cz.vity.freerapid.gui.dialogs.WrappedPluginData;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.methods.GetMethod;
import org.java.plugin.JpfException;
import org.jdesktop.application.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class DownloadNewPluginsTask extends DownloadTask {
    private final static Logger logger = Logger.getLogger(DownloadNewPluginsTask.class.getName());

    private final ManagerDirector director;
    private final List<WrappedPluginData> fileList;
    private final boolean beQuiet;
    private static boolean restartIsRequiredToUpdateSomePlugins = false;
    private List<File> newPluginsFiles = new ArrayList<File>();
    private Collection<WrappedPluginData> updatedPlugins = new LinkedList<WrappedPluginData>();

    public DownloadNewPluginsTask(ManagerDirector director, ApplicationContext context, List<WrappedPluginData> fileList, boolean beQuiet) {
        super(context.getApplication());
        this.director = director;
        this.fileList = fileList;
        this.beQuiet = beQuiet;
        ScreenInputBlocker blocker = new ScreenInputBlocker(this, BlockingScope.APPLICATION, Swinger.getActiveFrame(), null);
        this.setInputBlocker(blocker);
        setUseRelativeStoreFileIfPossible(false);
    }

    @Override
    protected Void doInBackground() throws Exception {
        message("downloadNewPluginsTask");
        final ClientManager clientManager = director.getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getEnabledConnections();
        if (connectionSettingses.isEmpty())
            throw new NoAvailableConnection(getResourceMap().getString("noAvailableConnection"));
        client = new DownloadClient();
        client.initClient(connectionSettingses.get(0));
        initDownloadThread();

        final File dir = director.getPluginsManager().getPluginsDir();
        if (!dir.canWrite()) {
            throw new IOException(getResourceMap().getString("pluginsDirectoryIsNotWriteable"));
        }
        boolean success = false;
        for (WrappedPluginData data : fileList) {
            if (isCancelled())
                break;
            final DownloadFile file = data.getHttpFile();
            try {
                setDownloadFile(file);
                downloadFile.setSaveToDirectory(dir);
                processFile(file);
                if (data.isNew()) {
                    newPluginsFiles.add(data.getHttpFile().getOutputFile());
                } else {
                    updatedPlugins.add(data);
                }
                success = true;
            } catch (Exception e) {
                file.setState(DownloadState.ERROR);
                setFileErrorMessage(e);
                LogUtils.processException(logger, e);
            }
        }
        message("DownloadNewPluginsTask.reloadingPlugins");
        updatePlugins();
        if (!success)
            throw new UpdateFailedException("UpdateFailed");
        return null;
    }

    private void processFile(final DownloadFile file) throws Exception {
        final GetMethod getMethod = client.getGetMethod(file.getFileUrl().toExternalForm());
        final InputStream inputStream = client.makeRequestForFile(getMethod);
        if (isCancelled())
            return;
        if (inputStream != null) {
            saveToFile(inputStream);
            checkRewrite(file);
            if (isCancelled())
                return;
            file.setState(DownloadState.COMPLETED);
        } else {
            throw new IOException("FileWasNotFoundOnServer");
        }
    }


    private void updatePlugins() throws JpfException {
        final PluginsManager pluginsManager = director.getPluginsManager();
        pluginsManager.reregisterAll();
    }

    @Override
    protected void cancelled() {
        super.cancelled();
        for (WrappedPluginData data : fileList) {
            final DownloadFile file = data.getHttpFile();
            if (file.getState() != DownloadState.COMPLETED) {
                file.setState(DownloadState.CANCELLED);
            }
        }
        try {
            updatePlugins();
        } catch (JpfException e) {
            LogUtils.processException(logger, e);
        }
    }

    private void checkRewrite(DownloadFile downloadFile) throws IOException {
        final File out = downloadFile.getOutputFile();
        if (out.exists()) {
            out.delete();
        }
        final File storeFile = downloadFile.getStoreFile();
        if (storeFile != null) {
            final boolean b = storeFile.renameTo(out);
            if (!b) {
                throw new IOException("Renaming target file failed " + downloadFile.getStoreFile() + " ->" + out);
            }
        }
    }

    @Override
    protected int checkExists() throws InvocationTargetException, InterruptedException {
        return -2;
    }

    @Override
    protected boolean useTemporaryFiles() {
        return true;
    }

    @Override
    protected void failed(Throwable cause) {
        LogUtils.processException(logger, cause);
        if (handleRuntimeException(cause))
            return;
        error(cause);
        if (cause instanceof UnknownHostException) {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(this.getResourceMap(), cause);
        }

    }

    @Override
    protected void succeeded(Void result) {
        // blocker.unblock();
        final int updateMethod = AppPrefs.getProperty(UserProp.PLUGIN_UPDATE_METHOD, UserProp.PLUGIN_UPDATE_METHOD_DEFAULT);
        if (restartIsRequiredToUpdateSomePlugins) {
            boolean restart = beQuiet && updateMethod == UserProp.PLUGIN_UPDATE_METHOD_AUTO_RESTART && director.getDataManager().checkAllComplete();
            if (!restart) {
                restart = Swinger.getChoiceYesNo(getResourceMap().getString("installed")) == Swinger.RESULT_YES;
            }
            if (restart) {
                director.getMenuManager().getFileActions().restartApplication();
            }
        } else {
            if (!beQuiet) {
                if (!updatedPlugins.isEmpty() || !newPluginsFiles.isEmpty()) {
                    Swinger.showInformationDialog(getResourceMap().getString("installedSuccessFully"));
                }
            }
        }
    }
}
