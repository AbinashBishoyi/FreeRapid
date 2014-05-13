package cz.vity.freerapid.sandbox;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public class BugTest {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                show();
            }
        });
    }

    private static void show() {
        final JFrame test = new JFrame("Test");
        test.getContentPane().add(getCombobox());
        test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        test.setLocationRelativeTo(null);
        test.setSize(300, 200);
        test.setVisible(true);
    }

    private static Component getCombobox() {
        final JComboBox jComboBox = new JComboBox();
        AutoCompleteDecorator.decorate(jComboBox);
        return jComboBox;
    }
}
