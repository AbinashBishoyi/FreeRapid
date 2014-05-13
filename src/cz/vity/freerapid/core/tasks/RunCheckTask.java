package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.ShareDownloadService;
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
//        final GetMethod getMethod = client.getGetMethod("http://data.idnes.cz/televize/img/1/1466255.jpg");
//        InputStream stream = client.makeRequestForFile(getMethod);
//        final BufferedImage image = loadCaptcha(stream);
//        final String s = askForCaptcha(image);
//        System.out.println("s = " + s);
        downloadFile.setDownloaded(0);
        final int seconds = AppPrefs.getProperty(UserProp.ERROR_SLEEP_TIME, UserProp.ERROR_SLEEP_TIME_DEFAULT);
        if (seconds > 0)
            sleep(seconds);
        downloadFile.setState(DownloadState.GETTING);
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
