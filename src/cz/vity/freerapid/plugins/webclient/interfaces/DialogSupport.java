package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * @author Ladislav Vitasek
 */
public interface DialogSupport {

    PremiumAccount showAccountDialog(Class clazz) throws Exception;

    int showOKCancelDialog(Component container) throws Exception;

    public String askForCaptcha(final BufferedImage image) throws Exception;
}
