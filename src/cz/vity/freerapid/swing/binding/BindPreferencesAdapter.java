package cz.vity.freerapid.swing.binding;

import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
final class BindPreferencesAdapter extends MyPreferencesAdapter {
    public BindPreferencesAdapter(String key, Object defaultValue) {
        super(key, defaultValue);
        this.prefs.addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (getKey().equals(evt.getKey()))
                    fireValueChange(null, getValue(), false);
            }
        });
    }
}
