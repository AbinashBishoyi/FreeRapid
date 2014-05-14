/*
 * @(#)WindowsPreferencesFactory.java	1.8 05/11/17
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package cz.vity.freerapid.core;

import java.util.prefs.Preferences;
import java.util.prefs.PreferencesFactory;

public final class WinPreferencesFactory implements PreferencesFactory {

    private static Preferences instance = null;

    public WinPreferencesFactory() {
    }

    /**
     * Returns WindowsPreferences.userRoot
     */
    public Preferences userRoot() {
        return getPrefs();
    }

    /**
     * Returns WindowsPreferences.systemRoot
     */
    public Preferences systemRoot() {
        return getPrefs();
    }

    private Preferences getPrefs() {
        if (instance == null) {
            return instance = new WinPreferences();
        } else return instance;
    }
}