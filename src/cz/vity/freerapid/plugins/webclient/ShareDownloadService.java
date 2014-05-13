package cz.vity.freerapid.plugins.webclient;

/**
 * @author Vity
 */
public interface ShareDownloadService {
    String getName();

    String getId();

    int getMaxDownloadsFromOneIP();

//    Icon getFaviconImage();
//
//    Icon getSmallImage();
//
//    Icon getBigImage();

    void run(HttpFileDownloader downloader) throws Exception;
}
