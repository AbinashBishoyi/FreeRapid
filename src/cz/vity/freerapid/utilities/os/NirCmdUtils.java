package cz.vity.freerapid.utilities.os;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinUser;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final class NirCmdUtils extends AbstractSystemCommander {
    private final static Logger logger = Logger.getLogger(NirCmdUtils.class.getName());
    private final static String PATH = "tools/nircmd/nircmd.exe";

    NirCmdUtils() {
    }

    private boolean createDesktopShortcut() {
        //shortcut [filename] [folder] [shortcut title] {arguments} {icon file} {icon resource number} {ShowCmd} {Start In Folder} {Hot Key}
        return createShortCut(Consts.APPVERSION, "", "~$folder.desktop$", "");
    }

    private boolean createStartMenuShortcut() {
        return createShortCut(Consts.APPVERSION, "", "~$folder.programs$", "");
    }

    private boolean createStartupShortcut() {
        return createShortCut(Consts.APPVERSION, "-m", "~$folder.startup$", "\"\" \"min\"");
    }

    private boolean createQuickLaunchShortcut() {
        return createShortCut(Consts.APPVERSION, "", "~$folder.appdata$\\Microsoft\\Internet Explorer\\Quick Launch", "");
    }

    private boolean startNewApplicationInstance() {
        final String exe;
        if (!System.getProperties().containsKey("exePath")) {
            final String appPath = Utils.getAppPath();
            final String appSep = Utils.addFileSeparator(appPath);
            exe = appSep + Consts.WINDOWS_EXE_NAME;
        } else {
            exe = System.getProperty("exePath");
        }
        return run(exe + " " + Utils.getApplicationArguments(), false);
    }

    private boolean createShortCut(final String shortcutTitle, final String arguments, final String type, final String moreCommands) {
        final String appPath = Utils.getAppPath();
        final String appSep = Utils.addFileSeparator(appPath);
        final String exe = appSep + Consts.WINDOWS_EXE_NAME;
        final String icon = appSep + Consts.WINDOWS_ICON_NAME;

        final String cmd = String.format("shortcut \"%s\" \"%s\" \"%s\" \"%s\" \"%s\" %s", exe, type, shortcutTitle, arguments, icon, moreCommands);
        return runCommand(cmd, true);
    }

    @Override
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

    @Override
    public boolean shutDown(OSCommand shutDownCommand, boolean force) {
        if (!OSCommand.shutDownCommands.contains(shutDownCommand))
            throw new IllegalArgumentException("OS command " + shutDownCommand + " is not a shut down command");
        if (shutDownCommand == OSCommand.RESTART_APPLICATION) {
            return startNewApplicationInstance();
        }
        String command = "";
        switch (shutDownCommand) {
            case HIBERNATE:
                command = "hibernate";
                break;
            case STANDBY:
                command = "standby";
                break;
            case REBOOT:
                command = "exitwin reboot";
                break;
            case SHUTDOWN:
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

    @Override
    public boolean isSupported(OSCommand command) {
        return true;
    }

    private static boolean runCommand(final String cmd, final boolean waitForResult) {
        final String command = Utils.addFileSeparator(Utils.getAppPath()) + PATH;
        return run("\"" + new File(command).getPath() + "\" " + cmd, waitForResult);
    }

    private static boolean run(final String cmd, final boolean waitForResult) {
        if (!Utils.isWindows())
            return true;
        logger.info("System command: " + cmd);
        try {
            final Process process = Runtime.getRuntime().exec(SysCommand.splitCommand(cmd));
            if (waitForResult) {
                process.waitFor();
                return process.exitValue() == 0;
            } else return true;
        } catch (IOException e) {
            logger.warning("Command: " + cmd + " failed for some reason");
            LogUtils.processException(logger, e);
            return false;
        } catch (InterruptedException e) {
            LogUtils.processException(logger, e);
            return false;
        }
    }

    @Override
    public boolean findTopLevelWindow(final String windowTitle, final boolean caseSensitive) throws IOException {
        final String stringToFind = caseSensitive ? windowTitle : windowTitle.toLowerCase();
        final boolean[] result = {false};
        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(final WinDef.HWND hwnd, final Pointer pointer) {
                String title = getWindowTitle(hwnd);
                if (title != null) {
                    if (!caseSensitive) {
                        title = title.toLowerCase();
                    }
                    if (title.contains(stringToFind)) {
                        result[0] = true;
                        return false;
                    }
                }
                return true;
            }
        }, Pointer.NULL);
        return result[0];
    }

    @Override
    public List<String> getTopLevelWindowsList() throws IOException {
        final List<String> list = new LinkedList<String>();
        User32.INSTANCE.EnumWindows(new WinUser.WNDENUMPROC() {
            @Override
            public boolean callback(final WinDef.HWND hwnd, final Pointer pointer) {
                final String title = getWindowTitle(hwnd);
                if (title != null) {
                    list.add(title);
                }
                return true;
            }
        }, Pointer.NULL);
        return list;
    }

    private static String getWindowTitle(final WinDef.HWND hwnd) {
        final char[] name = new char[128];
        final int len = User32.INSTANCE.GetWindowText(hwnd, name, name.length);
        if (len > 0) {
            return new String(name, 0, len);
        } else {
            return null;
        }
    }

}
