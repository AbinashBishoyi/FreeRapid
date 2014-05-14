package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.tasks.CoreTask;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.TaskService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
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

    public void saveToFile(final Set<PluginMetaData> files) {
        final TaskService service = director.getTaskServiceManager().getTaskService(TaskServiceManager.WORK_WITH_FILE_SERVICE);
        service.execute(new CoreTask<Void, Void>(context.getApplication()) {
            @Override
            protected Void doInBackground() throws Exception {
                director.getDatabaseManager().saveCollection(files);
                return null;
            }
        });
    }

    public Collection<PluginMetaData> getItems() {
        return Collections.unmodifiableCollection(loadData());
    }

    private Collection<PluginMetaData> loadData() {
          Set<PluginMetaData> result = null;
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
              } else result = new HashSet<PluginMetaData>();
              //rename old file history file into another one, so we won't import it again next time
              //noinspection ResultOfMethodCallIgnored
              srcFile.renameTo(new File(context.getLocalStorage().getDirectory(), FILES_LIST_XML + ".imported"));
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