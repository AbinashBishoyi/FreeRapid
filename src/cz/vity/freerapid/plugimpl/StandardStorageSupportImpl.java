package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import org.jdesktop.application.ApplicationContext;

/**
 * @author Ladislav Vitasek
 */
public class StandardStorageSupportImpl implements ConfigurationStorageSupport {
    private final ApplicationContext context;

    public StandardStorageSupportImpl(ApplicationContext context) {
        this.context = context;
    }

    public Object loadConfigFromFile(String fileName) throws Exception {
        return context.getLocalStorage().load(fileName);
    }

    public void storeConfigToFile(Object object, String fileName) throws Exception {
        context.getLocalStorage().save(object, fileName);
    }

}
