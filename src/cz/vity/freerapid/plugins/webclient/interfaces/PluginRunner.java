package cz.vity.freerapid.plugins.webclient.interfaces;

/**
 * Interface that represents "worker" - main code of the plugin - for downloading file
 *
 * @author Ladislav Vitasek
 */
public interface PluginRunner {

    /**
     * Initialization of runner - this method is called as first before methods run() and runCheck()
     *
     * @param shareDownloadService download service that is associated to this runner
     * @param downloadTask         file downloader
     * @throws Exception if initialization failed
     */
    void init(ShareDownloadService shareDownloadService, HttpFileDownloadTask downloadTask) throws Exception;

    /**
     * Main "bussiness logic" of the plugin - process of downloading
     * If no exception is thrown, everything is being considered to be OK - file state is set to FileState.EXISTING_AND_CHECKED automatically
     *
     * @throws Exception
     * @see cz.vity.freerapid.plugins.webclient.DownloadState
     * @see cz.vity.freerapid.plugins.webclient.FileState
     */
    void runCheck() throws Exception;

    /**
     * Main "bussiness logic" of the plugin - process of file downloading from service
     * If no exception is thrown, everything is being considered to be OK - file download state is set to COMPLETED automatically
     *
     * @throws Exception if anything went wrong
     * @see cz.vity.freerapid.plugins.webclient.DownloadState
     * @see cz.vity.freerapid.plugins.webclient.FileState
     */
    void run() throws Exception;

}
