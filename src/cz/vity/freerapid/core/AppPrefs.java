package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.LocalStorage;
import org.jdesktop.application.ResourceMap;

import java.io.*;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * Sprava uzivatelskych properties
 *
 * @author Vity
 */
public final class AppPrefs {
    private final static Logger logger = Logger.getLogger(AppPrefs.class.getName());

    /**
     * Soubor pod kterym jsou polozky ulozeny
     */
    private final String propertiesFileName;

    private static volatile Preferences properties;
    private final ApplicationContext context;
    private String userNode;

    AppPrefs(ApplicationContext context, Map<String, String> properties, boolean resetOptions) {
        this.context = context;
        final String id = context.getResourceMap().getString("Application.id");
        if (id == null || id.isEmpty())
            throw new IllegalStateException("Config property Application.ID is empty!");
        this.propertiesFileName = id.toLowerCase() + ".xml";
        AppPrefs.properties = loadProperties();
        if (resetOptions) {
            try {
                AppPrefs.properties.clear();
            } catch (BackingStoreException e) {
                LogUtils.processException(logger, e);
            }
        }
        if (!properties.isEmpty()) {
            for (Map.Entry<String, String> entry : properties.entrySet()) {
                final String value = entry.getValue();
                if ("default".equals(value))
                    AppPrefs.properties.remove(entry.getKey());
                else AppPrefs.properties.put(entry.getKey(), value);
            }
        }
    }


    /**
     * Vrati nastaveni z properties fajlu
     *
     * @param key          klic property
     * @param defaultValue defaultni hodnota, ktera se pouzije pokud neni hodnota nalezena
     * @return hodnota uzivatelskeho nastaveni
     */
    public static int getProperty(final String key, final int defaultValue) {
        return getPreferences().getInt(key, defaultValue);
    }

    /**
     * Vrati nastaveni z properties fajlu
     *
     * @param key          klic property
     * @param defaultValue defaultni hodnota, ktera se pouzije pokud neni hodnota nalezena
     * @return hodnota uzivatelskeho nastaveni
     */
    public static boolean getProperty(final String key, final boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    /**
     * Vrati nastaveni z properties fajlu. Pokud neni hodnota klice nalezena, vraci null!
     *
     * @param key klic property
     * @return hodnota uzivatelskeho nastaveni
     */
    public static String getProperty(final String key) {
        return getPreferences().get(key, null);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     *
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final boolean value) {
        getPreferences().putBoolean(key, value);
    }


    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     *
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final String value) {
        getPreferences().put(key, value);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     *
     * @param key   hodnota klice
     * @param value hodnota uzivatelskeho nastaveni
     */
    public static void storeProperty(final String key, final int value) {
        getPreferences().putInt(key, value);
    }

    /**
     * Provede ulozeni uzivatelskeho nastaveni do Properties
     *
     * @param key          hodnota klice
     * @param defaultValue hodnota uzivatelskeho nastaveni
     * @return String value
     */
    public static String getProperty(final String key, final String defaultValue) {
        return getPreferences().get(key, defaultValue);
    }


    /**
     * Odstraneni klic-hodnota z properties fajlu
     *
     * @param key klic property k odstaneni
     */
    public static void removeProperty(final String key) {
        getPreferences().remove(key);
    }

    /**
     * Provede ulozeni properties do souboru definovaneho systemem. Uklada se do XML.
     */
    public void store() {
        OutputStream outputStream = null;
        try {
            if (!getProperty(FWProp.PROXY_SAVEPASSWORD, false))
                removeProperty(FWProp.PROXY_PASSWORD);
            final LocalStorage localStorage = context.getLocalStorage();
            final File outDir = localStorage.getDirectory();
            outDir.mkdirs();
            //outputStream = localStorage.openOutputFile(DEFAULT_PROPERTIES);
            outputStream = new FileOutputStream(new File(outDir, propertiesFileName));
            getPreferences().exportNode(outputStream);
            outputStream.close();
            logger.config("Preferences were saved successfuly");
        } catch (IOException e) {
            try {
                if (outputStream != null)
                    outputStream.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, e.getMessage(), ex);
            }
            logger.severe("Couldn't save app getPreferences(). This is a fatal error. Please reinstall the application.");
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
    }

    private String getUserNode() {
        if (userNode == null) {
            final ResourceMap map = context.getResourceMap();
            final String vendor = map.getString("Application.vendorId");
            final String applicationID = map.getString("Application.id");
            if (vendor.isEmpty() || applicationID.isEmpty())
                logger.warning("AppPrefs - vendor or application ID is empty");
            userNode = vendor.toLowerCase() + "/" + applicationID.toLowerCase();
        }
        return userNode;
    }

    /**
     * Provede nacteni properties ze souboru definovaneho systemem. Pokud nacteni selze, vraci prazdne getPreferences().
     * Properties se nacitaji z XML.
     *
     * @return User Preferences instance
     */
    private Preferences loadProperties() {
        final LocalStorage localStorage = context.getLocalStorage();
        final File storageDir = localStorage.getDirectory();
        final File userFile = new File(storageDir, propertiesFileName);
        if (!(userFile.exists())) {
            logger.log(Level.CONFIG, "File with user settings " + userFile + " was not found. First run. Using default settings");
            return Preferences.userRoot().node(getUserNode());
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(new File(storageDir, propertiesFileName));
            //props.loadFromXML(inputStream);
            Preferences.importPreferences(inputStream);
            inputStream.close();
        } catch (FileNotFoundException e) {
            logger.log(Level.CONFIG, "User preferences file was not found (first application start?)");
        } catch (Exception e) {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            logger.log(Level.WARNING, e.getMessage(), e);
        }
        return Preferences.userRoot().node(getUserNode());
    }

    public static Preferences getPreferences() {
        if (properties == null)
            throw new IllegalStateException("Properties were not initialized yet");
        return properties;
    }
}