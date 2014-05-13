package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final class OneInstanceClient {
    private final static Logger logger = Logger.getLogger(OneInstanceClient.class.getName());

    private OneInstanceClient() {
    }

    public static boolean checkInstance(final Collection<String> openFiles) {
        if (!AppPrefs.getProperty(FWProp.ONEINSTANCE, true))
            return false;
        Socket clientSocket = null;
        try {
            logger.info("Testing existing instance");
            final int port = AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, Consts.ONE_INSTANCE_SERVER_PORT);
            clientSocket = new Socket("localhost", port);
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
        } catch (IOException e) {
            logger.info("No other instance is running.");
            final OneInstanceServer server = new OneInstanceServer();
            server.start();
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

}