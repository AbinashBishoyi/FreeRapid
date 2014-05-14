package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class SpeedColumnComparator implements Comparator<DownloadFile> {
    private EstTimeColumnComparator estTimeColumnComparator;

    public SpeedColumnComparator() {
        estTimeColumnComparator = new EstTimeColumnComparator();
    }

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final DownloadState state = o1.getState();
        final DownloadState state2 = o2.getState();
        if (state == DownloadState.DOWNLOADING && state2 != DownloadState.DOWNLOADING) {
            return 1;
        }
        if (state != DownloadState.DOWNLOADING && state2 == DownloadState.DOWNLOADING) {
            return -1;
        }
        if (state == DownloadState.DOWNLOADING) {//both downloading
            long value1, value2;
            if (o1.getSpeed() >= 0) {
                value1 = o1.getSpeed();
            } else value1 = 0;
            if (o2.getSpeed() >= 0) {
                value2 = o2.getSpeed();
            } else value2 = 0;
            return new Long(value1).compareTo(value2);
        }
        return estTimeColumnComparator.compare(o1, o2);//ani jeden nestahuje
    }


}