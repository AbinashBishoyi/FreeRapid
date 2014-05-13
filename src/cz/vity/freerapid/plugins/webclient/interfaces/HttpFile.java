package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;

import java.net.URL;

/**
 * @author Vity
 */
public interface HttpFile {
    long getFileSize();

    void setFileSize(long fileSize);

    DownloadState getState();

    void setState(DownloadState state);

    FileState getFileState();

    void setFileState(FileState state);

    URL getFileUrl();

    String getFileName();

    void setFileName(String fileName);

    void setNewURL(URL fileUrl);

    void setPluginID(final String pluginID);

    String getPluginID();

    String getDescription();

    void setDescription(String description);
}
