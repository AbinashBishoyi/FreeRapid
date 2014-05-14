package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class AvgSpeedColumnComparator implements Comparator<DownloadFile> {
    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final DownloadState state1 = o1.getState();
        final DownloadState state2 = o2.getState();
        final boolean isProcessState1 = DownloadsActions.isProcessState(state1);
        final boolean isProcessState2 = DownloadsActions.isProcessState(state2);
        if (isProcessState1 && !isProcessState2) {
            return -1;
        }
        if (!isProcessState1 && isProcessState2) {
            return 1;
        }
        if (isProcessState1) {//obsa stahuji
            final int p1 = DownloadsActions.getPriorityForState(state1);
            final int p2 = DownloadsActions.getPriorityForState(state2);
            if (p1 == p2) {
                float value1, value2;
                if (o1.getSpeed() >= 0) {
                    value1 = o1.getAverageSpeed();
                } else value1 = 0;
                if (o2.getSpeed() >= 0) {
                    value2 = o2.getAverageSpeed();
                } else value2 = 0;
                return Float.compare(value1, value2);
            } else return new Integer(p1).compareTo(p2);

        }
        return 0;//ani jeden nestahuje
    }
}