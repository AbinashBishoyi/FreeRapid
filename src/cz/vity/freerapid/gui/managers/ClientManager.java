package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.ssl.EasySSLProtocolSocketFactory;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;

import java.io.File;
import java.net.Proxy;
import java.net.ProxySelector;
import java.util.*;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class ClientManager {

    private final static String SOCKS_PREFIX_REGEXP = "^(\\$SOCKS\\$|SOCKS\\:)";

    private final static Logger logger = Logger.getLogger(ClientManager.class.getName());

    private final List<ConnectionSettings> availableConnections = new ArrayList<ConnectionSettings>(2);
    private Stack<HttpDownloadClient> workingClientsPool = new Stack<HttpDownloadClient>();
    private static final String PROXY_LIST_DEFAULT_PATH = new File(Utils.getAppPath(), "proxy.list").getAbsolutePath();
    public static final int MAX_DOWNLOADING = 9;

    private ConnectionSettings defaultConnectionSettings = new ConnectionSettings();

    private volatile int popCount;
    private final Object connectionSettingsLock = new Object();
    private final ManagerDirector managerDirector;
    private int rotate = 0;

    public ClientManager(ManagerDirector managerDirector) {
        this.managerDirector = managerDirector;
        defaultConnectionSettings.setDefault(true);
        final boolean useSystemProxies = AppPrefs.getProperty(UserProp.USE_SYSTEM_PROXIES, UserProp.USE_SYSTEM_PROXIES_DEFAULT);
        if (!useSystemProxies)
            ProxySelector.setDefault(null);
        //System.setProperty("java.net.useSystemProxies", useSystemProxies);

        popCount = 0;
        initSSL();
        updateConnectionSettings();
    }

    private void initSSL() {
        try {
            ProtocolSocketFactory sf = new EasySSLProtocolSocketFactory();
            Protocol p = new Protocol("https", sf, 443);
            Protocol.registerProtocol("https", p);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            logger.warning("SSL initialization failed - some plugins won't work");
        }
    }

    private void updateProxies() {
        if (AppPrefs.getProperty(UserProp.USE_PROXY_LIST, true)) {
            final String file = AppPrefs.getProperty(UserProp.PROXY_LIST_PATH, PROXY_LIST_DEFAULT_PATH);
            final File f = new File(file);
            if (f.exists() && f.isFile() && f.canRead()) {
                try {
                    readProxyList(f);
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
    }

    private void updateDefault() {
        if (useDefaultConnection()) {
            final boolean isEnabled = defaultConnectionSettings.isEnabled();
            defaultConnectionSettings = new ConnectionSettings();
            defaultConnectionSettings.setDefault(true);
            initDefaultProxySettings(defaultConnectionSettings);
            defaultConnectionSettings.setEnabled(isEnabled);
            availableConnections.add(defaultConnectionSettings);
        }
    }

    private boolean useDefaultConnection() {
        return AppPrefs.getProperty(UserProp.USE_DEFAULT_CONNECTION, UserProp.USE_DEFAULT_CONNECTION_DEFAULT);
    }

    private void initDefaultProxySettings(ConnectionSettings connectionSettings) {
        final String proxySetSystemDefault = System.getProperty("proxySet", "false");
        logger.info("Proxy set system default connection: " + proxySetSystemDefault);
        if (AppPrefs.getProperty(FWProp.PROXY_USE, Boolean.valueOf(proxySetSystemDefault))) {

            final String url = AppPrefs.getProperty(FWProp.PROXY_URL, System.getProperty("proxyHost", "localhost"));
            int port;
            try {
                port = Integer.valueOf(AppPrefs.getProperty(FWProp.PROXY_PORT, System.getProperty("proxyPort", "8080")));
            } catch (NumberFormatException e) {
                port = 8080;
            }

            if (AppPrefs.getProperty(FWProp.PROXY_LOGIN, false)) {
                final String userName = AppPrefs.getProperty(FWProp.PROXY_USERNAME, "");
                final String password = Utils.generateXorString(AppPrefs.getProperty(FWProp.PROXY_PASSWORD, ""));
                connectionSettings.setProxy(url, port, Proxy.Type.HTTP, userName, password);
            } else
                connectionSettings.setProxy(url, port, Proxy.Type.HTTP); // TODO: add option for SOCKS in UI
            logger.info("Setting proxy configuration ON with configuration: " + connectionSettings.toString());
        } else {
            logger.info("Setting proxy configuration OFF for default connection");
        }

    }

    private void readProxyList(File f) {
        Proxy.Type proxyType;

        final Pattern patternWhole = Pattern.compile("((\\w*)(:(.*?))?@)?(.*?):(\\d{2,5})");
        final Pattern socksPattern = Pattern.compile(SOCKS_PREFIX_REGEXP, Pattern.CASE_INSENSITIVE);
        final String[] strings = Utils.loadFile(f).split("(\\s)");
        for (String s : strings) {
            if (s.isEmpty())
                continue;
            final Matcher matcherSocks = socksPattern.matcher(s);
            if (matcherSocks.find()) {
                proxyType = Proxy.Type.SOCKS;
                s = s.substring(matcherSocks.group(1).length());
            } else
                proxyType = Proxy.Type.HTTP;

            final Matcher matcher = patternWhole.matcher(s);
            if (matcher.matches()) {
                int i = s.lastIndexOf('@');
                final String s2;
                final ConnectionSettings settings = new ConnectionSettings();
                if (i >= 0) {
                    String s1 = s.substring(0, i);
                    s2 = s.substring(i + 1);
                    i = s1.indexOf(':');
                    final String[] hostPort = s2.split(":");
                    final Integer port = Integer.valueOf(hostPort[1]);
                    if (port > 65535)
                        continue;
                    if (i > 0)
                        settings.setProxy(hostPort[0], port, proxyType, s1.substring(0, i), s1.substring(i + 1));
                    else
                        settings.setProxy(hostPort[0], port, proxyType, s1, null);
                } else {
                    s2 = s;
                    final String[] hostPort = s2.split(":");
                    final Integer port = Integer.valueOf(hostPort[1]);
                    if (port > 65535)
                        continue;
                    settings.setProxy(hostPort[0], port, proxyType);
                }
                availableConnections.add(settings);
                logger.info("Reading proxy definition " + settings.toString());
            } else
                logger.warning("String " + s + " does not match to proxy definition pattern - [username[:password@]]host:port");
        }

    }

    public List<ConnectionSettings> getAvailableConnections() {
        synchronized (connectionSettingsLock) {
            return Collections.unmodifiableList(availableConnections);
        }
    }

    public List<ConnectionSettings> getEnabledConnections() {
        synchronized (connectionSettingsLock) {
            return Collections.unmodifiableList(getEnabled());
        }
    }

    public List<ConnectionSettings> getRotatedEnabledConnections() {
        synchronized (connectionSettingsLock) {
            final List<ConnectionSettings> list = new ArrayList<ConnectionSettings>(availableConnections.size());
            for (ConnectionSettings settings : availableConnections) {
                if (settings.isEnabled() && !settings.isDefault()) {
                    list.add(settings);
                }
            }

            if (list.size() > 1) {//rotate enabled proxies
                Collections.rotate(list, rotate++);
            }
            if (useDefaultConnection() && defaultConnectionSettings.isEnabled())
                list.add(0, defaultConnectionSettings);
            return list;
        }
    }

    private List<ConnectionSettings> getEnabled() {
        final List<ConnectionSettings> list = new LinkedList<ConnectionSettings>();
        for (ConnectionSettings settings : availableConnections) {
            if (settings.isEnabled()) {
                list.add(settings);
            }
        }
        return list;
    }

    public void setConnectionEnabled(ConnectionSettings settings, boolean enabled) {
        synchronized (connectionSettingsLock) {
            settings.setEnabled(enabled);
        }
    }

    public synchronized HttpDownloadClient popWorkingClient() {
        if (popCount < MAX_DOWNLOADING) {
            ++popCount;
            if (workingClientsPool.isEmpty()) {
                return new DownloadClient();
            } else return workingClientsPool.pop();
        } else throw new IllegalStateException("Cannot pop more connections");
    }

    public synchronized void pushWorkingClient(HttpDownloadClient client) {
        --popCount;
        workingClientsPool.add(client);
    }

//    private int getMaxDownloads() {
//        return AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT);
//    }

    public void updateDefaultConnection() {
        updateConnectionSettings();
    }

    public void updateConnectionSettings() {
        synchronized (connectionSettingsLock) {
            availableConnections.clear();
            updateDefault();

            updateProxies();
        }
        final MenuManager menuManager = managerDirector.getMenuManager();
        if (menuManager != null)
            menuManager.updateConnectionSettings(getAvailableConnections());
    }

    public void updateProxyConnectionList() {
        updateConnectionSettings();
    }
}
