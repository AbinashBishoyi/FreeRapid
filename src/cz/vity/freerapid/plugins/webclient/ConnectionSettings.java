package cz.vity.freerapid.plugins.webclient;

import org.jdesktop.application.Application;

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

    public ConnectionSettings() {
        //setProxy("localhost", 8081);
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
            return Application.getInstance().getContext().getResourceMap().getString("defaultConnection");
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

        return true;
    }

    public int hashCode() {
        int result;
        result = (userName != null ? userName.hashCode() : 0);
        result = 31 * result + (proxyURL != null ? proxyURL.hashCode() : 0);
        result = 31 * result + proxyPort;
        result = 31 * result + (proxySet ? 1 : 0);
        return result;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
