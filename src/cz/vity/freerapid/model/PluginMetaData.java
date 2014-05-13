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
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
final public class PluginMetaData extends AbstractBean implements Comparable<PluginMetaData> {
    private final static Logger logger = Logger.getLogger(PluginMetaData.class.getName());

    private String id;
    private boolean enabled;
    private boolean updatesEnabled;
    private Pattern supportedURL;
    private PluginDescriptor descriptor;
    private boolean hasOptions;
    private String services;
    private String www;
    private String premium;

    static {
        try {
            BeanInfo info = Introspector.getBeanInfo(DownloadFile.class);
            PropertyDescriptor[] propertyDescriptors =
                    info.getPropertyDescriptors();
            for (PropertyDescriptor pd : propertyDescriptors) {
                final Object name = pd.getName();
                if ("supportedURL".equals(name) || "descriptor".equals(name) || "www".equals(name) || "services".equals(name) || "hasOptions".equals(name) || "premium".equals(name)) {
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
        this.updatesEnabled = true;
        setPluginDescriptor(descriptor);
    }

    public void setPluginDescriptor(PluginDescriptor descriptor) {
        supportedURL = Pattern.compile(DescriptorUtils.getAttribute("urlRegex", "XX", descriptor), Pattern.CASE_INSENSITIVE);
        hasOptions = DescriptorUtils.getAttribute("hasOptions", false, descriptor);
        services = DescriptorUtils.getAttribute("services", "", descriptor);
        www = DescriptorUtils.getAttribute("www", Consts.WEBURL, descriptor);
        premium = DescriptorUtils.getAttribute("premiumFor", null, descriptor);
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

    public boolean hasPremium() {
        return premium != null;
    }

    public void setEnabled(boolean enabled) {
        boolean oldValue = this.enabled;
        this.enabled = enabled;
        firePropertyChange("enabled", oldValue, this.enabled);
    }

    public boolean isUpdatesEnabled() {
        return updatesEnabled;
    }

    public void setUpdatesEnabled(boolean updatesEnabled) {
        boolean oldValue = this.updatesEnabled;
        this.updatesEnabled = updatesEnabled;
        firePropertyChange("updatesEnabled", oldValue, this.updatesEnabled);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PluginMetaData that = (PluginMetaData) o;

        return id.equals(that.id);

    }

    public int compareTo(PluginMetaData o) {
        return this.id.compareToIgnoreCase(o.id);
    }

    public int hashCode() {
        return id.hashCode();
    }


    public String getVersion() {
        return descriptor.getVersion().toString();
    }

    public boolean isPremiumFor(PluginMetaData data) {
        return this.premium != null && data.getId().equals(premium);
    }
}