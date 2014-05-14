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
        super(new File(MainApp.getAContext().getLocalStorage().getDirectory(), "app.log").getAbsolutePath());
    }

}
