package cz.vity.freerapid.gui.content.comparators;

import cz.vity.freerapid.model.DownloadFile;

import java.text.Collator;
import java.util.Comparator;

/**
 * @author Vity
 */
public final class ServiceColumnComparator implements Comparator<DownloadFile> {

    @Override
    public final int compare(DownloadFile o1, DownloadFile o2) {
        final String serviceName1 = o1.getServiceName();
        final String serviceName2 = o2.getServiceName();
        return Collator.getInstance().compare(serviceName1, serviceName2);
    }


}