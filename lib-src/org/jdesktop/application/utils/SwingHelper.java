/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.utils;

import javax.swing.*;
import java.awt.*;

/**
 *
 * @author etf
 */
public final class SwingHelper {

    private SwingHelper() {
    }

    public static Rectangle computeVirtualGraphicsBounds() {
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment
                .getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices();
        for (GraphicsDevice gd : gs) {
            GraphicsConfiguration gc = gd.getDefaultConfiguration();
            virtualBounds = virtualBounds.union(gc.getBounds());
        }
        return virtualBounds;
    }

    public static boolean isResizable(Window window) {
        boolean resizable = true;
        if (window instanceof Frame) {
            resizable = ((Frame) window).isResizable();
        } else if (window instanceof Dialog) {
            resizable = ((Dialog) window).isResizable();
        }
        return resizable;
    }

    /**
     * Calculates default location for the specified window.
     * @return default location for the window
     * @param window the window location is calculated for.
     *               It should not be null.
     * @since 1.9
     */
    public static Point defaultLocation(Window window) {
        GraphicsConfiguration gc = window.getGraphicsConfiguration();
        Rectangle bounds = gc.getBounds();
        Insets insets = window.getToolkit().getScreenInsets(gc);
        int x = bounds.x + insets.left;
        int y = bounds.y + insets.top;
        return new Point(x, y);
    }

    /**
     * Finds the nearest RootPaneContainer of the provided Component. 
     * Primarily, if a JPopupMenu (such as used by JMenus when they are visible) has no parent,
     * the search continues with the JPopupMenu's invoker instead. Fixes BSAF-77
     *
     * @return a RootPaneContainer for the provided component
     * @param root the Component
     *
     * @author Eric Heumann
     * @since 1.9
     */
    public static RootPaneContainer findRootPaneContainer(Component root) {
        while (root != null) {
            if (root instanceof RootPaneContainer) {
                return (RootPaneContainer) root;
            } else if (root instanceof JPopupMenu && root.getParent() == null) {
                root = ((JPopupMenu) root).getInvoker();
            } else {
                root = root.getParent();
            }
        }
        return null;
    }
}
