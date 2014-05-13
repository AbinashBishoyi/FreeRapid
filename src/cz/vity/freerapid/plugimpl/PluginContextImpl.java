package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.plugins.webclient.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.OptionsDialogSupport;
import cz.vity.freerapid.plugins.webclient.PluginContext;

/**
 * @author Ladislav Vitasek
 */
public class PluginContextImpl implements PluginContext {

    private OptionsDialogSupport optionsDialogSupport;
    private ConfigurationStorageSupport storageSupport;


    public ConfigurationStorageSupport getConfigurationStorageSupport() {
        return storageSupport;
    }

    private PluginContextImpl(OptionsDialogSupport optionsDialogSupport, ConfigurationStorageSupport storageSupport) {
        this.optionsDialogSupport = optionsDialogSupport;
        this.storageSupport = storageSupport;
    }

    public OptionsDialogSupport getDialogSupport() {
        return optionsDialogSupport;
    }

    public static PluginContext create(OptionsDialogSupport optionsDialogSupport, ConfigurationStorageSupport storageSupport) {
        return new PluginContextImpl(optionsDialogSupport, storageSupport);
    }

}
