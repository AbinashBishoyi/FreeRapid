package cz.vity.freerapid.gui;

import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.Utils;

import java.io.File;

/**
 * @author Ladislav Vitasek
 */
public class FRDUtils {

    public static File getAbsRelPath(File path) {
        if (System.getProperties().containsKey("portable")) {
            return FileUtils.getRelativeDirectory(new File(Utils.getAppPath()), path);
        }
        return path;
    }

    public static File getAbsRelPath(String path) {
        return getAbsRelPath(new File(path));
    }


}
