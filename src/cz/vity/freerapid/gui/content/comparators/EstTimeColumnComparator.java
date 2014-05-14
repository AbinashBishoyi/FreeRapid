package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class EstTimeColumnComparator implements Comparator<DownloadFile> {
    private NameColumnComparator nameComparator;


    public EstTimeColumnComparator() {
        nameComparator = new NameColumnComparator();
    }

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
        final int p1 = DownloadsActions.getPriorityForState(state1);
        final int p2 = DownloadsActions.getPriorityForState(state2);
        if (!isProcessState1) { //the second one is not downloading too
            final int res = new Integer(p1).compareTo(p2);
            if (res == 0) {
                return nameComparator.compare(o1, o2);
            }
//            return Collator.getInstance().compare(ContentPanel.stateToString(state1), ContentPanel.stateToString(state2));
        }
        if (p1 == p2) {//both in process state
            //downloading both
            long hasToBeDownloaded = o1.getFileSize() - o1.getDownloaded();
            double avgSpeed = o1.getShortTimeAvgSpeed();
            boolean compare1 = hasToBeDownloaded >= 0 && avgSpeed > 0;
            long hasToBeDownloaded2 = o1.getFileSize() - o1.getDownloaded();
            double avgSpeed2 = o2.getShortTimeAvgSpeed();
            boolean compare2 = hasToBeDownloaded2 >= 0 && avgSpeed2 > 0;
            if (compare1 && !compare2)
                return -1;
            if (!compare1 && compare2)
                return 1;
            if (compare1 && compare2) {
                long value1 = Math.round((double) hasToBeDownloaded / avgSpeed);
                long value2 = Math.round((double) hasToBeDownloaded2 / avgSpeed2);
                return -new Long(value1).compareTo(value2);
            }
            return nameComparator.compare(o1, o2);
        } else return
                new Integer(p1).compareTo(p2);
    }
}