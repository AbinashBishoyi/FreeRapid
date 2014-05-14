package cz.vity.freerapid.sandbox;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.border.DropShadowBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * @author Vity
 */
public class TrayPopWindow extends JWindow {

    private JLabel textLabel;
    private JButton btnBack;
    private JButton btnForward;
    private JButton btnClose;

    public TrayPopWindow() {
        super();
        init();

    }

    private void init() {
        setPreferredSize(new Dimension(300, 140));
        setSize(getPreferredSize());
        final JPanel contentPane = new JPanel();

        this.setContentPane(contentPane);
        this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 2, 4));
        this.setLayout(new BorderLayout());
        final JPanel titlePanel = new JPanel();
        JLabel labelIcon = new JLabel();
        btnBack = new JButton();        
        btnForward = new JButton();
        btnClose = new JButton();
        btnClose.setBorderPainted(false);

        titlePanel.setLayout(new FormLayout(
        			"default, $lcgap, default:grow, 2*($lcgap, center:default), $lcgap, default:grow, $lcgap, default",
        			"default"));
        contentPane.setBorder(new DropShadowBorder(Color.BLACK, 4, 0.5f, 4, true, true, true, true));

        titlePanel.setBorder(BorderFactory.createCompoundBorder(new DropShadowBorder(Color.BLACK, 2, 0.3f, 4, false, false, true, false), BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        //---- labelIcon ----
        labelIcon.setText("text");
        titlePanel.add(labelIcon, CC.xy(1, 1));

        //---- btnBack ----
        btnBack.setText("text");
        titlePanel.add(btnBack, CC.xy(5, 1));

        //---- btnForward ----
        btnForward.setText("text");
        titlePanel.add(btnForward, CC.xy(7, 1));

        //---- btnClose ----
        btnClose.setText("text");
        titlePanel.add(btnClose, CC.xy(11, 1));


        this.add(titlePanel, BorderLayout.NORTH);
        final JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
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

    public JButton getBtnClose() {
        return btnClose;
    }

    private AbstractButton getButton(final Action action) {
        final JButton button = new JButton(action);
        return setButtonProperties(button, action);
    }


    private AbstractButton setButtonProperties(AbstractButton button, Action action) {
        button.setRolloverEnabled(true);
        button.setIconTextGap(0);
        final Object desc = action.getValue(Action.SHORT_DESCRIPTION);

        final Object keystroke = action.getValue(Action.ACCELERATOR_KEY);
        if (desc != null) {
            button.setToolTipText(desc.toString());
        }

        button.setMnemonic(0);
        button.setFocusable(false);
        return button;
    }

}
