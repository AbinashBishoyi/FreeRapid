package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.model.DownloadFile;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class ProgressColumnComparator implements Comparator<DownloadFile> {
    private EstTimeColumnComparator comp2;

    public ProgressColumnComparator() {
        comp2 = new EstTimeColumnComparator();
    }

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final int progress = ContentPanel.getProgress(o1);
        final int progress2 = ContentPanel.getProgress(o2);
        if (progress == progress2)
            return comp2.compare(o1, o2);
        else return new Integer(progress).compareTo(progress2);
    }
}