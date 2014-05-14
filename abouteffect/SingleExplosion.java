package cz.vity.freerapid.gui.dialogs.abouteffect;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * @author kirillg
 */
public class SingleExplosion {
    float x;

    float y;

    float radius;

    float opacity;

    Color color;

    public SingleExplosion(Color color, float x, float y, float radius) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.opacity = 1.0f;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setOpacity(float opacity) {
        this.opacity = opacity;
    }

    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setComposite(AlphaComposite.SrcOver.derive(this.opacity));
        g2d.setColor(this.color);
        g2d.fill(new Ellipse2D.Float(this.x - this.radius, this.y
                - this.radius, 2 * radius, 2 * radius));
        g2d.dispose();
    }
}