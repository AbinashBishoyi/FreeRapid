package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class ProgressBarColumnComparator implements Comparator<DownloadFile> {


    public ProgressBarColumnComparator() {
//        comp2 = new EstTimeColumnComparator();
    }

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final int[] res1 = getSortValue(o1);
        final int[] res2 = getSortValue(o2);
        if (res1[0] == res2[0]) {
            return new Integer(res1[1]).compareTo(res2[1]);
        } else {
            return new Integer(res1[0]).compareTo(res2[0]);
        }
    }

    private static int[] getSortValue(DownloadFile downloadFile) {
        final int[] result = new int[2];
        final int toQueued = downloadFile.getTimeToQueued();
        final DownloadState state = downloadFile.getState();
        if ((state == DownloadState.ERROR || state == DownloadState.SLEEPING) && toQueued >= 0) {
            result[0] = 2;
            result[1] = toQueued;
        } else {
            final int sleep = downloadFile.getSleep();
            if (state == DownloadState.WAITING && sleep >= 0) {
                result[0] = 1;
                result[1] = sleep;
            } else {
                result[0] = 0;
                result[1] = ContentPanel.getProgress(downloadFile);
            }
        }
        return result;
    }
}