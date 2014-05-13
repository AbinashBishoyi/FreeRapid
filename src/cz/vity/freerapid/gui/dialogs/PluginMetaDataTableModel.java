package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.swing.SwingXUtils;
import org.jdesktop.swingx.hyperlink.LinkModel;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 * @author Ladislav Vitasek
 */
class PluginMetaDataTableModel extends AbstractTableModel implements ListDataListener {
    private final ArrayListModel<PluginMetaData> model;
    private final String[] columns;
    static final int COLUMN_ACTIVE = 0;
    static final int COLUMN_UPDATE = 1;
    static final int COLUMN_ID = 2;
    static final int COLUMN_VERSION = 3;
    static final int COLUMN_SERVICES = 4;
    static final int COLUMN_AUTHOR = 5;
    static final int COLUMN_WWW = 6;


    public PluginMetaDataTableModel(ArrayListModel<PluginMetaData> model, String[] columns) {
        super();
        this.model = model;
        this.columns = columns;
        model.addListDataListener(this);
    }

    @Override
    public int getRowCount() {
        return model.getSize();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex == COLUMN_ACTIVE || columnIndex == COLUMN_UPDATE;
    }

    @Override
    public String getColumnName(int column) {
        return this.columns[column];
    }

    @Override
    public int getColumnCount() {
        return this.columns.length;
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == COLUMN_ACTIVE || columnIndex == COLUMN_UPDATE) {
            return Boolean.class;
        } else if (columnIndex == COLUMN_WWW)
            return LinkModel.class;
        else return String.class;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        final PluginMetaData data = model.get(rowIndex);
        switch (columnIndex) {
            case COLUMN_ACTIVE:
                return data.isEnabled();
            case COLUMN_UPDATE:
                return data.isUpdatesEnabled();
            case COLUMN_ID:
                return data.getId();
            case COLUMN_VERSION:
                return data.getVersion();
            case COLUMN_SERVICES:
                return data.getServices();
            case COLUMN_AUTHOR:
                return data.getVendor();
            case COLUMN_WWW:
                return SwingXUtils.createLink(data.getWWW());
            default:
                assert false;
        }
        return data;
    }

    public PluginMetaData getObject(final int rowIndex) {
        return model.get(rowIndex);
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == COLUMN_ACTIVE) {
            model.get(rowIndex).setEnabled((Boolean) aValue);
            this.fireTableCellUpdated(rowIndex, columnIndex);
        }
        if (columnIndex == COLUMN_UPDATE) {
            model.get(rowIndex).setUpdatesEnabled((Boolean) aValue);
            this.fireTableCellUpdated(rowIndex, columnIndex);
        }
    }

    @Override
    public void intervalAdded(ListDataEvent e) {
        fireTableRowsInserted(e.getIndex0(), e.getIndex1());
    }

    @Override
    public void intervalRemoved(ListDataEvent e) {
        fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
    }

    @Override
    public void contentsChanged(ListDataEvent e) {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }
}
