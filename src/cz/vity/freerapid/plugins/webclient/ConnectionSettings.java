package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.swing.EDTPropertyChangeSupport;
import org.jdesktop.application.Application;

import java.beans.PropertyChangeListener;

/**
 * @author Vity
 */
public class ConnectionSettings {

    private String userName;
    private String password;
    private String proxyURL;
    private int proxyPort;
    private boolean proxySet;
    private boolean enabled = true;
    private String defaultConnectionLabel;

    private final EDTPropertyChangeSupport pcs;

    public ConnectionSettings() {
        //setProxy("localhost", 8081);
        defaultConnectionLabel = Application.getInstance().getContext().getResourceMap().getString("defaultConnection");
        pcs = new EDTPropertyChangeSupport(this);
    }

    public void setProxy(String proxyURL, int proxyPort, String userName, String password) {
        this.proxyURL = proxyURL;
        this.proxyPort = proxyPort;
        this.userName = userName;
        this.password = password;
        proxySet = true;
    }

    public void setProxy(String proxyURL, int proxyPort) {
        this.proxyURL = proxyURL;
        this.proxyPort = proxyPort;
        this.proxySet = true;
    }

    public boolean isProxySet() {
        return proxySet;
    }

    public int getProxyPort() {
        return proxyPort;
    }

    public String getProxyURL() {
        return proxyURL;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public boolean hasUserName() {
        return userName != null;
    }

    @Override
    public String toString() {
        if (isProxySet()) {
            final String url = getProxyURL() + ":" + getProxyPort();
            if (hasUserName()) {
                return getUserName() + "@" + url;
            } else return url;
        } else {
            return defaultConnectionLabel;
        }

    }

//    @SuppressWarnings({"RedundantIfStatement"})
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || getClass() != o.getClass()) return false;
//
//        ConnectionSettings that = (ConnectionSettings) o;
//
//        if (proxyPort != that.proxyPort) return false;
//        if (proxySet != that.proxySet) return false;
//        if (!proxyURL.equals(that.proxyURL)) return false;
//
//        return true;
//    }
//
//    @Override
//    public int hashCode() {
//        int result = proxyURL.hashCode();
//        result = 31 * result + proxyPort;
//        result = 31 * result + (proxySet ? 1 : 0);
//        return result;
//    }

    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionSettings that = (ConnectionSettings) o;

        if (proxyPort != that.proxyPort) return false;
        if (proxySet != that.proxySet) return false;
        if (!proxyURL.equalsIgnoreCase(that.proxyURL)) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

        return true;
    }


    public int hashCode() {
        int result;
        result = (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (proxyURL != null ? proxyURL.toLowerCase().hashCode() : 0);
        result = 31 * result + proxyPort;
        result = 31 * result + (proxySet ? 1 : 0);
        return result;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        final boolean oldValue = this.enabled;
        this.enabled = enabled;
        pcs.firePropertyChange("enabled", oldValue, enabled);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }
}
