package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.HttpFileDownloader;

import javax.swing.*;

/**
 * @author Vity
 */
public interface ShareDownloadService {
    String getName();

    String getId();

    int getMaxDownloadsFromOneIP();

    boolean supportsRunCheck();

    Icon getFaviconImage();
//
//    Icon getSmallImage();
//
//    Icon getBigImage();

    void run(HttpFileDownloader downloader) throws Exception;

    void runCheck(HttpFileDownloader downloader) throws Exception;

    void showOptions() throws Exception;

    PluginContext getPluginContext();

    void setPluginContext(PluginContext pluginContext);
}
