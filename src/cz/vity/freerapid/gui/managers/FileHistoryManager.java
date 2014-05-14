package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class FileHistoryManager extends AbstractBean {
    private final static Logger logger = Logger.getLogger(FileHistoryManager.class.getName());

    private final ManagerDirector director;
    private final ApplicationContext context;

//    private boolean loaded = false;
    private static final String FILES_LIST_XML = "history.xml";

 //   private int dataChanged = 0;

    public FileHistoryManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        init();
    }

    private void init() {
//        if (AppPrefs.getProperty(UserProp.AUTOSAVE_ENABLED, UserProp.AUTOSAVE_ENABLED_DEFAULT)) {
//            PropertyAdapter<FileHistoryManager> adapter = new PropertyAdapter<FileHistoryManager>(this, "dataChanged", true);
//
//            final int time = AppPrefs.getProperty(UserProp.AUTOSAVE_TIME, UserProp.AUTOSAVE_TIME_DEFAULT);
//
//            DelayedReadValueModel delayedReadValueModel = new DelayedReadValueModel(adapter, time * 1000, true);
//            delayedReadValueModel.addValueChangeListener(new PropertyChangeListener() {
//
//                public void propertyChange(PropertyChangeEvent evt) {
//                    saveListToFileOnBackground();
//                }
//            });
//        }

    }

//    private void saveListToFileOnBackground() {
//        //assert loaded;
//        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
//        service.execute(new Task(context.getApplication()) {
//            protected Object doInBackground() throws Exception {
//                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//                final ArrayListModel<FileHistoryItem> files;
//
//                files = new ArrayListModel<FileHistoryItem>(getItems());//getItems je synchronizovana
//
//                saveToFile(files);
//
//                return null;
//            }
//
//            @Override
//            protected void failed(Throwable cause) {
//                LogUtils.processException(logger, cause);
//            }
//        });
//
//    }


    @SuppressWarnings({"unchecked"})
    private List<FileHistoryItem> loadList(final File srcFile) throws IOException {
        final LinkedList<FileHistoryItem> list = new LinkedList<FileHistoryItem>();
        final LocalStorage localStorage = context.getLocalStorage();
        if (!srcFile.exists()) {
            return list;
        }

        final Object o = localStorage.load(FILES_LIST_XML);

        if (o instanceof ArrayListModel) {
            return (List<FileHistoryItem>) o;
        }
        return list;
    }

    public List<FileHistoryItem> getItems() {
        return loadFileHistoryList();
    }

    private List<FileHistoryItem> loadFileHistoryList() {
        List<FileHistoryItem> result = null;
        final File srcFile = new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML);
        if (srcFile.exists()) {
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
                director.getDatabaseManager().saveCollection(result);
            } else result = new ArrayList<FileHistoryItem>();
            //noinspection ResultOfMethodCallIgnored
            srcFile.renameTo(new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".imported"));
            return result;
        } else {
            return director.getDatabaseManager().loadAll(FileHistoryItem.class);
        }
    }

    public void addHistoryItem(final DownloadFile file, final File savedAs) {
        final FileHistoryItem item = new FileHistoryItem(file, savedAs);
        director.getDatabaseManager().saveOrUpdate(item);
    }

    public void clearHistory() {
        director.getDatabaseManager().removeAll(FileHistoryItem.class);
    }


    public void removeItems(Collection<FileHistoryItem> items) {
        director.getDatabaseManager().removeCollection(items);
    }



//    private void fireDataChanged() {
//        firePropertyChange("dataChanged", this.dataChanged, ++this.dataChanged);
//    }
//
//
}
