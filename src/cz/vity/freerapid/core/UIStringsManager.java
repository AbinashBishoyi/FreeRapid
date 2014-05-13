package cz.vity.freerapid.core;

import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Creates localization for Java internal SWING strings
 *
 * @author Vity
 */
final public class UIStringsManager {
    private static boolean loaded = false;

    /**
     * Do not instantiate UIStringsManager.
     */
    private UIStringsManager() {
    }

    /**
     * Loads strings for associated by current locale into UIManager
     *
     * @param manager resource manager for creating resource maps
     * @see javax.swing.UIManager
     */
    public static void load(ResourceManager manager) {
        if (loaded)
            return;

        final ResourceMap map = manager.getResourceMap(UIStringsManager.class);
        final List<String> stringList = map.getBundleNames();
        final ResourceBundle bundle = ResourceBundle.getBundle(stringList.get(0));
        for (String key : bundle.keySet()) {
            UIManager.put(key, bundle.getString(key));
        }
        loaded = true;
    }
}
