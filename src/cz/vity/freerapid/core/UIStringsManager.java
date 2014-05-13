package cz.vity.freerapid.core;

import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;

/**
 * @author Vity
 */
final public class UIStringsManager {
    private static boolean loaded = false;

    public static void load(ResourceManager manager) {
        if (loaded)
            return;

        final ResourceMap map = manager.getResourceMap(UIStringsManager.class);

        for (String key : map.getBundleNames()) {
            UIManager.put(key, map.getString(key));
        }
        loaded = true;
    }
}
