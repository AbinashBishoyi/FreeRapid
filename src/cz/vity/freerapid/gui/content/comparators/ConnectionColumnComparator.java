package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;

import java.text.Collator;
import java.util.Comparator;

/**
 * @author Vity
 */
public final class ConnectionColumnComparator implements Comparator<DownloadFile> {

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final Object[] res1 = getSortValue(o1);
        final Object[] res2 = getSortValue(o2);
        if (res1[0].equals(res2[0])) {
            return Collator.getInstance().compare(res1[1], res2[1]);
        } else {
            return ((Integer) (res1[0])).compareTo((Integer) res2[0]);
        }
    }

    private static Object[] getSortValue(DownloadFile downloadFile) {
        final Object[] result = new Object[2];

        final DownloadTask task = downloadFile.getTask();

        ConnectionSettings con = null;
        if (downloadFile.getState() == DownloadState.SLEEPING || downloadFile.getState() == DownloadState.ERROR) {
            con = downloadFile.getConnectionSettings();
        }

        if (con == null && task != null) {
            final HttpDownloadClient client = task.getClient();
            if (client != null)
                con = client.getSettings();
        }

        result[1] = "";
        if (con != null) {
            if (con.isProxySet()) {
                result[0] = 1;
                String value = String.format("%s:%s", con.getProxyURL(), con.getProxyPort());
                if (con.getUserName() != null) {
                    value = con.getUserName() + "@" + value;
                }
                result[1] = value;
            } else result[0] = 0;
        } else {
            result[0] = 2;
        }
        return result;
    }
}