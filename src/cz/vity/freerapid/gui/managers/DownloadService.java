package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;

import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * @author Vity
 */
class DownloadService {
    private Collection<ConnectionSettings> downloading = new Vector<ConnectionSettings>();
    private Collection<DownloadFile> testing = new Vector<DownloadFile>();
    private Collection<ConnectionSettings> problems = new Vector<ConnectionSettings>();

    private String serviceName;
    //private final ShareDownloadService service;
    private int maxDownloads;

    public DownloadService(PluginMetaData fileService, ShareDownloadService service) {
        //  this.service = service;
        setPluginMetaData(fileService);
    }


    public boolean canDownloadWith(ConnectionSettings connectionSettings) {
        if (problems.contains(connectionSettings)) {
            //System.out.println("Still in problematic connections");
            return false;
        }
        int foundCount = 0;
        for (ConnectionSettings settings : downloading) {
            if (settings.equals(connectionSettings)) {
                ++foundCount;
                //depends on user settings
                if (foundCount >= this.maxDownloads) {
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
//        System.out.println("Adding problematic connection");
        problems.add(settings);
    }

    public void removeProblematicConnection(ConnectionSettings settings) {
//        System.out.println("Renewing problematic connection");
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


    @Override
    public String toString() {
        return serviceName;
    }

    public void setPluginMetaData(PluginMetaData metaData) {
        this.serviceName = metaData.getServices();
        this.maxDownloads = metaData.getMaxAllowedDownloads();
    }
}
