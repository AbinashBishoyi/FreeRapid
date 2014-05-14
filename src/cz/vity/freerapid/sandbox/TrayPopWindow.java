package cz.vity.freerapid.sandbox;

import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import java.awt.*;

/**
 * @author Vity
 */
public class TrayPopWindow extends JWindow {

    private JLabel textLabel;

    public TrayPopWindow() {
        super();
        init();

    }

    private void init() {
        setPreferredSize(new Dimension(200, 80));
        setSize(getPreferredSize());
        this.setLayout(new BorderLayout());
        final JLabel freeRapid = new JLabel("FreeRapid");
        freeRapid.setBorder(new DropShadowBorder(Color.BLACK, 2, 0.5f, 2, true, true, false, true));
        freeRapid.setBackground(Color.GREEN);
        this.add(freeRapid, BorderLayout.NORTH);
        freeRapid.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(new DropShadowBorder(Color.BLACK, 4, 0.5f, 4, true, true, true, true), BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        this.add(panel, BorderLayout.CENTER);
        textLabel = new JLabel();
        panel.add(textLabel, BorderLayout.CENTER);
    }

    public String getTextLabel() {
        return textLabel.getText();
    }

    public void setTextLabel(String textLabel) {
        this.textLabel.setText(textLabel);
    }

    public void setY(int y) {
        setLocation(getLocation().x, y);
    }

}
