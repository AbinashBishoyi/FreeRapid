package org.jdesktop.application.utils;

import java.security.AccessController;
import java.security.PrivilegedAction;

/**
 *
 * Class containing help methods on application level.
 * @author Vity
 */
public final class AppHelper {

    private static PlatformType activePlatformType = null;

    private AppHelper() {
    }


    /*
    * Defines the default value for the platform resource,
    * either "osx" or "default".
    */
    public static PlatformType getPlatform() {
        if (activePlatformType != null)
            return activePlatformType;
        activePlatformType = PlatformType.DEFAULT;
        PrivilegedAction<String> doGetOSName = new PrivilegedAction<String>() {

            @Override
            public String run() {
                return System.getProperty("os.name");
            }
        };

        String osName = AccessController.doPrivileged(doGetOSName);
        if (osName != null) {
            osName = osName.toLowerCase();
            for (PlatformType platformType : PlatformType.values()) {
                for (String pattern : platformType.getPatterns()) {
                    if (osName.startsWith(pattern)) {
                        return activePlatformType = platformType;
                    }
                }
            }
        }
        return activePlatformType = PlatformType.DEFAULT;
    }
}
