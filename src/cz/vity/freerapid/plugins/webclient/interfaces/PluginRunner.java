package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.HttpFileDownloader;

/**
 * @author Ladislav Vitasek
 */
public interface PluginRunner<C extends ShareDownloadService> {

    void init(C shareDownloadService, HttpFileDownloader downloader) throws Exception;

    void run(HttpFileDownloader downloader) throws Exception;

    void runCheck(HttpFileDownloader downloader) throws Exception;

}
