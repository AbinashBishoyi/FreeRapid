package cz.vity.freerapid.model;

import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.utilities.DescriptorUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.java.plugin.registry.PluginDescriptor;
import org.jdesktop.application.AbstractBean;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
final public class PluginMetaData extends AbstractBean implements Comparable<PluginMetaData> {
    private final static Logger logger = Logger.getLogger(PluginMetaData.class.getName());

    private String id;
    private boolean updatesEnabled;
    private boolean enabled;
    private Pattern supportedURL;
    private PluginDescriptor descriptor;
    private boolean hasOptions;
    private String services;
    private String www;
    private boolean premium;
    private boolean favicon;
    private boolean removeCompleted;
    private boolean resumeSupported;
    private int maxParallelDownloads;
    private int priority;
    private int maxAllowedDownloads;
    private boolean clipboardMonitored;

    static {
        try {
            BeanInfo info = Introspector.getBeanInfo(DownloadFile.class);
            PropertyDescriptor[] propertyDescriptors =
                    info.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                final Object name = pd.getName();
                if ("supportedURL".equals(name) || "descriptor".equals(name) || "www".equals(name) || "services".equals(name) || "hasOptions".equals(name) || "premium".equals(name) || "favicon".equals(name) || "resumeSupported".equals(name) || "maxParallelDownloads".equals(name)) {
                    pd.setValue("transient", Boolean.TRUE);
                }
            }
        } catch (IntrospectionException e) {
            LogUtils.processException(logger, e);
        }
    }


    public PluginMetaData() {
    }

    public PluginMetaData(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        this.id = descriptor.getId();
        this.enabled = true;
        this.clipboardMonitored = true;
        this.updatesEnabled = true;
        this.priority = -1;
        this.maxAllowedDownloads = -1;
        setPluginDescriptor(descriptor);
    }

    public void setPluginDescriptor(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        supportedURL = Pattern.compile(DescriptorUtils.getAttribute("urlRegex", "XX", descriptor), Pattern.CASE_INSENSITIVE);
        hasOptions = DescriptorUtils.getAttribute("hasOptions", false, descriptor);
        services = DescriptorUtils.getAttribute("services", getId(), descriptor).toLowerCase(Locale.ENGLISH);
        www = DescriptorUtils.getAttribute("www", Consts.WEBURL, descriptor);
        premium = DescriptorUtils.getAttribute("premium", false, descriptor);
        favicon = DescriptorUtils.getAttribute("faviconImage", null, descriptor) != null;
        removeCompleted = DescriptorUtils.getAttribute("removeCompleted", false, descriptor);
        maxParallelDownloads = DescriptorUtils.getAttribute("maxDownloads", 1, descriptor);
        if (priority == -1)
            priority = DescriptorUtils.getAttribute("priority", (premium) ? 100 : 1000, descriptor);
        if (maxAllowedDownloads > 1)
            maxAllowedDownloads = Math.min(maxParallelDownloads, maxAllowedDownloads);
        else if (maxAllowedDownloads == -1) maxAllowedDownloads = maxParallelDownloads;

        resumeSupported = DescriptorUtils.getAttribute("resumeSupported", true, descriptor);
    }


    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final String url) {
        return supportedURL.matcher(url).matches();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isOptionable() {
        return hasOptions;
    }

    public String getServices() {
        return services;
    }

    public String getVendor() {
        return descriptor.getVendor();
    }

    public String getWWW() {
        return www;
    }

    public boolean isDescriptorSet() {
        return descriptor != null;
    }


    public void setEnabled(boolean enabled) {
        boolean oldValue = this.enabled;
        this.enabled = enabled;
        firePropertyChange("enabled", oldValue, enabled);
    }

    public boolean isUpdatesEnabled() {
        return updatesEnabled;
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        boolean oldValue = this.updatesEnabled;
        this.updatesEnabled = updatesEnabled;
        firePropertyChange("updatesEnabled", oldValue, updatesEnabled);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginMetaData that = (PluginMetaData) o;

        return id.equals(that.id);

    }

    @Override
    public int compareTo(PluginMetaData o) {
        return this.id.compareToIgnoreCase(o.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }


    public String getVersion() {
        return descriptor.getVersion().toString();
    }

    public boolean isPremium() {
        return premium;
    }

    public boolean hasFavicon() {
        return favicon;
    }

    public boolean isRemoveCompleted() {
        return removeCompleted;
    }

    public String toString() {
        return "PluginMetaData{" +
                "id='" + id + '\'' + " Version=" + getVersion() +
                '}';
    }

    public boolean isResumeSupported() {
        return resumeSupported;
    }


    public int getMaxParallelDownloads() {
        return maxParallelDownloads;
    }

    public int getPriority() {
        return priority;
    }


    public int getMaxAllowedDownloads() {
        return maxAllowedDownloads;
    }

    public boolean isClipboardMonitored() {
        return clipboardMonitored;
    }

    public void setPriority(int value) {
        Integer oldValue = this.priority;
        this.priority = value;
        firePropertyChange("priority", oldValue, value);
    }

    public void setMaxAllowedDownloads(int maxAllowedDownloads) {
        int oldValue = this.maxAllowedDownloads;
        this.maxAllowedDownloads = maxAllowedDownloads;
        firePropertyChange("maxAllowedDownloads", oldValue, maxAllowedDownloads);
    }

    public void setClipboardMonitored(boolean clipboardMonitored) {
        final boolean oldValue = this.clipboardMonitored;
        this.clipboardMonitored = clipboardMonitored;
        firePropertyChange("clipboardMonitored", oldValue, clipboardMonitored);
    }
}
