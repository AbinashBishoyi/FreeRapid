package cz.vity.freerapid.utilities;

import cz.vity.freerapid.core.MainApp;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;

/**
 * @author ntoskrnl
 */
public class LogFileHandler extends FileHandler {

    public LogFileHandler() throws IOException, SecurityException {
        super(getLogFile().getAbsolutePath());
    }

    public static void init() {
        final File folder = getLogFile().getParentFile();
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
    }

    public static File getLogFile() {
        final File folder = new File(MainApp.getAContext().getLocalStorage().getDirectory(), "log");
        //noinspection ResultOfMethodCallIgnored
        if (!folder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            folder.mkdirs();
        }
        return new File(folder, "app.log");
    }

}
