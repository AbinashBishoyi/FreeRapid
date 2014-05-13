package cz.vity.freerapid.gui.managers;

import org.jdesktop.application.ApplicationContext;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class ClipboardMonitorManager implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ClipboardMonitorManager.class.getName());

    private ApplicationContext context;

    public ClipboardMonitorManager(ApplicationContext context, ManagerDirector managerDirector) {
        this.context = context;
        init();
    }

    private void init() {
        final Clipboard clipboard = context.getClipboard();

        clipboard.addFlavorListener(new FlavorListener() {
            public void flavorsChanged(FlavorEvent e) {
                final Object data;
                try {
                    data = clipboard.getData(DataFlavor.stringFlavor);
                    logger.info("novej clipboard:" + data);
                    Toolkit.getDefaultToolkit().beep();
                } catch (UnsupportedFlavorException e1) {
                    e1.printStackTrace();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        System.out.println("Lost ownership");
        //Toolkit.getDefaultToolkit().beep();
    }
}
