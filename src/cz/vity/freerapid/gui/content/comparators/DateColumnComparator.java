package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;

import java.util.Comparator;
import java.util.Date;

/**
 * @author Vity
 */
public final class DateColumnComparator implements Comparator<DownloadFile> {
    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final Date dateInserted = o1.getDateInserted();
        if (dateInserted == null && o2.getDateInserted() == null) {
            return 0;
        }
        if (dateInserted == null) {
            return -1;
        }
        if (o2.getDateInserted() == null) {
            return 1;
        }
        return dateInserted.compareTo(o2.getDateInserted());
    }
}
