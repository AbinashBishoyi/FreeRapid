package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class FileHistoryManager implements Application.ExitListener {
    private final static Logger logger = Logger.getLogger(FileHistoryManager.class.getName());

    private final ApplicationContext context;

    private final ArrayListModel<FileHistoryItem> items = new ArrayListModel<FileHistoryItem>();

    private boolean loaded = false;
    private static final String FILES_LIST_XML = "history.xml";

    public FileHistoryManager(ApplicationContext context) {
        this.context = context;
        this.context.getApplication().addExitListener(this);
    }

    public boolean canExit(EventObject event) {
        return true;
    }

    public void willExit(EventObject event) {
        saveList();
    }

    private void saveList() {
        final LocalStorage localStorage = context.getLocalStorage();
        try {
            localStorage.save(items, FILES_LIST_XML);
        } catch (IOException e) {
            LogUtils.processException(logger, e);
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
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
        this.loaded = true;
    }

    public synchronized ArrayListModel<FileHistoryItem> getItems() {
        if (!loaded)
            loadList();
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

    public void removeSelected(int[] indexes) {
        final ArrayListModel<FileHistoryItem> items = getItems();
        final List<FileHistoryItem> toRemoveList = getSelectionToList(indexes);
        for (FileHistoryItem file : toRemoveList) {
            items.remove(file);
        }
    }
}
