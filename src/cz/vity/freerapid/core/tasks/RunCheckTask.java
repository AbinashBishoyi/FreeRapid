package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.jdesktop.application.Application;

import static cz.vity.freerapid.plugins.webclient.DownloadState.QUEUED;

/**
 * @author Ladislav Vitasek
 */
public class RunCheckTask extends DownloadTask {

    public RunCheckTask(Application application, HttpDownloadClient client, DownloadFile downloadFile, ShareDownloadService service) {
        super(application, client, downloadFile, service);
    }

    @Override
    protected Void doInBackground() throws Exception {
        initDownloadThread();
        downloadFile.setState(DownloadState.TESTING);
        service.runCheck(this);//run plugin
        service = null;
        return null;
    }


    @Override
    protected void succeeded(Void result) {
        downloadFile.setFileState(FileState.CHECKED_AND_EXISTING);
        if (downloadFile.getProperties().containsKey("previousState")) {
            final DownloadState previousState = (DownloadState) downloadFile.getProperties().remove("previousState");
            if (previousState != DownloadState.ERROR && previousState != DownloadState.SLEEPING) {
                downloadFile.setState(previousState);
            } else
                downloadFile.setState(QUEUED);

        } else
            downloadFile.setState(QUEUED);
    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
        //updateFileState(cause)
    }
}
