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
import java.util.ArrayList;
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


    private List<DownloadFile> loadFileHistoryList() {
          List<DownloadFile> result = null;
          final File srcFile = new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML);
          if (srcFile.exists()) { //extract from old file, we ignore existence of backup file in case the main file does not exist
              try {
                  result = loadList(srcFile);
              } catch (Exception e) {
                  LogUtils.processException(logger, e);
                  logger.info("Trying to renew file from backup");
                  try {
                      FileUtils.renewBackup(srcFile);
                      result = loadList(srcFile);
                  } catch (FileNotFoundException ex) {
                      //ignore
                  } catch (Exception e1) {
                      LogUtils.processException(logger, e);
                  }
              }
              if (result != null) {
                  //re-save into database
                  director.getDatabaseManager().saveCollection(result);
              } else result = new ArrayList<DownloadFile>();
              //rename old file history file into another one, so we won't import it again next time
              //noinspection ResultOfMethodCallIgnored
              srcFile.renameTo(new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".imported"));
              return result;
          } else {
              //load from database
              return director.getDatabaseManager().loadAll(DownloadFile.class);
          }
      }

    void loadListToBean(Collection<DownloadFile> downloadFiles) {
        final List<DownloadFile> result = loadFileHistoryList();
        initDownloadFiles(downloadFiles, result);
    }

    @SuppressWarnings({"unchecked"})
    private List<DownloadFile> loadList(final File srcFile) throws IOException {
        final List<DownloadFile> list = new LinkedList<DownloadFile>();
        final LocalStorage localStorage = context.getLocalStorage();
        if (!srcFile.exists()) {
            return list;
        }

        final Object o = localStorage.load(FILES_LIST_XML);

        if (o instanceof ArrayListModel) {
            return (ArrayListModel<DownloadFile>) o;
        }
        return list;
    }


    private void initDownloadFiles(Collection<DownloadFile> list, Collection<DownloadFile> o) {
        final boolean downloadOnStart = AppPrefs.getProperty(UserProp.DOWNLOAD_ON_APPLICATION_START, UserProp.DOWNLOAD_ON_APPLICATION_START_DEFAULT);
        final boolean removeCompleted = AppPrefs.getProperty(UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT) == UserProp.REMOVE_COMPLETED_DOWNLOADS_AT_STARTUP;
        final boolean recheckOnStart = AppPrefs.getProperty(UserProp.RECHECK_FILES_ON_START, UserProp.RECHECK_FILES_ON_START_DEFAULT);

        for (DownloadFile file : o) {
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
            if (file.getDownloaded() <= 0)
                file.setRealDownload(0);
            else
                file.setDownloaded(file.getRealDownload());
            file.resetSpeed();
            file.setTimeToQueued(-1);
            file.addPropertyChangeListener(dataManager);
            list.add(file);
        }
    }

    void saveToFile(Collection<DownloadFile> downloadFiles) {
        synchronized (saveFileLock) {
            logger.info("=====Saving queue into the XML file=====");
            try {
                director.getDatabaseManager().saveCollection(downloadFiles);
            } catch (Exception e) {
                LogUtils.processException(logger, e);
            } finally {
                logger.info("=====Finishing saving queue into the XML file=====");
            }
        }
    }

    void saveListToFileOnBackground(final Collection<DownloadFile> downloadFiles) {
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
        service.execute(new Task(context.getApplication()) {
            protected Object doInBackground() throws Exception {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                logger.info("--------saveListToBeansOnBackground------");
                final Collection<DownloadFile> files;
                synchronized (dataManager.getLock()) {
                    files = new ArrayList<DownloadFile>(downloadFiles);
                }

                saveToFile(files);
                logger.info("--------saveListToBeansOnBackground end ------");
                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
            }
        });

    }


}
