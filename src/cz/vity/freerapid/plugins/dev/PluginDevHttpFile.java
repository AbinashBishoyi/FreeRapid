package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;

import java.io.File;
import java.net.URL;
import java.util.logging.Logger;

/**
 * Implementation of HttpFile - for developing purposes
 *
 * @author Vity
 */
class PluginDevHttpFile implements HttpFile {
    private final static Logger logger = Logger.getLogger(PluginDevHttpFile.class.getName());

    /**
     * Field fileSize
     */
    private long fileSize;

    /**
     * Field state  - default state is DownloadState.PAUSED
     */
    private DownloadState state = DownloadState.PAUSED;
    /**
     * Field fileName
     */
    private String fileName;
    /**
     * Field downloaded
     */
    private long downloaded = 0;
    /**
     * Field sleep
     */
    private int sleep;
    /**
     * Field averageSpeed
     */
    private float averageSpeed;
    /**
     * Field speed
     */
    private long speed;
    /**
     * Field errorMessage
     */
    private String errorMessage;
    /**
     * Field fileUrl
     */
    private URL fileUrl = null;
    /**
     * Field saveToDirectory
     */
    private File saveToDirectory;
    /**
     * Field description
     */
    private String description;
    /**
     * Field fileState
     */
    private FileState fileState;
    /**
     * Field serviceID - plugin ID
     */
    private String serviceID;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getFileSize() {
        return fileSize;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
        logger.info("Setting file size to " + fileSize);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DownloadState getState() {
        return state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setState(DownloadState state) {
        this.state = state;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getFileName() {
        return fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getDownloaded() {
        return downloaded;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
        logger.info("Setting downloaded to " + downloaded);
    }

    /**
     * Getter for property 'sleep'.
     *
     * @return Value for property 'sleep'.
     */
    public int getSleep() {
        return sleep;
    }

    /**
     * Setter for property 'sleep'.
     *
     * @param sleep Value to set for property 'sleep'.
     */
    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    /**
     * Getter for property 'averageSpeed'.
     *
     * @return Value for property 'averageSpeed'.
     */
    public float getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * Setter for property 'averageSpeed'.
     *
     * @param averageSpeed Value to set for property 'averageSpeed'.
     */
    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    /**
     * Getter for property 'speed'.
     *
     * @return Value for property 'speed'.
     */
    public long getSpeed() {
        return speed;
    }

    /**
     * Setter for property 'speed'.
     *
     * @param speed Value to set for property 'speed'.
     */
    public void setSpeed(long speed) {
        this.speed = speed;
    }

    /**
     * Getter for property 'errorMessage'.
     *
     * @return Value for property 'errorMessage'.
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Setter for property 'errorMessage'.
     *
     * @param errorMessage Value to set for property 'errorMessage'.
     */
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    /**
     * {@inheritDoc}
     */
    public URL getFileUrl() {
        return fileUrl;
    }

    /**
     * Setter for property 'fileUrl'.
     *
     * @param fileUrl Value to set for property 'fileUrl'.
     */
    public void setFileUrl(URL fileUrl) {
        this.fileUrl = fileUrl;
        logger.info("Setting new fileUrl to " + fileUrl);
    }

    /**
     * Getter for property 'saveToDirectory'.
     *
     * @return Value for property 'saveToDirectory'.
     */
    public File getSaveToDirectory() {
        return saveToDirectory;
    }

    /**
     * Setter for property 'saveToDirectory'.
     *
     * @param saveToDirectory Value to set for property 'saveToDirectory'.
     */
    public void setSaveToDirectory(File saveToDirectory) {
        this.saveToDirectory = saveToDirectory;
    }

    /**
     * {@inheritDoc}
     */
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    public FileState getFileState() {
        return fileState;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileState(FileState state) {
        this.fileState = state;
        logger.info("setting new file state to " + state);
    }

    /**
     * {@inheritDoc}
     */
    public void setNewURL(URL fileUrl) {
        setFileUrl(fileUrl);
    }


    /**
     * {@inheritDoc}
     */
    public void setPluginID(String pluginID) {
        this.serviceID = pluginID;
        logger.info("setting new serviceID to " + pluginID);
    }

    /**
     * {@inheritDoc}
     */
    public String getPluginID() {
        return serviceID;
    }
}
