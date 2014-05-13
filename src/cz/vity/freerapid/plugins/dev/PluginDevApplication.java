package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.dev.plugimpl.DevDialogSupport;
import cz.vity.freerapid.plugins.dev.plugimpl.DevPluginContextImpl;
import cz.vity.freerapid.plugins.dev.plugimpl.DevStorageSupport;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.jdesktop.application.Application;

/**
 * Help application for testing plugins.
 *
 * @author Vity
 */
public abstract class PluginDevApplication extends Application {

    /**
     * Returns new instance of HttpFileDownloadTask
     *
     * @param file     the file that should be downloaded
     * @param settings internet connection settings
     * @return instance of HttpFileDownloadTask for processing file
     */
    protected HttpFileDownloadTask getHttpFileDownloader(HttpFile file, ConnectionSettings settings) {
        return new PluginDevDownloadTask(file, settings);
    }

    @Override
    protected void initialize(String[] args) {
        super.initialize(args);
    }

    /**
     * Returns new instance of HttpFile for testing purposes
     *
     * @return instance  of HttpFile
     * @see cz.vity.freerapid.plugins.dev.PluginDevHttpFile
     */
    protected HttpFile getHttpFile() {
        return new PluginDevHttpFile();
    }

    /**
     * Runs plugin test
     *
     * @param service  service that is used for downloading
     * @param file     file that is being downloaded
     * @param settings internet connection settings
     * @throws Exception when anything went wrong
     */
    public void testRun(ShareDownloadService service, HttpFile file, ConnectionSettings settings) throws Exception {
        final PluginContext plugContext = DevPluginContextImpl.create(new DevDialogSupport(this.getContext()), new DevStorageSupport(this.getContext()));
        service.setPluginContext(plugContext);
        if (service.supportsRunCheck())
            service.runCheck(getHttpFileDownloader(file, settings));
        service.run(getHttpFileDownloader(file, settings));
    }


}
