package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskService;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class TaskServiceManager {
    private final static Logger logger = Logger.getLogger(TaskServiceManager.class.getName());

    public static final String DOWNLOAD_SERVICE = "downloadService";
    public static final String RUN_CHECK_SERVICE = "runCheckService";
    public static final String MOVE_FILE_SERVICE = "moveFile";
    public static final String WORK_WITH_FILE_SERVICE = "workWithFile";
    public static final String DATABASE_SERVICE = "databaseService";

    private final ApplicationContext context;

    public TaskServiceManager(final ApplicationContext context) {
        this.context = context;
    }

    public synchronized TaskService getTaskService(final String name) {
        final TaskService service = context.getTaskService(name);
        if (service == null) {
            if (DOWNLOAD_SERVICE.equals(name)) {
                return initDownloadTaskService();
            } else if (RUN_CHECK_SERVICE.equals(name)) {
                return initRunCheckTaskService();
            } else if (MOVE_FILE_SERVICE.equals(name)) {
                return initMoveFileTaskService();
            } else if (WORK_WITH_FILE_SERVICE.equals(name)) {
                return initWorkWithFileTaskService();
            } else if (DATABASE_SERVICE.equals(name)) {
                return initWorkWithDatabaseTaskService();
            }
        }
        return service;
    }

    private TaskService initDownloadTaskService() {
        return initTaskService(DOWNLOAD_SERVICE, Executors.newCachedThreadPool());
    }

    private TaskService initRunCheckTaskService() {
        final int max = AppPrefs.getProperty(UserProp.MAX_SIMULTANEOUS_RUN_CHECK, UserProp.MAX_SIMULTANEOUS_RUN_CHECK_DEFAULT);
        return initTaskService(RUN_CHECK_SERVICE, newExecutorService(max));
    }

    private TaskService initMoveFileTaskService() {
        return initTaskService(MOVE_FILE_SERVICE, newExecutorService(1));
    }

    private TaskService initWorkWithFileTaskService() {
        return initTaskService(WORK_WITH_FILE_SERVICE, newExecutorService(1));
    }

    private TaskService initWorkWithDatabaseTaskService() {
        return initTaskService(DATABASE_SERVICE, Executors.newSingleThreadExecutor());
    }

    private static ExecutorService newExecutorService(final int numThreads) {
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                numThreads, numThreads, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private TaskService initTaskService(final String name, final ExecutorService executor) {
        final TaskService service = new TaskService(name, executor);
        context.addTaskService(service);
        logger.info("Creating pool " + name);
        return service;
    }

    public void runTask(String taskServiceName, Task task) {
        this.getTaskService(taskServiceName).execute(task);
    }

}
