package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.ShareDownloadService;

import java.util.*;

/**
 * @author Vity
 */
class DownloadService {
    private List<ConnectionSettings> downloading = new Vector<ConnectionSettings>();
    private Set<ConnectionSettings> problems = Collections.synchronizedSet(new HashSet<ConnectionSettings>());

    private String serviceName;
    private int maxDownloadsFromOneIP;

    public DownloadService(ShareDownloadService fileService) {
        this.serviceName = fileService.getName();
        this.maxDownloadsFromOneIP = fileService.getMaxDownloadsFromOneIP();
    }


    public boolean canDownloadWith(ConnectionSettings connectionSettings) {
        if (problems.contains(connectionSettings))
            return false;
        int foundCount = 0;
        final int oneIP = getMaxDownloadsFromOneIP();
        for (ConnectionSettings settings : downloading) {
            if (settings.equals(connectionSettings)) {
                ++foundCount;
                if (foundCount >= oneIP) {
                    return false;
                }
            }
        }
        return true;
    }

    public void addDownloadingClient(HttpDownloadClient client) {
//        if (!this.canDownloadWith(client.getSettings()))
//            throw new IllegalStateException("Cannot download more through this IP");
        downloading.add(client.getSettings());
    }

    public void addProblematicConnection(ConnectionSettings settings) {
        problems.add(settings);
    }

    public void removeProblematicConnection(ConnectionSettings settings) {
        problems.remove(settings);
    }

    public void finishedDownloading(HttpDownloadClient client) {
        downloading.remove(client.getSettings());
    }


    public int getProblematicConnectionsCount() {
        return problems.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DownloadService that = (DownloadService) o;

        return serviceName.equals(that.serviceName);

    }

    @Override
    public int hashCode() {
        return serviceName.hashCode();
    }

    public String getServiceName() {
        return serviceName;
    }


    @Override
    public String toString() {
        return getServiceName();
    }

    public int getMaxDownloadsFromOneIP() {
        return maxDownloadsFromOneIP;
    }
}
