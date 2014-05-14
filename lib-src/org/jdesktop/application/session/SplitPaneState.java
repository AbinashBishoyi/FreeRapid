/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application.session;

import javax.swing.*;

/**
 * This Java Bean records the {@code dividerLocation} and {@code
 * orientation} properties of a {@code JSplitPane}.  A {@code
 * SplitPaneState} object created by {@link
 * SplitPaneProperty#getSessionState} and used to restore the
 * selected tab by {@link SplitPaneProperty#setSessionState}.
 *
 * @see SplitPaneProperty
 * @see #save
 * @see #restore
 */
public class SplitPaneState {

    private int dividerLocation = -1;
    private int orientation = JSplitPane.HORIZONTAL_SPLIT;

    private void checkOrientation(int orientation) {
        if ((orientation != JSplitPane.HORIZONTAL_SPLIT) && (orientation != JSplitPane.VERTICAL_SPLIT)) {
            throw new IllegalArgumentException("invalid orientation");
        }
    }

    public SplitPaneState() {
        super();
    }

    public SplitPaneState(int dividerLocation, int orientation) {
        super();
        checkOrientation(orientation);
        if (dividerLocation < -1) {
            throw new IllegalArgumentException("invalid dividerLocation");
        }
        this.dividerLocation = dividerLocation;
        this.orientation = orientation;
    }

    public int getDividerLocation() {
        return dividerLocation;
    }

    public void setDividerLocation(int dividerLocation) {
        if (dividerLocation < -1) {
            throw new IllegalArgumentException("invalid dividerLocation");
        }
        this.dividerLocation = dividerLocation;
    }

    public int getOrientation() {
        return orientation;
    }

    public void setOrientation(int orientation) {
        checkOrientation(orientation);
        this.orientation = orientation;
    }
}
