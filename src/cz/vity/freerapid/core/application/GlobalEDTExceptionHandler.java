package cz.vity.freerapid.core.application;

import cz.vity.freerapid.swing.Swinger;

import javax.persistence.PersistenceException;
import javax.swing.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Trida pro zachytavani "neodchycenych" vyjimek na urovni EDT Pri zachyceni vyjimky zaloguje a ukaze uzivateli error
 * dialog o neocekavane chybe.
 *
 * @author Vity
 */
public class GlobalEDTExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static Logger logger = Logger.getLogger(GlobalEDTExceptionHandler.class.getName());

    private static Set<String> reported = new HashSet<String>(2);

    private final static Object lock = new Object();

    private static String previousError = "";

    public void uncaughtException(final Thread t, final Throwable e) {
        //https://appframework.dev.java.net/issues/show_bug.cgi?id=65
        if (e instanceof PersistenceException) {
            if (e.getMessage().contains("error 141")) {
                logger.severe("Another instance of FRD is already running. Multiple instances are not supported.");
                return;
            }
        }
        if (e instanceof IllegalStateException) { //app framework hack
            if (e.getMessage().contains("cannot open system"))
                return;
        }
        if (e instanceof java.lang.InternalError) {
            logger.log(Level.SEVERE, "Uncaught exception on thread ", e);
            Swinger.showErrorDialog("errorMessageBasic", e, false);
            return;
        }
        final StringWriter s = new StringWriter();
        final PrintWriter writer = new PrintWriter(s);
        e.printStackTrace(writer);
        final String msg = s.toString();
        synchronized (lock) {
            if (previousError != null && previousError.contains(msg))
                return;
        }
        if (SwingUtilities.isEventDispatchThread())
            logger.log(Level.SEVERE, "Uncaught exception on EDT. ", e);
        else
            logger.log(Level.SEVERE, "Uncaught exception on thread " + t.getName(), e);
        //final MainApp app = MainApp.getInstance();
        writer.close();
        if (msg.contains("java.lang.ClassCastException: java.awt.TrayIcon cannot be cast to java.awt.Component")) {
            logger.severe(msg + "JRE bug - ignoring this exception");
            return;
        }
        if (msg.contains("NoSuchMethodError: java.awt.Rectangle.union")) {
            logger.severe(msg + "invalid JRE installation - ignoring this exception");
            return;
        }

        boolean contains = reported.contains(msg);

        reported.add(msg);

        if (msg.contains("NoClassDefFoundError: Could not initialize class sun.awt.shell.Win32ShellFolder2") || msg.contains("WinampMoveStyle") || msg.contains("JToolTip cannot be cast to javax.swing.text.JTextComponent") || msg.contains("Connection is not open") || msg.contains("Buffers have not been created") || msg.contains("RejectedExecutionException") || msg.contains("OutOfMemoryError") || msg.contains("Could not get shell folder ID list")) {
            contains = true;
        }

        final String message;
        if (msg.contains("Non-Java exception raised, not handled!")) {
            message = "errorMessageMacOSXBug";
            contains = true;
        } else message = "errorMessageBasic";
        final boolean contains1 = contains;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                synchronized (lock) {
                    if (previousError != null && previousError.contains(msg))
                        return;
                    Swinger.showErrorDialog(message, e, !contains1);
                    previousError = msg;
                }
            }
        });

    }


}
