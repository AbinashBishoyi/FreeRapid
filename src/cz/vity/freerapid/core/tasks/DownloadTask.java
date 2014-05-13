package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.exceptions.NotEnoughSpaceException;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Sound;
import org.jdesktop.application.*;

import javax.swing.*;
import java.io.*;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public abstract class DownloadTask extends CoreTask<Void, Long> {
    private final static Logger logger = Logger.getLogger(DownloadTask.class.getName());
    protected final DownloadClient client;
    protected final DownloadFile downloadFile;
    private long speedInBytes;
    private long counter;
    private Integer sleep = 0;
    private File outputFile;
    private File storeFile;
    private static final String MOVE_FILE_SERVICE = "moveFile";


    public DownloadTask(Application application, DownloadClient client, DownloadFile downloadFile) {
        super(application);
        this.client = client;
        this.downloadFile = downloadFile;
        this.setInputBlocker(null);
        this.setUserCanCancel(true);
    }

    protected OutputStream getFileOutputStream(File f, long fileSize) throws NotEnoughSpaceException, FileNotFoundException {
        if (f.getParentFile().getFreeSpace() < fileSize) {
            throw new NotEnoughSpaceException();
        }
        return new FileOutputStream(f);
    }

    protected void initBackground() {
        //client.initClient();
    }

    protected void saveToFile(InputStream inputStream) throws Exception {
        boolean temporary = AppPrefs.getProperty(UserProp.USE_TEMPORARY_FILES, true);

        final byte[] buffer = new byte[100000];
        OutputStream fileOutputStream = null;
        java.util.Timer speed = null;
        final String fileName = downloadFile.getFileName();
        outputFile = downloadFile.getOutputFile();
        //outputFile = new File("d:/vystup.pdf");
        storeFile = (temporary) ? File.createTempFile(fileName + ".", ".part", downloadFile.getSaveToDirectory()) : outputFile;
        final long fileSize = downloadFile.getFileSize();
        try {
            try {
                fileOutputStream = getFileOutputStream(storeFile, fileSize);
                int len;
                counter = 0;
                downloadFile.setState(DownloadState.DOWNLOADING);
                setSpeed(0);
                final long time = System.currentTimeMillis();
                speed = new java.util.Timer();
                speed.schedule(new TimerTask() {
                    private long lastSize;

                    public void run() {
                        final long speed = counter - lastSize;
                        setSpeed(speed);
                        final long current = System.currentTimeMillis();
                        final double l = (current - time) / 1000.0;
                        lastSize = counter;
                        setDownloaded(counter);
                        if (l == 0) {
                            setAverageSpeed(0.0F);
                        } else
                            setAverageSpeed((float) ((float) counter / l));
                    }
                }, 0, 1000);
                while ((len = inputStream.read(buffer)) != -1) {
                    fileOutputStream.write(buffer, 0, len);
                    counter += len;
                    if (isCancelled()) {
                        fileOutputStream.flush();
                        break;
                    }
                }
                if (!isCancelled()) {
                    if (counter != fileSize)
                        throw new IOException("Error during download. File is not complete");
                    setDownloaded(fileSize);//100%
                }
            }
            catch (Exception e) {
                if (storeFile != null && storeFile.exists())
                    storeFile.delete();
                throw e;
            }
            finally {
                if (speed != null)
                    speed.cancel();
//                try {
//                    if (inputStream != null)
//                        inputStream.close();
//                } catch (IOException e) {
//                    LogUtils.processException(logger, e);
//                }
                try {
                    if (fileOutputStream != null) {
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    logger.log(Level.SEVERE, "Error closing file stream", e);
                }
            }
        }
        finally {
            if (isCancelled()) {
                logger.info("Deleting partial file " + storeFile);
                final boolean b = storeFile.delete();
                if (!b)
                    logger.info("Deleting partial file failed (" + storeFile + ")");
            }
        }

    }

    @Override
    protected void cancelled() {
        downloadFile.setState(DownloadState.CANCELLED);
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
        firePropertyChange("averageSpeed", 0, avgSpeed);
    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
        error(cause);
        if (cause instanceof NotEnoughSpaceException) {
            Swinger.showErrorMessage(getResourceMap(), "NotEnoughSpaceException", (storeFile != null) ? storeFile : "");
        }
    }

    private void error(Throwable cause) {
        downloadFile.setState(DownloadState.ERROR);
        downloadFile.setErrorMessage(cause.getMessage());
        Sound.playSound("error.wav");
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

                int property = AppPrefs.getProperty(UserProp.FILE_ALREADY_EXISTS, UserProp.ASK);
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
        final MoveFileTask moveFileTask = new MoveFileTask(getApplication(), storeFile, outputFile, true, overWriteFile);
        moveFileTask.addTaskListener(new TaskListener.Adapter<Void, Void>() {
            public boolean succeeded = false;

            @Override
            public void finished(TaskEvent<Void> event) {
                super.succeeded(event);
                if (succeeded)
                    ((MainApp) getApplication()).getManagerDirector().getDataManager().checkComplete();
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
        final ApplicationContext context = getApplication().getContext();
        TaskService service = context.getTaskService(MOVE_FILE_SERVICE);
        if (service == null) {
            service = new TaskService(MOVE_FILE_SERVICE, new ThreadPoolExecutor(
                    1,   // corePool size
                    1,  // maximumPool size
                    1L, TimeUnit.SECONDS,  // non-core threads time to live
                    new LinkedBlockingQueue<Runnable>()));
            context.addTaskService(service);
        }
        service.execute(moveFileTask);
    }

    protected void sleep(int seconds) throws InterruptedException {
        setSleep(0);
        downloadFile.setState(DownloadState.WAITING);

        logger.info("Going to sleep on " + (seconds) + " seconds");
        for (int i = seconds; i > 0; i--) {
            if (isCancelled())
                break;
            setSleep(i);
            Thread.sleep(1000);
        }
    }

    public DownloadFile getDownloadFile() {
        return downloadFile;
    }

    public DownloadClient getClient() {
        return client;
    }
}
