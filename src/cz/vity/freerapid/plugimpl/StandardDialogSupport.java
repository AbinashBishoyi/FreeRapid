package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.plugins.webclient.interfaces.DialogSupport;
import cz.vity.freerapid.swing.Swinger;
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

    public PremiumAccount showAccountDialog(Class clazz) {
        return null;
    }

    public int showOKCancelDialog(Component container) {
        return 0;
    }

    public String askForCaptcha(final BufferedImage image) throws Exception {
        synchronized (captchaLock) {
            captchaResult = "";
            SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    if (AppPrefs.getProperty(UserProp.ACTIVATE_WHEN_CAPTCHA, UserProp.ACTIVATE_WHEN_CAPTCHA_DEFAULT))
                        Swinger.bringToFront(((SingleFrameApplication) context.getApplication()).getMainFrame(), true);
                    captchaResult = (String) JOptionPane.showInputDialog(null, context.getResourceMap(DownloadTask.class).getString("InsertWhatYouSee"), context.getResourceMap(DownloadTask.class).getString("InsertCaptcha"), JOptionPane.PLAIN_MESSAGE, new ImageIcon(image), null, null);

                }
            });
            image.flush();
            return captchaResult;
        }
    }
}
