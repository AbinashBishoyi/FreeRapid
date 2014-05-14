package cz.vity.freerapid.utilities.os;

import java.util.EnumSet;

/**
 * @author Ladislav Vitasek
 */
public enum OSCommand {
    CREATE_DESKTOP_SHORTCUT, CREATE_STARTMENU_SHORTCUT, CREATE_STARTUP_SHORTCUT, CREATE_QUICKLAUNCH_SHORTCUT, HIBERNATE, SHUTDOWN, REBOOT, STANDBY, RESTART_APPLICATION, LIST_TOP_WINDOWS;

    public static EnumSet<OSCommand> shutDownCommands = EnumSet.of(HIBERNATE, SHUTDOWN, REBOOT, STANDBY, RESTART_APPLICATION);
    public static EnumSet<OSCommand> shortCutCommands = EnumSet.of(CREATE_DESKTOP_SHORTCUT, CREATE_STARTMENU_SHORTCUT, CREATE_STARTUP_SHORTCUT, CREATE_QUICKLAUNCH_SHORTCUT);
}
