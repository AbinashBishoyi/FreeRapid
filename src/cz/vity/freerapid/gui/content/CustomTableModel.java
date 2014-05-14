package cz.vity.freerapid.gui.content;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.model.DownloadFile;

import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;

/**
 * @author Ladislav Vitasek
 */
final class CustomTableModel extends AbstractTableModel implements ListDataListener {
    private final ArrayListModel<DownloadFile> model;
    private final String[] columns;


    public CustomTableModel(ArrayListModel<DownloadFile> model, String[] columns) {
        super();
        this.model = model;
        this.columns = columns;
        model.addListDataListener(this);
    }

    public int getRowCount() {
        return model.getSize();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    @Override
    public String getColumnName(int column) {
        return this.columns[column];
    }

    public int getColumnCount() {
        return this.columns.length;
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        return model.get(rowIndex);
    }

    public void intervalAdded(ListDataEvent e) {
        fireTableRowsInserted(e.getIndex0(), e.getIndex1());
    }

    public void intervalRemoved(ListDataEvent e) {
        fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
    }

    public void contentsChanged(ListDataEvent e) {
        fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
    }
}
