package cz.vity.freerapid.core.tasks;

/**
 * @author Ladislav Vitasek
 */
public class ConnectionSettings {

    private String userName;
    private String password;
    private String proxyURL;
    private int proxyPort;
    private boolean proxySet;

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

    @Override
    public String toString() {

        return super.toString();
    }
}
