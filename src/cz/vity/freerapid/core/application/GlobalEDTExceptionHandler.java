package cz.vity.freerapid.core.application;

import cz.vity.freerapid.swing.Swinger;

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

    public void uncaughtException(final Thread t, final Throwable e) {
        //https://appframework.dev.java.net/issues/show_bug.cgi?id=65
        if (e instanceof IllegalStateException) { //app framework hack
            if (e.getMessage().contains("cannot open system"))
                return;
        }
        if (e instanceof java.lang.InternalError) {
            logger.log(Level.SEVERE, "Uncaught exception on thread ", e);
            Swinger.showErrorDialog("errorMessageBasic", e, false);
            return;
        }
        if (SwingUtilities.isEventDispatchThread())
            logger.log(Level.SEVERE, "Uncaught exception on EDT. ", e);
        else
            logger.log(Level.SEVERE, "Uncaught exception on thread " + t.getName(), e);
        //final MainApp app = MainApp.getInstance();
        final StringWriter s = new StringWriter();
        final PrintWriter writer = new PrintWriter(s);
        e.printStackTrace(writer);
        writer.close();
        final String msg = s.toString();
        final boolean contains = reported.contains(msg);
        reported.add(msg);

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Swinger.showErrorDialog("errorMessageBasic", e, !contains);
            }
        });


    }


}
