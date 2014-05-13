package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.webclient.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.OptionsDialogSupport;
import cz.vity.freerapid.plugins.webclient.PluginContext;

/**
 * @author Ladislav Vitasek
 */
public class PluginDevContext implements PluginContext {
    public PluginDevContext() {

    }

    public OptionsDialogSupport getDialogSupport() {
        return null;
    }

    public ConfigurationStorageSupport getConfigurationStorageSupport() {
        return new ConfigurationStorageSupportImpl();
    }
}
