package cz.vity.freerapid.swing;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;

/**
 * Pretizene UI pro Separator v toolbaru
 *
 * @author Vity
 */
@SuppressWarnings({"WeakerAccess"})
public final class ToolbarSeparatorUI extends javax.swing.plaf.basic.BasicToolBarSeparatorUI {
    private ToolbarSeparatorUI() {
        shadow = UIManager.getColor("controlDkShadow");
        highlight = UIManager.getColor("controlLtHighlight");
    }

    @SuppressWarnings({"UnusedAssignment"})
    public static ComponentUI createUI(JComponent c) {
        c = null;//must be
        return new ToolbarSeparatorUI();
    }

    @Override
    public Dimension getPreferredSize(JComponent c) {
        Dimension dimension = super.getPreferredSize(c);
        if (dimension == null)
            dimension = new Dimension(10, 15);
        dimension.width = Math.min(dimension.width, 10);
        return dimension;
    }

    @Override
    public Dimension getMaximumSize(JComponent c) {
        return this.getPreferredSize(c);
    }

    public final void paint(final Graphics g, final JComponent c) {
        final Dimension s = c.getSize();
        final int sWidth = s.width / 2;

        g.setColor(shadow);
        g.drawLine(sWidth, 0, sWidth, s.height);

        g.setColor(highlight);
        g.drawLine(sWidth + 1, 0, sWidth + 1, s.height);
    }
}

