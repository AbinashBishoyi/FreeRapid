package cz.vity.freerapid.core;

import java.util.Hashtable;
import java.util.Map;
import java.util.prefs.AbstractPreferences;
import java.util.prefs.BackingStoreException;

/**
 * @author Vity
 */
final class WinPreferences extends AbstractPreferences {

    private final Map<String, String> properties = new Hashtable<String, String>();

    public WinPreferences() {
        super(null, "");
    }

    protected String[] childrenNamesSpi() throws BackingStoreException {
        return new String[]{"asd"};
    }

    protected AbstractPreferences childSpi(String name) {
        return this;
    }

    protected void flushSpi() throws BackingStoreException {

    }

    protected String getSpi(String key) {
        return properties.get(key);
    }

    @SuppressWarnings({"ToArrayCallWithZeroLengthArrayArgument"})
    protected String[] keysSpi() throws BackingStoreException {
        return properties.keySet().toArray(new String[0]);
    }

    protected void putSpi(String key, String value) {
        properties.put(key, value);
    }

    protected void removeNodeSpi() throws BackingStoreException {

    }

    protected void removeSpi(String key) {
        properties.remove(key);
    }

    protected void syncSpi() throws BackingStoreException {

    }
}
