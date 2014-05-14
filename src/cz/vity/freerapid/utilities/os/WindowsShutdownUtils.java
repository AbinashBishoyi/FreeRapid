package cz.vity.freerapid.utilities.os;

import com.sun.jna.Native;
import com.sun.jna.win32.StdCallLibrary;

/**
 * @author ntoskrnl
 */
final class WindowsShutdownUtils {

    private WindowsShutdownUtils() {
    }

    public static boolean shutdown(final boolean force) {
        int flags = EWX_POWEROFF | EWX_FORCEIFHUNG;
        if (force) flags |= EWX_FORCE;
        return User32.INSTANCE.ExitWindowsEx(flags, SHTDN_REASON_FLAG_PLANNED);
    }

    public static boolean reboot(final boolean force) {
        int flags = EWX_REBOOT | EWX_FORCEIFHUNG;
        if (force) flags |= EWX_FORCE;
        return User32.INSTANCE.ExitWindowsEx(flags, SHTDN_REASON_FLAG_PLANNED);
    }

    public static boolean standby() {
        return PowrProf.INSTANCE.SetSuspendState(false, false, false);
    }

    public static boolean hibernate() {
        return PowrProf.INSTANCE.SetSuspendState(true, false, false);
    }

    private final static int EWX_LOGOFF = 0x00;
    private final static int EWX_POWEROFF = 0x08;
    private final static int EWX_REBOOT = 0x02;
    private final static int EWX_FORCE = 0x04;
    private final static int EWX_FORCEIFHUNG = 0x10;
    private final static int SHTDN_REASON_FLAG_PLANNED = 0x80000000;

    private static interface User32 extends StdCallLibrary {
        public final static User32 INSTANCE = (User32) Native.loadLibrary("user32", User32.class);

        public boolean ExitWindowsEx(int uFlags, int dwReason);
    }

    private static interface PowrProf extends StdCallLibrary {
        public final static PowrProf INSTANCE = (PowrProf) Native.loadLibrary("powrprof", PowrProf.class);

        public boolean SetSuspendState(boolean Hibernate, boolean ForceCritical, boolean DisableWakeEvent);
    }

}
