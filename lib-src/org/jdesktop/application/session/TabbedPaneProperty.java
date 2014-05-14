/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */
package org.jdesktop.application.session;

import javax.swing.*;
import java.awt.*;

/**
 * A {@code sessionState} property for JTabbedPane.
 * <p>
 * This class defines how the session state for {@code JTabbedPanes}
 * is {@link WindowProperty#getSessionState saved} and
 * and {@link WindowProperty#setSessionState restored} in
 * terms of a property called {@code sessionState}.  The
 * JTabbedPane's {@code selectedIndex} is saved and restored
 * if the number of tabs ({@code tabCount}) hasn't changed.
 * <p>
 * {@code TabbedPaneProperty} is registered for {@code
 * JTabbedPane.class} by default, so this class applies to
 * JTabbedPane and any subclass of JTabbedPane.  One can
 * override the default with the {@link #putProperty putProperty}
 * method.
 *
 * @see TabbedPaneState
 * @see #save
 * @see #restore
 */
public class TabbedPaneProperty implements PropertySupport {

    private void checkComponent(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("null component");
        }
        if (!(component instanceof JTabbedPane)) {
            throw new IllegalArgumentException("invalid component");
        }
    }

    /**
     * Returns a {@link TabbedPaneState TabbedPaneState} object
     * for {@code JTabbedPane c}.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code Component c}
     * isn't a non-null {@code JTabbedPane}.
     *
     * @param c the {@code JTabbedPane} whose selectedIndex will
     *     recoreded in a {@code TabbedPaneState} object.
     * @return the {@code TabbedPaneState} object
     * @see #setSessionState
     * @see TabbedPaneState
     */
    @Override
    public Object getSessionState(Component c) {
        checkComponent(c);
        JTabbedPane p = (JTabbedPane) c;
        return new TabbedPaneState(p.getSelectedIndex(), p.getTabCount());
    }

    /**
     * Restore the {@code JTabbedPane's} {@code selectedIndex}
     * property if the number of {@link JTabbedPane#getTabCount tabs}
     * has not changed.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code c} is
     * not a {@code JTabbedPane} or if {@code state} is non-null
     * but not an instance of {@link TabbedPaneState}.
     *
     * @param c the JTabbedPane whose state is to be restored
     * @param state the {@code TabbedPaneState} to be restored
     * @see #getSessionState
     * @see TabbedPaneState
     */
    @Override
    public void setSessionState(Component c, Object state) {
        checkComponent(c);
        if (state == null) return;
        if (state instanceof TabbedPaneState) {
            JTabbedPane p = (JTabbedPane) c;
            TabbedPaneState tps = (TabbedPaneState) state;
            if (p.getTabCount() == tps.getTabCount()) {
                p.setSelectedIndex(tps.getSelectedIndex());
            }
        } else {
            throw new IllegalArgumentException("invalid state");
        }
    }
}
