package cz.vity.freerapid.gui.managers;


import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.model.DownloadFile;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.File;
import java.net.URL;

/**
 * @author Vity
 */
@Entity
final public class FileHistoryItem implements Identifiable<Long> {

    @Id()
    @GeneratedValue()
    private Long dbId;

    private URL url;
    private long finishedTime;

    private File outputFile;
    private String description;
    private String fileType;

    private long fileSize;
    private String fileName;
    private float averageSpeed;

    private String shareDownloadServiceID;
    private String connection;


    public FileHistoryItem() {

    }

    public FileHistoryItem(DownloadFile file, File savedTo) {
        this.url = file.getFileUrl();
        this.finishedTime = System.currentTimeMillis();
        this.outputFile = savedTo;
        this.description = file.getDescription();
        this.fileName = file.getFileName();
        this.fileSize = file.getFileSize();
        this.fileType = file.getFileType();
        averageSpeed = file.getAverageSpeed();
        this.shareDownloadServiceID = file.getPluginID();
        connection = file.getConnectionSettings().toString();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(long finishedTime) {
        this.finishedTime = finishedTime;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(File outputFile) {
        this.outputFile = outputFile;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public float getAverageSpeed() {
        return averageSpeed;
    }

    public String getConnection() {
        return connection;
    }

    public void setConnection(String connection) {
        this.connection = connection;
    }

    public void setAverageSpeed(float averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public String getShareDownloadServiceID() {
        return shareDownloadServiceID;
    }

    public void setShareDownloadServiceID(String shareDownloadServiceID) {
        this.shareDownloadServiceID = shareDownloadServiceID;
    }


    public String toString() {
        return "FileHistoryItem{" +
                "dbId=" + dbId +
                " url=" + url +
                '}';
    }


    @Override
    public Long getIdentificator() {
        return dbId;
    }
}
