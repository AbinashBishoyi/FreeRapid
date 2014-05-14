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
