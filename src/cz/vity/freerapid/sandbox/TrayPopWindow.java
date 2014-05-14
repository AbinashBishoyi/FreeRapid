package cz.vity.freerapid.sandbox;

import com.jgoodies.forms.factories.CC;
import com.jgoodies.forms.layout.FormLayout;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.border.DropShadowBorder;
import org.jdesktop.swingx.painter.MattePainter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 * @author Vity
 */
public class TrayPopWindow extends JWindow {

    private JLabel textLabel;
    private JButton btnBack;
    private JButton btnForward;
    private JButton btnClose;

    public static final GradientPaint AERITH = new GradientPaint(
            new Point2D.Double(0, 0),
            Color.WHITE,
            new Point2D.Double(1, 0),
            new Color(64, 110, 161));

    public static final GradientPaint RED_XP = new GradientPaint(
            new Point2D.Double(0, 0),
            new Color(0xff, 0xff, 0xff),
            new Point2D.Double(1, 0),
            new Color(0xcc, 0xcc, 0xcc));


    public TrayPopWindow() {
        super();
        init();

    }

    private void init() {
        setPreferredSize(new Dimension(300, 140));
        setSize(getPreferredSize());
        final JXPanel titlePanel = new JXPanel();
        titlePanel.setBackgroundPainter(new MattePainter(AERITH, true));
//        titlePanel.setForeground(new Color(168, 204, 241));
//        titlePanel.setBackground(new Color(44, 61, 146));

        final JPanel contentPane = new JPanel();


        this.setContentPane(contentPane);
        this.setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 3, 4));
        this.setLayout(new BorderLayout());

        JLabel labelIcon = new JLabel();
        btnBack = new JButton();
        btnForward = new JButton();
        btnClose = new JButton();
        btnClose.setBorderPainted(false);

        titlePanel.setLayout(new FormLayout(
                "default, $lcgap, default:grow, 3*($lcgap, center:default), $lcgap, default:grow, $lcgap, default",
                "default"));
        contentPane.setBorder(new DropShadowBorder(Color.BLACK, 4, 0.5f, 4, true, true, true, true));

        titlePanel.setBorder(BorderFactory.createCompoundBorder(new DropShadowBorder(Color.BLACK, 2, 0.3f, 4, false, false, true, false), BorderFactory.createEmptyBorder(4, 4, 4, 4)));

        //---- labelIcon ----
        labelIcon.setText("text");
        titlePanel.add(labelIcon, CC.xy(1, 1));

        //---- btnBack ----
        btnBack.setText("text");
        titlePanel.add(btnBack, CC.xy(5, 1));

        final JLabel countLabel = new JLabel("1 of 1");
        titlePanel.add(countLabel, CC.xy(7, 1));

        //---- btnForward ----
        btnForward.setText("text");
        titlePanel.add(btnForward, CC.xy(9, 1));

        //---- btnClose ----
        btnClose.setText("text");
        titlePanel.add(btnClose, CC.xy(13, 1));


        this.add(titlePanel, BorderLayout.NORTH);
        final JXPanel panel = new JXPanel(new BorderLayout());
        final MattePainter mattePainter = new MattePainter(RED_XP, true);
        //panel.setDirection(GradientPanel.HORIZONTAL);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 4, 4));

        MattePainter matte = new MattePainter(new Color(71, 51, 51));

        panel.setBackgroundPainter(mattePainter);

        this.add(panel, BorderLayout.CENTER);
        textLabel = new JLabel();
        textLabel.setFont(textLabel.getFont().deriveFont(Font.BOLD).deriveFont(14.0f));
        panel.add(textLabel, BorderLayout.NORTH);
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
