/**
 * @author Ladislav Vitasek
 */
package cz.vity.freerapid.utilities.os;

import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;

final public class SystemCommanderFactory {
    private static SystemCommanderFactory ourInstance = new SystemCommanderFactory();
    private SystemCommander commander = null;

    public static SystemCommanderFactory getInstance() {
        return ourInstance;
    }

    private SystemCommanderFactory() {
    }

    public SystemCommander getSystemCommanderInstance(ApplicationContext context) {
        if (commander == null) {
//            commander = new LinuxCmdUtils(context.getLocalStorage().getDirectory());
            if (Utils.isWindows())
                commander = new NirCmdUtils();
            else {
                commander = new LinuxCmdUtils(context.getLocalStorage().getDirectory());
            }
        }
        return commander;
    }
}
