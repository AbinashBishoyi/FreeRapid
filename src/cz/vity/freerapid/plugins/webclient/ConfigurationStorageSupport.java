package cz.vity.freerapid.plugins.webclient;

/**
 * @author Ladislav Vitasek
 */
public interface ConfigurationStorageSupport {

    Object loadConfigFromFile(final String fileName) throws Exception;

    void storeConfigToFile(final Object object, final String fileName) throws Exception;
}
