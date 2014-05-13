package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.dialogs.AccountDialog;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ladislav Vitasek
 */
public class StandardDialogSupport implements DialogSupport {
    private volatile String captchaResult;
    private final static Object captchaLock = new Object();
    private final ApplicationContext context;

    public StandardDialogSupport(final ApplicationContext context) {
        this.context = context;
    }

    public PremiumAccount showAccountDialog(final PremiumAccount account, final String title) throws Exception {
        final PremiumAccount[] result = new PremiumAccount[]{null};
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    getAccount(title, account, result);
                }
            });
        } else getAccount(title, account, result);

        return result[0];
    }

    public boolean showOKCancelDialog(final Component container, final String title) throws Exception {
        final boolean[] dialogResult = new boolean[]{false};
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    dialogResult[0] = Swinger.showInputDialog(title, container, true) == Swinger.RESULT_OK;
                }
            });
        } else dialogResult[0] = Swinger.showInputDialog(title, container, true) == Swinger.RESULT_OK;
        return dialogResult[0];
    }

    public void showOKDialog(final Component container, final String title) throws Exception {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    Swinger.showInputDialog(title, container, false);
                }
            });
        } else Swinger.showInputDialog(title, container, false);
    }

    public String askForCaptcha(final BufferedImage image) throws Exception {
        synchronized (captchaLock) {
            captchaResult = "";
            if (!EventQueue.isDispatchThread()) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    public void run() {
                        askCaptcha(image);
                    }
                });
            } else askCaptcha(image);
            image.flush();
            return captchaResult;
        }
    }

    private void askCaptcha(BufferedImage image) {
        if (AppPrefs.getProperty(UserProp.ACTIVATE_WHEN_CAPTCHA, UserProp.ACTIVATE_WHEN_CAPTCHA_DEFAULT))
            Swinger.bringToFront(((SingleFrameApplication) context.getApplication()).getMainFrame(), true);
        captchaResult = (String) JOptionPane.showInputDialog(null, context.getResourceMap(DownloadTask.class).getString("InsertWhatYouSee"), context.getResourceMap(DownloadTask.class).getString("InsertCaptcha"), JOptionPane.PLAIN_MESSAGE, new ImageIcon(image), null, null);
    }


    private void getAccount(String title, PremiumAccount account, PremiumAccount[] result) {
        final SingleXFrameApplication app = (SingleXFrameApplication) context.getApplication();
        final AccountDialog dialog = new AccountDialog(app.getMainFrame(), title, account);
        app.prepareDialog(dialog, true);
        result[0] = dialog.getAccount();
    }
}
