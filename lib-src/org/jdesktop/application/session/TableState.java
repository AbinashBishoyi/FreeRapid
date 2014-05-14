/*
 * Copyright (C) 2009 Illya Yalovyy
 * Use is subject to license terms.
 */

package org.jdesktop.application.session;

/**
 * This Java Bean records the {@code columnWidths} for all
 * of the columns in a JTable.  A width of -1 is used to
 * mark {@code TableColumns} that are not resizable.
 *
 * @see TableProperty
 * @see #save
 * @see #restore
 */
public class TableState {

    private int[] columnWidths = new int[0];

    private int[] copyColumnWidths(int[] columnWidths) {
        if (columnWidths == null) {
            throw new IllegalArgumentException("invalid columnWidths");
        }
        int[] copy = new int[columnWidths.length];
        System.arraycopy(columnWidths, 0, copy, 0, columnWidths.length);
        return copy;
    }

    public TableState() {
        super();
    }

    public TableState(int[] columnWidths) {
        super();
        this.columnWidths = copyColumnWidths(columnWidths);
    }

    public int[] getColumnWidths() {
        return copyColumnWidths(columnWidths);
    }

    public void setColumnWidths(int[] columnWidths) {
        this.columnWidths = copyColumnWidths(columnWidths);
    }
}
