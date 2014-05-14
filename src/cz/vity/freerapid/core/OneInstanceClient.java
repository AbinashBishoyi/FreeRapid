package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.EventObject;
import java.util.logging.Logger;

/**
 * @author Vity
 */
final class OneInstanceClient {
    private final static Logger logger = Logger.getLogger(OneInstanceClient.class.getName());

    private OneInstanceClient() {
    }

    public static boolean checkInstance(final Collection<String> openFiles, AppPrefs prefs, ApplicationContext context) {
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
                //Testing ESET - first run of app ever - if it's blocked -> eset (high probability) or port is used by another app
                if (AppPrefs.getProperty(UserProp.SHOW_PAYPAL_REQUEST, MainApp.BUILD_REQUEST - 1) != MainApp.BUILD_REQUEST) {
                    logger.warning("Detecting ESET - disabling OneInstance functionality");
                    AppPrefs.storeProperty(FWProp.ONEINSTANCE, false);
                    return false;
                }
                logger.info("Application is already running. Exiting");
                return true;
            } else {
                logger.info("No other instance is running - first instance");
                oneInstanceServerStart(prefs, context);
            }
        } catch (IOException e) {
            logger.info("No other instance is running.");
            oneInstanceServerStart(prefs, context);
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

    private static void oneInstanceServerStart(AppPrefs prefs, ApplicationContext context) {
        final OneInstanceServer server = new OneInstanceServer(prefs);
        server.start();
        context.getApplication().addExitListener(new Application.ExitListener() {
            public boolean canExit(EventObject event) {
                return true;
            }

            public void willExit(EventObject event) {
                final ServerSocket serverSocket = server.getServerSocket();
                if (serverSocket != null)
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        //ignore
                    }
            }
        });

    }

}