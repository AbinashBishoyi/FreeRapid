package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class OneInstanceClient {
    private final static Logger logger = Logger.getLogger(OneInstanceClient.class.getName());

    private OneInstanceClient() {
    }

    public static boolean checkInstance(final Collection<String> openFiles, AppPrefs prefs) {
        if (!AppPrefs.getProperty(FWProp.ONEINSTANCE, FWProp.ONE_INSTANCE_DEFAULT))
            return false;
        Socket clientSocket = null;
        try {
            logger.info("Testing existing instance");
            final String portString = AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, null);
            if (portString != null) {
                clientSocket = new Socket("localhost", Integer.parseInt(portString));
                if (openFiles != null && !openFiles.isEmpty()) {
                    OutputStream out = null;
                    try {
                        out = clientSocket.getOutputStream();
                        if (out != null) {
                            for (String file : openFiles) {
                                out.write(file.getBytes());
                                out.write('\n');
                            }
                            out.close();
                        }
                    } catch (IOException e) {
                        LogUtils.processException(logger, e);
                        if (out != null) {
                            try {
                                out.close();
                            } catch (Exception ex) {
                                LogUtils.processException(logger, ex);
                            }
                        }
                    }
                }
                logger.info("Application is already running. Exiting");
                return true;
            } else {
                logger.info("No other instance is running - first start");
                oneInstanceServerStart(prefs);
            }
        } catch (IOException e) {
            logger.info("No other instance is running.");
            oneInstanceServerStart(prefs);
        } finally {
            if (clientSocket != null)
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
        }
        return false;
    }

    private static void oneInstanceServerStart(AppPrefs prefs) {
        final OneInstanceServer server = new OneInstanceServer(prefs);
        server.start();
    }

}