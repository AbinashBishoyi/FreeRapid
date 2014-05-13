package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ladislav Vitasek
 */
public interface DialogSupport {

    PremiumAccount showAccountDialog(PremiumAccount premiumAccount, String dialogTitle) throws Exception;

    boolean showOKCancelDialog(Component container, String dialogTitle) throws Exception;

    public void showOKDialog(Component container, String dialogTitle) throws Exception;

    public String askForCaptcha(final BufferedImage image) throws Exception;
}
