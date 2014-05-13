package cz.vity.freerapid.plugins.dev.plugimpl;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import org.jdesktop.application.ApplicationContext;

/**
 * Configuration locale storage for testing purposes in test plugin application
 *
 * @author Ladislav Vitasek
 */
public class DevStorageSupport implements ConfigurationStorageSupport {
    /**
     * Field context
     */
    private final ApplicationContext context;

    /**
     * Constructor - creates a new DevStorageSupport instance.
     *
     * @param context application context
     */
    public DevStorageSupport(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public Object loadConfigFromFile(String fileName) throws Exception {
        return context.getLocalStorage().load(fileName);
    }

    @Override
    public void storeConfigToFile(Object object, String fileName) throws Exception {
        context.getLocalStorage().save(object, fileName);
    }

}