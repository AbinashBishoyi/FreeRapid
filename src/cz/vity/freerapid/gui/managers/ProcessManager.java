package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.core.tasks.DownloadClient;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.DownloadState;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import javax.swing.*;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
@SuppressWarnings({"InfiniteLoopStatement"})
public class ProcessManager extends Thread {
    private final static Logger logger = Logger.getLogger(ProcessManager.class.getName());
    private final ManagerDirector director;
    //private final ManagerDirector director;
    private final ApplicationContext appContext;
    private volatile BlockingQueue<DownloadFile> queue;
    private volatile BlockingQueue<DownloadClient> clients;
    private final Object lock;
    private DataManager dataManager;

    public ProcessManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        dataManager = director.getDataManager();
        lock = dataManager.getLock();
        //  this.director = director;
        this.appContext = context;
        this.queue = new ArrayBlockingQueue<DownloadFile>(50);
        this.clients = new ArrayBlockingQueue<DownloadClient>(5);
        this.clients.addAll(dataManager.getClients());
    }

    @Override
    public void run() {
        while (true) {
            try {

                final DownloadClient client;
                final DownloadFile downloadFile;
                updateNext();
                downloadFile = queue.take();
                logger.info("Gettting downloadFile " + downloadFile);
                client = clients.take();

                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        synchronized (lock) {
                            if (downloadFile.getState() != DownloadState.QUEUED)
                                clients.add(client);
                            else
                                startDownload(client, downloadFile);
                        }
                    }
                });
            } catch (InterruptedException e) {
                LogUtils.processException(logger, e);
            }
        }
    }

    void updateNext() {
        if (queue.size() > 0)
            return;
        final DataManager dataManager = director.getDataManager();
        synchronized (dataManager.getLock()) {
            final ArrayListModel<DownloadFile> files = dataManager.getDownloadFiles();
            final DownloadFile[] f = files.toArray(new DownloadFile[files.size()]);
            for (int i = f.length - 1; i >= 0; i--) {
                DownloadFile downloadFile = f[i];
                if (downloadFile.getState() == DownloadState.QUEUED) {
                    queue.add(downloadFile);
                    return;
                }
            }
        }
    }

    private void startDownload(final DownloadClient client, final DownloadFile downloadFile) {
        final DownloadState s = downloadFile.getState();
        logger.info("starting download in state s = " + s);
        final DownloadSupportFactory supportFactory = DownloadSupportFactory.getInstance();
        try {
            final DownloadTask task = supportFactory.getDownloadTaskInstance(this.appContext.getApplication(), client, downloadFile);
            downloadFile.setTask(task);
            task.addTaskListener(new TaskListener.Adapter<Void, Long>() {

                @Override
                public void finished(TaskEvent<Void> event) {
                    downloadFile.setTask(null);
                    clients.add(client);
                }
            });
            this.appContext.getTaskService().execute(task);
        } catch (NotSupportedDownloadServiceException e) {
            LogUtils.processException(logger, e);
        }
    }

    public void clearQueue() {
        queue.clear();
    }

    public void addToQueue(List<DownloadFile> list) {
        updateNext();
        //queue.addAll(list);
    }

    public void removeFromQueue(List<DownloadFile> toRemoveList) {
        queue.removeAll(toRemoveList);
    }
}
