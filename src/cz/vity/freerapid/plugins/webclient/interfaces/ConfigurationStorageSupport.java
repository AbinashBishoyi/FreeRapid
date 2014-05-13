package cz.vity.freerapid.plugins.webclient.interfaces;

/**
 * @author Ladislav Vitasek
 */
public interface ConfigurationStorageSupport {

    Object loadConfigFromFile(final String fileName) throws Exception;

    void storeConfigToFile(final Object object, final String fileName) throws Exception;
}
