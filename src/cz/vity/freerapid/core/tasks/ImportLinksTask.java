package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
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
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
public class ImportLinksTask extends CoreTask<List<FileInfo>, Void> {
    private final static Logger logger = Logger.getLogger(ImportLinksTask.class.getName());

    private final MainApp app;
    private final ContainerPlugin plugin;
    private final File[] files;
    private final File saveToDirectory = new File(AppPrefs.getProperty(UserProp.LAST_COMBO_PATH, ""));//TODO!

    public ImportLinksTask(final MainApp app, final ContainerPlugin plugin, final File[] files) {
        super(app);
        this.app = app;
        this.plugin = plugin;
        this.files = files;
        setTaskToForeground();
    }

    @Override
    protected List<FileInfo> doInBackground() throws Exception {
        int i = 0;
        setProgress(i, 0, files.length);
        final List<ConnectionSettings> settingsList = app.getManagerDirector().getClientManager().getAvailableConnections();
        plugin.setConnectionSettings(settingsList.isEmpty() ? null : settingsList.get(0));
        plugin.setDialogSupport(new StandardDialogSupportImpl(app.getContext()));
        final List<FileInfo> list = new LinkedList<FileInfo>();
        for (final File file : files) {
            message("importingLinks", Utils.shortenFileName(file));
            InputStream stream = null;
            try {
                list.addAll(plugin.read(stream = new FileInputStream(file), file.toString()));
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Exception e) {
                        LogUtils.processException(logger, e);
                    }
                }
            }
            setProgress(++i, 0, files.length);
        }
        if (list.isEmpty()) {
            throw new ContainerException("noLinksFound");
        }
        return list;
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
    protected void succeeded(final List<FileInfo> list) {
        app.getManagerDirector().getDataManager().addLinksToQueueFromContainer(list, saveToDirectory, null, true);
    }

}
