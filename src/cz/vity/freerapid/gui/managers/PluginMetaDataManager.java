package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class PluginMetaDataManager {
    private final static Logger logger = Logger.getLogger(PluginMetaDataManager.class.getName());

    //private final ManagerDirector director;
    private final ApplicationContext context;

    private final Set<PluginMetaData> items = new HashSet<PluginMetaData>();

    //private boolean loaded = false;
    private static final String FILES_LIST_XML = "plugins.xml";

    private final Object saveFileLock = new Object();


    @SuppressWarnings({"UnusedDeclaration"})
    public PluginMetaDataManager(ApplicationContext context) {
        //  this.director = director;
        this.context = context;
//        this.context.getApplication().addExitListener(this);
    }

//    private void saveListToFileOnBackground() {
//        //assert loaded;
//        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
//        service.execute(new Task(context.getApplication()) {
//            protected Object doInBackground() throws Exception {
//                Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
//                final Set<PluginMetaData> files;
//
//                files = new ArraySet<PluginMetaData>(getItems());//getItems je synchronizovana
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

//    public boolean canExit(EventObject event) {
//        return true;
//    }
//
//    public void willExit(EventObject event) {
//        synchronized (this) {
//            if (!loaded) // pokud to neni loaded, tak to znamena, ze jsem s tim seznamem nemanipuloval
//                return;
//            saveToFile(items);
//        }
//    }

    //

    public void saveToFile(Set<PluginMetaData> files) {
        synchronized (saveFileLock) {
            logger.info("=====Saving PluginMetaData list into XML file=====");
            final LocalStorage localStorage = context.getLocalStorage();
            File dstFile = new File(localStorage.getDirectory(), FILES_LIST_XML);
            try {
                if (AppPrefs.getProperty(UserProp.MAKE_FILE_BACKUPS, UserProp.MAKE_FILE_BACKUPS_DEFAULT))
                    FileUtils.makeBackup(dstFile);
                localStorage.save(files, FILES_LIST_XML);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            } finally {
                logger.info("=====Saving PluginMetaData list finished =====");
            }
        }
    }


    @SuppressWarnings({"unchecked"})
    private Set<PluginMetaData> loadList(final File srcFile) throws IOException {
        final Set<PluginMetaData> set = new HashSet<PluginMetaData>();
        final LocalStorage localStorage = context.getLocalStorage();
        if (!srcFile.exists()) {
            return set;
        }

        final Object o = localStorage.load(FILES_LIST_XML);

        if (o instanceof Set) {
            return (Set<PluginMetaData>) o;
        }
        return set;
    }

    public synchronized Set<PluginMetaData> getItems() {
        loadData();
        return Collections.unmodifiableSet(items);
    }

    private void loadData() {
        Set<PluginMetaData> result = null;
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
            if (result != null)
                this.items.addAll(result);
        }
    }
}