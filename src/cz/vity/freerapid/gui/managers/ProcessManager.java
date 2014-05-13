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
import cz.vity.freerapid.plugins.webclient.DownloadState;
import static cz.vity.freerapid.plugins.webclient.DownloadState.*;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.swing.EDTPropertyChangeSupport;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;
import org.jdesktop.application.TaskService;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class ProcessManager extends Thread {
    private final static Logger logger = Logger.getLogger(ProcessManager.class.getName());
    private final ApplicationContext context;
    private DataManager dataManager;

    private final EDTPropertyChangeSupport pcs;

    private volatile Map<String, DownloadService> services = new Hashtable<String, DownloadService>();
    private volatile Map<DownloadFile, ConnectionSettings> forceDownloadFiles = new Hashtable<DownloadFile, ConnectionSettings>();
    private volatile List<DownloadFile> forceValidateCheck = new Vector<DownloadFile>();

    private boolean threadSuspended;
    //private final Object queueLock;
    private final Object manipulation = new Object();
    private final java.util.Timer errorTimer = new java.util.Timer();
    private PluginsManager pluginsManager;
    private volatile int downloading;
    private TaskService taskService;
    private ClientManager clientManager;


    public ProcessManager(ManagerDirector director, ApplicationContext context) {
        super();
        dataManager = director.getDataManager();
        pluginsManager = director.getPluginsManager();

        pcs = new EDTPropertyChangeSupport(this);
        this.context = context;
        taskService = director.getTaskServiceManager().getTaskService(TaskServiceManager.DOWNLOAD_SERVICE);
        clientManager = director.getClientManager();

        setDownloading(0);

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

            final List<ConnectionSettings> connectionSettingses = clientManager.getRotatedEnabledConnections();
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
        final int downloading = getDownloading();
        if (downloading == ClientManager.MAX_DOWNLOADING) {
            return false;
        } else {
            if (forceDownload)
                return true;
            final int maxDownloads = AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT);
            return maxDownloads > downloading;
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

            final List<ConnectionSettings> connectionSettingses = clientManager.getRotatedEnabledConnections();
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

    private void queueDownload(final DownloadFile downloadFile, final ConnectionSettings settings, DownloadService downloadService, final ShareDownloadService service, final boolean runCheck) {
        if (downloadFile.getState() != QUEUED) {
            logger.info("QUEUED not found - found " + downloadFile.getState());
            return;
        }
        final HttpDownloadClient client = clientManager.popWorkingClient();
        setDownloading(downloading + 1);
        client.initClient(settings);
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
                    finishedDownloading(downloadFile, client, null, runCheck);
                } else {
                    startDownload(downloadFile, client, service, runCheck);
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

    private void startDownload(final DownloadFile downloadFile, final HttpDownloadClient client, ShareDownloadService service, final boolean runCheck) {
        final DownloadState s = downloadFile.getState();
        logger.info("starting download in state s = " + s);
        try {
            final DownloadTask task;
            if (runCheck) {
                task = new RunCheckTask(context.getApplication(), client, downloadFile, service);
            } else
                task = new DownloadTask(context.getApplication(), client, downloadFile, service);
            downloadFile.setTask(task);
            task.addTaskListener(new TaskListener.Adapter<Void, Long>() {

                @Override
                public void finished(TaskEvent<Void> event) {
                    finishedDownloading(downloadFile, client, task, runCheck);
                    downloadFile.setTask(null);
                }

            });
            taskService.execute(task);
        } catch (NotSupportedDownloadServiceException e) {
            LogUtils.processException(logger, e);
        }
    }

    private void finishedDownloading(final DownloadFile file, final HttpDownloadClient client, final DownloadTask task, final boolean runCheck) {
        synchronized (manipulation) {
            final String serviceID = file.getPluginID();
            final DownloadService service = services.get(pluginsManager.getPluginMetadata(serviceID).getServices());
            if (service == null)
                throw new IllegalStateException("Download service not found:" + serviceID);
            if (runCheck) {
                service.finishedTestingFile(file);
            } else {
                service.finishedDownloading(client);
            }
            clientManager.pushWorkingClient(client);
            setDownloading(downloading - 1);

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

                        service.addProblematicConnection(settings);
                        final int purge = errorTimer.purge();
                        if (purge > 0)
                            logger.info("Purging timer threads count:" + purge);
                        if (error == DownloadTaskError.YOU_HAVE_TO_WAIT_ERROR) {
                            int waitTime = task.getYouHaveToSleepSecondsTime();
                            errorTimer.schedule(new ErrorTimerTask(service, settings, file, waitTime), 0, 1000);
                        } else
                            errorTimer.schedule(new ErrorTimerTask(service, settings, file), 0, 1000);
                    }
                }
            }
        }
        wakeUp();
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

    private class ErrorTimerTask extends TimerTask {
        private int counter;
        private final DownloadService service;
        private final ConnectionSettings settings;
        private final DownloadFile file;
        private long lastTime;

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
        }

        public void run() {
            final DownloadState state = file.getState();

            if ((state != ERROR && state != SLEEPING)) { //doslo ke zmene stavu z venci
                this.cancel(); //zrusime timer
                if (state != WAITING) {
                    file.setTimeToQueued(-1); //odecitani casu
                    file.setTimeToQueuedMax(-1);
                }
                renewProblematicConnection();
                file.resetErrorAttempts(); //je nutne vyresetovat pocet error pokusu
                queueUpdated();
                return;
            }

            file.setTimeToQueued(--counter); //normalni prubeh, jeden tick
            final long currentTime = System.currentTimeMillis();
            if (counter <= 0 || (currentTime - lastTime > 1000 * 60)) { //zarazeni zpatky do fronty
                file.setTimeToQueued(-1);
                file.setTimeToQueuedMax(-1);
                renewProblematicConnection();
                file.setState(QUEUED);
                this.cancel();
                queueUpdated();
            }
            this.lastTime = currentTime;
        }

        private void renewProblematicConnection() {
            service.removeProblematicConnection(settings);
        }
    }

    public int getDownloading() {
        return downloading;
    }

    public void setDownloading(int downloading) {
        int oldValue = this.downloading;
        this.downloading = downloading;
        pcs.firePropertyChange("downloading", oldValue, this.downloading);
    }


    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
}
