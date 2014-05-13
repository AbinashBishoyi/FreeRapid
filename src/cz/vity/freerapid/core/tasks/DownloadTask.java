package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.TaskServiceManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.*;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Sound;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jdesktop.application.Application;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DownloadTask extends CoreTask<Void, Long> implements HttpFileDownloader {
    private final static Logger logger = Logger.getLogger(DownloadTask.class.getName());
    protected final HttpDownloadClient client;
    protected final DownloadFile downloadFile;
    private ShareDownloadService service;
    private long speedInBytes;
    private float averageSpeed;
    private long counter;
    private Integer sleep = 0;
    private File outputFile;
    private File storeFile;
    private static java.util.Timer timer = new java.util.Timer(true);
    private DownloadTaskError serviceError;

    private int youHaveToSleepSecondsTime = 0;
    private String captchaResult;
    private static final int NO_DATA_TIMEOUT_LIMIT = 75;
    private static final int INPUT_BUFFER_SIZE = 50000;
    private static final int OUTPUT_FILE_BUFFER_SIZE = 600000;
    private volatile boolean connectionTimeOut;


    public DownloadTask(Application application, HttpDownloadClient client, DownloadFile downloadFile, ShareDownloadService service) {
        super(application);
        this.client = client;
        this.downloadFile = downloadFile;
        this.service = service;
        this.serviceError = DownloadTaskError.NO_ERROR;
        this.setInputBlocker(null);
        this.setUserCanCancel(true);
        this.youHaveToSleepSecondsTime = 0;
        this.connectionTimeOut = false;
        this.speedInBytes = 0;
        this.averageSpeed = 0;
    }

    protected Void doInBackground() throws Exception {
        client.getHTTPClient().getHttpConnectionManager().closeIdleConnections(0);
//        final GetMethod getMethod = client.getGetMethod("http://data.idnes.cz/televize/img/1/1466255.jpg");
//        InputStream stream = client.makeRequestForFile(getMethod);
//        final BufferedImage image = loadCaptcha(stream);
//        final String s = askForCaptcha(image);
//        System.out.println("s = " + s);
        downloadFile.setDownloaded(0);
        final int seconds = AppPrefs.getProperty(UserProp.ERROR_SLEEP_TIME, UserProp.ERROR_SLEEP_TIME_DEFAULT);
        if (seconds > 0)
            sleep(seconds);
        downloadFile.setState(DownloadState.GETTING);
        service.run(this);//run plugin
        service = null;
        return null;
    }

    protected OutputStream getFileOutputStream(File f, long fileSize) throws NotEnoughSpaceException, FileNotFoundException {
        if (f.getParentFile().getFreeSpace() < fileSize) {
            throw new NotEnoughSpaceException();
        }
        return new BufferedOutputStream(new FileOutputStream(f), OUTPUT_FILE_BUFFER_SIZE);
    }

    protected void initBackground() {
        //client.initClient();
    }

    public boolean isTerminated() {
        return this.isCancelled() || Thread.currentThread().isInterrupted();
    }

    public void saveToFile(InputStream inputStream) throws Exception {
        boolean temporary = AppPrefs.getProperty(UserProp.USE_TEMPORARY_FILES, true);

        final byte[] buffer = new byte[INPUT_BUFFER_SIZE];
        final OutputStream[] fileOutputStream = new OutputStream[]{null};
        final String fileName = downloadFile.getFileName();
        outputFile = downloadFile.getOutputFile();
        //outputFile = new File("d:/vystup.pdf");
        final File saveToDirectory = downloadFile.getSaveToDirectory();
        if (!saveToDirectory.exists())
            saveToDirectory.mkdirs();

        storeFile = (temporary) ? File.createTempFile(fileName + ".", ".part", saveToDirectory) : outputFile;
        final long fileSize = downloadFile.getFileSize();

        if (temporary)
            storeFile.deleteOnExit();

        try {
            try {
                fileOutputStream[0] = getFileOutputStream(storeFile, fileSize);
                int len;
                counter = 0;
                downloadFile.setState(DownloadState.DOWNLOADING);
                setSpeed(0);
                final long time = System.currentTimeMillis();

                timer.schedule(new TimerTask() {
                    private long lastSize = 0;
                    private int noDataTimeOut = 0; //10 seconds to timeout

                    public void run() {

                        if (isTerminated() || downloadFile.getState() != DownloadState.DOWNLOADING) {
                            this.cancel();
                            return;
                        }

                        final long speed = counter - lastSize;

                        setSpeed(speed);

                        if (speed == 0) {
                            if (++noDataTimeOut >= NO_DATA_TIMEOUT_LIMIT) { //X seconds with no data
                                logger.info("Cancelling download - no downloaded data during " + NO_DATA_TIMEOUT_LIMIT + " seconds");
                                connectionTimeOut = true;
//                                closeFileStream(fileOutputStream[0]);
//                                fileOutputStream[0] = null;

                                this.cancel();//radsi driv
                                DownloadTask.this.cancel(true);
                                return;
                            }
                        } else {
                            noDataTimeOut = 0;
                            lastSize = counter;
                            setDownloaded(counter);
                        }

                        final long current = System.currentTimeMillis();
                        final double l = (current - time) / 1000.0;
                        if (l == 0) {
                            setAverageSpeed(0.0F);
                        } else
                            setAverageSpeed((float) ((float) counter / l));
                    }
                }, 0, 1000);

                //data downloading-------------------------------
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream[0].write(buffer, 0, len);
                    counter += len;
                    if (isTerminated()) {
                        fileOutputStream[0].flush();
                        break;
                    }
                }
                //-----------------------------------------------

                if (!isTerminated()) {
                    if (counter != fileSize)
                        throw new IOException("Error during download.\nStream was closed unexpectedly.\nFile is not complete");
                    setDownloaded(fileSize);//100%
                } else {
                    logger.info("File downloading was terminated");
                }
            }
            catch (Exception e) {
                if (storeFile != null && storeFile.exists()) {
                    closeFileStream(fileOutputStream[0]);
                    fileOutputStream[0] = null;

                    storeFile.delete();
                }
                throw e;
            }
            finally {
//                if (timer != null)
//                    timer.cancel();
//                try {
//                    if (inputStream != null)
//                        inputStream.close();
//                } catch (IOException e) {
//                    LogUtils.processException(logger, e);
//                }
                closeFileStream(fileOutputStream[0]);
                fileOutputStream[0] = null;
            }
        }
        finally {
            setSpeed(0);
            setAverageSpeed(0);
            checkDeleteTempFile();
        }

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
        if (isTerminated() && storeFile.exists()) {
            logger.info("Deleting partial file " + storeFile);
            final boolean b = storeFile.delete();
            if (!b)
                logger.info("Deleting partial file failed (" + storeFile + ")");
        }
    }

    @Override
    protected void cancelled() {
        if (connectionTimeOut) {//no data in many seconds
            downloadFile.setState(DownloadState.ERROR);
            this.setServiceError(DownloadTaskError.CONNECTION_TIMEOUT);//we try reconnect
        } else
            downloadFile.setState(DownloadState.CANCELLED);
        downloadFile.setDownloaded(0);
        setSpeed(0);
        setAverageSpeed(0);
    }

    protected void setSpeed(final long speedInBytes) {
        Long oldValue, newValue;
        synchronized (this) {
            oldValue = this.speedInBytes;
            this.speedInBytes = speedInBytes;
            newValue = this.speedInBytes;
        }
        firePropertyChange("speed", oldValue, newValue);
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

    protected void setDownloaded(long counter) {
        firePropertyChange("downloaded", 0, counter);
    }

    protected void setAverageSpeed(float avgSpeed) {
        final float oldValue, newValue;
        synchronized (this) {
            oldValue = this.averageSpeed;
            this.averageSpeed = avgSpeed;
            newValue = this.averageSpeed;
        }
        firePropertyChange("averageSpeed", oldValue, newValue);
    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
        error(cause);
        if (cause instanceof NotEnoughSpaceException) {
            Swinger.showErrorMessage(getResourceMap(), "NotEnoughSpaceException", (storeFile != null) ? storeFile : "");
        } else if (cause instanceof UnknownHostException) {
            downloadFile.setErrorMessage("Unknown host error - connection problem?");
        } else
        if (cause instanceof URLNotAvailableAnymoreException || cause instanceof PluginImplementationException || cause instanceof CaptchaEntryInputMismatchException || cause instanceof NoRouteToHostException) {
            setServiceError(DownloadTaskError.NOT_RECOVERABLE_DOWNLOAD_ERROR);
        } else if (cause instanceof YouHaveToWaitException) {
            final YouHaveToWaitException waitException = (YouHaveToWaitException) cause;
            this.youHaveToSleepSecondsTime = waitException.getHowManySecondsToWait();
            setServiceError(DownloadTaskError.YOU_HAVE_TO_WAIT_ERROR);
        }

    }

    private void error(Throwable cause) {
        downloadFile.setState(DownloadState.ERROR);
        downloadFile.setErrorMessage(cause.getMessage());
        setServiceError(DownloadTaskError.GENERAL_ERROR);
        if (!(cause instanceof YouHaveToWaitException)) {
            if (AppPrefs.getProperty(UserProp.PLAY_SOUNDS_FAILED, true))
                Sound.playSound("error.wav");
        }
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
        if (storeFile != null && storeFile.exists()) {
            if (storeFile.equals(outputFile)) //pokud zapisovaci == vystupnimu
            {
                downloadFile.setState(DownloadState.COMPLETED);
                return;
            }
            if (outputFile.exists()) {
                //Rename/Overwrite/Skip/Ask

                int property = AppPrefs.getProperty(UserProp.FILE_ALREADY_EXISTS, UserProp.FILE_ALREADY_EXISTS_DEFAULT);
                if (property == UserProp.ASK) {
                    property = Swinger.showOptionDialog(getResourceMap(), JOptionPane.QUESTION_MESSAGE, "fileAlreadyExists", new String[]{"renameFile", "overWriteFile", "skipFile"}, outputFile);
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
            downloadFile.setState(DownloadState.COMPLETED);
            if (storeFile != null && storeFile.exists()) {
                storeFile.delete();
            }
        }
    }

    private void runMoveFileTask(boolean overWriteFile) {
        final MoveFileTask moveFileTask = new MoveFileTask(getApplication(), storeFile, downloadFile.getOutputFile(), true, overWriteFile, downloadFile);
        moveFileTask.addTaskListener(new TaskListener.Adapter<Void, Void>() {
            public boolean succeeded = false;

            @Override
            public void finished(TaskEvent<Void> event) {
                super.succeeded(event);
                if (succeeded) {
                    final Application app = getApplication();
                    final boolean allComplete = ((MainApp) app).getManagerDirector().getDataManager().checkComplete();
                    if (allComplete) {
                        final boolean sound = AppPrefs.getProperty(UserProp.PLAY_SOUNDS_OK, true);
                        if (sound)
                            Sound.playSound("done.wav");
                        if (AppPrefs.getProperty(UserProp.CLOSE_WHEN_COMPLETED, false)) {
                            app.getContext().getTaskService().execute(new CloseInTimeTask(app));
                        }
                    }
                }
            }


            @Override
            public void succeeded(TaskEvent<Void> event) {
                this.succeeded = true;
                downloadFile.setState(DownloadState.COMPLETED);
            }

            @Override
            public void failed(TaskEvent<Throwable> event) {
                downloadFile.setState(DownloadState.ERROR);
                //noinspection ThrowableResultOfMethodCallIgnored
                downloadFile.setErrorMessage(getResourceMap().getString("transferFailed", event.getValue().getMessage()));
                Sound.playSound("error.wav");
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
    }

    public HttpFile getDownloadFile() {
        return downloadFile;
    }

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

    public BufferedImage loadCaptcha(InputStream inputStream) throws FailedToLoadCaptchaPictureException {
        if (inputStream == null)
            throw new NullPointerException("Input stream for captcha is null");
        try {
            return ImageIO.read(inputStream);
        } catch (IOException e) {
            throw new FailedToLoadCaptchaPictureException("Reading captcha picture failed", e);
        }
    }

    public String askForCaptcha(final BufferedImage image) throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            public void run() {
                captchaResult = "";
                while (captchaResult.isEmpty()) {
                    captchaResult = (String) JOptionPane.showInputDialog(null, "Insert what you see", "Insert Captcha", JOptionPane.PLAIN_MESSAGE, new ImageIcon(image), null, null);
                    if (captchaResult == null)
                        break;
                }
                image.flush();
            }
        });
        return captchaResult;
    }

    public String getCaptcha(final String url) throws FailedToLoadCaptchaPictureException {
        final GetMethod getMethod = client.getGetMethod(url);
        try {
            InputStream stream = client.makeRequestForFile(getMethod);
            if (stream == null)
                throw new FailedToLoadCaptchaPictureException();
            return askForCaptcha(loadCaptcha(stream));
        } catch (FailedToLoadCaptchaPictureException e) {
            throw e;
        } catch (Exception e) {
            throw new FailedToLoadCaptchaPictureException(e);
        }
    }
}
