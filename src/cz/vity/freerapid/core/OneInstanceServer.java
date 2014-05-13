package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */

/**
 * @author Ladislav Vitasek
 */
final class OneInstanceServer extends Thread {
    private final static Logger logger = Logger.getLogger(OneInstanceServer.class.getName());

    public OneInstanceServer() {
        super();    //call to super
        this.setPriority(Thread.MIN_PRIORITY);
    }

    public final void run() {
        Socket clientSocket = null;
        try {
            logger.info("Creating a local socket server");
            final int port = AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, Consts.ONE_INSTANCE_SERVER_PORT);
            ServerSocket serverSocket = new ServerSocket(port, 1);
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
            LogUtils.processException(logger, e);
        }
    }


    static boolean isAppInUse() {
        if (!AppPrefs.getProperty(FWProp.ONEINSTANCE, true))
            return false;
        ServerSocket serverSocket = null;
        try {
            final int port = AppPrefs.getProperty(FWProp.ONE_INSTANCE_SERVER_PORT, Consts.ONE_INSTANCE_SERVER_PORT);
            serverSocket = new ServerSocket(port, 1);
            return false; //it is not in use => splash screen
        } catch (IOException e) {
            //not in use
            return true;
        } finally {
            if (serverSocket != null)
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
        }
    }

}