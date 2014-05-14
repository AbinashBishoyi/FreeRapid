package cz.vity.freerapid.sandbox;

/**
 * @author Vity
 */

import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Arrays;
import java.util.Timer;
import java.util.TimerTask;


public class TableTest extends JFrame {

    private TestTableModel tableModel = new TestTableModel();
    private JXTable jxTable = new JXTable(tableModel);
    private JTable tableCore = new JTable(tableModel);


    public TableTest() {
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(jxTable, BorderLayout.NORTH);
        getContentPane().add(new JLabel("^^^ JXTable  vvvv Core JTable - drag mouse from Top to Bottom"), BorderLayout.CENTER);
        getContentPane().add(tableCore, BorderLayout.SOUTH);

        final TableRowSorter<TableModel> rowSorter = new TableRowSorter<TableModel>(tableModel);

        rowSorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(0, SortOrder.DESCENDING)));
        tableCore.setRowSorter(rowSorter);

        jxTable.setSortable(true);
        jxTable.setSortOrder(0, SortOrder.DESCENDING);
        jxTable.setUpdateSelectionOnSort(false);

        rowSorter.setSortsOnUpdates(true);
        pack();
        setSize(400, 450);
        setVisible(true);

        TimerTask task = new TimerTask() {
            int i = 0;

            public void run() {
                //tableModel.updateRow(i++ % 10);
            }
        };
        Timer timer = new Timer("Table Updater");
        timer.schedule(task, 1000, 1000);

        task = new TimerTask() {
            int i = 0;

            public void run() {
                tableModel.removeRow(4);
            }
        };
        timer = new Timer("Table Updater");
        timer.schedule(task, 10000, 5000);

    }


    private class TestTableModel extends DefaultTableModel {

//        public TestTableModel() {
//            super(new Integer[10][5], new Object[]{"A", "B", "C", "D", "E"});
//
//            for (int row = 0; row < data.length; row++) {
//                fillRow(row);
//            }
//        }
//
//        public Object getValueAt(int rowIndex, int columnIndex) {
//            return data[rowIndex][columnIndex];
//        }
//
//        public void updateRow(final int i) {
//            fillRow(i);
//            SwingUtilities.invokeLater(new Runnable() {
//                public void run() {
//                    fireTableRowsUpdated(i, i);
//                }
//            });
//        }
//
//        public void fillRow(int row) {
//            for (int col = 0; col < data[row].length; col++) {
//                data[row][col] = (int) Math.round(Math.random() * 20);
//            }
//            data[row][0] = row;
//
//        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new TableTest();
            }
        });
    }

}

