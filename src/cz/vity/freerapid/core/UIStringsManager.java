package cz.vity.freerapid.core;

import org.jdesktop.application.ResourceManager;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;

/**
 * @author Vity
 */
public class UIStringsManager {


    public static void load(ResourceManager manager) {

        final ResourceMap map = manager.getResourceMap(UIStringsManager.class);

        for (String key : map.getBundleNames()) {
            UIManager.put(key, map.getString(key));
        }
    }
}
