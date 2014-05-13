package cz.vity.freerapid.sandbox;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public class BugTest {
    //135589adfgpqtuxz
    private static final byte charArr[] = {
            49, 51, 53, 53, 56, 57, 97, 100, 102, 103,
            112, 113, 116, 117, 120, 122
    };


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                show();
            }
        });
    }

    private static void show() {
        final String s = new String(charArr);
        System.out.println("s = " + s);
        final JFrame test = new JFrame("Test3");
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
