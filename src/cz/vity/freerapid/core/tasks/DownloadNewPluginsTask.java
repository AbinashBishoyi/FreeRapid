package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.tasks.exceptions.NoAvailableConnection;
import cz.vity.freerapid.core.tasks.exceptions.UpdateFailedException;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdesktop.application.ApplicationContext;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class DownloadNewPluginsTask extends DownloadTask {
    private final static Logger logger = Logger.getLogger(DownloadNewPluginsTask.class.getName());

    private final ManagerDirector director;
    private final List<DownloadFile> fileList;

    public DownloadNewPluginsTask(ManagerDirector director, ApplicationContext context, List<DownloadFile> fileList) {
        super(context.getApplication());
        this.director = director;
        this.fileList = fileList;
        this.setInputBlocker(new ScreenInputBlocker(this, BlockingScope.APPLICATION, getMainFrame(), null));
        setUseRelativeStoreFileIfPossible(false);
    }


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
        boolean success = false;
        for (DownloadFile file : fileList) {
            if (isCancelled())
                break;
            try {
                setDownloadFile(file);
                downloadFile.setSaveToDirectory(dir);
                processFile(file);
                success = true;
            } catch (Exception e) {
                file.setState(DownloadState.ERROR);
                setFileErrorMessage(e);
                LogUtils.processException(logger, e);
            }
        }
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

    @Override
    protected void cancelled() {
        super.cancelled();
        for (DownloadFile file : fileList) {
            if (file.getState() != DownloadState.COMPLETED)
                file.setState(DownloadState.CANCELLED);
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
        final int choiceYesNo = Swinger.getChoiceYesNo(getResourceMap().getString("installed"));
        if (choiceYesNo == Swinger.RESULT_YES) {
            director.getMenuManager().getFileActions().restartApplication();
        }
    }
}
