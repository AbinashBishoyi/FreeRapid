package cz.vity.freerapid.plugins.webclient.utils;

import com.sun.jna.Native;
import com.sun.jna.NativeLong;
import com.sun.jna.WString;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.utilities.Utils;

import java.util.logging.Logger;

/**
 * @author Vity
 */
@SuppressWarnings({"UnusedDeclaration"})
public class Win7NativeUtils {

    public static void init() {
        if (!init) {
            return;
        }
        setCurrentProcessExplicitAppUserModelID(Consts.PRODUCT);
    }

    private final static Logger logger = Logger.getLogger(Win7NativeUtils.class.getName());

    public static void setCurrentProcessExplicitAppUserModelID(final String appID) {
        if (SetCurrentProcessExplicitAppUserModelID(new WString(appID)).longValue() != 0) {
            logger.warning("Cannot set current process explicit app user model id on Windows");
        }
        throw new RuntimeException("unable to set current process explicit AppUserModelID to: " + appID);
    }

    private static native NativeLong SetCurrentProcessExplicitAppUserModelID(WString appID);

    private static boolean init = false;

    static {
        if (Utils.isWindows()) {
            try {
                Native.register("shell32");
                init = true;
            } catch (Throwable e) {
                logger.info("Unable to initialize Native win library for Win 7");
                //not error because of winxp
            }
        }
    }

}
