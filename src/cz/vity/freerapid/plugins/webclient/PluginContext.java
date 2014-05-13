package cz.vity.freerapid.plugins.webclient;

/**
 * @author Ladislav Vitasek
 */
public interface PluginContext {
    OptionsDialogSupport getDialogSupport();

    ConfigurationStorageSupport getConfigurationStorageSupport();
}
