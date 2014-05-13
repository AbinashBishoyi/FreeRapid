package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.dev.plugimpl.DevDialogSupport;
import cz.vity.freerapid.plugins.dev.plugimpl.DevPluginContextImpl;
import cz.vity.freerapid.plugins.dev.plugimpl.DevStorageSupport;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloader;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;
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

    @Deprecated
    public void run(ShareDownloadService service, HttpFile file, ConnectionSettings settings) throws Exception {
        testRun(service, file, settings);
    }

    public void testRun(ShareDownloadService service, HttpFile file, ConnectionSettings settings) throws Exception {
        final PluginContext plugContext = DevPluginContextImpl.create(new DevDialogSupport(this.getContext()), new DevStorageSupport(this.getContext()));
        service.setPluginContext(plugContext);
        if (service.supportsRunCheck())
            service.runCheck(getHttpFileDownloader(file, settings));
        service.run(getHttpFileDownloader(file, settings));
    }


}
