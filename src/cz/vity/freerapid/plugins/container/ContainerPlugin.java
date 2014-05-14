package cz.vity.freerapid.plugins.container;

import cz.vity.freerapid.plugins.LibraryPlugin;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * @author ntoskrnl
 */
public abstract class ContainerPlugin extends LibraryPlugin {

    protected ConnectionSettings connectionSettings;
    protected DialogSupport dialogSupport;

    public abstract List<String[]> getSupportedFiles();

    public abstract List<FileInfo> read(InputStream is, String name) throws Exception;

    public abstract void write(List<FileInfo> files, OutputStream os, String name) throws Exception;

    public ConnectionSettings getConnectionSettings() {
        return connectionSettings;
    }

    public void setConnectionSettings(final ConnectionSettings connectionSettings) {
        this.connectionSettings = connectionSettings;
    }

    public DialogSupport getDialogSupport() {
        return dialogSupport;
    }

    public void setDialogSupport(final DialogSupport dialogSupport) {
        this.dialogSupport = dialogSupport;
    }

    public HttpDownloadClient createDownloadClient() throws Exception {
        if (connectionSettings == null) {
            throw new ServiceConnectionProblemException("noAvailableConnection");
        }
        final DownloadClient downloadClient = new DownloadClient();
        downloadClient.initClient(connectionSettings);
        return downloadClient;
    }

}
