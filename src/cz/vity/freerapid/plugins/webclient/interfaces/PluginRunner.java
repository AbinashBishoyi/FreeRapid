package cz.vity.freerapid.plugins.webclient.interfaces;

/**
 * @author Ladislav Vitasek
 */
public interface PluginRunner {

    void init(ShareDownloadService shareDownloadService, HttpFileDownloader downloader) throws Exception;

    void run() throws Exception;

    void runCheck() throws Exception;

}
