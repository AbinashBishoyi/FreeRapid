package cz.vity.freerapid.swing.components;

import javax.swing.*;
import java.awt.*;

public class CompTitledPane extends JPanel {
    protected CompTitledBorder border;
    protected JComponent component;
    protected JPanel panel;

    public CompTitledPane(JComponent component) {
        this.component = component;
        border = new CompTitledBorder(component);
        border.setTitleJustification(5);
        setBorder(border);
        panel = new JPanel();
        this.setLayout(new BorderLayout());
        this.add(component);
        this.add(panel);
    }

    public JComponent getTitleComponent() {
        return component;
    }

    public void setTitleComponent(JComponent newComponent) {
        remove(component);
        add(newComponent);
        border.setTitleComponent(newComponent);
        component = newComponent;
    }

    public JPanel getContentPane() {
        return panel;
    }

    @Override
    public void doLayout() {
        //  super.doLayout();
        Rectangle rect = getBounds();
        Insets insets = getInsets();
        rect.x = 0;
        rect.y = 0;
        Rectangle compR = border.getComponentRect(rect, insets);
        component.setBounds(compR);
        rect.x += insets.left;
        rect.y += insets.top;
        rect.width -= insets.left + insets.right;
        rect.height -= insets.top + insets.bottom;
        panel.setBounds(rect);
    }
}
