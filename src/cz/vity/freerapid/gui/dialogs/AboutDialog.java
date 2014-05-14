package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.gui.dialogs.abouteffect.VolleyExplosion;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.pushingpixels.trident.Timeline;
import org.pushingpixels.trident.TimelineScenario;
import org.pushingpixels.trident.callback.TimelineScenarioCallback;
import org.pushingpixels.trident.swing.SwingRepaintTimeline;

import javax.swing.*;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

/**
 * @author Vity
 * @author KirillG
 * Effect source code: http://kenai.com/projects/trident/pages/SimpleTimelineScenario
 */

public class AboutDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(AboutDialog.class.getName());
    private JLabel xImagePanel;
    private JLabel infoLabel;
    private AudioClip audioClip;
    private final Set<VolleyExplosion> volleys;
    @SuppressWarnings({"MismatchedQueryAndUpdateOfCollection"})
    private final Map<VolleyExplosion, TimelineScenario> volleyScenarios;

    public AboutDialog(Frame owner) throws HeadlessException {
        super(owner, true);
        this.setName("AboutDialog");
        this.volleys = new HashSet<VolleyExplosion>();
        this.volleyScenarios = new HashMap<VolleyExplosion, TimelineScenario>();

        try {
            initComponents();
            build();
            String title = this.getTitle();
            title = title + ' ' + Consts.VERSION;
            final String buildNumber = readBuildNumber();
            if (buildNumber != null)
                title = title + "  build #" + buildNumber;
            this.setTitle(title);
            Timeline repaint = new SwingRepaintTimeline(this);
            repaint.playLoop(Timeline.RepeatBehavior.LOOP);

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


        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if ((getWidth() == 0) || (getHeight() == 0))
                    return;
                new Thread() {
                    @Override
                    public void run() {
                        //noinspection ConstantConditions
                        for (int i = 10; i >= 0; i--)
                            addExplosions(5);

                    }
                }.start();
            }
        });

    }


    private String readBuildNumber() {
        try {
            String classContainer = AboutDialog.class.getProtectionDomain().getCodeSource().getLocation().toString();
            URL manifestUrl = new URL("jar:" + classContainer + "!/META-INF/MANIFEST.MF");
            Manifest manifest = new Manifest(manifestUrl.openStream());
            final Attributes attributes = manifest.getMainAttributes();
            return attributes.getValue("Build");
        } catch (FileNotFoundException e) {
            //logger.info("Manifest was not found - IDE mode");
            return null;
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            return null;
        }
    }


    private void buildGUI() {
        final Icon imageIcon = Swinger.getResourceMap().getIcon("splash.iconImage");
        xImagePanel.setIcon(imageIcon);
        final Dimension pref = new Dimension(imageIcon.getIconWidth() + 2, imageIcon.getIconHeight() + 2);
        xImagePanel.setPreferredSize(pref);
        xImagePanel.setBorder(BorderFactory.createEtchedBorder());
        xImagePanel.setLayout(new BoxLayout(xImagePanel, BoxLayout.Y_AXIS));
        xImagePanel.add(infoLabel);
        xImagePanel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        xImagePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Browser.showHomepage();
            }
        });
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

    private void addExplosions(int count) {
        final CountDownLatch latch = new CountDownLatch(count);

        for (int i = 0; i < count; i++) {
            int r = (int) (255 * Math.random());
            int g = (int) (100 + 155 * Math.random());
            int b = (int) (50 + 205 * Math.random());
            Color color = new Color(r, g, b);

            int x = 60 + (int) ((xImagePanel.getWidth() - 120) * Math.random());
            int y = 60 + (int) ((xImagePanel.getHeight() - 120) * Math.random());
            final VolleyExplosion exp = new VolleyExplosion(x, y, color);
            synchronized (volleys) {
                volleys.add(exp);
                TimelineScenario scenario = exp.getExplosionScenario();
                scenario.addCallback(new TimelineScenarioCallback() {
                    @Override
                    public void onTimelineScenarioDone() {
                        synchronized (volleys) {
                            volleys.remove(exp);
                            volleyScenarios.remove(exp);
                            latch.countDown();
                        }
                    }
                });
                volleyScenarios.put(exp, scenario);
                scenario.play();
            }
        }

        try {
            latch.await();
        } catch (Exception exc) {
            //ignore
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
        xImagePanel = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                synchronized (volleys) {
                    for (VolleyExplosion exp : volleys) {
                        exp.paint(g);
                    }
                }
            }
        };
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
                dialogPane.setBorder(Borders.DIALOG);
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
                    buttonBar.setBorder(Borders.BUTTON_BAR_PAD);
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
