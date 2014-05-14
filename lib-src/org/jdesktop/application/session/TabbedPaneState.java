/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.session;

/**
 * This Java Bean record the {@code selectedIndex} and {@code
 * tabCount} properties of a {@code JTabbedPane}.  A {@code
 * TabbedPaneState} object created by {@link
 * TabbedPaneProperty#getSessionState} and used to restore the
 * selected tab by {@link TabbedPaneProperty#setSessionState}.
 *
 * @see TabbedPaneProperty
 * @see #save
 * @see #restore
 */
public class TabbedPaneState {

    private int selectedIndex;
    private int tabCount;

    public TabbedPaneState() {
        super();
        selectedIndex = -1;
        tabCount = 0;
    }

    public TabbedPaneState(int selectedIndex, int tabCount) {
        super();
        if (tabCount < 0) {
            throw new IllegalArgumentException("invalid tabCount");
        }
        if ((selectedIndex < -1) || (selectedIndex > tabCount)) {
            throw new IllegalArgumentException("invalid selectedIndex");
        }
        this.selectedIndex = selectedIndex;
        this.tabCount = tabCount;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        if (selectedIndex < -1) {
            throw new IllegalArgumentException("invalid selectedIndex");
        }
        this.selectedIndex = selectedIndex;
    }

    public int getTabCount() {
        return tabCount;
    }

    public void setTabCount(int tabCount) {
        if (tabCount < 0) {
            throw new IllegalArgumentException("invalid tabCount");
        }
        this.tabCount = tabCount;
    }
}
