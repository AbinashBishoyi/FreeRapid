package cz.vity.freerapid.utilities;

import org.java.plugin.registry.PluginAttribute;
import org.java.plugin.registry.PluginDescriptor;

import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final public class DescriptorUtils {
    private final static Logger logger = Logger.getLogger(DescriptorUtils.class.getName());

    private DescriptorUtils() {
    }

    public static String getAttribute(final String name, final String defaultValue, final PluginDescriptor descriptor) {
        final PluginAttribute attribute = descriptor.getAttribute(name);
        if (attribute == null) {
            //     logger.warning(name + " attribute was not found in plugin manifest for plugin " + descriptor.getId());
            return defaultValue;
        } else return attribute.getValue();
    }

    public static boolean getAttribute(final String name, final boolean defaultValue, final PluginDescriptor descriptor) {
        final PluginAttribute attribute = descriptor.getAttribute(name);
        if (attribute == null) {
            //   logger.warning(name + " attribute was not found in plugin manifest for plugin " + descriptor.getId());
            return defaultValue;
        } else return "true".equalsIgnoreCase(attribute.getValue());
    }

    public static int getAttribute(final String name, final int defaultValue, final PluginDescriptor descriptor) {
        final PluginAttribute attribute = descriptor.getAttribute(name);
        if (attribute == null) {
            //   logger.warning(name + " attribute was not found in plugin manifest for plugin " + descriptor.getId());
            return defaultValue;
        } else {
            final String value = attribute.getValue();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                LogUtils.processException(logger, e);
                return defaultValue;
            }
        }
    }
}
