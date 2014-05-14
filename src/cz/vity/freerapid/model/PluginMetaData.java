package cz.vity.freerapid.model;

import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.gui.managers.interfaces.Identifiable;
import cz.vity.freerapid.utilities.DescriptorUtils;
import org.java.plugin.registry.PluginDescriptor;
import org.jdesktop.application.AbstractBean;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Locale;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
@Entity
final public class PluginMetaData extends AbstractBean implements Identifiable<Long>, Comparable<PluginMetaData> {
    private final static Logger logger = Logger.getLogger(PluginMetaData.class.getName());
    private final static Pattern NOPE_URL_MATCHER = Pattern.compile("&&&XXX&&&");

    @Id
    @GeneratedValue
    private Long dbId;

    //persisted info
    private String id;
    private boolean updatesEnabled;
    private boolean enabled;
    private int pluginPriority;
    private int maxAllowedDownloads;
    private boolean clipboardMonitored;
    private boolean removeCompleted;

    @Transient
    private Pattern supportedURL;
    @Transient
    private PluginDescriptor descriptor;
    @Transient
    private boolean hasOptions;
    @Transient
    private String services;
    @Transient
    private String www;
    @Transient
    private boolean premium;
    @Transient
    private boolean favicon;
    @Transient
    private boolean resumeSupported;
    @Transient
    private int maxParallelDownloads;
    @Transient
    private boolean libraryPlugin;

//    static {
//        try {
//            BeanInfo info = Introspector.getBeanInfo(DownloadFile.class);
//            PropertyDescriptor[] propertyDescriptors =
//                    info.getPropertyDescriptors();
//            for (PropertyDescriptor pd : propertyDescriptors) {
//                final Object name = pd.getName();
//                if ("supportedURL".equals(name) || "descriptor".equals(name) || "www".equals(name) || "services".equals(name) || "hasOptions".equals(name) || "premium".equals(name) || "favicon".equals(name) || "resumeSupported".equals(name) || "maxParallelDownloads".equals(name) || "libraryPlugin".equals(name)) {
//                    pd.setValue("transient", Boolean.TRUE);
//                }
//            }
//        } catch (IntrospectionException e) {
//            LogUtils.processException(logger, e);
//        }
//    }

    public PluginMetaData() {
        //default values
        this.enabled = true;
        this.clipboardMonitored = true;
        this.updatesEnabled = true;
        this.pluginPriority = -1;
        this.maxAllowedDownloads = -1;
        this.libraryPlugin = false;
    }

    public PluginMetaData(PluginDescriptor descriptor) {
        this();
        this.descriptor = descriptor;
        this.id = descriptor.getId();
        setPluginDescriptor(descriptor);
    }

    public void setPluginDescriptor(PluginDescriptor descriptor) {
        this.descriptor = descriptor;
        hasOptions = DescriptorUtils.getAttribute("hasOptions", false, descriptor);
        services = DescriptorUtils.getAttribute("services", getId(), descriptor).toLowerCase(Locale.ENGLISH);
        www = DescriptorUtils.getAttribute("www", Consts.WEBURL, descriptor);
        premium = DescriptorUtils.getAttribute("premium", false, descriptor);
        favicon = DescriptorUtils.getAttribute("faviconImage", null, descriptor) != null;
        removeCompleted = DescriptorUtils.getAttribute("removeCompleted", false, descriptor);
        maxParallelDownloads = DescriptorUtils.getAttribute("maxDownloads", 1, descriptor);
        libraryPlugin = DescriptorUtils.getAttribute("libraryPlugin", false, descriptor) || maxParallelDownloads == 0;
        if (pluginPriority == -1)
            pluginPriority = DescriptorUtils.getAttribute("priority", (premium) ? 1000 : 100, descriptor);
        if (libraryPlugin) {
            supportedURL = NOPE_URL_MATCHER;
        } else {
            supportedURL = Pattern.compile(DescriptorUtils.getAttribute("urlRegex", "&&&XX&&&", descriptor), Pattern.CASE_INSENSITIVE);
        }
        if (maxAllowedDownloads > 1) {
            maxAllowedDownloads = Math.min(maxParallelDownloads, maxAllowedDownloads);
        } else {
            if (maxAllowedDownloads == -1) maxAllowedDownloads = maxParallelDownloads;
        }


        resumeSupported = DescriptorUtils.getAttribute("resumeSupported", true, descriptor);
    }

    public boolean isLibraryPlugin() {
        return libraryPlugin;
    }

    /**
     * Overuje, zda je dane URL podporovane mezi pluginy
     *
     * @param url
     * @return vraci v pripade, ze nejaky plugin podporuje dane URL, jinak false
     */
    public boolean isSupported(final String url) {
        return !isLibraryPlugin() && supportedURL.matcher(url).matches();
    }


    public Long getIdentificator() {
        return dbId;
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
                "dbId=" + dbId +
                " id='" + id + '\'' + " Version=" + getVersion() +
                '}';
    }

    public boolean isResumeSupported() {
        return resumeSupported;
    }


    public int getMaxParallelDownloads() {
        return maxParallelDownloads;
    }

    public int getPluginPriority() {
        return pluginPriority;
    }

    @Deprecated
    /**
     * because of XMLserialization in 0.85a3 - to 0.85a4
     */
    public void setPriority(int value) {
        //this.setPluginPriority(value);   
    }


    public int getMaxAllowedDownloads() {
        return maxAllowedDownloads;
    }

    public boolean isClipboardMonitored() {
        return clipboardMonitored;
    }

    public void setPluginPriority(int value) {
        Integer oldValue = this.pluginPriority;
        this.pluginPriority = value;
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
