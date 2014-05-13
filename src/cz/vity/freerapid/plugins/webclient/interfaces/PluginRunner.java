package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.HttpFileDownloader;

/**
 * @author Ladislav Vitasek
 */
public interface PluginRunner {

    void init(ShareDownloadService shareDownloadService, HttpFileDownloader downloader) throws Exception;

    void run(HttpFileDownloader downloader) throws Exception;

    void runCheck(HttpFileDownloader downloader) throws Exception;

}
