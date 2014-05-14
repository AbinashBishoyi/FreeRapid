package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugimpl.StandardDialogSupportImpl;
import cz.vity.freerapid.plugins.container.ContainerException;
import cz.vity.freerapid.plugins.container.ContainerPlugin;
import cz.vity.freerapid.plugins.container.FileInfo;
import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
public class ExportLinksTask extends CoreTask<Void, Void> {
    private final static Logger logger = Logger.getLogger(ExportLinksTask.class.getName());

    private final MainApp app;
    private final ContainerPlugin plugin;
    private final List<DownloadFile> fileList;
    private final File destination;

    public ExportLinksTask(final MainApp app, final ContainerPlugin plugin, final List<DownloadFile> fileList, final File destination) {
        super(app);
        this.app = app;
        this.plugin = plugin;
        this.fileList = fileList;
        this.destination = destination;
        setTaskToForeground();
    }

    @Override
    protected Void doInBackground() throws Exception {
        OutputStream stream = null;
        try {
            final List<ConnectionSettings> settingsList = app.getManagerDirector().getClientManager().getAvailableConnections();
            plugin.setConnectionSettings(settingsList.isEmpty() ? null : settingsList.get(0));
            plugin.setDialogSupport(new StandardDialogSupportImpl(app.getContext()));
            message("exportingLinks", Utils.shortenFileName(destination));
            final List<FileInfo> infoList = new ArrayList<FileInfo>(fileList.size());
            for (final DownloadFile file : fileList) {
                infoList.add(file.toFileInfo());
            }
            plugin.write(infoList, stream = new FileOutputStream(destination), destination.toString());
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
    }

    @Override
    protected void failed(final Throwable cause) {
        LogUtils.processException(logger, cause);
        if (cause instanceof ContainerException) {
            Swinger.showErrorMessage(getResourceMap(), cause.getMessage());
        } else if (cause instanceof ServiceConnectionProblemException) {
            if ("noAvailableConnection".equals(cause.getMessage())) {
                Swinger.showErrorMessage(getResourceMap(), "noAvailableConnection");
            } else {
                Swinger.showErrorMessage(getResourceMap(), "connectionProblem", Utils.getThrowableDescription(cause));
            }
        } else if (cause instanceof UnknownHostException) {
            Swinger.showErrorMessage(getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(getResourceMap(), "otherProblem", Utils.getThrowableDescription(cause));
        }
    }

    @Override
    protected void succeeded(final Void result) {
        Swinger.showInformationDialog(getResourceMap().getString("successfullyExported", Utils.shortenFileName(destination, 60)));
    }

}
