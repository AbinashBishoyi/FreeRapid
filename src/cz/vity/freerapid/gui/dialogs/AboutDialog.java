package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class AboutDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(AboutDialog.class.getName());
    private JLabel xImagePanel;
    private JLabel infoLabel;
    private AudioClip audioClip;

    public AboutDialog(Frame owner) throws HeadlessException {
        super(owner, true);
        this.setName("AboutDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }


    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnOK;
    }

    private void build() {
        inject();
        buildGUI();

        //final ActionMap actionMap = getActionMap();
        setAction(btnOK, ("okBtnAction"));

        pack();
        setResizable(false);
        locateOnOpticalScreenCenter(this);
//        SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                audioClip = Sound.playSound("boss.mid");
//            }
//        });


    }

    private void buildGUI() {
        final Icon imageIcon = Swinger.getResourceMap().getIcon("splash.iconImage");
        xImagePanel.setIcon(imageIcon);
        final Dimension pref = new Dimension(imageIcon.getIconWidth() + 2, imageIcon.getIconHeight() + 2);
        xImagePanel.setPreferredSize(pref);
        xImagePanel.setBorder(BorderFactory.createEtchedBorder());
        xImagePanel.setLayout(new BoxLayout(xImagePanel, BoxLayout.Y_AXIS));
        xImagePanel.add(infoLabel);
        infoLabel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 30));
        infoLabel.setLocation(30, 30);
    }


    @Override
    protected void windowIsClosing() {
        stopSound();
    }

    private void stopSound() {
        if (audioClip != null) {
            audioClip.stop();
            audioClip = null;
        }
    }

    @Action
    public void okBtnAction() {
        stopSound();
        doClose();
    }

//    private ActionMap getActionMap() {
//        return Swinger.getActionMap(this.getClass(), this);
//    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        infoLabel = new JLabel();
        infoLabel.setName("infoLabel");

        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        xImagePanel = new JLabel();
        JPanel buttonBar = new JPanel();
        btnOK = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== AboutDialog ========
        {
            this.setName("AboutDialog");
            Container aboutDialogContentPane = this.getContentPane();
            aboutDialogContentPane.setLayout(new BorderLayout());

            //======== dialogPane ========
            {
                dialogPane.setBorder(Borders.DIALOG_BORDER);
                dialogPane.setName("dialogPane");
                dialogPane.setLayout(new BorderLayout());

                //======== contentPanel ========
                {
                    contentPanel.setName("contentPanel");
                    //contentPanel.setLayout(new BorderLayout());

                    //---- xImagePanel ----
                    xImagePanel.setName("xImagePanel");
                    contentPanel.add(xImagePanel, BorderLayout.CENTER);
                    contentPanel.add(infoLabel);
                }
                dialogPane.add(contentPanel, BorderLayout.CENTER);

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                    buttonBar.setName("buttonBar");

                    //---- btnOK ----
                    btnOK.setName("btnOK");

                    PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                            "default:grow, max(pref;42dlu), default:grow",
                            "pref"), buttonBar);

                    buttonBarBuilder.add(btnOK, cc.xy(2, 1));
                }
                dialogPane.add(buttonBar, BorderLayout.SOUTH);
            }
            aboutDialogContentPane.add(dialogPane, BorderLayout.CENTER);
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private JButton btnOK;

}
