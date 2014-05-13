package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.jdesktop.application.Application;

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
        downloadFile.setCheckedFileName(true);
        downloadFile.setState(DownloadState.QUEUED);
    }

}
