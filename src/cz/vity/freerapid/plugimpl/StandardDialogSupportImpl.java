package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.dialogs.AccountDialog;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.SingleFrameApplication;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

/**
 * Standard implementation of DialogSupport
 *
 * @author Ladislav Vitasek
 */
public class StandardDialogSupportImpl implements DialogSupport {

    private final static Logger logger = Logger.getLogger(StandardDialogSupportImpl.class.getName());

    /**
     * result from the user's input for CAPTCHA
     */
    private volatile String captchaResult;
    /**
     * synchronization lock - to block more than 1 CAPTCHA dialog
     */
    private final static Object captchaLock = new Object();
    /**
     * application context
     */
    private final ApplicationContext context;

    /**
     * Constructor - creates a new StandardDialogSupportImpl instance.
     *
     * @param context application context
     */
    public StandardDialogSupportImpl(final ApplicationContext context) {
        this.context = context;
    }

    @Override
    public PremiumAccount showAccountDialog(final PremiumAccount account, final String title) throws Exception {
        final PremiumAccount[] result = new PremiumAccount[]{null};
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    getAccount(title, account, result);
                }
            });
        } else getAccount(title, account, result);

        return result[0];
    }

    @Override
    public boolean showOKCancelDialog(final Component container, final String title) throws Exception {
        final boolean[] dialogResult = new boolean[]{false};
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    dialogResult[0] = Swinger.showInputDialog(title, container, true) == Swinger.RESULT_OK;
                }
            });
        } else dialogResult[0] = Swinger.showInputDialog(title, container, true) == Swinger.RESULT_OK;
        return dialogResult[0];
    }

    @Override
    public void showOKDialog(final Component container, final String title) throws Exception {
        if (!EventQueue.isDispatchThread()) {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Swinger.showInputDialog(title, container, false);
                }
            });
        } else Swinger.showInputDialog(title, container, false);
    }

    @Override
    public String askForCaptcha(final BufferedImage image) throws Exception {
        return askForCaptcha(new ImageIcon(image));
    }

    private void askCaptcha(Icon image) {
        if (AppPrefs.getProperty(UserProp.ACTIVATE_WHEN_CAPTCHA, UserProp.ACTIVATE_WHEN_CAPTCHA_DEFAULT))
            Swinger.bringToFront(((SingleFrameApplication) context.getApplication()).getMainFrame(), true);
        captchaResult = (String) JOptionPane.showInputDialog(null, context.getResourceMap(DownloadTask.class).getString("InsertWhatYouSee"), context.getResourceMap(DownloadTask.class).getString("InsertCaptcha"), JOptionPane.PLAIN_MESSAGE, image, null, null);
    }


    private void getAccount(String title, PremiumAccount account, PremiumAccount[] result) {
        final SingleXFrameApplication app = (SingleXFrameApplication) context.getApplication();
        final AccountDialog dialog = new AccountDialog(app.getMainFrame(), title, account);
        try {
            app.prepareDialog(dialog, true);
        } catch (IllegalStateException e) {
            LogUtils.processException(logger, e);
        }
        result[0] = dialog.getAccount();
    }


    public String askForCaptcha(final Icon image) throws Exception {
        synchronized (captchaLock) {
            captchaResult = "";
            if (!EventQueue.isDispatchThread()) {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        askCaptcha(image);
                    }
                });
            } else askCaptcha(image);
            if (image instanceof ImageIcon) {
                ImageIcon icon = (ImageIcon) image;
                icon.getImage().flush();
            }
            return captchaResult;
        }
    }
}
