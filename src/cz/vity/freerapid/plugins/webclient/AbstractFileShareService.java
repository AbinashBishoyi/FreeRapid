package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.exceptions.NotSupportedDownloadByServiceException;
import org.java.plugin.Plugin;

/**
 * @author Vity
 */
public abstract class AbstractFileShareService extends Plugin implements ShareDownloadService {

    public AbstractFileShareService() {
        super();
    }

    protected void doStart() throws Exception {

    }

    protected void doStop() throws Exception {

    }

    @Override
    public String toString() {
        return getName();
    }

    public void run(HttpFileDownloader downloader) throws Exception {
        if (!supportsURL(downloader.getDownloadFile().getFileUrl().toExternalForm())) {
            throw new NotSupportedDownloadByServiceException();
        }
    }
}
