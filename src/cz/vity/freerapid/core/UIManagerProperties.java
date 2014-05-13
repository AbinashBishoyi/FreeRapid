package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.util.Map;
import java.util.Properties;

/**
 * @author Vity
 */
public class UIManagerProperties {
    private final static String UI_PROPERTIES_FILENAME_PROPERTY = "UIManager.properties";
    private final ResourceMap map;

    public UIManagerProperties(ResourceMap map) {
        this.map = map;
    }

    public void load() {
        final String fileName = map.getString(UI_PROPERTIES_FILENAME_PROPERTY);
        if (fileName == null)
            return;
        final Properties properties = Utils.loadProperties(Utils.addFileSeparator(map.getResourcesDir()) + fileName, true);
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            UIManager.put(entry.getKey(), entry.getValue());
        }
    }
}
