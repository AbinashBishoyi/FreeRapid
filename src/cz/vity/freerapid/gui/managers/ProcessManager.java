package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.application.GlobalEDTExceptionHandler;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.core.tasks.DownloadTaskError;
import cz.vity.freerapid.core.tasks.RunCheckTask;
import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.gui.managers.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;
import org.jdesktop.application.TaskService;

import javax.swing.*;
import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.*;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

import static cz.vity.freerapid.plugins.webclient.DownloadState.*;

/**
 * @author Vity
 */
public class ProcessManager extends Thread {
    private final static Logger logger = Logger.getLogger(ProcessManager.class.getName());
    private final ApplicationContext context;
    private DataManager dataManager;

    private final PropertyChangeSupport pcs;

    private volatile Map<String, DownloadService> services = new Hashtable<String, DownloadService>();
    private volatile Map<DownloadFile, ConnectionSettings> forceDownloadFiles = new Hashtable<DownloadFile, ConnectionSettings>();
    private volatile List<DownloadFile> forceValidateCheck = new Vector<DownloadFile>();

    private boolean threadSuspended;
    private final Object downloadingLock = new Object();
    private final Object manipulation = new Object();
    private final java.util.Timer errorTimer = new java.util.Timer();
    private PluginsManager pluginsManager;
    private AtomicInteger downloading = new AtomicInteger(0);
    private final TaskService downloadTaskService;
    private final TaskService runCheckTaskService;
    private ClientManager clientManager;


