package cz.vity.freerapid.plugins.dev;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;

/**
 * @author Ladislav Vitasek
 */
public class PluginDevContext implements PluginContext {
    public PluginDevContext() {

    }

    public DialogSupport getDialogSupport() {
        return null;
    }

    public ConfigurationStorageSupport getConfigurationStorageSupport() {
        return new ConfigurationStorageSupportImpl();
    }
}
