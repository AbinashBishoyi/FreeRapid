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
        Date dateInserted = o1.getDateInserted();
        Date dateInserted2 = o2.getDateInserted();
        if (dateInserted == null) {
            return -1;
        }
        if (o2.getDateInserted() == null) {
            return 1;
        }
        return dateInserted.compareTo(dateInserted2);
    }
}
