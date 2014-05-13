package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.HttpFile;

import java.io.File;
import java.net.URL;

/**
 * @author Vity
 */
class PluginDevHttpFile implements HttpFile {
    private long fileSize;

    private DownloadState state = DownloadState.PAUSED;
    private String fileName;
    private long downloaded = 0;
    private int sleep;
    private float averageSpeed;
    private long speed;
    private String errorMessage;
    private URL fileUrl = null;
    private File saveToDirectory;
    private String description;
    private FileState fileState;
    private String serviceID;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public DownloadState getState() {
        return state;
    }

    public void setState(DownloadState state) {
        this.state = state;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(long downloaded) {
        this.downloaded = downloaded;
    }

    public int getSleep() {
        return sleep;
    }

    public void setSleep(int sleep) {
        this.sleep = sleep;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public long getSpeed() {
        return speed;
    }

    public void setSpeed(long speed) {
        this.speed = speed;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public URL getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(URL fileUrl) {
        this.fileUrl = fileUrl;
    }

    public File getSaveToDirectory() {
        return saveToDirectory;
    }

    public void setSaveToDirectory(File saveToDirectory) {
        this.saveToDirectory = saveToDirectory;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public FileState getFileState() {
        return fileState;
    }

    public void setFileState(FileState state) {
        this.fileState = state;
    }

    public void setNewURL(URL fileUrl) {
        setFileUrl(fileUrl);
    }


    public void setPluginID(String pluginID) {
        this.serviceID = pluginID;
    }

    public String getPluginID() {
        return serviceID;
    }
}
