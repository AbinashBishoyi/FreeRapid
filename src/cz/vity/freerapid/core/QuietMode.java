package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Sound;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.ApplicationContext;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
public final class QuietMode {

    private final static Logger logger = Logger.getLogger(QuietMode.class.getName());
    private final static QuietMode INSTANCE = new QuietMode();
    private final ApplicationContext context = MainApp.getAContext();

    public static QuietMode getInstance() {
        return INSTANCE;
    }

    private QuietMode() {
    }

    public boolean isEnabled() {
        return AppPrefs.getProperty(UserProp.QUIET_MODE_ENABLED, UserProp.QUIET_MODE_ENABLED_DEFAULT);
    }

    public void setEnabled(final boolean enabled) {
        AppPrefs.storeProperty(UserProp.QUIET_MODE_ENABLED, enabled);
    }

    public boolean isActive() {
        if (isEnabled()) {
            final String mode = AppPrefs.getProperty(UserProp.QUIET_MODE_ACTIVATION_MODE, UserProp.QUIET_MODE_ACTIVATION_MODE_DEFAULT);
            if (UserProp.QUIET_MODE_ACTIVATION_ALWAYS.equals(mode)) {
                return true;
            } else if (UserProp.QUIET_MODE_ACTIVATION_WHEN_WINDOWS_FOUND.equals(mode)) {
                return findWindow();
            }
        }
        return false;
    }

    private boolean findWindow() {
        final List<String> windows;
        try {
            windows = SystemCommanderFactory.getInstance().getSystemCommanderInstance(context).getTopLevelWindowsList();
        } catch (final IOException e) {
            LogUtils.processException(logger, e);
            return false;
        }
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Detected system windows " + Arrays.toString(windows.toArray(new String[windows.size()])));
        }
        final boolean caseSensitive = AppPrefs.getProperty(UserProp.QUIET_MODE_CASE_SENSITIVE_SEARCH, UserProp.QUIET_MODE_CASE_SENSITIVE_SEARCH_DEFAULT);
        final List<String> stringsToFind = getActivationStrings();
        if (!caseSensitive) {
            for (int i = 0; i < stringsToFind.size(); i++) {
                stringsToFind.set(i, stringsToFind.get(i).toLowerCase(Locale.ENGLISH));
            }
        }
        for (String window : windows) {
            if (!caseSensitive) {
                window = window.toLowerCase(Locale.ENGLISH);
            }
            //i would use String#matches
            for (String stringToFind : stringsToFind) {
                if (window.contains(stringToFind)) {
                    logger.info("Quiet mode is active");
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isSoundsDisabled() {
        return AppPrefs.getProperty(UserProp.QUIET_MODE_NO_SOUNDS, UserProp.QUIET_MODE_NO_SOUNDS_DEFAULT);
    }

    public boolean isCaptchaDisabled() {
        return AppPrefs.getProperty(UserProp.QUIET_MODE_NO_CAPTCHA, UserProp.QUIET_MODE_NO_CAPTCHA_DEFAULT);
    }

    public boolean isDialogsDisabled() {
        return AppPrefs.getProperty(UserProp.QUIET_MODE_NO_CONFIRM_DIALOGS, UserProp.QUIET_MODE_NO_CONFIRM_DIALOGS_DEFAULT);
    }

    public void playUserInteractionRequiredSound() {
        if (AppPrefs.getProperty(UserProp.QUIET_MODE_PLAY_SOUND_ON_ACTIVATE, UserProp.QUIET_MODE_PLAY_SOUND_ON_ACTIVATE_DEFAULT)) {
            Sound.playSound(context.getResourceMap().getString("userInteractionRequiredWav"), false);
        }
    }

    public List<String> getActivationStrings() {
        final List<String> list = new ArrayList<String>();
        final String quietModeActivationStrings = AppPrefs.getProperty(UserProp.QUIET_MODE_ACTIVATION_STRINGS, UserProp.QUIET_MODE_ACTIVATION_STRINGS_DEFAULT);
        for (String string : quietModeActivationStrings.split("(?<!\\\\)\\|")) {
            string = string.trim();
            if (!string.isEmpty()) {
                list.add(string.replace("\\|", "|"));
            }
        }
        return list;
    }

    public void setActivationStrings(final List<String> list) {
        final StringBuilder sb = new StringBuilder();
        for (final String string : list) {
            sb.append(string.replace("|", "\\|")).append('|');
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        AppPrefs.storeProperty(UserProp.QUIET_MODE_ACTIVATION_STRINGS, sb.toString());
    }

}
