package cz.vity.freerapid.utilities;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final public class NirCmdUtils {
    private final static Logger logger = Logger.getLogger(NirCmdUtils.class.getName());
    private final static String PATH = "tools/nircmd/nircmd.exe";

    public enum ShutdownType {
        AUTOSHUTDOWN_HIBERNATE,
        AUTOSHUTDOWN_SHUTDOWN,
        AUTOSHUTDOWN_REBOOT,
        AUTOSHUTDOWN_STANDBY,
    }

    public boolean createDesktopShortcut(final String exeName, final String shortcutTitle, final String iconName) {
        //shortcut [filename] [folder] [shortcut title] {arguments} {icon file} {icon resource number} {ShowCmd} {Start In Folder} {Hot Key}
        return createShortCut(exeName, shortcutTitle, iconName, "~$folder.desktop$", "");
    }

    public boolean createStartMenuShortcut(final String exePath, final String shortcutTitle, final String iconPath) {
        return createShortCut(exePath, shortcutTitle, iconPath, "~$folder.programs$", "");
    }

    public boolean createStartupShortcut(final String exePath, final String shortcutTitle, final String iconPath) {
        return createShortCut(exePath, shortcutTitle, iconPath, "~$folder.startup$", "\"\" \"min\"");
    }

    private boolean createShortCut(final String exeName, final String shortcutTitle, final String iconPath, final String type, final String moreCommands) {
        final String appPath = Utils.getAppPath();
        final String exe = Utils.addFileSeparator(appPath) + exeName;
        final String icon = Utils.addFileSeparator(appPath) + iconPath;

        final String cmd = String.format("shortcut \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" %s", exe, type, shortcutTitle, "", (iconPath == null) ? "" : icon, moreCommands);
        return runCommand(cmd, true);
    }

    public boolean shutDown(ShutdownType type, final boolean force) {
        String command = "";
        switch (type) {
            case AUTOSHUTDOWN_HIBERNATE:
                command = "hibernate";
                break;
            case AUTOSHUTDOWN_STANDBY:
                command = "standby";
                break;
            case AUTOSHUTDOWN_REBOOT:
                command = "exitwin reboot";
                break;
            case AUTOSHUTDOWN_SHUTDOWN:
                command = "exitwin shutdown";
                break;
            default:
                assert false;
                break;
        }
        if (force)
            command += " force";
        return runCommand("cmdwait 2200 " + command, false);
    }


    private static boolean runCommand(final String cmd, final boolean waitForResult) {
        if (!Utils.isWindows())
            return true;
        logger.info("NirCmd command:" + cmd);
        try {
            final String command = Utils.addFileSeparator(Utils.getAppPath()) + PATH;
            final Process process = Runtime.getRuntime().exec(command + " " + cmd);
            if (waitForResult) {
                process.waitFor();
                return process.exitValue() == 0;
            } else return true;
        } catch (IOException e) {
            logger.warning("NirCmd command:" + cmd);
            LogUtils.processException(logger, e);
            return false;
        } catch (InterruptedException e) {
            LogUtils.processException(logger, e);
            return false;
        }
    }
}
