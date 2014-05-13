package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.tasks.DownloadClient;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.core.tasks.RapidShareDownloadFileTask;
import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.Application;

/**
 * @author Ladislav Vitasek
 */
public class DownloadSupportFactory {
    private final static DownloadSupportFactory instance = new DownloadSupportFactory();

    private DownloadSupportFactory() {

    }

    public static DownloadSupportFactory getInstance() {
        return instance;
    }

    public boolean isSupported(DownloadFile file) {
        return file.getFileUrl().toString().contains("rapidshare");
    }

    public DownloadTask getDownloadTaskInstance(Application app, DownloadClient client, DownloadFile file) throws NotSupportedDownloadServiceException {
        if (isSupported(file)) {
            return new RapidShareDownloadFileTask(app, client, file);
        } else throw new NotSupportedDownloadServiceException();
    }
}
