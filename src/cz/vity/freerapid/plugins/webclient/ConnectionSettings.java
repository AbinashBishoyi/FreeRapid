package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.swing.EDTPropertyChangeSupport;
import org.jdesktop.application.Application;

import java.beans.PropertyChangeListener;

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
    private final EDTPropertyChangeSupport pcs;

    /**
     * Field isDefault
     */
    private boolean isDefault = false;


    /**
     * Constructor - creates a new ConnectionSettings instance.
     */
    public ConnectionSettings() {
        //setProxy("localhost", 8081);
        defaultConnectionLabel = Application.getInstance().getContext().getResourceMap().getString("defaultConnection");
        pcs = new EDTPropertyChangeSupport(this);
    }

    /**
     * Method setProxy sets proxy settings
     *
     * @param proxyURL  url of proxy server
     * @param proxyPort proxy server port
     * @param userName  access user name
     * @param password  access user password
     */
    public void setProxy(String proxyURL, int proxyPort, String userName, String password) {
        this.userName = userName;
        this.password = password;
        setProxy(proxyURL, proxyPort);
    }

    /**
     * @param proxyURL
     * @param proxyPort
     */
    public void setProxy(String proxyURL, int proxyPort) {
        this.proxyURL = proxyURL;
        this.proxyPort = proxyPort;
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
            final String url = getProxyURL() + ":" + getProxyPort();
            if (hasUserName()) {
                return getUserName() + "@" + url;
            } else return url;
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
        if (proxyURL != null ? !proxyURL.equals(that.proxyURL) : that.proxyURL != null) return false;
        if (userName != null ? !userName.equals(that.userName) : that.userName != null) return false;

        return true;
    }


    @Override
    public int hashCode() {
        int result;
        result = (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (proxyURL != null ? proxyURL.toLowerCase().hashCode() : 0);
        result = 31 * result + proxyPort;
        result = 31 * result + (proxySet ? 1 : 0);
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
     * @see cz.vity.freerapid.swing.EDTPropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    /**
     * Method delegations
     *
     * @param listener
     * @see cz.vity.freerapid.swing.EDTPropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    /**
     * Method delegation.
     *
     * @return
     * @see cz.vity.freerapid.swing.EDTPropertyChangeSupport#getPropertyChangeListeners()
     */
    public PropertyChangeListener[] getPropertyChangeListeners() {
        return pcs.getPropertyChangeListeners();
    }


    /**
     * Method delegation
     *
     * @param propertyName name of bean property
     * @param listener     listener to add
     * @see cz.vity.freerapid.swing.EDTPropertyChangeSupport#addPropertyChangeListener(String, java.beans.PropertyChangeListener)
     */
    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(propertyName, listener);
    }

    /**
     * Method delegation
     *
     * @param propertyName bean property name
     * @param listener     listener to remove from property
     * @see cz.vity.freerapid.swing.EDTPropertyChangeSupport#removePropertyChangeListener(String, java.beans.PropertyChangeListener)
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
