package cz.vity.freerapid.sandbox;

import org.jdesktop.swingx.JXFrame;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public class BugTest2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                show();
            }
        });
    }

    private static void show() {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.aero.AeroLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        final JXFrame frame = new JXFrame("Test3");
        //JFrame.setDefaultLookAndFeelDecorated(false);
        frame.setUndecorated(true);
        frame.getContentPane().setLayout(new BorderLayout());
        final JXStatusBar statusBar = new JXStatusBar();
        statusBar.add(new JLabel("asdasd"));
        statusBar.add(new JLabel("asdasd"));
        statusBar.add(new JLabel("asdasd"));
        statusBar.add(new JLabel("asdasd"));
        frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setMinimumSize(new Dimension(30, 30));
        frame.setSize(300, 200);
        frame.setVisible(true);
    }

}