/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.session;

import java.awt.*;

/**
 * This Java Bean defines the {@code Window} state preserved across
 * sessions: the Window's {@code bounds}, and the bounds of the
 * Window's {@code GraphicsConfiguration}, i.e. the bounds of the
 * screen that the Window appears on.  If the Window is actually a
 * Frame, we also store its extendedState.  {@code WindowState} objects
 * are stored and restored by the {@link WindowProperty WindowProperty}
 * class.
 *
 * @see WindowProperty
 * @see #save
 * @see #restore
 */
public class WindowState {

    private final Rectangle bounds;
    private Rectangle gcBounds = null;
    private int screenCount;
    private int frameState = Frame.NORMAL;

    public WindowState() {
        super();
        bounds = new Rectangle();
    }

    public WindowState(Rectangle bounds, Rectangle gcBounds, int screenCount, int frameState) {
        super();
        if (bounds == null) {
            throw new IllegalArgumentException("null bounds");
        }
        if (screenCount < 1) {
            throw new IllegalArgumentException("invalid screenCount");
        }
        this.bounds = bounds;
        this.gcBounds = gcBounds;
        // can be null
        this.screenCount = screenCount;
        this.frameState = frameState;
    }

    public Rectangle getBounds() {
        return new Rectangle(bounds);
    }

    public void setBounds(Rectangle bounds) {
        this.bounds.setBounds(bounds);
    }

    public int getScreenCount() {
        return screenCount;
    }

    public void setScreenCount(int screenCount) {
        this.screenCount = screenCount;
    }

    public int getFrameState() {
        return frameState;
    }

    public void setFrameState(int frameState) {
        this.frameState = frameState;
    }

    public Rectangle getGraphicsConfigurationBounds() {
        return (gcBounds == null) ? null : new Rectangle(gcBounds);
    }

    public void setGraphicsConfigurationBounds(Rectangle gcBounds) {
        this.gcBounds = (gcBounds == null) ? null : new Rectangle(gcBounds);
    }
}