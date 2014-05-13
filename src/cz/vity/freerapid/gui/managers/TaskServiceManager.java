package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskService;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Ladislav Vitasek
 */
public class TaskServiceManager {
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
        final int poolSize = AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT);
        return initTaskService(poolSize, 10, 5L, DOWNLOAD_SERVICE, new LinkedBlockingQueue<Runnable>(10));
    }

    private TaskService initMoveFileTaskService() {
        return initTaskService(1, 1, 5L, WORK_WITH_FILE_SERVICE, new LinkedBlockingQueue<Runnable>());
    }

    private TaskService initWorkWithFileTaskService() {
        return initTaskService(1, 1, 5L, MOVE_FILE_SERVICE, new LinkedBlockingQueue<Runnable>());
    }

    private TaskService initTaskService(int corePoolSize, int maximumPoolSize, long keepAliveTime, String name, LinkedBlockingQueue<Runnable> runnables) {
        final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(
                corePoolSize,   // corePool size
                maximumPoolSize,  // maximumPool size
                keepAliveTime, TimeUnit.SECONDS,  // non-core threads time to live
                runnables);
        final TaskService service = new TaskService(name, threadPool);
        context.addTaskService(service);
        return service;
    }


}
