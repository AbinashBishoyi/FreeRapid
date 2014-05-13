package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdesktop.application.ApplicationContext;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * @author Ladislav Vitasek
 */
public class DownloadNewPluginsTask extends DownloadTask {
    private final ManagerDirector director;
    private final List<DownloadFile> fileList;

    public DownloadNewPluginsTask(ManagerDirector director, ApplicationContext context, List<DownloadFile> fileList) {
        super(context.getApplication());
        this.director = director;
        this.fileList = fileList;
        this.setInputBlocker(new ScreenInputBlocker(this, BlockingScope.APPLICATION, getMainFrame(), null));
    }


    protected Void doInBackground() throws Exception {
        message("downloadNewPluginsTask");
        final ClientManager clientManager = director.getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getEnabledConnections();
        if (connectionSettingses.isEmpty())
            return null;
        client = new DownloadClient();
        client.initClient(connectionSettingses.get(0));
        initDownloadThread();
        Thread.sleep(10000);

        final File dir = director.getPluginsManager().getPluginsDir();
        for (DownloadFile file : fileList) {
            try {
                setDownloadFile(file);
                downloadFile.setSaveToDirectory(dir);
                processFile(file);
            } catch (Exception e) {
                file.setState(DownloadState.ERROR);
                setFileErrorMessage(e);
            }
        }
        return null;
    }

    private void processFile(final DownloadFile file) throws Exception {
        final GetMethod getMethod = client.getGetMethod(file.getFileUrl().toExternalForm());
        final InputStream inputStream = client.makeRequestForFile(getMethod);
        if (inputStream != null) {
            saveToFile(inputStream);
            checkRewrite(file);
            file.setState(DownloadState.COMPLETED);
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

    private void checkRewrite(DownloadFile downloadFile) {
        final File out = downloadFile.getOutputFile();
        if (out.exists()) {
            out.delete();
        }
        storeFile.renameTo(out);
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
        if (handleRuntimeException(cause))
            return;
        error(cause);
    }

    @Override
    protected void succeeded(Void result) {
        final int choiceYesNo = Swinger.getChoiceYesNo("New plugins were installed.\nFor applying new versions of plugins you need to restart application.\nDo you want to restart it now?");
        if (choiceYesNo == Swinger.RESULT_YES) {
            director.getMenuManager().getFileActions().restartApplication();
        }
    }
}