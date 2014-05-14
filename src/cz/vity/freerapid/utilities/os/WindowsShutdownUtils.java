package cz.vity.freerapid.utilities.os;

import com.sun.jna.Native;
import com.sun.jna.Structure;
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
        final Advapi32 advapi32 = loadAdvApi32();

        final WinNT.HANDLEByReference token = new WinNT.HANDLEByReference();
        advapi32.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), TOKEN_ADJUST_PRIVILEGES, token);

        final WinNT.LUID luid = new WinNT.LUID();
        advapi32.LookupPrivilegeValue(null, SE_SHUTDOWN_NAME, luid);

        final TOKEN_PRIVILEGES tp = new TOKEN_PRIVILEGES(1);
        tp.Privileges[0] = new LUID_AND_ATTRIBUTES(luid, new WinDef.DWORD(SE_PRIVILEGE_ENABLED));
        advapi32.AdjustTokenPrivileges(token.getValue(), false, tp, 0, null, new IntByReference(0));
    }

    private final static int EWX_LOGOFF = 0x00;
    private final static int EWX_POWEROFF = 0x08;
    private final static int EWX_REBOOT = 0x02;
    private final static int EWX_FORCE = 0x04;
    private final static int EWX_FORCEIFHUNG = 0x10;
    private final static int SHTDN_REASON_FLAG_PLANNED = 0x80000000;
    private final static int TOKEN_ADJUST_PRIVILEGES = 0x0020;
    private final static int SE_PRIVILEGE_ENABLED = 0x00000002;
    private final static String SE_SHUTDOWN_NAME = "SeShutdownPrivilege";

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

    private static interface Advapi32 extends StdCallLibrary {
        public boolean OpenProcessToken(WinNT.HANDLE ProcessHandle, int DesiredAccess, WinNT.HANDLEByReference TokenHandle);

        public boolean LookupPrivilegeValue(String lpSystemName, String lpName, WinNT.LUID lpLuid);

        public boolean AdjustTokenPrivileges(WinNT.HANDLE TokenHandle, boolean DisableAllPrivileges, TOKEN_PRIVILEGES NewState, int BufferLength, TOKEN_PRIVILEGES PreviousState, IntByReference ReturnLength);
    }

    private static Advapi32 loadAdvApi32() {
        return (Advapi32) Native.loadLibrary("advapi32", Advapi32.class);
    }

    private static class TOKEN_PRIVILEGES extends Structure {
        public WinDef.DWORD PrivilegeCount;
        public LUID_AND_ATTRIBUTES[] Privileges;

        public TOKEN_PRIVILEGES(int nbOfPrivileges) {
            PrivilegeCount = new WinDef.DWORD(nbOfPrivileges);
            Privileges = new LUID_AND_ATTRIBUTES[nbOfPrivileges];
        }
    }

    private static class LUID_AND_ATTRIBUTES extends Structure {
        public WinNT.LUID Luid;
        public WinDef.DWORD Attributes;

        public LUID_AND_ATTRIBUTES(WinNT.LUID luid, WinDef.DWORD attributes) {
            Luid = luid;
            Attributes = attributes;
        }
    }

}
