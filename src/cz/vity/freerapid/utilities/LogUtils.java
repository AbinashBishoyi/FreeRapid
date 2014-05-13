package cz.vity.freerapid.utilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Vity
 */

public class LogUtils {

    private LogUtils() {
    }

    /**
     * Logging utilities initialization
     *
     * @param file logging file for configuration
     */
    public static void initLogging(String file) {
        InputStream inputStream = null;
        try {
            inputStream = LogUtils.class.getClassLoader().getResourceAsStream(file);
            if (inputStream == null)
                throw new IOException("Log properties file was not found: " + file);
            LogManager.getLogManager().readConfiguration(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                LogManager.getLogManager().readConfiguration();
            } catch (IOException e1) {
                System.err.println("Loading of the logging properties failed");
            }
        }
        finally {
            if (inputStream != null)
                try {
                    inputStream.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
        }
    }

    public static void processException(Logger logger, final Throwable e) {
        logger.log(Level.SEVERE, "", e);
    }
}