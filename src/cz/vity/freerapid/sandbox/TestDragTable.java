package cz.vity.freerapid.sandbox;

/**
 * @author Ladislav Vitasek
 */

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.dnd.DnDConstants;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class TestDragTable extends JTable implements Runnable {


    JTable jt;

    public void run() {
        JTextField jtf = new JTextField("Drag This!");
        jtf.setDragEnabled(true);
        String[][] data = new String[10][10];
        String[] names = new String[10];
        for (int i = 0; i < 10; i++) {
            names[i] = String.valueOf(i);
            for (int j = 0; j < 10; j++)
                data[i][j] = String.valueOf(j);
        }
        DefaultTableModel dtm = new DefaultTableModel(data, names);
        jt = this;
        jt.setModel(dtm);
        jt.setRowSelectionAllowed(true);
        jt.setUI(new MyTableUI());
        jt.setDragEnabled(true);

        jt.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                jt.selectAll();
            }
        });
        jt.setDragEnabled(false);

        jt.addMouseMotionListener(new MouseMotionListener() {
            public void mouseDragged(MouseEvent e) {
                final int row = jt.rowAtPoint(e.getPoint());
                System.out.println("row = " + row);
            }

            public void mouseMoved(MouseEvent e) {
                e.consume();
            }
        });
        JFrame jf = new JFrame();
        jf.add(jt);
        jf.add(jtf, BorderLayout.NORTH);
        jf.pack();
        jf.setVisible(true);

    }

    public static class TransferHandler2 extends TransferHandler {
        TransferHandler2() {
            super();
        }

        public boolean canImport(TransferHandler.TransferSupport support) {
            return true;
        }
    }


    public static void main(String... args) {
        SwingUtilities.invokeLater(new TestDragTable());
    }

    public class MyTableUI extends BasicTableUI {
        protected MouseInputListener createMouseInputListener() {
            return new MyMouseInputHandler();
        }

        class MyMouseInputHandler extends MouseInputHandler {

            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
            }

            public void mouseDragged(MouseEvent e) {
                // Only do special handling if we are drag enabled with multiple selection
                if (table.getDragEnabled() &&
                        table.getSelectionModel().getSelectionMode() == ListSelectionModel.MULTIPLE_INTERVAL_SELECTION) {
                    table.getTransferHandler().exportAsDrag(table, e, DnDConstants.ACTION_COPY);
                } else {
                    super.mouseDragged(e);
                }
            }
        }
    }


}
