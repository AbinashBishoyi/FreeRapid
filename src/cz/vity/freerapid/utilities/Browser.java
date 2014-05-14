package cz.vity.freerapid.utilities;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.swing.Swinger;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pomocna trida pro spousteni weboveho browseru nebo emailoveho klienta
 *
 * @author Vity
 */
public class Browser {
    private final static Logger logger = Logger.getLogger(Browser.class.getName());

    private Browser() {
    }

    /**
     * Otevre browser nebo emailoveho klienta
     *
     * @param mailOrUrl pokud hodnota zacina mailto, otevira se klient, jinak browser
     */
    public static void openBrowser(String mailOrUrl) {
        assert mailOrUrl != null;
        if (!(mailOrUrl.length() > 0 && Desktop.isDesktopSupported()))
            return;
        try {
            URI uri = new URI(mailOrUrl);
            if (!mailOrUrl.startsWith("mailto")) {
                Desktop.getDesktop().browse(uri);
            } else {
                Desktop.getDesktop().mail(uri);
            }
        } catch (IOException e) {
            //ignore
            //http://bugtracker.wordrider.net/task/477
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Opening browser failed", e);
            Swinger.showErrorDialog("errorOpeningBrowser", e);
        }

    }

    public static void showHomepage() {
        openBrowser(AppPrefs.getProperty(FWProp.WEBURL, Consts.WEBURL));
    }

    public static void openBrowser(URL url) {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.BROWSE))
            return;
        try {
            Desktop.getDesktop().browse(url.toURI());
        } catch (IOException e) {
            //ignore
            //http://bugtracker.wordrider.net/task/477
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Opening browser failed", e);
            Swinger.showErrorDialog("errorOpeningBrowser", e);
        }
    }
}
