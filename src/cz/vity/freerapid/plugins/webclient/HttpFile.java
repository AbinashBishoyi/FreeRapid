package cz.vity.freerapid.plugins.webclient;

import java.net.URL;

/**
 * @author Vity
 */
public interface HttpFile {
    long getFileSize();

    void setFileSize(long fileSize);

    DownloadState getState();

    void setState(DownloadState state);

    URL getFileUrl();

    String getFileName();

    void setFileName(String fileName);

    void setErrorMessage(String errorMessage);

    String getErrorMessage();

    void setFileUrl(URL fileUrl);

    String getDescription();

    void setDescription(String description);
}
