package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;

import java.util.*;

/**
 * @author Vity
 */
class DownloadService {
    private List<ConnectionSettings> downloading = new Vector<ConnectionSettings>();
    private List<DownloadFile> testing = new Vector<DownloadFile>();
    private Set<ConnectionSettings> problems = Collections.synchronizedSet(new HashSet<ConnectionSettings>());

    private String serviceName;
    private int maxDownloadsFromOneIP;

    public DownloadService(PluginMetaData fileService, ShareDownloadService service) {
        this.serviceName = fileService.getServices();
        this.maxDownloadsFromOneIP = service.getMaxDownloadsFromOneIP();
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

    public boolean canDownloadBeforeCheck(DownloadFile testFile, List<DownloadFile> list, boolean startFromTop) {
        if (testing.isEmpty())
            return true;
        final int index = list.indexOf(testFile);
        for (DownloadFile file : testing) {
            final int i = list.indexOf(file);
            if (startFromTop) {
                if (i < index)
                    return false;
            } else {
                if (i > index)
                    return false;

            }
        }
        return true;
    }


    public void addTestingFile(DownloadFile file) {
        testing.add(file);
    }

    public void finishedTestingFile(DownloadFile file) {
        testing.remove(file);
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
