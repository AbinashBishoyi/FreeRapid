/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.session;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * A {@code sessionState} property for JTable
 * <p>
 * This class defines how the session state for {@code JTables}
 * is {@link WindowProperty#getSessionState saved} and
 * and {@link WindowProperty#setSessionState restored} in
 * terms of a property called {@code sessionState}.
 * We save and restore the width of each resizable
 * {@code TableColumn}, if the number of columns haven't
 * changed.
 * <p>
 * {@code TableProperty} is registered for {@code
 * JTable.class} by default, so this class applies to
 * JTable and any subclass of JTable.  One can
 * override the default with the {@link #putProperty putProperty}
 * method.
 *
 * @see TableState
 * @see #save
 * @see #restore
 */
public class TableProperty implements PropertySupport {

    private void checkComponent(Component component) {
        if (component == null) {
            throw new IllegalArgumentException("null component");
        }
        if (!(component instanceof JTable)) {
            throw new IllegalArgumentException("invalid component");
        }
    }

    /**
     * Returns a {@link TableState TableState} object
     * for {@code JTable c} or null, if none of the JTable's
     * columns are {@link TableColumn#getResizable resizable}.
     * A width of -1 is used to mark {@code TableColumns}
     * that are not resizable.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code Component c}
     * isn't a non-null {@code JTable}.
     *
     * @param c the {@code JTable} whose columnWidths will be
     *     saved in a {@code TableState} object.
     * @return the {@code TableState} object or null
     * @see #setSessionState
     * @see TableState
     */
    @Override
    public Object getSessionState(Component c) {
        checkComponent(c);
        JTable table = (JTable) c;
        int[] columnWidths = new int[table.getColumnCount()];
        boolean resizableColumnExists = false;
        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn tc = table.getColumnModel().getColumn(i);
            columnWidths[i] = (tc.getResizable()) ? tc.getWidth() : -1;
            if (tc.getResizable()) {
                resizableColumnExists = true;
            }
        }
        return (resizableColumnExists) ? new TableState(columnWidths) : null;
    }

    /**
     * Restore the width of each resizable {@code TableColumn}, if
     * the number of columns haven't changed.
     * <p>
     * Throws an {@code IllegalArgumentException} if {@code c} is
     * not a {@code JTable} or if {@code state} is not an instance
     * of {@link TableState}.
     *
     * @param c the JTable whose column widths are to be restored
     * @param state the {@code TableState} to be restored
     * @see #getSessionState
     * @see TableState
     */
    @Override
    public void setSessionState(Component c, Object state) {
        checkComponent(c);
        if (!(state instanceof TableState)) {
            throw new IllegalArgumentException("invalid state");
        }
        JTable table = (JTable) c;
        int[] columnWidths = ((TableState) state).getColumnWidths();
        if (table.getColumnCount() == columnWidths.length) {
            for (int i = 0; i < columnWidths.length; i++) {
                if (columnWidths[i] != -1) {
                    TableColumn tc = table.getColumnModel().getColumn(i);
                    if (tc.getResizable()) {
                        tc.setPreferredWidth(columnWidths[i]);
                    }
                }
            }
        }
    }
}
