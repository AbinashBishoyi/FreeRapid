package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloader;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.jdesktop.application.Application;

/**
 * @author Vity
 */
public abstract class PluginApplication extends Application {

    protected HttpFileDownloader getHttpFileDownloader(HttpFile file, ConnectionSettings settings) {
        return new PluginDevDownloader(file, settings);
    }

    @Override
    protected void initialize(String[] args) {
        super.initialize(args);

    }

    protected HttpFile getHttpFile() {
        return new PluginDevHttpFile();
    }

    public void run(ShareDownloadService service, HttpFile file, ConnectionSettings settings) throws Exception {

        service.run(getHttpFileDownloader(file, settings));
    }


}
