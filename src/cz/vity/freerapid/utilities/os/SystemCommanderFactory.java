package cz.vity.freerapid.utilities.os;

import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;

import java.io.File;

/**
 * @author Ladislav Vitasek
 */
final public class SystemCommanderFactory {
    private final static SystemCommanderFactory ourInstance = new SystemCommanderFactory();
    private SystemCommander commander = null;

    static {
        System.setProperty("jna.nounpack", "true");
        System.setProperty("jna.boot.library.path", new File("lib").getAbsolutePath());
    }

    public static SystemCommanderFactory getInstance() {
        return ourInstance;
    }

    private SystemCommanderFactory() {
    }

    public SystemCommander getSystemCommanderInstance(ApplicationContext context) {
        if (commander == null) {
            if (Utils.isWindows()) {
                commander = new WindowsCommander();
            } else {
                commander = new UnixCommander(context.getLocalStorage().getDirectory());
            }
        }
        return commander;
    }
}
