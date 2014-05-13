package cz.vity.freerapid.plugins.webclient;

import javax.swing.*;

/**
 * @author Vity
 */
public interface ShareDownloadService {
    String getName();

    boolean supportsURL(String url);

    int getMaxDownloadsFromOneIP();

    Icon getFaviconImage();

    Icon getSmallImage();

    Icon getBigImage();

    void run(HttpFileDownloader downloader) throws Exception;
}
