package cz.vity.freerapid.utilities;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class OSDesktop {
    private final static Logger logger = Logger.getLogger(OSDesktop.class.getName());

    public static void openFile(File file) {
        if (!file.exists())
            return;
        if (Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(file);
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }
        }
    }
}
