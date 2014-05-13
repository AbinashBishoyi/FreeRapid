package cz.vity.freerapid.plugins.webclient;

import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
public interface OptionsDialogSupport {

    PremiumAccount showAccountDialog(Class clazz);

    int showOKCancelDialog(Component container);
}
