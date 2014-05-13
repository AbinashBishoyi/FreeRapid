package cz.vity.freerapid.core.application;

import cz.vity.freerapid.swing.Swinger;

import javax.swing.*;
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

    public void uncaughtException(final Thread t, final Throwable e) {
        //https://appframework.dev.java.net/issues/show_bug.cgi?id=65
        if (e instanceof IllegalStateException) { //app framework hack
            if (e.getMessage().contains("cannot open system"))
                return;
        }
        logger.log(Level.SEVERE, "Uncaught exception on EDT. ", e);
        //final MainApp app = MainApp.getInstance();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Swinger.showErrorDialog("errorMessageBasic", e, true);
            }
        });


    }


}
