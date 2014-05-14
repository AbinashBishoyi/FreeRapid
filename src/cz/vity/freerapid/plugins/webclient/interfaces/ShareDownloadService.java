package cz.vity.freerapid.plugins.webclient.interfaces;

import javax.swing.*;

/**
 * Interface that represents downloading plugin for service/hoster on the web
 *
 * @author Vity
 */
public interface ShareDownloadService {
    /**
     * Getter - human readable
     *
     * @return non null string - service name
     */
    String getName();

    /**
     * Unique ID of the plugin
     *
     * @return unique ID plugin
     */
    String getId();

    /**
     * Checks whether implementation/service supports a check for file existence before downloading
     *
     * @return true if the implementation supports existence check, false otherwise
     */
    boolean supportsRunCheck();

    /**
     * This method is called before method run() and it's called only once after plugin initialization.<br />
     * Here you should make things needed necessary to run only once (like CAPTCHA recognition etc.).
     *
     * @since 0.83
     */
    void pluginInit();

    /**
     * This method is called when plugin is unloaded from main program. <br />
     * Here you should free allocated resources in plugin.
     *
     * @since 0.83
     */
    void pluginStop();

    /**
     * Returns small icon (16x16) that represents service on the web
     *
     * @return small icon, null if there is no associated icon
     */
    Icon getFaviconImage();

    /**
     * Main executable method for downloading file
     *
     * @param downloadTask file that is being downloaded
     * @throws Exception exception during downloading
     */
    void run(HttpFileDownloadTask downloadTask) throws Exception;

    /**
     * Checks for file existence before downloading
     * Shouldn't be called if the supportsRunCheck method returns false
     *
     * @param downloadTask file that is being checked
     * @throws Exception
     * @throws IllegalStateException if service does not supports checking file
     */
    void runCheck(HttpFileDownloadTask downloadTask) throws Exception;

    /**
     * Method called from user preferences to show configurable dialog to user
     *
     * @throws Exception
     */
    void showOptions() throws Exception;

    /**
     * Returns instance of plugin context to allow access UI or Locale storage
     *
     * @return instance of plugin context
     */
    PluginContext getPluginContext();

    /**
     * Sets the new plugin context for this plugin
     *
     * @param pluginContext instance of plugin context
     */
    void setPluginContext(PluginContext pluginContext);
}
