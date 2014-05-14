package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;

import java.util.Comparator;

/**
 * @author Vity
 */
public final class CheckedColumnComparator implements Comparator<DownloadFile> {

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        return -new Integer(o1.getFileState().ordinal()).compareTo(o2.getFileState().ordinal());
    }
}