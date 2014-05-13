package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Vity
 */

/**
 * @author Vity
 */
final class OneInstanceServer extends Thread {
    private final static Logger logger = Logger.getLogger(OneInstanceServer.class.getName());
    private final AppPrefs prefs;
    private volatile ServerSocket serverSocket;

    public OneInstanceServer(AppPrefs prefs) {
        super();    //call to super
        this.prefs = prefs;
        this.setPriority(Thread.MIN_PRIORITY);
    }

    public final void run() {
        this.setName("OneInstanceServer");
        Socket clientSocket = null;
        try {
            logger.info("Creating a local socket server");
            final String portString = AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, null);
            serverSocket = null;
            if (portString == null) { //first run
                int checkPort = Consts.ONE_INSTANCE_SERVER_PORT;
                int attempts = 10;
                while (--attempts > 0) {
                    try {
                        logger.info("Trying to create a local socket server on port " + checkPort);
                        serverSocket = new ServerSocket(checkPort, 1);
                        AppPrefs.storeProperty(FWProp.ONE_INSTANCE_SERVER_PORT, checkPort);
                        prefs.store();
                        break;
                    } catch (IOException e) {
                        logger.info("Failed to create a local socket server on port " + checkPort + "  Reason:" + e.getMessage());
                        ++checkPort;
                    }
                }

            } else
                serverSocket = new ServerSocket(AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, Consts.ONE_INSTANCE_SERVER_PORT), 1);

            if (serverSocket == null)
                throw new IOException("Cannot find available free port for starting");

            while (!isInterrupted()) {
                logger.info("Waiting for connection");
                clientSocket = serverSocket.accept();
                logger.info("Got a connection");
                final BufferedReader stream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line;
                while ((line = stream.readLine()) != null) {
                    if (line.length() == 0 || line.startsWith("-") || "\n".equals(line))
                        continue;
//                    final String fn = line;
                    //TODO zpracovat data z prikazove radky druhe instance aplikace
//                    SwingUtilities.invokeLater(new Runnable() {
//                        public void run() {
//                            OpenFileAction.open(new File(fn));
//                        }
//                    });
                }
                stream.close();
                clientSocket.close();
                final MainApp app = MainApp.getInstance(MainApp.class);
                app.getMainFrame().toFront();
            }
        } catch (IOException e) {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e1) {
                    LogUtils.processException(logger, e);
                }
            }
            if (!serverSocket.isClosed())
                LogUtils.processException(logger, e);
        }
    }

//    static boolean isAppInUse() {
//        if (!AppPrefs.getProperty(FWProp.ONEINSTANCE, FWProp.ONE_INSTANCE_DEFAULT))
//            return false;
//        ServerSocket serverSocket = null;
//        try {
//            final int port = AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, Consts.ONE_INSTANCE_SERVER_PORT);
//            serverSocket = new ServerSocket(port, 1);
//            return false; //it is not in use => splash screen
//        } catch (IOException e) {
//            //not in use
//            return true;
//        } finally {
//            if (serverSocket != null)
//                try {
//                    serverSocket.close();
//                } catch (IOException e) {
//                    LogUtils.processException(logger, e);
//                }
//        }
//    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }
}