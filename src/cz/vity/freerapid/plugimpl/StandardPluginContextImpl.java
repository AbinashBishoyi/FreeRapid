package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.plugins.webclient.interfaces.PluginContext;

/**
 * @author Ladislav Vitasek
 */
public class StandardPluginContextImpl implements PluginContext {
    /**
     * instance of dialog support
     */
    private DialogSupport dialogSupport;
    /**
     * instance of storage locale support
     */
    private ConfigurationStorageSupport storageSupport;


    /**
     * {@inheritDoc}
     */
    public ConfigurationStorageSupport getConfigurationStorageSupport() {
        return storageSupport;
    }

    private StandardPluginContextImpl(DialogSupport dialogSupport, ConfigurationStorageSupport storageSupport) {
        this.dialogSupport = dialogSupport;
        this.storageSupport = storageSupport;
    }

    /**
     * {@inheritDoc}
     */
    public DialogSupport getDialogSupport() {
        return dialogSupport;
    }

    /**
     * Factory method for creating new Plugin context
     *
     * @param dialogSupport  instance of dialog support
     * @param storageSupport instance of local confiration storage support
     * @return new instance of PluginContext
     */
    public static PluginContext create(DialogSupport dialogSupport, ConfigurationStorageSupport storageSupport) {
        return new StandardPluginContextImpl(dialogSupport, storageSupport);
    }

}
