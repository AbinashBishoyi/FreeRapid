package cz.vity.freerapid.gui.managers;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskService;

import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class TaskServiceManager {
    private final static Logger logger = Logger.getLogger(TaskServiceManager.class.getName());

    public static final String DOWNLOAD_SERVICE = "downloadService";
    public static final String MOVE_FILE_SERVICE = "moveFile";
    public static final String WORK_WITH_FILE_SERVICE = "workWithFile";

    private ApplicationContext context;

    public TaskServiceManager(ApplicationContext context) {
        this.context = context;
    }

    public synchronized TaskService getTaskService(String name) {
        final TaskService service = context.getTaskService(name);
        if (service == null) {
            if (DOWNLOAD_SERVICE.equals(name)) {
                return initDownloadTaskService();
            } else if (MOVE_FILE_SERVICE.equals(name)) {
                return initMoveFileTaskService();
            } else if (WORK_WITH_FILE_SERVICE.equals(name)) {
                return initWorkWithFileTaskService();
            }
        }
        return service;
    }

    private TaskService initDownloadTaskService() {
        //final int poolSize = AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT);
        return initTaskService(1, 10, 60L, DOWNLOAD_SERVICE, new SynchronousQueue<Runnable>());
    }

    private TaskService initMoveFileTaskService() {
        return initTaskService(1, 1, 5L, MOVE_FILE_SERVICE, new LinkedBlockingQueue<Runnable>());
    }

    private TaskService initWorkWithFileTaskService() {
        return initTaskService(1, 1, 5L, WORK_WITH_FILE_SERVICE, new LinkedBlockingQueue<Runnable>());
    }

    private TaskService initTaskService(int corePoolSize, int maximumPoolSize, long keepAliveTime, String name, BlockingQueue<Runnable> runnables) {
        final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                corePoolSize,   // corePool size
                maximumPoolSize,  // maximumPool size
                keepAliveTime, TimeUnit.SECONDS,  // non-core threads time to live
                runnables);
        final TaskService service = new TaskService(name, threadPool);
        context.addTaskService(service);
        logger.info("Creating pool " + name);
        return service;
    }


}
