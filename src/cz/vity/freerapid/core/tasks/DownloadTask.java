package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.FRDUtils;
import cz.vity.freerapid.gui.managers.TaskServiceManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFileDownloadTask;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Sound;
import org.apache.commons.httpclient.SimpleHttpConnectionManager;
import org.jdesktop.application.Application;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DownloadTask extends CoreTask<Void, Long> implements HttpFileDownloadTask {
    private final static Logger logger = Logger.getLogger(DownloadTask.class.getName());
    protected HttpDownloadClient client;
    protected DownloadFile downloadFile;
    protected ShareDownloadService service;
    private Integer sleep = 0;
    private static java.util.Timer timer = new java.util.Timer();
    private DownloadTaskError serviceError;

    private int youHaveToSleepSecondsTime = 0;
    private static final int INPUT_BUFFER_SIZE = 1024;
    private static final int OUTPUT_FILE_BUFFER_SIZE = 600000;
    private volatile boolean connectionTimeOut;
    private int fileAlreadyExists;
    private volatile byte[] buffer;
    private boolean useRelativeStoreFileIfPossible = true;

    public DownloadTask(Application application) {
        super(application);
        init();
    }

    public DownloadTask(Application application, HttpDownloadClient client, DownloadFile downloadFile, ShareDownloadService service) {
        super(application);
        this.client = client;
        this.downloadFile = downloadFile;
        this.service = service;
        init();
        downloadFile.setConnectionSettings(client.getSettings());
    }

    protected void init() {
        this.serviceError = DownloadTaskError.NO_ERROR;
        this.setInputBlocker(null);
        this.setUserCanCancel(true);
        this.youHaveToSleepSecondsTime = 0;
        this.connectionTimeOut = false;
        fileAlreadyExists = -2;
    }

    @Override
    protected Void doInBackground() throws Exception {
        initDownloadThread();

        if (downloadFile.getDownloaded() < 0)
            downloadFile.setDownloaded(0);
        final int seconds = AppPrefs.getProperty(UserProp.ERROR_SLEEP_TIME, UserProp.ERROR_SLEEP_TIME_DEFAULT);
        if (seconds > 0)
            sleep(seconds);
        downloadFile.setState(DownloadState.GETTING);
        service.run(this);//run plugin
        service = null;
        return null;
    }

    protected void initDownloadThread() {
        client.getHTTPClient().setHttpConnectionManager(new SimpleHttpConnectionManager());
        final int timerPurge = timer.purge();
        if (timerPurge > 0)
            logger.info("Purged timers " + timerPurge);
        client.getHTTPClient().getHttpConnectionManager().closeIdleConnections(0);
    }

    private CountingOutputStream getFileOutputStream(final File f, final long fileSize, final long startPosition) throws NotEnoughSpaceException, IOException {
        final long freeSpace = f.getUsableSpace();
        logger.info("Free space on disk: " + freeSpace);
        final int minDiskSpace = AppPrefs.getProperty(UserProp.MIN_DISK_SPACE, UserProp.MIN_DISK_SPACE_DEFAULT);
        if (freeSpace < fileSize + (minDiskSpace * 1024 * 1024L)) { //+ 30MB
            throw new NotEnoughSpaceException();
        }

        final OutputStream fos;
        if (AppPrefs.getProperty(UserProp.ANTI_FRAGMENT_FILES, UserProp.ANTI_FRAGMENT_FILES_DEFAULT)) {
            synchronized (DownloadTask.class) {
                if (isTerminated())
                    return null;
                fos = FileUtils.createEmptyFile(f, fileSize, startPosition, this);
                if (isTerminated())
                    return null;
            }
        } else {
            if (startPosition == 0)
                fos = new FileOutputStream(f);
            else {
                final RandomAccessFile raf = new RandomAccessFile(f, "rw");
                raf.seek(startPosition);
                fos = new FileOutputStream(raf.getFD());
            }
        }

        return new CountingOutputStream(fos);
    }

    protected void initBackground() {
        //client.initClient();
    }

    @Override
    public boolean isTerminated() {
        return this.isCancelled() || Thread.currentThread().isInterrupted();
    }

    @Override
    public void saveToFile(InputStream inputStream) throws Exception {
        if (inputStream == null)
            throw new NullPointerException("Input stream for saving cannot be null");
        downloadFile.setFileState(FileState.CHECKED_AND_EXISTING);
        final boolean temporary = useTemporaryFiles();

        setBuffer(new byte[AppPrefs.getProperty(UserProp.INPUT_BUFFER_SIZE, INPUT_BUFFER_SIZE)]);

        final String fileName = downloadFile.getFileName();
        File outputFile = downloadFile.getOutputFile();
        //outputFile = new File("d:/vystup.pdf");
        if (temporary) {
            this.fileAlreadyExists = checkExists();
            if (this.fileAlreadyExists == UserProp.SKIP) {
                this.cancel(true);
                return;
            }
        }

        final SpeedRegulator speedRegulator = ((MainApp) this.getApplication()).getManagerDirector().getSpeedRegulator();
        final File saveToDirectory = downloadFile.getSaveToDirectory();
        CountingOutputStream cos = null;
        OutputStream fileOutputStream = null;
        try {
            if (!saveToDirectory.exists())
                saveToDirectory.mkdirs();
            File storeFile = downloadFile.getStoreFile();
            if (downloadFile.getStoreFile() == null || !downloadFile.getStoreFile().exists()) {
                storeFile = (temporary) ? File.createTempFile(fileName + ".", ".part", saveToDirectory) : outputFile;
                downloadFile.setStoreFile(useRelativeStoreFileIfPossible ? FRDUtils.getAbsRelPath(storeFile) : storeFile);
                downloadFile.setDownloaded(0);
            }
            final long fileSize = downloadFile.getFileSize();
            Long startPositionObject = (Long) downloadFile.getProperties().get(DownloadClient.START_POSITION);
            final long startPosition;
            if (startPositionObject == null) {
                startPosition = 0L;
            } else {
                startPosition = startPositionObject;
                downloadFile.getProperties().remove(DownloadClient.START_POSITION);
            }

            try {
                cos = getFileOutputStream(storeFile, fileSize, startPosition);
                fileOutputStream = getBufferedOutputStream(cos);
                if (isTerminated()) {
                    closeFileStream(fileOutputStream);
                    checkDeleteTempFile();
                    return;
                }
                int len;
                long counter = 0;
                downloadFile.setState(DownloadState.DOWNLOADING);
                Long suppose = (Long) downloadFile.getProperties().get(DownloadClient.SUPPOSE_TO_DOWNLOAD);
                if (suppose == null)
                    suppose = downloadFile.getFileSize();
                else downloadFile.getProperties().remove(DownloadClient.SUPPOSE_TO_DOWNLOAD);

                logger.info("starting download from position " + startPosition);
                downloadFile.setDownloaded(startPosition);
                downloadFile.setRealDownload(downloadFile.getDownloaded());

                speedRegulator.addDownloading(downloadFile, this);
                //data downloading-------------------------------
                byte[] buf = getBuffer();
                while ((len = inputStream.read(buf)) != -1) {
                    fileOutputStream.write(buf, 0, len);
                    counter += len; //read from stream
                    downloadFile.setRealDownload(startPosition + cos.count);//real written to disk
                    if (isTerminated()) {
                        fileOutputStream.flush();
                        break;
                    }
                    final boolean ok = speedRegulator.takeTokens(downloadFile, len);
                    if (!ok && counter != suppose) {
                        //System.out.println("Going to sleep to slow down speed");
                        Thread.sleep(1000);
                    }
                    buf = this.buffer;
                }
                //-----------------------------------------------
                if (!isTerminated()) {
                    if (counter != suppose) {
                        logger.info("File size does not match - expected " + suppose + " but " + counter + " was downloaded");
                        throw new IOException("ErrorDuringDownload");
                    }
                } else {
                    logger.info("File downloading was terminated");
                }
            }
            catch (Exception e) {
                throw e;
            }
            catch (Throwable e) {
                throw new IOException("ErrorDuringDownload", e);
            }
            finally {
                closeFileStream(fileOutputStream);
                checkDeleteTempFile();
                if (!wasInterrupted(downloadFile.getStoreFile()) && cos != null) {
                    downloadFile.setRealDownload(startPosition + cos.count);//real written to disk
                    downloadFile.setDownloaded(downloadFile.getRealDownload());
                }
            }
        }
        finally {
            closeFileStream(fileOutputStream);
            checkDeleteTempFile();
        }

    }

    private OutputStream getBufferedOutputStream(OutputStream out) {
        return new BufferedOutputStream(out, AppPrefs.getProperty(UserProp.OUTPUT_FILE_BUFFER_SIZE, OUTPUT_FILE_BUFFER_SIZE));
    }

    protected boolean useTemporaryFiles() {
        return AppPrefs.getProperty(UserProp.USE_TEMPORARY_FILES, true);
    }

    private void closeFileStream(OutputStream fileOutputStream) {
        try {
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error closing file stream", e);
        }
    }

    private void checkDeleteTempFile() {
        final File storeFile = downloadFile.getStoreFile();
        if (wasInterrupted(storeFile)) {
            logger.info("Deleting partial file " + storeFile);
            //     storeFile.deleteOnExit();
            final boolean b = storeFile.delete();
            if (!b)
                logger.info("Deleting partial file failed (" + storeFile + ")");
            else downloadFile.setStoreFile(null);
        }
        if (downloadFile.getState() == DownloadState.CANCELLED)
            downloadFile.setDownloaded(0);
    }

    private boolean wasInterrupted(File storeFile) {
        return isTerminated() && storeFile != null && storeFile.exists() && (downloadFile.getState() == DownloadState.CANCELLED || downloadFile.getState() == DownloadState.DELETED);
    }

    @Override
    protected void cancelled() {
        if (downloadFile != null) {
            if (connectionTimeOut) {//no data in many seconds
                downloadFile.setState(DownloadState.ERROR);
                this.setServiceError(DownloadTaskError.CONNECTION_TIMEOUT);//we try reconnect
            } else {
                if (downloadFile.getState() != DownloadState.PAUSED)
                    downloadFile.setState(DownloadState.CANCELLED);
            }
        }
    }

    public void setConnectionTimeOut(boolean connectionTimeOut) {
        this.connectionTimeOut = connectionTimeOut;
    }

    protected void setSleep(final int sleep) {
        Integer oldValue, newValue;
        synchronized (this) {
            oldValue = this.sleep;
            this.sleep = sleep;
            newValue = this.sleep;
        }
        firePropertyChange("sleep", oldValue, newValue);
    }

    @Override
    protected void failed(Throwable cause) {
        if (cause instanceof IllegalArgumentException) {
            cause = new PluginImplementationException(service.getName() + " " + downloadFile.getFileUrl().toExternalForm(), cause);
        } else if (!(cause instanceof ErrorDuringDownloadingException)) {
            super.failed(cause);
            LogUtils.processException(logger, cause);
        }
        error(cause);
        if (cause instanceof PluginImplementationException) {
            logger.warning("Content from the last request\n" + client.getContentAsString());
            LogUtils.processException(logger, cause);
        }
        if (cause instanceof NotEnoughSpaceException) {
            downloadFile.setErrorMessage(getResourceMap().getString("NotEnoughSpaceException", downloadFile.getStoreFile().getParentFile()));
            setServiceError(DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR);
        } else if (cause instanceof UnknownHostException) {
            downloadFile.setErrorMessage(getResourceMap().getString("UnknownHostError"));
        } else if (cause instanceof NotRecoverableDownloadException) {
            setServiceError(DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR);
        } else if (cause instanceof YouHaveToWaitException) {
            final YouHaveToWaitException waitException = (YouHaveToWaitException) cause;
            this.youHaveToSleepSecondsTime = waitException.getHowManySecondsToWait();
            setServiceError(DownloadTaskError.YOU_HAVE_TO_WAIT_ERROR);
        }
        final boolean connectError = cause instanceof NoRouteToHostException || cause instanceof ConnectException || cause instanceof UnknownHostException;

        if (AppPrefs.getProperty(UserProp.TEST_FILE, UserProp.TEST_FILE_DEFAULT))
            updateFileState(cause, connectError);

        if (AppPrefs.getProperty(UserProp.DISABLE_CONNECTION_ON_EXCEPTION, UserProp.DISABLE_CONNECTION_ON_EXCEPTION_DEFAULT)) {
            if (connectError) {
                setServiceError(DownloadTaskError.NO_ROUTE_TO_HOST);
            }
        }

        if (getServiceError() == DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR) {
            downloadFile.setErrorAttemptsCount(0);
            downloadFile.setTimeToQueued(-1);
            downloadFile.setTimeToQueuedMax(-1);
        }
        final Application app = getApplication();
        if (isAllComplete(app)) {
            checkShutDown(app);
        }
    }

    protected void updateFileState(Throwable cause, boolean connectError) {
        if (cause instanceof URLNotAvailableAnymoreException || cause instanceof InvalidURLOrServiceProblemException) {
            downloadFile.setFileState(FileState.FILE_NOT_FOUND);
        } else {
            if (!connectError && (!(cause instanceof CaptchaEntryInputMismatchException)) && (serviceError == DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR)) {
                downloadFile.setFileState(FileState.ERROR_GETTING_INFO);
            }
        }
    }


    protected void error(Throwable cause) {

        setFileErrorMessage(cause);
        setServiceError(DownloadTaskError.GENERAL_ERROR);
        if (!(cause instanceof YouHaveToWaitException)) {
            if (AppPrefs.getProperty(UserProp.PLAY_SOUNDS_FAILED, true))
                Sound.playSound(getContext().getResourceMap().getString("errorWav"));
            downloadFile.setState(DownloadState.ERROR);
        } else downloadFile.setState(DownloadState.SLEEPING);
    }

    protected void setFileErrorMessage(Throwable cause) {
        downloadFile.setErrorMessage(Swinger.getMessageFromException(getResourceMap(), cause));
    }

    @Override
    protected void interrupted(InterruptedException e) {
        cancelled();
    }

    @Override
    protected void succeeded(Void result) {
        super.succeeded(result);
        boolean runTask = false;
        boolean overWriteFile = false;
        final File storeFile = downloadFile.getStoreFile();
        final File outputFile = downloadFile.getOutputFile();
        if (storeFile != null && storeFile.exists()) {
            if (storeFile.equals(outputFile)) //pokud zapisovaci == vystupnimu
            {
                setCompleted();
                return;
            }
            if (outputFile.exists()) {
                //Rename/Overwrite/Skip/Ask

                int property = UserProp.RENAME;
                try {
                    property = fileAlreadyExistsProperty();
                } catch (InvocationTargetException e) {
                    LogUtils.processException(logger, e);
                } catch (InterruptedException e) {
                    LogUtils.processException(logger, e);
                }
                switch (property) {
                    case UserProp.OVERWRITE:
                        runTask = true;
                        overWriteFile = true;
                        break;
                    case UserProp.RENAME:
                        runTask = true;
                        break;
                }
            } else runTask = true;
        }
        if (runTask) {
            runMoveFileTask(overWriteFile);
        } else {
            setCompleted();
            if (storeFile != null && storeFile.exists()) {
                if (storeFile.delete())
                    downloadFile.setStoreFile(null);
            }
        }
    }

    protected int checkExists() throws InvocationTargetException, InterruptedException {
        if (!downloadFile.getOutputFile().exists())
            return -2;
        return fileAlreadyExistsProperty();
    }

    private int fileAlreadyExistsProperty() throws InvocationTargetException, InterruptedException {
        if (fileAlreadyExists != -2)
            return fileAlreadyExists;
        final int[] property = new int[]{AppPrefs.getProperty(UserProp.FILE_ALREADY_EXISTS, UserProp.FILE_ALREADY_EXISTS_DEFAULT)};
        if (property[0] == UserProp.ASK) {
            synchronized (DownloadTask.class) {
                if (!EventQueue.isDispatchThread()) {
                    SwingUtilities.invokeAndWait(new Runnable() {
                        public void run() {
                            property[0] = showFileAlreadyExistsDialog();
                        }
                    });
                } else return showFileAlreadyExistsDialog();
            }
        }
        return property[0];
    }

    private int showFileAlreadyExistsDialog() {
        return Swinger.showOptionDialog(getResourceMap(), JOptionPane.QUESTION_MESSAGE, "errorMessage", "fileAlreadyExists", new String[]{"renameFile", "overWriteFile", "skipFile"}, downloadFile.getOutputFile());
    }

    private void setCompleted() {
        downloadFile.setCompleteTaskDuration(this.getExecutionDuration(TimeUnit.SECONDS));
        if (downloadFile.getState() != DownloadState.QUEUED) {
            downloadFile.setState(DownloadState.COMPLETED);
            if (AppPrefs.getProperty(UserProp.BLIND_MODE, UserProp.BLIND_MODE_DEFAULT)) {
                Sound.playSound(getContext().getResourceMap().getString("doneWav"));
            }
        }

    }

    private void runMoveFileTask(boolean overWriteFile) {
        final MoveFileTask moveFileTask = new MoveFileTask(getApplication(), downloadFile.getStoreFile(), downloadFile.getOutputFile(), true, overWriteFile, downloadFile);
        moveFileTask.addTaskListener(new TaskListener.Adapter<Void, Void>() {
            public boolean succeeded = false;

            @Override
            public void finished(TaskEvent<Void> event) {
                super.succeeded(event); //???
                if (succeeded) {
                    doAllSucceededActions();
                }
            }


            @Override
            public void succeeded(TaskEvent<Void> event) {
                this.succeeded = true;
                setCompleted();
            }

            @Override
            public void failed(TaskEvent<Throwable> event) {
                downloadFile.setState(DownloadState.ERROR);
                downloadFile.setErrorMessage(getResourceMap().getString("transferFailed", event.getValue().getMessage()));
                Sound.playSound(getContext().getResourceMap().getString("errorWav"));
            }

            @Override
            public void cancelled(TaskEvent<Void> event) {
                downloadFile.setState(DownloadState.CANCELLED);
            }

            @Override
            public void interrupted(TaskEvent<InterruptedException> event) {
                downloadFile.setState(DownloadState.CANCELLED);
            }
        });
        final MainApp app = (MainApp) this.getApplication();
        final TaskServiceManager serviceManager = app.getManagerDirector().getTaskServiceManager();
        serviceManager.getTaskService(TaskServiceManager.MOVE_FILE_SERVICE).execute(moveFileTask);
    }

    private void doAllSucceededActions() {
        final Application app = getApplication();
        final boolean allComplete = isAllComplete(app);
        if (allComplete) {
            final boolean sound = AppPrefs.getProperty(UserProp.PLAY_SOUNDS_OK, true);
            if (sound)
                Sound.playSound(getContext().getResourceMap().getString("doneWav"));
            checkShutDown(app);
        }
    }

    private void checkShutDown(Application app) {
        if (AppPrefs.getProperty(UserProp.AUTOSHUTDOWN, UserProp.AUTOSHUTDOWN_DEFAULT) != UserProp.AUTOSHUTDOWN_DISABLED) {
            app.getContext().getTaskService().execute(new CloseInTimeTask(app));
        }
    }

    private boolean isAllComplete(Application app) {
        return ((MainApp) app).getManagerDirector().getDataManager().checkComplete();
    }

    public void sleep(int seconds) throws InterruptedException {
        setSleep(0);
        downloadFile.setState(DownloadState.WAITING);
        downloadFile.setTimeToQueuedMax(seconds);
        logger.info("Going to sleep on " + (seconds) + " seconds");
        for (int i = seconds; i > 0; i--) {
            if (isTerminated())
                break;
            setSleep(i);
            Thread.sleep(1000);
        }
        if (isTerminated())
            throw new InterruptedException();
    }

    @Override
    public DownloadFile getDownloadFile() {
        return downloadFile;
    }

    @Override
    public HttpDownloadClient getClient() {
        return client;
    }

    public DownloadTaskError getServiceError() {
        return serviceError;
    }

    private void setServiceError(DownloadTaskError serviceError) {
        this.serviceError = serviceError;
    }

    public int getYouHaveToSleepSecondsTime() {
        return youHaveToSleepSecondsTime;
    }

    protected void setDownloadFile(DownloadFile downloadFile) {
        this.downloadFile = downloadFile;
    }


    void setBuffer(byte[] buffer) {
        assert buffer.length != 0;
        this.buffer = buffer;
    }

    byte[] getBuffer() {
        return this.buffer;
    }


    protected void setUseRelativeStoreFileIfPossible(boolean useRelativeStoreFileIfPossible) {
        this.useRelativeStoreFileIfPossible = useRelativeStoreFileIfPossible;
    }
}
