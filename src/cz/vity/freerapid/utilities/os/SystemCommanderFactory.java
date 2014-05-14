package cz.vity.freerapid.utilities.os;

import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;

/**
 * @author Ladislav Vitasek
 */
final public class SystemCommanderFactory {
    private final static SystemCommanderFactory ourInstance = new SystemCommanderFactory();
    private SystemCommander commander = null;

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
