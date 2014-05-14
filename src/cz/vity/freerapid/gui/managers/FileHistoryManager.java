package cz.vity.freerapid.gui.managers;


import com.jgoodies.common.collect.ArrayListModel;
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

    }

    @SuppressWarnings({"unchecked"})
    private List<FileHistoryItem> loadList(final File srcFile) throws IOException {
        final List<FileHistoryItem> list = new LinkedList<FileHistoryItem>();
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
        if (srcFile.exists()) { //extract from old file
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
            } else result = new ArrayList<FileHistoryItem>();
            //rename old file history file into another one, so we won't import it again next time
            //noinspection ResultOfMethodCallIgnored
            srcFile.renameTo(new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".imported"));
            return result;
        } else {
            //load from database
            return director.getDatabaseManager().loadAll(FileHistoryItem.class);
        }
    }

    public void addHistoryItem(final DownloadFile file, final File savedAs) {
        final FileHistoryItem item = new FileHistoryItem(file, savedAs);
        director.getDatabaseManager().saveOrUpdate(item);
        fireDataAdded(item);
    }

    public void clearHistory() {
        director.getDatabaseManager().removeAll(FileHistoryItem.class);
    }


    public void removeItems(Collection<FileHistoryItem> items) {
        director.getDatabaseManager().removeCollection(items);
    }


    private void fireDataAdded(FileHistoryItem dataAdded) {
        firePropertyChange("dataAdded", null, dataAdded);
    }
//
//
}
