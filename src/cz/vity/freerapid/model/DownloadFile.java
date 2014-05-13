package cz.vity.freerapid.model;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.HttpFile;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.AbstractBean;

import java.beans.*;
import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

/**
 * @author Vity
 *         Hashcode a Equals nepretizeny na url (muze byt vic souboru s touto url, neni unikatni),
 *         pocita se s tim v ProcessManageru pri force download.
 */
public class DownloadFile extends AbstractBean implements PropertyChangeListener, HttpFile {
    private final static Logger logger = Logger.getLogger(DownloadFile.class.getName());

    private long fileSize;
    private DownloadTask task = null;
    private volatile DownloadState state = DownloadState.PAUSED;
    private String fileName;
    private long downloaded = 0;
    private int sleep;
    private float averageSpeed;
    private long speed;
    private String errorMessage;
    private URL fileUrl = null;
    private File saveToDirectory;
    private String description;
    private String fileType;
    private int timeToQueued = -1;
    private int timeToQueuedMax = -1;
    private int errorAttemptsCount;
    private String shareDownloadServiceID;
    private String serviceName = null;

    static {
        try {
            BeanInfo info = Introspector.getBeanInfo(DownloadFile.class);
            PropertyDescriptor[] propertyDescriptors =
                    info.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                if ("task".equals(pd.getName()) || "speed".equals(pd.getName()) || "averageSpeed".equals(pd.getName())) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        } catch (IntrospectionException e) {
            LogUtils.processException(logger, e);
        }
    }

    public DownloadFile() {//XMLEncoder
        //logger.info("Konstruktor empty");
    }

    public DownloadFile(URL fileUrl, File saveToDirectory, String description) {
        this.fileUrl = fileUrl;
        this.saveToDirectory = saveToDirectory;
        this.description = description;
        this.fileSize = -1;
        final String urlStr = fileUrl.toExternalForm();
        this.fileName = FileTypeIconProvider.identifyFileName(urlStr);
        //this.downloaded = 0;
        resetErrorAttempts();
        this.sleep = -1;
        this.averageSpeed = 0;
        this.speed = 0;
        this.timeToQueued = -1;
        setFileType(FileTypeIconProvider.identifyFileType(fileName));
    }

    public File getSaveToDirectory() {
        return saveToDirectory;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        long oldValue = this.fileSize;
        this.fileSize = fileSize;
        firePropertyChange("fileSize", oldValue, this.fileSize);
    }

    public DownloadTask getTask() {
        return task;
    }

    public void setTask(DownloadTask task) {
        if (task == null) {
            if (this.task != null)
                this.task.removePropertyChangeListener(this);
        } else {
            task.addPropertyChangeListener(this);
        }
        //System.out.println("task = " + task);
        this.task = task;
    }

    public DownloadState getState() {
        return state;
    }

    public void setState(DownloadState state) {
        if (this.state == DownloadState.DELETED)
            return;
        DownloadState oldValue = this.state;
        this.state = state;
        logger.info("Setting state to " + state.toString());
        firePropertyChange("state", oldValue, this.state);
    }

    public URL getFileUrl() {
        return fileUrl;
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        String oldValue = this.fileName;
        this.fileName = fileName;
        setFileType(FileTypeIconProvider.identifyFileType(this.fileName));
        firePropertyChange("fileName", oldValue, this.fileName);
    }

    @Override
    public String toString() {
        return fileUrl.toString();
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        long oldValue = this.downloaded;
        this.downloaded = downloaded;
        logger.fine("setting downloaded to " + downloaded);
        firePropertyChange("downloaded", oldValue, this.downloaded);
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("downloaded".equals(evt.getPropertyName())) {
            this.setDownloaded((Long) evt.getNewValue());
        } else if ("sleep".equals(evt.getPropertyName())) {
            this.setSleep((Integer) evt.getNewValue());
        } else if ("speed".equals(evt.getPropertyName())) {
            this.setSpeed((Long) evt.getNewValue());
        } else if ("averageSpeed".equals(evt.getPropertyName())) {
            this.setAverageSpeed((Float) evt.getNewValue());
        }
    }

    public void setSpeed(long speed) {
        long oldValue = this.speed;
        this.speed = speed;
        firePropertyChange("speed", oldValue, this.speed);
    }

    public long getSpeed() {
        return speed;
    }

    public void setSleep(int sleep) {
        int oldValue = this.sleep;
        this.sleep = sleep;
        firePropertyChange("sleep", oldValue, this.sleep);
    }

    public void setAverageSpeed(float averageSpeed) {
        float oldValue = this.averageSpeed;
        this.averageSpeed = averageSpeed;
        firePropertyChange("averageSpeed", oldValue, this.averageSpeed);
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getSleep() {
        return sleep;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public String getServiceName() {
        if (serviceName == null) {
            return "";
        } else return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public File getOutputFile() {
        return new File(this.getSaveToDirectory(), fileName);
    }

    public void setFileUrl(URL fileUrl) {
        this.fileUrl = fileUrl;
    }

    public void setSaveToDirectory(File saveToDirectory) {
        this.saveToDirectory = saveToDirectory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        String oldValue = this.description;
        this.description = description;
        firePropertyChange("description", oldValue, this.description);
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        //String oldValue = this.fileType;
        this.fileType = fileType;
        //firePropertyChange("fileType", oldValue, this.fileType);
    }

    public String getShareDownloadServiceID() {
        return shareDownloadServiceID;
    }


    public void setTimeToQueued(int i) {
        int oldValue = this.timeToQueued;
        this.timeToQueued = i;
        firePropertyChange("timeToQueued", oldValue, this.timeToQueued);
    }

    public int getTimeToQueued() {
        return timeToQueued;
    }

    public void setErrorAttemptsCount(int errorAttemptsCount) {
        this.errorAttemptsCount = errorAttemptsCount;
    }

    public int getErrorAttemptsCount() {
        return errorAttemptsCount;
    }

    public void resetErrorAttempts() {
        this.errorAttemptsCount = AppPrefs.getProperty(UserProp.ERROR_ATTEMPTS_COUNT, UserProp.ERROR_ATTEMPTS_COUNT_DEFAULT);
    }

    public void setShareDownloadServiceID(String shareDownloadServiceID) {
        this.shareDownloadServiceID = shareDownloadServiceID;
        this.serviceName = shareDownloadServiceID.toLowerCase();
    }

    public int getTimeToQueuedMax() {
        return timeToQueuedMax;
    }

    public void setTimeToQueuedMax(int timeToQueuedMax) {
        this.timeToQueuedMax = timeToQueuedMax;
    }

    public void resetSpeed() {
        setSpeed(0);
        setAverageSpeed(0);
    }
}
