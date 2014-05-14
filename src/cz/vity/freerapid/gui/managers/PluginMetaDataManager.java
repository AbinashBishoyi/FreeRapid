package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
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

    //private boolean loaded = false;
    private static final String FILES_LIST_XML = "plugins.xml";

    private ManagerDirector director;

    @SuppressWarnings({"UnusedDeclaration"})
    public PluginMetaDataManager(ManagerDirector director) {
        this.director = director;
        this.context = director.getContext();

    }

    public void saveToDatabase(final Set<PluginMetaData> files) {
        Runnable runnable = new Runnable() {
            public void run() {
                logger.info("Saving metadata info into database - start");
                director.getDatabaseManager().saveCollection(files);
                logger.info("Saving metadata info into database - end");
            }
        };
        director.getDatabaseManager().runOnTask(runnable);
    }

    public Collection<PluginMetaData> getItems() {
        return Collections.unmodifiableCollection(loadData());
    }

    private Collection<PluginMetaData> loadData() {
        Set<PluginMetaData> result = null;
        final File srcFile = new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML);
        if (!srcFile.exists()) { //extract from old file, we ignore existence of backup file in case the main file does not exist
            final File backupFile = FileUtils.getBackupFile(srcFile);
            if (backupFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                backupFile.renameTo(srcFile);
            }
        }

        final File targetImportedFile = new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".imported");
        if (srcFile.exists() && !targetImportedFile.exists()) { //extract from old file
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
                saveToDatabase(result);
            } else result = new HashSet<PluginMetaData>();
            //rename old file history file into another one, so we won't import it again next time
            //noinspection ResultOfMethodCallIgnored
            final File backupFile = FileUtils.getBackupFile(srcFile);
            if (backupFile.exists()) {
                //noinspection ResultOfMethodCallIgnored
                backupFile.renameTo(new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".backup.imported"));
            }
            srcFile.renameTo(targetImportedFile);
            return result;
        } else {
            //load from database
            return director.getDatabaseManager().loadAll(PluginMetaData.class);
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

}