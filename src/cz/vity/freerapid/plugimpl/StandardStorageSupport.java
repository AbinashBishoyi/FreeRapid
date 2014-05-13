package cz.vity.freerapid.plugimpl;

import cz.vity.freerapid.plugins.webclient.interfaces.ConfigurationStorageSupport;
import org.jdesktop.application.ApplicationContext;

/**
 * @author Ladislav Vitasek
 */
public class StandardStorageSupport implements ConfigurationStorageSupport {
    private final ApplicationContext context;

    public StandardStorageSupport(ApplicationContext context) {
        this.context = context;
    }

    public Object loadConfigFromFile(String fileName) throws Exception {
        return context.getLocalStorage().load(fileName);
    }

    public void storeConfigToFile(Object object, String fileName) throws Exception {
        context.getLocalStorage().save(object, fileName);
    }

}
