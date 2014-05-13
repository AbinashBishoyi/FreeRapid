package cz.vity.freerapid.sandbox;

/**
 * @author Ladislav Vitasek
 */

import com.sun.jna.examples.WindowUtils;

import javax.swing.*;
import java.awt.*;


public class TranslucentFrame extends JFrame {

    private float alpha = 1.0f;


    public TranslucentFrame(GraphicsConfiguration gc) {
        super(gc);
        this.init();
    }

    public TranslucentFrame(String title, GraphicsConfiguration gc) {
        super(title, gc);
        this.init();
    }

    public TranslucentFrame(String title) throws HeadlessException {
        super(title);
        this.init();
    }

    public TranslucentFrame() {
        super();
        this.init();
    }


    private void init() {
        this.setUndecorated(true);
        System.setProperty("sun.java2d.noddraw", "true");
        //System.setProperty("sun.java2d.opengl", "true");
        //this.setAlpha(alpha);
    }

    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
        if (b)
            WindowUtils.setWindowTransparent(this, true);
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        super.addImpl(comp, constraints, index);
        if (comp instanceof JComponent) {
            JComponent jcomp = (JComponent) comp;
            jcomp.setOpaque(false);
        }
    }

    public void setAlpha(float alpha) {
        WindowUtils.setWindowAlpha(this, alpha);
    }

    public float getAlpha() {
        return alpha;
    }

}