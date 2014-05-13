package cz.vity.freerapid.model;

import org.jdesktop.application.AbstractBean;

/**
 * @author Ladislav Vitasek
 */
public class SupportedPlugin extends AbstractBean {
    private String id;
    private boolean enabled;
    private boolean updatesEnabled;

    public SupportedPlugin() {
    }

    public SupportedPlugin(String id) {
        this.id = id;
        this.enabled = true;
        this.updatesEnabled = true;
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

        SupportedPlugin that = (SupportedPlugin) o;

        return id.equals(that.id);

    }

    public int hashCode() {
        return id.hashCode();
    }

    //    static {
//        try {
//            BeanInfo info = Introspector.getBeanInfo(DownloadFile.class);
//            PropertyDescriptor[] propertyDescriptors =
//                    info.getPropertyDescriptors();
//            for (PropertyDescriptor pd : propertyDescriptors) {
//                final Object name = pd.getName();
//                if ("supportedURL".equals(name)) {
//                    pd.setValue("transient", Boolean.TRUE);
//                }
//            }
//        } catch (IntrospectionException e) {
//            LogUtils.processException(logger, e);
//        }
//    }


}
