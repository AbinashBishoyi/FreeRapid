package cz.vity.freerapid.swing.binding;

import com.jgoodies.binding.value.ValueModel;

/**
 * @author Vity
 */
public final class BindUtils {
    private BindUtils() {
    }

    public static ValueModel getPrefsValueModel(String key, Object defaultValue) {
        return new BindPreferencesAdapter(key, defaultValue);
    }

    public static ValueModel getReadOnlyPrefsValueModel(String key, Object defaultValue) {
        return new MyPreferencesAdapter(key, defaultValue);
    }
}
