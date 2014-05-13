package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.dev.plugimpl.DevDialogSupport;
import cz.vity.freerapid.plugins.dev.plugimpl.DevPluginContextImpl;
import cz.vity.freerapid.plugins.dev.plugimpl.DevStorageSupport;
import cz.vity.freerapid.plugins.exceptions.ErrorDuringDownloadingException;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import org.jdesktop.application.Application;

import java.util.logging.Logger;

/**
 * Help application for testing plugins.
 *
 * @author Vity
 */
public abstract class PluginDevApplication extends Application {
    private final static Logger logger = Logger.getLogger(PluginDevApplication.class.getName());

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
     * Runs plugin test - both runCheck (if it is supported by service) and run() on the service implementation
     *
     * @param service  service that is used for downloading
     * @param file     file that is being downloaded
     * @param settings internet connection settings
     * @throws Exception when anything went wrong
     */
    public void testRun(ShareDownloadService service, HttpFile file, ConnectionSettings settings) throws Exception {
        final PluginContext plugContext = getPluginContext();
        service.setPluginContext(plugContext);
        final HttpFileDownloadTask fileDownloader = getHttpFileDownloader(file, settings);
        try {
            if (service.supportsRunCheck())
                service.runCheck(fileDownloader);
            service.run(fileDownloader);
        } catch (ErrorDuringDownloadingException e) {
            if (fileDownloader != null)
                logger.warning(fileDownloader.getClient().getContentAsString());
            throw e;
        }
    }

    /**
     * Runs plugin runCheck test only (if it is supported by service, otherwise does nothing)
     *
     * @param service  service that is used for downloading
     * @param file     file that is being downloaded
     * @param settings internet connection settings
     * @throws Exception when anything went wrong
     */
    public void testRunCheck(ShareDownloadService service, HttpFile file, ConnectionSettings settings) throws Exception {
        final PluginContext plugContext = getPluginContext();
        if (service.supportsRunCheck()) {
            HttpFileDownloadTask fileDownloader = getHttpFileDownloader(file, settings);
            try {
                service.setPluginContext(plugContext);
                service.runCheck(fileDownloader);
            } catch (ErrorDuringDownloadingException e) {
                logger.warning(fileDownloader.getClient().getContentAsString());
                throw e;
            }
        }
    }

    private PluginContext getPluginContext() {
        return DevPluginContextImpl.create(new DevDialogSupport(this.getContext()), new DevStorageSupport(this.getContext()));
    }


}
