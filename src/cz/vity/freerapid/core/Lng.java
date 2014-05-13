package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.Utils;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class Lng {
    private final static Logger logger = Logger.getLogger(Lng.class.getName());

    private static List<SupportedLanguage> supportedLanguages = null;

    private static final String LANG_LIST_FILE = "languages.properties";
    private static final String LANG_NONAME_ICON = "blank.gif";
    public static final String localeLanguageCode = Locale.getDefault().getLanguage().toUpperCase();
    private static String selLanguageCode;


    private Lng() {
    }

    public static synchronized List<SupportedLanguage> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new LinkedList<SupportedLanguage>();
            final Properties languages = Utils.loadProperties(LANG_LIST_FILE, true);
            int counter = -1;
            final String lngPostfix = "language", lngNamePostfix = ".name", lngMnemonicPostfix = ".mnemonic", lngIconPostfix = ".icon";
            String lngCode, lngItem;
            Integer mnemonic;
            SupportedLanguage language;
            while ((lngCode = languages.getProperty(lngItem = (lngPostfix + ++counter))) != null) {
                mnemonic = (int) languages.getProperty(lngItem + lngMnemonicPostfix, "\0").charAt(0);
                language = new SupportedLanguage(lngCode, languages.getProperty(lngItem + lngNamePostfix, "?"), languages.getProperty(lngItem + lngIconPostfix, LANG_NONAME_ICON), mnemonic);
                supportedLanguages.add(language);
            }
        }
        return supportedLanguages;
    }

    public static void loadLangProperties() {
        selLanguageCode = AppPrefs.getProperty(FWProp.SELECTED_LANGUAGE, null);
        if (selLanguageCode == null) {
            selLanguageCode = FWProp.DEFAULT_LANG_CODE;
            for (SupportedLanguage supportedLanguage : getSupportedLanguages()) {

                if (supportedLanguage.getLanguageCode().equals(localeLanguageCode)) {
                    selLanguageCode = supportedLanguage.getLanguageCode();
                    break;
                }
            }
            if (!selLanguageCode.equals(localeLanguageCode))
                Locale.setDefault(new Locale(selLanguageCode, Locale.getDefault().getCountry()));
            //AppPrefs.storeProperty(FWProp.SELECTED_LANGUAGE, Locale.getDefault().getLanguage());
        } else {
            boolean found = false;
            for (SupportedLanguage lng : getSupportedLanguages()) {
                if (lng.getLanguageCode().equals(selLanguageCode)) {
                    found = true;
                    break;
                }
            }

            if (found) {
                if (!selLanguageCode.equals(localeLanguageCode))
                    Locale.setDefault(new Locale(selLanguageCode, Locale.getDefault().getCountry()));
            } else {
                selLanguageCode = FWProp.DEFAULT_LANG_CODE;
                for (SupportedLanguage supportedLanguage : getSupportedLanguages()) {

                    if (supportedLanguage.getLanguageCode().equals(localeLanguageCode)) {
                        selLanguageCode = supportedLanguage.getLanguageCode();
                        break;
                    }
                }
                Locale.setDefault(new Locale(selLanguageCode, Locale.getDefault().getCountry()));
            }
        }
    }

    public static SupportedLanguage getSelectedLanguage() {
        for (SupportedLanguage language : getSupportedLanguages()) {
            if (language.getLanguageCode().equals(selLanguageCode))
                return language;
        }
        logger.warning("Language code not found among available languages: " + selLanguageCode);
        return new SupportedLanguage(selLanguageCode);
    }
}
