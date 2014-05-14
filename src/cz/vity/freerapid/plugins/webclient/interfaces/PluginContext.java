package cz.vity.freerapid.plugins.webclient.interfaces;

/**
 * Context of the plugin - depends on the state where it's running.
 * plugin api for developing returns different instances.
 *
 * @author Ladislav Vitasek
 */
public interface PluginContext {
    /**
     * Returns instance of DialogSupport for getting access to UI
     *
     * @return instance of DialogSupport
     */
    DialogSupport getDialogSupport();

    /**
     * Returns instance of ConfigurationStorageSupport for getting access to local storage
     *
     * @return instance of ConfigurationStorageSupport
     */
    ConfigurationStorageSupport getConfigurationStorageSupport();

    /**
     * Returns instance of maintain support
     *
     * @return from a place where you can maintain queue
     */
    MaintainQueueSupport getQueueSupport();

}
