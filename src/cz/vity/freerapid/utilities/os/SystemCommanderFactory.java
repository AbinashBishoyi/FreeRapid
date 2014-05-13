/**
 * @author Ladislav Vitasek
 */
package cz.vity.freerapid.utilities.os;

import org.jdesktop.application.ApplicationContext;

public class SystemCommanderFactory {
    private static SystemCommanderFactory ourInstance = new SystemCommanderFactory();
    private SystemCommander commander = null;

    public static SystemCommanderFactory getInstance() {
        return ourInstance;
    }

    private SystemCommanderFactory() {
    }

    public SystemCommander getSystemCommanderInstance(ApplicationContext context) {
        if (commander == null) {
            commander = new LinuxCmdUtils(context.getLocalStorage().getDirectory());
//            if (Utils.isWindows())
//                commander = new NirCmdUtils();
//            else {
//                commander = new LinuxCmdUtils(context.getLocalStorage().getDirectory());
//            }
        }
        return commander;
    }
}
