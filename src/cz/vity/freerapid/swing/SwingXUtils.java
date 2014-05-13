package cz.vity.freerapid.swing;

import cz.vity.freerapid.utilities.Browser;
import org.jdesktop.swingx.hyperlink.LinkModel;
import org.jdesktop.swingx.hyperlink.LinkModelAction;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.renderer.HyperlinkProvider;

import javax.swing.table.TableCellRenderer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @author Ladislav Vitasek
 */
final public class SwingXUtils {


    private SwingXUtils() {
    }

    public static TableCellRenderer getHyperLinkTableCellRenderer() {
        final LinkModelAction action = new LinkModelAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final LinkModel linkModel = (LinkModel) e.getSource();
                Browser.openBrowser(linkModel.getURL());
            }
        });

        return new DefaultTableRenderer(new HyperlinkProvider(action, LinkModel.class));
    }

    public static LinkModel createLink(String urlString) {
        try {
            return createLink(new URL(urlString));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            // ignore - something went wrong
        }
        return null;
    }

    public static LinkModel createLink(URL urlString) {
        final String s = urlString.toExternalForm();
        return new LinkModel(s, null, urlString) {
            @Override
            public String toString() {
                return s;
            }
        };
    }


}
