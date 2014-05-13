package cz.vity.freerapid.plugins.dev.plugimpl;

import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ladislav Vitasek
 */
public class DevDialogSupport implements DialogSupport {
    private final static Object captchaLock = new Object();

    public DevDialogSupport(final ApplicationContext context) {

    }

    public PremiumAccount showAccountDialog(final PremiumAccount account, final String title) throws Exception {
        return account;
    }

    public boolean showOKCancelDialog(final Component container, final String title) throws Exception {
        final boolean[] dialogResult = new boolean[]{false};
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    dialogResult[0] = showInputDialog(title, container, true) == Swinger.RESULT_OK;
                }
            });
        } else return showInputDialog(title, container, true) == Swinger.RESULT_OK;
        return dialogResult[0];

    }

    public void showOKDialog(final Component container, final String title) throws Exception {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    showInputDialog(title, container, false);
                }
            });
        } else showInputDialog(title, container, false);
    }

    public String askForCaptcha(final BufferedImage image) throws Exception {
        synchronized (captchaLock) {
            final String[] captchaResult = new String[]{""};
            if (!EventQueue.isDispatchThread()) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        captchaResult[0] = getCaptcha(image);
                    }
                });
            } else captchaResult[0] = getCaptcha(image);
            image.flush();
            return captchaResult[0];
        }
    }

    private String getCaptcha(BufferedImage image) {
        return (String) JOptionPane.showInputDialog(null, "Insert what you see", "Insert CAPTCHA", JOptionPane.PLAIN_MESSAGE, new ImageIcon(image), null, null);
    }

    private static int showInputDialog(final String title, final Object inputObject, boolean cancelButton) {
        final String[] buttons;
        if (cancelButton)
            buttons = new String[]{"OK", "Cancel"};
        else
            buttons = new String[]{"Cancel"};
        final Object[] objects = new Object[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final String s = buttons[i];
            assert s != null;
            objects[i] = s;
        }
        final Frame frame = getActiveFrame();
        return JOptionPane.showOptionDialog(frame, inputObject, title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, objects, objects[0]);
    }

    private static Frame getActiveFrame() {
        final Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame.isActive())
                return frame;
        }
        return frames[0];
    }


}