package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
class FileListMaintainer {
    private final ApplicationContext context;
    private final ManagerDirector director;
    private final DataManager dataManager;
    private final Object saveFileLock = new Object();
    private final static Logger logger = Logger.getLogger(FileListMaintainer.class.getName());

    private static final String FILES_LIST_XML = "filesList.xml";


    public FileListMaintainer(ApplicationContext context, ManagerDirector director, DataManager dataManager) {
        this.context = context;
        this.director = director;
        this.dataManager = dataManager;
    }


    void loadListToBean(Collection<DownloadFile> downloadFiles) {
        final LocalStorage localStorage = context.getLocalStorage();
        final File srcFile = new File(localStorage.getDirectory(), FILES_LIST_XML);
        if (!srcFile.exists()) {
            return;
        }
        final boolean downloadOnStart = AppPrefs.getProperty(UserProp.DOWNLOAD_ON_APPLICATION_START, UserProp.DOWNLOAD_ON_APPLICATION_START_DEFAULT);
        final boolean removeCompleted = AppPrefs.getProperty(UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT) == UserProp.REMOVE_COMPLETED_DOWNLOADS_AT_STARTUP;
        final boolean recheckOnStart = AppPrefs.getProperty(UserProp.RECHECK_FILES_ON_START, UserProp.RECHECK_FILES_ON_START_DEFAULT);
        List<DownloadFile> result = null;
        try {
            result = loadListFromFile(srcFile, downloadOnStart, removeCompleted, recheckOnStart);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            logger.info("Trying to renew file from backup");
            try {
                FileUtils.renewBackup(srcFile);
                result = loadListFromFile(srcFile, downloadOnStart, removeCompleted, recheckOnStart);
            } catch (FileNotFoundException ex) {
                //ignore            
            } catch (Exception e1) {
                LogUtils.processException(logger, e);
            }
        }
        if (result != null)
            downloadFiles.addAll(result);
    }

    @SuppressWarnings({"unchecked"})
    List<DownloadFile> loadListFromFile(final File srcFile, final boolean downloadOnStart, final boolean removeCompleted, boolean recheckOnStart) throws IOException {
        LinkedList<DownloadFile> list = new LinkedList<DownloadFile>();
        final Object o = context.getLocalStorage().load(srcFile.getName());
        if (!(o instanceof ArrayListModel))
            return list;
        if (!srcFile.exists()) {
            return list;
        }
        for (DownloadFile file : (ArrayListModel<DownloadFile>) o) {
            final DownloadState state = file.getState();
            if (state == DownloadState.DELETED)
                continue;
            if (state == DownloadState.COMPLETED && removeCompleted) {
                continue;
            }
            if (state != DownloadState.COMPLETED) {
//                if (state != DownloadState.PAUSED)
//                    file.setDownloaded(0);
                if (recheckOnStart)
                    file.setFileState(FileState.NOT_CHECKED);
            }
            if (state == DownloadState.ERROR || state == DownloadState.SLEEPING) {
                //file.setDownloaded(0);
                if (downloadOnStart && file.getTimeToQueued() > 0) {
                    file.setTimeToQueued(-1);
                    file.setTimeToQueuedMax(-1);
                    file.setState(DownloadState.QUEUED);
                }
            }
            if (DownloadsActions.isProcessState(state)) {
                if (downloadOnStart) {
                    file.setState(DownloadState.QUEUED);
                } else
                    file.setState(DownloadState.PAUSED);
            }
            file.resetSpeed();
            file.setTimeToQueued(-1);
            file.addPropertyChangeListener(dataManager);
            list.add(file);
        }
        return list;
    }

    void saveToFile(ArrayListModel<DownloadFile> downloadFiles) {
        synchronized (saveFileLock) {
            logger.info("=====Saving queue into the XML file=====");
            final LocalStorage localStorage = context.getLocalStorage();
            File dstFile = new File(localStorage.getDirectory(), FILES_LIST_XML);
            try {
                if (AppPrefs.getProperty(UserProp.MAKE_FILE_BACKUPS, UserProp.MAKE_FILE_BACKUPS_DEFAULT))
                    FileUtils.makeBackup(dstFile);
                localStorage.save(downloadFiles, FILES_LIST_XML);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            } finally {
                logger.info("=====Finishing saving queue into the XML file=====");
            }

        }
    }

    void saveListToFileOnBackground(final Collection<DownloadFile> downloadFiles) {
        logger.info("--------saveListToBeansOnBackground------");
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
        service.execute(new Task(context.getApplication()) {
            protected Object doInBackground() throws Exception {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                final ArrayListModel<DownloadFile> files;
                synchronized (dataManager.getLock()) {
                    files = new ArrayListModel<DownloadFile>(downloadFiles);
                }

                saveToFile(files);

                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
            }
        });

    }


}
