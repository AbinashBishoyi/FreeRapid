package cz.vity.freerapid.utilities.os;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author ntoskrnl
 */
final class WindowsShutdownUtils {

    private WindowsShutdownUtils() {
    }

    public static boolean shutdown(final boolean force) {
        return exitWindowsEx(EWX_POWEROFF, force);
    }

    public static boolean reboot(final boolean force) {
        return exitWindowsEx(EWX_REBOOT, force);
    }

    public static boolean logout(final boolean force) {
        return exitWindowsEx(EWX_LOGOFF, force);
    }

    public static boolean standby() {
        return setSuspendState(false);
    }

    public static boolean hibernate() {
        return setSuspendState(true);
    }

    private static boolean setSuspendState(final boolean hibernate) {
        return loadPowrProf().SetSuspendState(hibernate, false, false);
    }

    private static boolean exitWindowsEx(int flags, final boolean force) {
        flags |= EWX_FORCEIFHUNG;
        if (force) flags |= EWX_FORCE;
        setShutdownPrivileges();
        return loadUser32().ExitWindowsEx(flags, SHTDN_REASON_FLAG_PLANNED);
    }

    private static void setShutdownPrivileges() {
        final WinNT.HANDLEByReference token = new WinNT.HANDLEByReference();
        Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), WinNT.TOKEN_ADJUST_PRIVILEGES, token);

        final WinNT.LUID luid = new WinNT.LUID();
        Advapi32.INSTANCE.LookupPrivilegeValue(null, WinNT.SE_SHUTDOWN_NAME, luid);

        final WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(1);
        tp.Privileges[0] = new WinNT.LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(WinNT.SE_PRIVILEGE_ENABLED));
        Advapi32.INSTANCE.AdjustTokenPrivileges(token.getValue(), false, tp, 0, null, new IntByReference(0));
    }

    private final static int EWX_LOGOFF = 0x00;
    private final static int EWX_POWEROFF = 0x08;
    private final static int EWX_REBOOT = 0x02;
    private final static int EWX_FORCE = 0x04;
    private final static int EWX_FORCEIFHUNG = 0x10;
    private final static int SHTDN_REASON_FLAG_PLANNED = 0x80000000;

    private static interface User32 extends StdCallLibrary {
        public boolean ExitWindowsEx(int uFlags, int dwReason);
    }

    private static User32 loadUser32() {
        return (User32) Native.loadLibrary("user32", User32.class);
    }

    private static interface PowrProf extends StdCallLibrary {
        public boolean SetSuspendState(boolean Hibernate, boolean ForceCritical, boolean DisableWakeEvent);
    }

    private static PowrProf loadPowrProf() {
        return (PowrProf) Native.loadLibrary("powrprof", PowrProf.class);
    }

}
