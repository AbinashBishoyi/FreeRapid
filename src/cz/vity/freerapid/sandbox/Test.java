package cz.vity.freerapid.sandbox;

/**
 * @author Ladislav Vitasek
 */

import com.sun.jna.examples.BalloonManagerDemo;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Test extends TranslucentFrame {

    public static void main(String[] args) {
        final BalloonManagerDemo demo = new BalloonManagerDemo();
        BalloonManagerDemo.main(args);
    }

    private BufferedImage img;

    public Test() {
        super();
        try {

            img = ImageIO.read(new File("c:\\temp\\egg.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        this.add(new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                g.drawImage(img, 0, 0, null);
            }
        });
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowDeactivated(WindowEvent e) {
                Test.this.toFront();
            }
        });

        this.setSize(img.getWidth(), img.getHeight());
        this.setVisible(true);
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

}