package cz.vity.freerapid.gui.dialogs.userprefs;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.SupportedLanguage;
import cz.vity.freerapid.swing.binding.MyPreferencesAdapter;

/**
 * @author Vity
 */
class LanguageAdapter extends MyPreferencesAdapter {

    public LanguageAdapter(String key, SupportedLanguage defaultValue) {
        super();
        this.prefs = AppPrefs.getPreferences();
        this.key = key;
        this.type = defaultValue.getClass();
        this.defaultValue = defaultValue;
    }

    @Override
    public void setValue(Object newValue) {
        if (newValue == null)
            throw new NullPointerException("The value must not be null.");
        setString(defaultValue.toString());
        defaultValue = newValue;
    }

    @Override
    public String getString() {
        return defaultValue.toString();
    }

    @Override
    public Object getValue() {
        return defaultValue;
    }
}