package cz.vity.freerapid.plugins.webclient;

import org.jdesktop.application.Application;

import javax.swing.event.SwingPropertyChangeSupport;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.net.Proxy;

/**
 * @author Vity
 */
public class ConnectionSettings {

    /**
     * Field userName
     */
    private String userName;
    /**
     * Field password
     */
    private String password;
    /**
     * Field proxyURL
     */
    private String proxyURL;
    /**
     * Field proxyPort
     */
    private int proxyPort;
    /**
     * Field proxySet
     */
    private boolean proxySet;
    /**
     * Field enabled -
     */
    private boolean enabled = true;
    /**
     * Field defaultConnectionLabel
     */
    private String defaultConnectionLabel;

    /**
     * Field pcs
     */
    private final PropertyChangeSupport pcs;

    /**
     * Field isDefault
     */
    private boolean isDefault = false;

    /**
     * Field proxyType
     */
    private Proxy.Type proxyType = Proxy.Type.DIRECT;


    /**
     * Constructor - creates a new ConnectionSettings instance.
     */
    public ConnectionSettings() {
        //setProxy("localhost", 8081);
        defaultConnectionLabel = Application.getInstance().getContext().getResourceMap().getString("defaultConnection");
        pcs = new SwingPropertyChangeSupport(this);
        proxyType = Proxy.Type.DIRECT;
    }

    /**
     * Method setProxy sets proxy settings
     *
     * @param proxyURL  url of proxy server
     * @param proxyPort proxy server port
     * @param userName  access user name
     * @param password  access user password
     */
    public void setProxy(String proxyURL, int proxyPort, Proxy.Type proxyType, String userName, String password) {
        this.userName = userName;
        this.password = password;
        setProxy(proxyURL, proxyPort, proxyType);
    }

    /**
     * @param proxyURL
     * @param proxyPort
     */
    public void setProxy(String proxyURL, int proxyPort) {
        setProxy(proxyURL, proxyPort, Proxy.Type.HTTP);
    }

    /**
     * @param proxyURL
     * @param proxyPort
     */
    public void setProxy(String proxyURL, int proxyPort, Proxy.Type proxyType) {
        this.proxyURL = proxyURL;
        this.proxyPort = proxyPort;
        this.proxyType = proxyType;
        this.proxySet = true;
    }

    /**
     * Method isProxySet returns the proxySet of this ConnectionSettings object.
     *
     * @return the proxySet (type boolean) of this ConnectionSettings object.
     */
    public boolean isProxySet() {
        return proxySet;
    }


    /**
     * Method getProxyType returns the ProxyType of this ConnectionSettings object.
     *
     * @return the proxyType (type ProxyType) of this ConnectionSettings object.
     */
    public Proxy.Type getProxyType() {
        return proxyType;
    }

    /**
     * Method getProxyPort returns the proxyPort of this ConnectionSettings object.
     *
     * @return the proxyPort (type int) of this ConnectionSettings object.
     */
    public int getProxyPort() {
        return proxyPort;
    }

    /**
     * Method getProxyURL returns the proxyURL of this ConnectionSettings object.
     *
     * @return the proxyURL (type String) of this ConnectionSettings object.
     */
    public String getProxyURL() {
        return proxyURL;
    }

    /**
     * Method getPassword returns the password of this ConnectionSettings object.
     *
     * @return the password (type String) of this ConnectionSettings object.
     */
    public String getPassword() {
        return password;
    }

    /**
     * Method getUserName returns the userName of this ConnectionSettings object.
     *
     * @return the userName (type String) of this ConnectionSettings object.
     */
    public String getUserName() {
        return userName;
    }

    /**
     * Method checks whether user name for proxy server was set.
     *
     * @return boolean true if user name was set (not null), false otherwise
     */
    public boolean hasUserName() {
        return userName != null;
    }

    @Override
    public String toString() {
        if (isProxySet()) {
            StringBuilder builder = new StringBuilder();
            if (hasUserName()) {
                builder.append(getUserName()).append('@');
            }
            builder.append(getProxyURL()).append(':').append(getProxyPort()).append(' ').append('(').append(this.getProxyType()).append(')');
            return builder.toString();
        } else {
            return defaultConnectionLabel;
        }
    }

    @Override
    @SuppressWarnings({"RedundantIfStatement"})
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConnectionSettings that = (ConnectionSettings) o;

        if (proxyPort != that.proxyPort) return false;
        if (proxySet != that.proxySet) return false;
        if (proxyType != that.proxyType) return false;
        if (proxyURL != null ? !proxyURL.equalsIgnoreCase(that.proxyURL) : that.proxyURL != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;


        return true;
    }

    public void setProxyType(Proxy.Type proxyType) {
        this.proxyType = proxyType;
    }

    @Override
    public int hashCode() {
        int result;
        result = (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (proxyURL != null ? proxyURL.toLowerCase().hashCode() : 0);
        result = 31 * result + proxyPort;
        result = 31 * result + (proxySet ? 1 : 0);
        result = 31 * result + (proxyType != null ? proxyType.hashCode() : 0);
        return result;
    }

    /**
     * Method isEnabled returns the enabled state of this ConnectionSettings object - if it should be used together
     *
     * @return the enabled (type boolean) of this ConnectionSettings object.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Method setEnabled sets its enabled state for this ConnectionSettings object.
     *
     * @param enabled the enabled state of this ConnectionSettings object.
     */
    public void setEnabled(boolean enabled) {
        final boolean oldValue = this.enabled;
        this.enabled = enabled;
        pcs.firePropertyChange("enabled", oldValue, enabled);
    }

    /**
     * Method delegations
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Method delegations
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Method delegation.
     *
     * @return
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }


    /**
     * Method delegation
     *
     * @param propertyName name of bean property
     * @param listener     listener to add
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Method delegation
     *
     * @param propertyName bean property name
     * @param listener     listener to remove from property
     */
    public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(propertyName, listener);
    }

    /**
     * Method isDefault checks whether this connection is marked as default.
     *
     * @return the default (type boolean) of this ConnectionSettings object.
     */
    public boolean isDefault() {
        return isDefault;
    }


    /**
     * Method setDefault sets a flag whether this connection is marked as default.
     *
     * @param aDefault boolean value
     */
    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }
}
