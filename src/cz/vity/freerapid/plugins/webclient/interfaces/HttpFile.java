package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;

import java.io.File;
import java.net.URL;
import java.util.Map;

/**
 * A JavaBean that represents file that is downloaded from the Internet.
 *
 * @author Vity
 */
public interface HttpFile {
    /**
     * Method to get whole file size of the give file.
     *
     * @return <=0 if file is not known
     */
    long getFileSize();

    /**
     * Setter to update file size (of complete file)
     *
     * @param fileSize new value of file size
     */
    void setFileSize(long fileSize);

    /**
     * Returns current download state of file.
     *
     * @return download state of file
     */
    DownloadState getState();

    /**
     * Setter
     *
     * @param state new download state during downloading
     */
    void setState(DownloadState state);

    /**
     * Getter
     *
     * @return returns actual file state (its existence state on the server)
     */
    FileState getFileState();

    /**
     * Setter
     *
     * @param state new value of file state
     */
    void setFileState(FileState state);

    /**
     * URL to file that is given by user
     *
     * @return URL of the downloaded file
     */
    URL getFileUrl();

    /**
     * Real physical file name of the downloaded file
     * File name is initialized from given URL to some value.
     * The final filename is updated by during makeFinalRequestForFile method
     *
     * @return this method should never return null value
     * @see HttpDownloadClient#makeFinalRequestForFile(org.apache.commons.httpclient.HttpMethod, HttpFile, boolean);
     */
    String getFileName();

    /**
     * Sets file name
     *
     * @param fileName file name
     */
    void setFileName(String fileName);

    /**
     * Sets new URL for this file for downloading.
     * Filename is extracted from this url. Filesize is set to 0.
     *
     * @param fileUrl new URL
     */
    void setNewURL(URL fileUrl);

    /**
     * Sets plugin ID associated with this file
     * If pluginID is an empty string, the plugin ID selected automatically.
     *
     * @param pluginID plugin ID
     */
    void setPluginID(final String pluginID);

    /**
     * Plugin ID associated with this file
     *
     * @return
     */
    String getPluginID();

    /**
     * Description for this file (password etc.)
     *
     * @return description for this file
     */
    String getDescription();

    /**
     * Description for this file
     *
     * @param description new value description
     */
    void setDescription(String description);

    /**
     * Returns value - how many bytes were already downloaded for this file.
     *
     * @return file size in bytes
     */
    long getDownloaded();

    /**
     * Sets how many bytes were already downloaded for this file
     *
     * @param downloaded file size in bytes
     */
    void setDownloaded(long downloaded);

    /**
     * Returns target save directory
     *
     * @return directory
     */
    File getSaveToDirectory();

    /**
     * Getter for property 'properties'.
     *
     * @return Value for property 'properties'.
     */
    Map<String, Object> getProperties();

    /**
     * File that is stored on the disk. It can be also a temporary file
     *
     * @return
     * @since 0.83
     */
    File getStoreFile();

    /**
     * @param storeFile
     * @since 0.83
     */
    void setStoreFile(File storeFile);

    /**
     * @return
     * @since 0.83
     */
    long getRealDownload();

    /**
     * @param resumeSupported
     * @since 0.83
     */
    void setResumeSupported(boolean resumeSupported);

    /**
     * Checks whether resume download is enabled
     *
     * @return
     * @since 0.83
     */
    boolean isResumeSupported();
}
