package cz.vity.freerapid.sandbox;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {
        final Rectangle r = new Rectangle().union(new Rectangle(0, 0, 800, 600));
        System.out.println("r = " + r);
        System.out.println("1");
        Thread.sleep(3000);
        Rectangle virtualBounds = new Rectangle();
        GraphicsEnvironment ge = GraphicsEnvironment.
                getLocalGraphicsEnvironment();
        GraphicsDevice[] gs =
                ge.getScreenDevices();
        final int length = gs.length;
        System.out.println("length = " + length);
        for (int j = 0; j < length; j++) {
            System.out.println("+");
            GraphicsDevice gd = gs[j];
            //problem is here
            GraphicsConfiguration[] gc =
                    gd.getConfigurations();
            for (int i = 0; i < gc.length; i++) {
                System.out.println("*");
                virtualBounds =
                        virtualBounds.union(gc[i].getBounds());
            }
        }
        System.out.println("2");

        Thread.sleep(3000);
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                JFrame frame = new JFrame("Test");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);
            }
        });
        System.out.println("3");
    }

}