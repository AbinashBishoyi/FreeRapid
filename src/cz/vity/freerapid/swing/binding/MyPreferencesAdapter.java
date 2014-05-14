/*
 * Copyright (c) 2002-2007 JGoodies Karsten Lentzsch. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JGoodies Karsten Lentzsch nor the names of
 *    its contributors may be used to endorse or promote products derived
 *    from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package cz.vity.freerapid.swing.binding;

import com.jgoodies.binding.value.AbstractValueModel;
import cz.vity.freerapid.core.AppPrefs;

import java.util.prefs.Preferences;

/**
 * A ValueModel implementation that reads and writes values from/to a key of a given <code>Preferences</code> node under
 * a specified key. Write changes fire value changes.<p>
 * <p/>
 * <strong>Example:</strong><pre>
 * String  prefsKey = "isShowing";
 * Boolean defaultValue = Boolean.TRUE;
 * Preferences prefs = Workbench.userPreferences();
 * ValueModel model = new PreferencesAdapter(prefs, prefsKey, defaultValue);
 * JCheckBox showingBox = new JCheckBox("Show tips");
 * showingBox.setModel(new ToggleButtonAdapter(model));
 * </pre>
 *
 * @author Karsten Lentzsch
 * @version $Revision: 1.6 $
 * @see java.util.prefs.Preferences
 */
public class MyPreferencesAdapter extends AbstractValueModel {

    private static final String ERROR_MSG =
            "Value must be a Boolean, Double, Float, Integer, Long, or String.";

    /**
     * Refers to the preferences node that is used to persist the bound data.
     */
    protected Preferences prefs;

    /**
     * Holds the preferences key that is used to access the stored value.
     */
    protected String key;

    /**
     * Refers to the type of accepted values.
     */
    protected Class<?> type;

    /**
     * Holds the default value that is used if the preferences do not yet store a value.
     */
    protected Object defaultValue;

    public MyPreferencesAdapter(String key, Object defaultValue) {
        this(AppPrefs.getPreferences(), key, defaultValue);
    }

    protected MyPreferencesAdapter() {

    }

    public String getKey() {
        return key;
    }

    //Instance Creation *******************************************************

    /**
     * Constructs a PreferencesAdapter on the given <code>Preferences</code> using the specified key and default value,
     * all which must be non-<code>null</code>.
     *
     * @param prefs        the <code>Preferences</code> used to store and retrieve
     * @param key          the key used to get and set values  in the Preferences
     * @param defaultValue the default value
     * @throws NullPointerException     if the Preferences, key, or default value is <code>null</code>
     * @throws IllegalArgumentException if the default value is of a type other than Boolean, Double, Float, Integer,
     *                                  Long, or String.
     */
    public MyPreferencesAdapter(
            Preferences prefs,
            final String key,
            final Object defaultValue) {
        if (prefs == null)
            throw new NullPointerException("The Preferences must not be null.");
        if (key == null)
            throw new NullPointerException("The key must not be null.");
        if (defaultValue == null)
            throw new NullPointerException("The default value must not be null.");
        if (!isBackedType(defaultValue))
            throw new IllegalArgumentException("The Default " + ERROR_MSG);
        this.prefs = prefs;
        this.key = key;
        this.type = defaultValue.getClass();
        this.defaultValue = defaultValue;
    }

    // ValueModel Implementation **********************************************

    /**
     * Looks up and returns the value from the preferences. The value is look up under this adapter's key. It will be
     * converted before it is returned.
     *
     * @return the retrieved and converted value
     * @throws ClassCastException if the type of the default value cannot be read from the preferences
     */
    public Object getValue() {
        if (type == Boolean.class)
            return getBoolean();
        else if (type == Double.class)
            return getDouble();
        else if (type == Float.class) {
            return getFloat();
        } else if (type == Integer.class)
            return getInt();
        else if (type == Long.class)
            return getLong();
        else if (type == String.class)
            return getString();
        else
            throw new ClassCastException(ERROR_MSG);
    }

