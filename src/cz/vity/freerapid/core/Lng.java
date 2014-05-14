package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import jlibs.xml.sax.binding.BindingHandler;
import org.xml.sax.InputSource;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public final class Lng {
    private static final Logger logger = Logger.getLogger(Lng.class.getName());
    private static final String LANG_LIST_FILE = "languages.xml";
    private static final String LOCALE_LANG_CODE = Locale.getDefault().getLanguage();
    private static final String LOCALE_COUNTRY_CODE = Locale.getDefault().getCountry();

    private static List<SupportedLanguage> supportedLanguages = null;
    private static String selLanguageCode;
    private static String selCountryCode;

    private Lng() {
    }

    public static synchronized List<SupportedLanguage> getSupportedLanguages() {
        if (supportedLanguages == null) {
            final BindingHandler handler = new BindingHandler(LanguageBinding.class);
            try {
                @SuppressWarnings({"unchecked", "UnnecessaryLocalVariable"})
                List<SupportedLanguage> list = (List<SupportedLanguage>) handler.parse(new InputSource(Utils.class.getClassLoader().getResourceAsStream(LANG_LIST_FILE)));
                supportedLanguages = list;
            } catch (Exception e) {
                LogUtils.processException(logger, e);
                supportedLanguages = Collections.emptyList();
            }
        }
        return supportedLanguages;
    }

    private static SupportedLanguage findSupportedLanguage(SupportedLanguage lang) {
        for (SupportedLanguage supportedLanguage : getSupportedLanguages()) {
            if (supportedLanguage.equals(lang)) {
                return supportedLanguage;
            }
        }
        return null;
    }

    public static void loadLangProperties() {
        selLanguageCode = AppPrefs.getProperty(FWProp.SELECTED_LANGUAGE, null);
        SupportedLanguage result;
        if (selLanguageCode == null) {
            result = findSupportedLanguage(new SupportedLanguage(LOCALE_LANG_CODE, LOCALE_COUNTRY_CODE));
            if (result == null) {
                result = findSupportedLanguage(new SupportedLanguage(LOCALE_LANG_CODE, ""));
                if (result == null) {
                    result = new SupportedLanguage(FWProp.DEFAULT_LANG_CODE, "");
                }
            }
        } else {
            selCountryCode = AppPrefs.getProperty(FWProp.SELECTED_COUNTRY, "");
            result = findSupportedLanguage(new SupportedLanguage(selLanguageCode, selCountryCode));
            if (result == null) {
                result = findSupportedLanguage(new SupportedLanguage(selLanguageCode, ""));
                if (result == null) {
                    result = new SupportedLanguage(FWProp.DEFAULT_LANG_CODE, "");
                }
            }
        }

        selLanguageCode = result.getLanguageCode();
        selCountryCode = result.getCountry();

        final Locale selLocale = new Locale(selLanguageCode.toLowerCase(Locale.ENGLISH), ("".equals(selCountryCode) ? LOCALE_COUNTRY_CODE : selCountryCode).toUpperCase(Locale.ENGLISH));
        logger.info("Setting locale " + selLocale);
        Locale.setDefault(selLocale);
    }

    public static SupportedLanguage getSelectedLanguage() {
        SupportedLanguage result = findSupportedLanguage(new SupportedLanguage(selLanguageCode, selCountryCode));
        if (result == null) {
            result = findSupportedLanguage(new SupportedLanguage(selLanguageCode, ""));
            if (result == null) {
                result = new SupportedLanguage(FWProp.DEFAULT_LANG_CODE, "");
            }
        }
        return result;
    }
}
