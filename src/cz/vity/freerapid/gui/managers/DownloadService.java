package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.ShareDownloadService;

import java.util.List;
import java.util.Vector;

/**
 * @author Vity
 */
class DownloadService {
    private List<ConnectionSettings> downloading = new Vector<ConnectionSettings>();

    private String serviceName;
    private int maxDownloadsFromOneIP;

    public DownloadService(ShareDownloadService fileService) {
        this.serviceName = fileService.getName();
        this.maxDownloadsFromOneIP = fileService.getMaxDownloadsFromOneIP();
    }


    public boolean canDownloadWith(ConnectionSettings connectionSettings) {
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
        if (!this.canDownloadWith(client.getSettings()))
            throw new IllegalStateException("Cannot download more through this IP");
        downloading.add(client.getSettings());
    }


    public void finishedDownloading(HttpDownloadClient client) {
        downloading.remove(client.getSettings());
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
