package cz.vity.freerapid.utilities.os;

import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
class LinuxCmdUtils implements SystemCommander {
    public static final String SYSTEM_COMMAND_PROPERTIES_FILE = "syscmd.properties";
    private final static Logger logger = Logger.getLogger(LinuxCmdUtils.class.getName());


    private Properties commands;

    LinuxCmdUtils(final File homeDirectory) {
        init(homeDirectory);
    }

    private void init(File homeDirectory) {
        File file = new File(homeDirectory, SYSTEM_COMMAND_PROPERTIES_FILE);
        if (file.isFile() && file.exists()) {
            commands = Utils.loadProperties(file.getAbsolutePath(), false);
        } else {
            file = new File(Utils.getAppPath(), SYSTEM_COMMAND_PROPERTIES_FILE);
            if (file.isFile() && file.exists()) {
                commands = Utils.loadProperties(file.getAbsolutePath(), false);
            } else commands = new Properties();
        }
    }

    public boolean createShortCut(final OSCommand shortCutCommand) {
        if (!OSCommand.shortCutCommands.contains(shortCutCommand))
            throw new IllegalArgumentException("OS command " + shortCutCommand + " is not a shortcut command");
        switch (shortCutCommand) {
            case CREATE_DESKTOP_SHORTCUT:
                return createDesktopShortcut();
            case CREATE_QUICKLAUNCH_SHORTCUT:
                return createQuickLaunchShortcut();
            case CREATE_STARTMENU_SHORTCUT:
                return createStartMenuShortcut();
            case CREATE_STARTUP_SHORTCUT:
                return createStartupShortcut();
            default:
                assert false;
                break;
        }
        return false;
    }

    private boolean createDesktopShortcut() {
        return makeShortcut(OSCommand.CREATE_DESKTOP_SHORTCUT);
    }

    private boolean makeShortcut(final OSCommand type) {
        return isSupported(type) && runCommand(getCommand(type), true);
    }

    private static String prepareCommand(String cmd) {
        final String appPath = Utils.getAppPath();
        final String appPathSep = Utils.addFileSeparator(appPath);
        cmd = cmd.replaceAll("%DIR%", appPath);
        cmd = cmd.replaceAll("%VERSION%", Consts.VERSION);
        cmd = cmd.replaceAll("%APPVERSION%", Consts.APPVERSION);
        cmd = cmd.replaceAll("%AUTHORS%", Consts.AUTHORS);
        cmd = cmd.replaceAll("%PRODUCT%", Consts.PRODUCT);
        cmd = cmd.replaceAll("%ICON_PATH%", appPathSep + Consts.LINUX_ICON_NAME);
        cmd = cmd.replaceAll("%SH_SCRIPT_PATH%", appPathSep + Consts.LINUX_SHELL_SCRIPT);
        return cmd;
    }


    private static boolean runCommand(String cmd, final boolean waitForResult) {
        cmd = prepareCommand(cmd);
        logger.info("Xcommand:" + cmd);
        try {
            final Process process = Runtime.getRuntime().exec(cmd);
            if (waitForResult) {
                process.waitFor();
                return process.exitValue() == 0;
            } else return true;
        } catch (IOException e) {
            logger.warning("XCommand command:" + cmd);
            LogUtils.processException(logger, e);
            return false;
        } catch (InterruptedException e) {
            LogUtils.processException(logger, e);
            return false;
        }
    }

    private boolean createStartMenuShortcut() {
        return makeShortcut(OSCommand.CREATE_STARTMENU_SHORTCUT);
    }

    private boolean createStartupShortcut() {
        return makeShortcut(OSCommand.CREATE_STARTUP_SHORTCUT);
    }

    private boolean createQuickLaunchShortcut() {
        return makeShortcut(OSCommand.CREATE_QUICKLAUNCH_SHORTCUT);
    }

    public boolean shutDown(OSCommand shutDownCommand, boolean force) {
        if (!OSCommand.shutDownCommands.contains(shutDownCommand))
            throw new IllegalArgumentException("OS command " + shutDownCommand + " is not a shut down command");
        return isSupported(shutDownCommand) && runCommand(getCommand(shutDownCommand), false);
    }

    public boolean isSupported(OSCommand command) {
        final String cmd = getKey(command);
        return commands.contains(cmd) && !(commands.getProperty(cmd, "").trim().isEmpty());
    }

    private String getCommand(OSCommand command) {
        return commands.getProperty(getKey(command), "");
    }

    private static String getKey(OSCommand command) {
        return command.toString().toLowerCase();
    }
}
