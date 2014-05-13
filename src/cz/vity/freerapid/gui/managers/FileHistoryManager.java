package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.value.DelayedReadValueModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.FileHistoryItem;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.*;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class FileHistoryManager extends AbstractBean implements Application.ExitListener {
    private final static Logger logger = Logger.getLogger(FileHistoryManager.class.getName());

    private final ManagerDirector director;
    private final ApplicationContext context;

    private final ArrayListModel<FileHistoryItem> items = new ArrayListModel<FileHistoryItem>();

    private boolean loaded = false;
    private static final String FILES_LIST_XML = "history.xml";

    private int dataChanged = 0;
    private final Object saveFileLock = new Object();


    public FileHistoryManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        this.context.getApplication().addExitListener(this);
        init();
    }

    private void init() {
        if (AppPrefs.getProperty(UserProp.AUTOSAVE_ENABLED, UserProp.AUTOSAVE_ENABLED_DEFAULT)) {

            PropertyAdapter<FileHistoryManager> adapter = new PropertyAdapter<FileHistoryManager>(this, "dataChanged", true);

            final int time = AppPrefs.getProperty(UserProp.AUTOSAVE_TIME, UserProp.AUTOSAVE_TIME_DEFAULT);

            DelayedReadValueModel delayedReadValueModel = new DelayedReadValueModel(adapter, time * 1000, true);
            delayedReadValueModel.addValueChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    saveListToFileOnBackground();
                }
            });
        }

    }

    private void saveListToFileOnBackground() {
        //assert loaded;
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
        service.execute(new Task(context.getApplication()) {
            protected Object doInBackground() throws Exception {
                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
                final ArrayListModel<FileHistoryItem> files;

                files = new ArrayListModel<FileHistoryItem>(getItems());//getItems je synchronizovana

                saveToFile(files);

                return null;
            }

            @Override
            protected void failed(Throwable cause) {
                LogUtils.processException(logger, cause);
            }
        });

    }

    public boolean canExit(EventObject event) {
        return true;
    }

    public void willExit(EventObject event) {
        synchronized (this) {
            if (!loaded) // pokud to neni loaded, tak to znamena, ze jsem s tim seznamem nemanipuloval
                return;
            saveToFile(items);
        }
    }

    private void saveToFile(ArrayListModel<FileHistoryItem> files) {
        synchronized (saveFileLock) {
            logger.info("=====Saving download history into XML file=====");
            final LocalStorage localStorage = context.getLocalStorage();
            try {
                localStorage.save(files, FILES_LIST_XML);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            } finally {
                logger.info("=====Saving download history finished =====");
            }
        }
    }


    @SuppressWarnings({"unchecked"})
    private void loadList() {
        final LocalStorage localStorage = context.getLocalStorage();
        if (new File(localStorage.getDirectory(), FILES_LIST_XML).exists()) {
            try {
                final Object o = localStorage.load(FILES_LIST_XML);
                if (o instanceof ArrayListModel) {
                    for (FileHistoryItem file : (ArrayListModel<FileHistoryItem>) o) {
                        this.items.add(file);
                    }
                }
                this.items.addListDataListener(new ListDataListener() {
                    public void intervalAdded(ListDataEvent e) {
                        fireDataChanged();
                    }

                    public void intervalRemoved(ListDataEvent e) {
                        fireDataChanged();
                    }

                    public void contentsChanged(ListDataEvent e) {

                    }
                });
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    public synchronized ArrayListModel<FileHistoryItem> getItems() {
        if (!loaded) {
            loadList();
            this.loaded = true;
        }
        return items;
    }

    public synchronized void addHistoryItem(DownloadFile file, File savedAs) {
        getItems().add(new FileHistoryItem(file, savedAs));
    }

    public synchronized void clearHistory() {
        getItems().clear();
    }

    public synchronized void removeItem(FileHistoryItem item) {
        getItems().remove(item);
    }

    public synchronized void removeItemByIndex(int index) {
        getItems().remove(index);
    }


    public List<FileHistoryItem> getSelectionToList(int[] selectedRows) {
        return selectionToList(selectedRows);
    }

    private List<FileHistoryItem> selectionToList(int[] indexes) {
        List<FileHistoryItem> list = new ArrayList<FileHistoryItem>();
        final ArrayListModel<FileHistoryItem> items = getItems();
        for (int index : indexes) {
            list.add(items.get(index));
        }
        return list;
    }

    public synchronized void removeSelected(int[] indexes) {
        final ArrayListModel<FileHistoryItem> items = getItems();
        final List<FileHistoryItem> toRemoveList = getSelectionToList(indexes);
        for (FileHistoryItem file : toRemoveList) {
            items.remove(file);
        }
    }

    private void fireDataChanged() {
        firePropertyChange("dataChanged", this.dataChanged, ++this.dataChanged);
    }

    public int getDataChanged() {
        return dataChanged;
    }

}
