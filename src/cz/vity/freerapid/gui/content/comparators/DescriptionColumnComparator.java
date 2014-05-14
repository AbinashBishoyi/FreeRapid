package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;

import java.text.Collator;
import java.util.Comparator;

/**
 * @author Vity
 */
public final class DescriptionColumnComparator implements Comparator<DownloadFile> {

    private Collator collator;

    public DescriptionColumnComparator() {
        collator = Collator.getInstance();
    }

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final String val1 = o1.getDescription();
        final String val2 = o2.getDescription();
        if (val1 == null && val2 == null) {
            return 0;
        }
        if (val1 == null) {
            return -1;
        }
        if (val2 == null) {
            return 1;
        }

        return collator.compare(val1, val2);
    }


}
