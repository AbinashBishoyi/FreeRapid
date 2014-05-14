package cz.vity.freerapid.gui.dialogs;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.xmlimport.ver1.Plugin;

/**
 * @author Ladislav Vitasek
 */
public class WrappedPluginData {
    private boolean selected;
    private String author;

    private DownloadFile httpFile;
    private String id;
    private String version;
    private String services;
    private boolean aNew;
    private boolean isPluginInUse;
    private boolean toBeDeleted;

    public WrappedPluginData(boolean selected, DownloadFile httpFile, Plugin pluginInfo) {
        this.selected = selected;
        this.httpFile = httpFile;
        this.id = pluginInfo.getId();
        this.author = pluginInfo.getVendor();
        version = pluginInfo.getVersion();
        services = pluginInfo.getServices();
        isPluginInUse = false;
        toBeDeleted = false;
    }

    public boolean isSelected() {
        return selected;
    }

    public String getID() {
        return id;
    }

    public String getAuthor() {
        return this.author;
    }

    public DownloadFile getHttpFile() {
        return httpFile;
    }

    public String getStatus() {
        return httpFile.getState().toString();
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getVersion() {
        return version;
    }

    public String getServices() {
        return services;
    }

    public void setNew(boolean aNew) {
        this.aNew = aNew;
    }

    public boolean isNew() {
        return aNew;
    }

    public boolean isPluginInUse() {
        return isPluginInUse;
    }

    public void setPluginInUse(boolean pluginInUse) {
        isPluginInUse = pluginInUse;
    }

    public boolean isToBeDeleted() {
        return toBeDeleted;
    }

    public void setToBeDeleted(boolean toBeDeleted) {
        this.toBeDeleted = toBeDeleted;
    }
}