    public ProcessManager(ManagerDirector director, ApplicationContext context) {
        super();
        dataManager = director.getDataManager();
        pluginsManager = director.getPluginsManager();

        pcs = new SwingPropertyChangeSupport(this);
        this.context = context;
        downloadTaskService = director.getTaskServiceManager().getTaskService(TaskServiceManager.DOWNLOAD_SERVICE);
        runCheckTaskService = director.getTaskServiceManager().getTaskService(TaskServiceManager.RUN_CHECK_SERVICE);
        clientManager = director.getClientManager();

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.MAX_DOWNLOADS_AT_A_TIME.equals(evt.getKey()))
                    queueUpdated();
            }
        });
    }


    @Override
    public void run() {
        this.setName("ProcessManagerThread");
        this.setUncaughtExceptionHandler(new GlobalEDTExceptionHandler());

        while (!isInterrupted()) {
            synchronized (dataManager.getLock()) {
                synchronized (manipulation) {
                    if (canCreateAnotherConnection(true) && !forceDownloadFiles.isEmpty())
                        executeForceDownload();
                    if (canCreateAnotherConnection(true) && !forceValidateCheck.isEmpty())
                        executeForceValidateCheck();
                    if (canCreateAnotherConnection(true))
                        execute();
                }
            }
            try {
                synchronized (this) {
                    threadSuspended = true;
                    logger.info("Test for sleeping");
                    while (threadSuspended) {
                        wait();
                    }
                }
            } catch (InterruptedException e) {
                //ignore
            }
        }
        logger.info("Process Manager thread was interrupted succesfuly");
    }

    private void executeForceValidateCheck() {
        DownloadFile[] array = new DownloadFile[forceValidateCheck.size()];
        array = forceValidateCheck.toArray(array);
        for (DownloadFile file : array) {
            logger.info("Getting file for check " + file);

            final ShareDownloadService service = pluginsManager.getService(file);
            if (service == null)
                continue;
            final DownloadService downloadService = getDownloadService(service);

            final List<ConnectionSettings> connectionSettingses = clientManager.getRotatedEnabledConnections(file.getFileUrl().getHost());
            if (file.getFileState() == FileState.NOT_CHECKED && service.supportsRunCheck() && !connectionSettingses.isEmpty()) {
                //pokud to podporuje plugin a  soucasne nebyl jeste ocheckovan a soucasne je k dispozici vubec nejake spojeni
                queueDownload(file, connectionSettingses.get(0), downloadService, service, true);
            }
            if (!canCreateAnotherConnection(false))
                break;
        }
        forceValidateCheck.clear();
    }

    private boolean canCreateAnotherConnection(final boolean forceDownload) {
        synchronized (downloadingLock) {
            if (forceDownload)
                return true;
            final int maxDownloads = AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT);
            return maxDownloads > getDownloading();
        }
    }

    private List<DownloadFile> getFilesForForceDownload() {
        return new LinkedList<DownloadFile>(forceDownloadFiles.keySet());
    }

    /**
     * Metoda, ktera se stara o prirazeni vlakna pro stahovani/zjistovani.
     * Rozhoduje, kdy co a cim spustit.
     *
     * @return
     */
    private boolean execute() {
        final List<DownloadFile> files = new ArrayList<DownloadFile>(dataManager.getDownloadFiles());

        final boolean testFiles = AppPrefs.getProperty(UserProp.TEST_FILE, UserProp.TEST_FILE_DEFAULT);
        final List<DownloadFile> queuedFiles = getQueued(files);
        for (DownloadFile file : queuedFiles) {
            logger.info("Getting downloadFile " + file);

            final ShareDownloadService service = pluginsManager.getService(file);
            if (service == null)
                continue;
            final DownloadService downloadService = getDownloadService(service);

            final List<ConnectionSettings> connectionSettingses = clientManager.getRotatedEnabledConnections(file.getFileUrl().getHost());
            if (testFiles && file.getFileState() == FileState.NOT_CHECKED && service.supportsRunCheck() && !connectionSettingses.isEmpty()) {
                //pokud to podporuje plugin a  soucasne nebyl jeste ocheckovan a soucasne je k dispozici vubec nejake spojeni
                //a soucasne je to zapnuto v nastavenich
                queueDownload(file, connectionSettingses.get(0), downloadService, service, true);
            } else {
                if (canCreateAnotherConnection(false)) {
                    if (downloadService.canDownloadBeforeCheck(file, files, isStartFromTop())) {
                        for (ConnectionSettings settings : connectionSettingses) {
                            if (downloadService.canDownloadWith(settings)) {
                                queueDownload(file, settings, downloadService, service, false);
                                break;
                            }
                        }
                    }
                }
            }
            if (!canCreateAnotherConnection(true))
                return true;
        }
        return false;
    }

    private boolean executeForceDownload() {
        for (DownloadFile file : getFilesForForceDownload()) {
            logger.info("Getting downloadFile " + file);

            final ShareDownloadService service = pluginsManager.getService(file);
            if (service == null)
                continue;
            final DownloadService downloadService = getDownloadService(service);

            final ConnectionSettings settings = forceDownloadFiles.remove(file);
            if (settings == null)
                throw new IllegalStateException("Cannot find forceDownloaded File");
            logger.info("Force downloading with settings " + settings);
            queueDownload(file, settings, downloadService, service, false);
            if (!canCreateAnotherConnection(true))
                return true;
        }
        return false;
    }

    private DownloadService getDownloadService(ShareDownloadService service) {
        final PluginMetaData metaData = pluginsManager.getPluginMetadata(service.getId());

        final String idServices = metaData.getServices();
        DownloadService downloadService = services.get(idServices);
        if (downloadService == null) {
            downloadService = new DownloadService(metaData, service);
            services.put(idServices, downloadService);
        } else {
            //pro pripad, ze se nezmenili services pluginu, ale metadata ano - napr. max. pocet stahovanych
            downloadService.setPluginMetaData(metaData);
        }

        logger.info("Getting plugin: " + metaData.toString());
        return downloadService;
    }

    public void forceDownload(final ConnectionSettings settings, List<DownloadFile> files) {
        synchronized (manipulation) {
            for (DownloadFile file : files) {
                if (!DownloadsActions.isProcessState(file.getState())) {
                    logger.info("Force downloading file " + file + " with settings " + settings);
                    forceDownloadFiles.put(file, settings);
                    file.setState(QUEUED);
                }
            }
        }
        queueUpdated();
    }

    public void forceValidateCheck(List<DownloadFile> files) {
        synchronized (manipulation) {
            for (DownloadFile file : files) {
                if (!DownloadsActions.isProcessState(file.getState())) {
                    logger.info("Force validate check file " + file);
                    forceValidateCheck.add(file);
                    file.setState(QUEUED);
                }
            }
        }
        queueUpdated();
    }

    private void queueDownload(final DownloadFile downloadFile, final ConnectionSettings settings, final DownloadService downloadService, final ShareDownloadService service, final boolean runCheck) {
        if (downloadFile.getState() != QUEUED) {
            logger.info("QUEUED not found - found " + downloadFile.getState());
            return;
        }
        if (!runCheck && AppPrefs.getProperty(UserProp.SKIP_DUPLICATE_FILES, UserProp.SKIP_DUPLICATE_FILES_DEFAULT)) {
            if (dataManager.isSameDownloading(downloadFile)) {
                downloadFile.setState(DownloadState.SKIPPED);
                downloadFile.setErrorMessage(context.getResourceMap().getString("fileIsBeingAlreadyDownloading"));
                return;
            }
        }

        final HttpDownloadClient client;
        synchronized (downloadingLock) {
            client = new DownloadClient();
            setDownloading(downloading.intValue(), downloading.incrementAndGet());
            client.setConnectionTimeOut(AppPrefs.getProperty(UserProp.CONNECTION_TIMEOUT, UserProp.CONNECTION_TIMEOUT_DEFAULT));
            client.initClient(settings);
        }
        if (runCheck) {
            downloadService.addTestingFile(downloadFile);
            downloadFile.setState(TESTING);
        } else {
            downloadService.addDownloadingClient(client);
            downloadFile.setState(GETTING);
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final DownloadState state = downloadFile.getState();
                if (!(state == GETTING || state == TESTING)) {
                    finishedDownloading(downloadFile, client, downloadService, null, runCheck);
                } else {
                    startDownload(downloadFile, client, downloadService, service, runCheck);
                }
            }
        });
    }

    private List<DownloadFile> getQueued(List<DownloadFile> queue) {
        //synchronized (dataManager.getLock()) {
        final List<DownloadFile> queued = new LinkedList<DownloadFile>();

        final boolean startFromTop = isStartFromTop();
        if (startFromTop) {
            for (DownloadFile downloadFile : queue) {
                if (downloadFile.getState() == QUEUED) {
                    queued.add(downloadFile);
                }
            }
        } else {
            for (int i = queue.size() - 1; i >= 0; i--) {
                final DownloadFile downloadFile = queue.get(i);
                if (downloadFile.getState() == QUEUED) {
                    queued.add(downloadFile);
                }
            }
        }
        return queued;
        //    }
    }

    private boolean isStartFromTop() {
        return AppPrefs.getProperty(UserProp.START_FROM_TOP, UserProp.START_FROM_TOP_DEFAULT);
    }

    private void startDownload(final DownloadFile downloadFile, final HttpDownloadClient client, final DownloadService downloadService, ShareDownloadService service, final boolean runCheck) {
        final DownloadState s = downloadFile.getState();
        logger.info("starting download in state s = " + s);
        try {
            final DownloadTask task;
            final TaskService taskService;
            if (runCheck) {
                task = new RunCheckTask(context.getApplication(), client, downloadFile, service);
                taskService = runCheckTaskService;
            } else {
                task = new DownloadTask(context.getApplication(), client, downloadFile, service);
                taskService = downloadTaskService;
            }
            downloadFile.setTask(task);
            updateResumable(downloadFile);
            task.addTaskListener(new TaskListener.Adapter<Void, Long>() {
                @Override
                public void finished(TaskEvent<Void> event) {
                    finishedDownloading(downloadFile, client, downloadService, task, runCheck);
                    downloadFile.setTask(null);
                }
            });
            try {
                taskService.execute(task);
            } catch (RejectedExecutionException e) {
                logger.severe(Utils.dumpStackTraces());
                logger.severe("downloading = " + downloading);
                throw e;
            } catch (Exception e) {
                logger.severe("downloading2 = " + downloading);
            }

        } catch (NotSupportedDownloadServiceException e) {
            LogUtils.processException(logger, e);
        }
    }

    private void updateResumable(DownloadFile downloadFile) {
        if (downloadFile.isResumeSupported()) {
            try {
                final boolean supportsResume = pluginsManager.getPluginMetadata(downloadFile.getPluginID()).isResumeSupported();
                downloadFile.setResumeSupported(supportsResume);
            } catch (NotSupportedDownloadServiceException e) {
                LogUtils.processException(logger, e);
            }

        }

    }

    private void finishedDownloading(final DownloadFile file, final HttpDownloadClient client, final DownloadService downloadService, final DownloadTask task, final boolean runCheck) {

        synchronized (manipulation) {
            if (runCheck) {
                downloadService.finishedTestingFile(file);
            } else {
                downloadService.finishedDownloading(client);
            }
            synchronized (downloadingLock) {
                setDownloading(downloading.intValue(), downloading.decrementAndGet());
            }

            if (task != null) {
                DownloadTaskError error = task.getServiceError();
                final ConnectionSettings settings = client.getSettings();
                if (error == DownloadTaskError.NO_ROUTE_TO_HOST) {
                    clientManager.setConnectionEnabled(settings, false);
                    //final int problematic = service.getProblematicConnectionsCount();
                    if (clientManager.getEnabledConnections().size() > 0) {
                        file.setState(QUEUED);
                    } else error = DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR;
                }
                final DownloadState state = file.getState();
                int errorAttemptsCount = file.getErrorAttemptsCount();
                if ((state == ERROR && errorAttemptsCount != 0) || (state == SLEEPING)) {
                    if (error == DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR && errorAttemptsCount != -1) {
                        file.setErrorAttemptsCount(0);
                    } else {
                        if (errorAttemptsCount != -1)
                            file.setErrorAttemptsCount(errorAttemptsCount - 1);

                        downloadService.addProblematicConnection(settings);
                        final int purge = errorTimer.purge();
                        if (purge > 0)
                            logger.info("Purging timer threads count:" + purge);
                        if (error == DownloadTaskError.YOU_HAVE_TO_WAIT_ERROR) {
                            int waitTime = task.getYouHaveToSleepSecondsTime();
                            errorTimer.schedule(new ErrorTimerTask(downloadService, settings, file, waitTime), 0, 1000);
                        } else
                            errorTimer.schedule(new ErrorTimerTask(downloadService, settings, file), 0, 1000);
                    }
                }
            }
        }

        checkCompleted(file);
        wakeUp();
    }

    private void checkCompleted(DownloadFile file) {
        if (file.getState() == COMPLETED) {
            boolean remove = false;
            if (Boolean.TRUE.equals(file.getProperties().get("removeCompleted"))) {
                remove = true;
            } else {
                final boolean removeCompleted = AppPrefs.getProperty(UserProp.REMOVE_COMPLETED_DECRYPTER, UserProp.REMOVE_COMPLETED_DECRYPTER_DEFAULT);
                try {
                    final PluginMetaData data = pluginsManager.getPluginMetadata(file.getPluginID());
                    if (removeCompleted && data.isRemoveCompleted()) {
                        remove = true;
                    }
                } catch (NotSupportedDownloadServiceException e) {
                    //ignore
                }
            }
            if (remove)
                dataManager.removeSelected(Arrays.asList(file));
        }
    }

    public void queueUpdated() {
        wakeUp();
    }

    private void wakeUp() {
        synchronized (this) {
            threadSuspended = false;
            this.notify();
        }
    }

    private class ErrorTimerTask extends TimerTask implements PropertyChangeListener {
        private int counter;
        private final DownloadService service;
        private final ConnectionSettings settings;
        private final DownloadFile file;
        private long lastTime;
        private boolean finished;

        public ErrorTimerTask(DownloadService service, ConnectionSettings settings, DownloadFile file) {
            this(service, settings, file, AppPrefs.getProperty(UserProp.AUTO_RECONNECT_TIME, UserProp.AUTO_RECONNECT_TIME_DEFAULT));
        }

        public ErrorTimerTask(DownloadService service, ConnectionSettings settings, DownloadFile file, int waitTime) {
            this.service = service;
            this.settings = settings;
            this.file = file;
            this.counter = waitTime;
            file.setTimeToQueuedMax(waitTime);
            this.lastTime = System.currentTimeMillis();
            finished = false;
            file.addPropertyChangeListener("state", this);
        }

        public void propertyChange(PropertyChangeEvent evt) {
            final DownloadState newState = (DownloadState) evt.getNewValue();
            if ((newState != ERROR && newState != SLEEPING)) { //doslo ke zmene stavu z venci
                synchronized (manipulation) {
                    this.cancel(); //zrusime timer
                    if (newState != WAITING) {
                        file.setTimeToQueued(-1); //odecitani casu
                        file.setTimeToQueuedMax(-1);
                    }
                    finished = true;
                    unregisterListener();
                    file.resetErrorAttempts(); //je nutne vyresetovat pocet error pokusu
                    renewProblematicConnection();
                }
                queueUpdated();
            }
        }

        private void unregisterListener() {
            file.removePropertyChangeListener("state", this);
        }

        public void run() {
            if (finished)
                return;

            file.setTimeToQueued(--counter); //normalni prubeh, jeden tick
            final long currentTime = System.currentTimeMillis();
            if (counter <= 0 || (currentTime - lastTime > 1000 * 60)) { //zarazeni zpatky do fronty
                synchronized (manipulation) {
                    unregisterListener();
                    file.setTimeToQueued(-1);
                    file.setTimeToQueuedMax(-1);
                    file.setState(QUEUED);
                    this.cancel();
                    renewProblematicConnection();
                }
                queueUpdated();
            }
            this.lastTime = currentTime;
        }

        private void renewProblematicConnection() {
            service.removeProblematicConnection(settings);
        }
    }

    public int getDownloading() {
        return downloading.get();
    }

    private void setDownloading(int intOldValue, int downloading) {
        pcs.firePropertyChange("downloading", intOldValue, downloading);
    }


    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }


    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
}
