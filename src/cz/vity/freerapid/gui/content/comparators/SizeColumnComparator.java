package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class SizeColumnComparator implements Comparator<DownloadFile> {
    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        return new Long(o1.getFileSize()).compareTo(o2.getFileSize());
    }
}
