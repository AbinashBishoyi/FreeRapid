package cz.vity.freerapid.utilities;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @author Ladislav Vitasek
 */
public class BugTest extends JFrame {
    private JTable table;

    public BugTest() throws HeadlessException {

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new BugTest().init();
            }
        });
    }

    private void init() {
        final JFrame frame = new JFrame("Test");
        frame.getContentPane().add(getTable());
        frame.setLocationRelativeTo(null);
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public JTable getTable() {
        final MyCustomTableModel dm = new MyCustomTableModel();
        table = new JTable(dm);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    dm.deleteRow();
                }
            }
        });
        final TableColumnModel tableColumnModel = table.getColumnModel();
        final int count = tableColumnModel.getColumnCount();
        final DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, Integer.toHexString(new Integer(value.toString())), isSelected, hasFocus, row, column);
            }
        };
//        for (int i = 0; i < count; i++) {
//            final TableColumn column = tableColumnModel.getColumn(i);
//            column.setCellRenderer(cellRenderer);
//        }
        return table;
    }

    private class MyCustomTableModel extends AbstractTableModel {
        int rows = 4;

        public int getRowCount() {
            return rows;
        }

        public int getColumnCount() {
            return 5;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return String.valueOf(rowIndex) + String.valueOf(columnIndex);
        }

        public void deleteRow() {
            if (rows < 0)
                return;
            --rows;
            table.getSelectionModel().setValueIsAdjusting(true);
            fireTableRowsDeleted(0, 0);
            table.getSelectionModel().setValueIsAdjusting(false);
        }
    }


}