    /**
     * Converts the given value to a string and puts it into the preferences.
     *
     * @param newValue the object to be stored
     * @throws IllegalArgumentException if the new value cannot be stored in the preferences due to an illegal type
     */
    public void setValue(Object newValue) {
        if (newValue == null)
            throw new NullPointerException("The value must not be null.");

//        Class valueType = newValue.getClass();
//        if (type != valueType)
//            throw new IllegalArgumentException(
//                    "The type of the value set must be consistent "
//                  + "with the default value type " + type);
//
        if (newValue instanceof Boolean)
            setBoolean((Boolean) newValue);
        else if (newValue instanceof Double)
            setDouble((Double) newValue);
        else if (newValue instanceof Float)
            setFloat((Float) newValue);
        else if (newValue instanceof Integer)
            setInt((Integer) newValue);
        else if (newValue instanceof Long)
            setLong((Long) newValue);
        else if (newValue instanceof String)
            setString((String) newValue);
    }

    // Convenience Accessors **************************************************

    /**
     * Looks up, converts and returns the stored value from the preferences. Returns the default value if no value has
     * been stored before.
     *
     * @return the stored value or the default
     */
    public boolean getBoolean() {
        return prefs.getBoolean(key, (Boolean) defaultValue);
    }

    /**
     * Looks up, converts and returns the stored value from the preferences. Returns the default value if no value has
     * been stored before.
     *
     * @return the stored value or the default
     */
    public double getDouble() {
        return prefs.getDouble(key, ((Number) defaultValue).doubleValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences. Returns the default value if no value has
     * been stored before.
     *
     * @return the stored value or the default
     */
    public float getFloat() {
        return prefs.getFloat(key, ((Number) defaultValue).floatValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences. Returns the default value if no value has
     * been stored before.
     *
     * @return the stored value or the default
     */
    public int getInt() {
        return prefs.getInt(key, ((Number) defaultValue).intValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences. Returns the default value if no value has
     * been stored before.
     *
     * @return the stored value or the default
     */
    public long getLong() {
        return prefs.getLong(key, ((Number) defaultValue).longValue());
    }

    /**
     * Looks up, converts and returns the stored value from the preferences. Returns the default value if no value has
     * been stored before.
     *
     * @return the stored value or the default
     */
    @Override
    public String getString() {
        return prefs.get(key, (String) defaultValue);
    }


    /**
     * Converts the given value to an Object and stores it in this adapter's Preferences under this adapter's
     * preferences key.
     *
     * @param newValue the value to put into the Preferences
     * @throws ClassCastException if the default value is not a Boolean
     */
    public void setBoolean(boolean newValue) {
        boolean oldValue = getBoolean();
        prefs.putBoolean(key, newValue);
        fireValueChange(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's Preferences under this adapter's
     * preferences key.
     *
     * @param newValue the value to put into the Preferences
     * @throws ClassCastException if the default value is not a Double
     */
    public void setDouble(double newValue) {
        double oldValue = getDouble();
        prefs.putDouble(key, newValue);
        fireValueChange(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's Preferences under this adapter's
     * preferences key.
     *
     * @param newValue the value to put into the Preferences
     * @throws ClassCastException if the default value is not a Float
     */
    public void setFloat(float newValue) {
        float oldValue = getFloat();
        prefs.putFloat(key, newValue);
        fireValueChange(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's Preferences under this adapter's
     * preferences key.
     *
     * @param newValue the value to put into the Preferences
     * @throws ClassCastException if the default value is not an Integer
     */
    public void setInt(int newValue) {
        int oldValue = getInt();
        prefs.putInt(key, newValue);
        fireValueChange(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's Preferences under this adapter's
     * preferences key.
     *
     * @param newValue the value to put into the Preferences
     * @throws ClassCastException if the default value is not a Long
     */
    public void setLong(long newValue) {
        long oldValue = getLong();
        prefs.putLong(key, newValue);
        fireValueChange(oldValue, newValue);
    }

    /**
     * Converts the given value to an Object and stores it in this adapter's Preferences under this adapter's
     * preferences key.
     *
     * @param newValue the value to put into the Preferences
     * @throws ClassCastException if the default value is not a String
     */
    public void setString(String newValue) {
        String oldValue = getString();
        prefs.put(key, newValue);
        fireValueChange(oldValue, newValue);
    }

    // Helper Code ************************************************************

    /**
     * Used to check the default value type during construction.
     */
    private boolean isBackedType(Object value) {
        Class<?> aClass = value.getClass();
        return ((aClass == Boolean.class)
                || (aClass == Double.class)
                || (aClass == Float.class)
                || (aClass == Integer.class)
                || (aClass == Long.class)
                || (aClass == String.class));
    }

}