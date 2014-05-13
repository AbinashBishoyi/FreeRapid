package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * <p>Class that provides basic functions to show dialogs for plugins.
 * </p>
 *
 * @author Ladislav Vitasek
 */
public interface DialogSupport {
    /**
     * Method shows simple input dialog with username and password fields and OK/Cancel buttons
     *
     * @param premiumAccount object with data about username and password
     * @param dialogTitle    title for dialog - usually plugin's name
     * @return if user pressed OK, returns updated instance of the premiumAccount parameter , otherwise it returns null value
     * @throws Exception if something went wrong
     */
    PremiumAccount showAccountDialog(PremiumAccount premiumAccount, String dialogTitle) throws Exception;

    /**
     * Shows simple OK/Cancel dialog with given content of given container
     *
     * @param container   a container with SWING components to show
     * @param dialogTitle title for dialog
     * @return returns true, if user pressed OK button, false otherwise
     * @throws Exception if something went wrong or thread was interrupted
     */
    boolean showOKCancelDialog(Component container, String dialogTitle) throws Exception;

    /**
     * Shows simple dialog with OK button with given content of given container
     *
     * @param container   a container with SWING components to show
     * @param dialogTitle title for dialog
     * @throws Exception if something went wrong or thread was interrupted
     */
    public void showOKDialog(Component container, String dialogTitle) throws Exception;

    /**
     * Shows default simple input dialog for getting input from user.
     *
     * @param image CAPTCHA image to show user
     * @return returns string given from user - returns null if user cancelled dialog; method can return empty string
     * @throws Exception if something went wrong or thread was interrupted
     */
    public String askForCaptcha(final BufferedImage image) throws Exception;
}
