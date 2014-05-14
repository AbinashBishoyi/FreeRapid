package cz.vity.freerapid.plugins.container;

import java.net.URL;

/**
 * @author ntoskrnl
 */
public class FileInfo {

    private final URL fileUrl;
    private String fileName;
    private long fileSize = -1;
    private String description;

    public FileInfo(final URL fileUrl) {
        if (fileUrl == null) {
            throw new IllegalArgumentException("fileUrl == null");
        }
        this.fileUrl = fileUrl;
    }

    /**
     * Constructs dto with informations about file which will be added to download list
     * @param fileUrl url where locates file
     * @param fileName with this name will be file saved
     * @param fileSize file size in Bytes
     * @since 0.852
     */
    public FileInfo(URL fileUrl, String fileName, long fileSize) {
        this(fileUrl, fileName, fileSize, null);
    }

    /**
     * Constructs dto with informations about file which will be added to download list
     * @param fileUrl url where locates file
     * @param fileName with this name will be file saved
     * @param fileSize file size in Bytes
     * @param description any description of file - not necessary
     * @since 0.852
     */
    public FileInfo(URL fileUrl, String fileName, long fileSize, String description) {
        if (fileUrl == null) {
            throw new IllegalArgumentException("fileUrl == null");
        }
        this.fileUrl = fileUrl;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.description = description;
    }

    public URL getFileUrl() {
        return fileUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(final long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}
