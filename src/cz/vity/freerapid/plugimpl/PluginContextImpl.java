package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;

/**
 * @author Ladislav Vitasek
 */
public class PluginContextImpl implements PluginContext {

    private DialogSupport dialogSupport;
    private ConfigurationStorageSupport storageSupport;


    public ConfigurationStorageSupport getConfigurationStorageSupport() {
        return storageSupport;
    }

    private PluginContextImpl(DialogSupport dialogSupport, ConfigurationStorageSupport storageSupport) {
        this.dialogSupport = dialogSupport;
        this.storageSupport = storageSupport;
    }

    public DialogSupport getDialogSupport() {
        return dialogSupport;
    }

    public static PluginContext create(DialogSupport dialogSupport, ConfigurationStorageSupport storageSupport) {
        return new PluginContextImpl(dialogSupport, storageSupport);
    }

}
