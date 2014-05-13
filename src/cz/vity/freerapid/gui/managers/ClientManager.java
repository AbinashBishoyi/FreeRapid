package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.utilities.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class ClientManager {
    private final static Logger logger = Logger.getLogger(ClientManager.class.getName());

    private final List<ConnectionSettings> availableConnections = new ArrayList<ConnectionSettings>(2);
    private final int maxClients;
    private Collection<HttpDownloadClient> clients;
    private static final String PROXY_LIST_DEFAULT_PATH = new File(Utils.getAppPath(), "proxy.list").getAbsolutePath();


    public ClientManager() {
        if (AppPrefs.getProperty(UserProp.USE_DEFAULT_CONNECTION, true))
            availableConnections.add(new ConnectionSettings());

        maxClients = AppPrefs.getProperty(UserProp.MAX_DOWNLOADS_AT_A_TIME, 5);
        //String input = "    vity:heslo@exfort.org:8787 vity2:angor@@exfort2.org:8788  exfort3.org:5478  pavel@exfort.org:564 exfort5.org";

        if (AppPrefs.getProperty(UserProp.USE_PROXY_LIST, true)) {
            final String file = AppPrefs.getProperty(UserProp.PROXY_LIST_PATH, PROXY_LIST_DEFAULT_PATH);
            final File f = new File(file);
            if (f.exists() && f.isFile() && f.canRead()) {
                readProxyList(f);
            }
        }
    }

    private void readProxyList(File f) {
        final Pattern patternWhole = Pattern.compile("((\\w*)(:(.*?))?@)?(.*?):(\\d{2,5})");
        final String[] strings = Utils.loadFile(f).split("(\\s)");
        for (String s : strings) {
            if (s.isEmpty())
                continue;
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
                    if (i > 0)
                        settings.setProxy(hostPort[0], Integer.valueOf(hostPort[1]), s1.substring(0, i), s1.substring(i + 1));
                    else
                        settings.setProxy(hostPort[0], Integer.valueOf(hostPort[1]), s1, null);
                } else {
                    s2 = s;
                    final String[] hostPort = s2.split(":");
                    settings.setProxy(hostPort[0], Integer.valueOf(hostPort[1]));
                }
                availableConnections.add(settings);
                logger.info("Reading proxy definition " + settings.toString());
            } else
                logger.warning("String " + s + " does not match to proxy definition pattern - [username[:password@]]host:port");
        }

    }

    public List<ConnectionSettings> getAvailableConnections() {
        return Collections.unmodifiableList(availableConnections);
    }

    public Collection<HttpDownloadClient> getClients() {
        if (clients == null) {
            clients = new ArrayList<HttpDownloadClient>(maxClients);
            for (int i = 0; i < maxClients; i++) {
                clients.add(new DownloadClient());
            }
        }
        return clients;
    }

    public int getMaxClients() {
        return maxClients;
    }
}
