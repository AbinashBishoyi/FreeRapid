package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.LimitedPlainDocument;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.binding.MyPresentationModel;
import cz.vity.freerapid.swing.components.CompTitledPane;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class ConnectDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(ConnectDialog.class.getName());
    private MyPresentationModel model = null;

    public ConnectDialog(JDialog owner) {
        super(owner, true);
        this.setName("ConnectDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
        }
    }

    @Action
    public void cancelBtnAction() {
        model.triggerFlush();
        doClose();
    }

    @Action
    public void okBtnAction() {
        this.setResult(RESULT_OK);
        model.triggerCommit();
        AppPrefs.storeProperty(FWProp.PROXY_PASSWORD, Utils.generateXorString(String.valueOf(fieldPassword.getPassword())));
        doClose();
    }

    @Override
    public void doClose() {
        if (model != null)
            model.release();
        super.doClose();
    }

    private void build() {
        inject();
        buildGUI();
        buildModels();
        setAction(btnOk, "okBtnAction");
        setAction(btnCancel, "cancelBtnAction");
        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

//    private ActionMap getActionMap() {
//        return Swinger.getActionMap(this.getClass(), this);
//    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOk;
    }

    private void buildModels() {
        model = new MyPresentationModel(null, new Trigger());
        bind(fieldHostName, FWProp.PROXY_URL, "");
        bind(fieldUserName, FWProp.PROXY_USERNAME, "");
        bind(fieldPort, FWProp.PROXY_PORT, "");
        bind(checkStorePassword, FWProp.PROXY_SAVEPASSWORD, false);
        bind(checkAuthentification, FWProp.PROXY_LOGIN, false);
        bind(checkUseProxy, FWProp.PROXY_USE, false);
        bind(checkSocksProxy, UserProp.DEFAULT_CONNECTION_SOCKS, UserProp.DEFAULT_CONNECTION_SOCKS_DEFAULT);
        fieldPassword.setText(Utils.generateXorString(AppPrefs.getProperty(FWProp.PROXY_PASSWORD, "")));
    }

    private void buildGUI() {
        checkAuthentification.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final boolean enabled = checkAuthentification.isSelected();
                updateEnabledUseLogin(enabled);
                if (enabled)
                    Swinger.inputFocus(fieldUserName);
            }
        });
        checkUseProxy.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateEnabledUseProxy(checkUseProxy.isSelected());
            }
        });
        final boolean useProxy = AppPrefs.getProperty(FWProp.PROXY_USE, false);
        updateEnabledUseProxy(useProxy);
        updateEnabledUseLogin(AppPrefs.getProperty(FWProp.PROXY_LOGIN, false) && useProxy);
    }

    private void updateEnabledUseProxy(final boolean enabled) {
        //checkProxy.setSelected(use);
        final Component components[] = contentPanel.getComponents();
        for (Component comp : components) {
            if (!comp.equals(checkUseProxy))
                comp.setEnabled(enabled);
        }
        checkSocksProxy.setEnabled(enabled);
        if (checkAuthentification.isSelected() && enabled)
            updateEnabledUseLogin(true);
        else
            updateEnabledUseLogin(false);
        if (enabled)
            Swinger.inputFocus(fieldHostName);
    }

    private void updateEnabledUseLogin(final boolean enabled) {
        fieldUserName.setEnabled(enabled);
        fieldPassword.setEnabled(enabled);
        checkStorePassword.setEnabled(enabled);
        labelLoginName.setEnabled(enabled);
        labelPassword.setEnabled(enabled);
        labelWarning.setEnabled(enabled);
    }


    private void bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
        Bindings.bind(checkBox, model.getBufferedPreferences(key, defaultValue));
    }

    private void bind(final JTextField field, final String key, final Object defaultValue) {
        Bindings.bind(field, model.getBufferedPreferences(key, defaultValue), false);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents


        JPanel dialogPane = new JPanel();
        checkUseProxy = new JCheckBox();
        checkSocksProxy = new JCheckBox();
        checkUseProxy.setName("checkUseHttpProxy");
        checkSocksProxy.setName("checkSocksProxy");

        final JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.LINE_AXIS));
        topPanel.add(checkUseProxy);
        topPanel.add(Box.createHorizontalGlue());
        topPanel.add(checkSocksProxy);

        CompTitledPane panelProxyPane = new CompTitledPane(topPanel);
        contentPanel = panelProxyPane.getContentPane();
        contentPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        JLabel labelHostName = new JLabel();
        fieldHostName = ComponentFactory.getTextField();
        JLabel labelPortNumber = new JLabel();
        fieldPort = ComponentFactory.getTextField();
        checkAuthentification = new JCheckBox();
        labelLoginName = new JLabel();
        fieldUserName = ComponentFactory.getTextField();
        labelPassword = new JLabel();
        fieldPassword = new JPasswordField();
        checkStorePassword = new JCheckBox();
        labelWarning = new JLabel();
        JXButtonPanel buttonBar = new JXButtonPanel();
        buttonBar.setCyclic(true);

        btnOk = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        fieldPort.setDocument(new LimitedPlainDocument("[0-9]{0,6}"));

        //======== this ========

        this.setName("ConnectDialog");
        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG);
            dialogPane.setName("dialogPane");
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setName("contentPanel");

                //---- labelHostName ----

                labelHostName.setLabelFor(fieldHostName);
                labelHostName.setName("labelHostName");

                //---- fieldHostName ----
                fieldHostName.setColumns(10);
                fieldHostName.setName("fieldHostName");

                //---- labelPortNumber ----

                labelPortNumber.setLabelFor(fieldPort);
                labelPortNumber.setName("labelPortNumber");

                //---- fieldPort ----
                fieldPort.setColumns(5);
                fieldPort.setName("fieldPort");

                //---- checkUserProxy ----
                checkAuthentification.setName("checkUserProxy");

                //---- labelLoginName ----

                labelLoginName.setLabelFor(fieldUserName);
                labelLoginName.setName("labelLoginName");

                //---- fieldLogin ----
                fieldUserName.setName("fieldLogin");

                //---- labelPassword ----

                labelPassword.setLabelFor(fieldPassword);
                labelPassword.setName("labelPassword");

                //---- fieldPassword ----
                fieldPassword.setName("fieldPassword");
                //---- checkStorePassword ----

                checkStorePassword.setName("checkStorePassword");

                //---- labelWarning ----
                labelWarning.setName("labelWarning");

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.PREF_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(labelHostName, cc.xy(1, 1));
                contentPanelBuilder.add(fieldHostName, cc.xy(3, 1));
                contentPanelBuilder.add(labelPortNumber, cc.xy(5, 1));
                contentPanelBuilder.add(fieldPort, cc.xy(7, 1));
                contentPanelBuilder.add(checkAuthentification, new CellConstraints(1, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets(0, 7, 0, 0)));
                contentPanelBuilder.add(labelLoginName, cc.xy(1, 5));
                contentPanelBuilder.add(fieldUserName, cc.xy(3, 5));
                contentPanelBuilder.add(labelPassword, cc.xy(1, 7));
                contentPanelBuilder.add(fieldPassword, cc.xy(3, 7));
                contentPanelBuilder.add(checkStorePassword, cc.xywh(5, 7, 3, 1));
                contentPanelBuilder.add(labelWarning, cc.xywh(1, 9, 7, 1));
            }
            dialogPane.add(panelProxyPane, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_PAD);
                buttonBar.setName("buttonBar");

                //---- btnOk ----

                btnOk.setName("btnOk");

                //---- btnCancel ----

                btnCancel.setName("btnCancel");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.GLUE_COLSPEC,
                                FormSpecs.BUTTON_COLSPEC,
                                FormSpecs.RELATED_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4}});

                buttonBarBuilder.add(btnOk, cc.xy(2, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private JTextField fieldHostName;
    private JTextField fieldPort;
    private JCheckBox checkAuthentification;
    private JTextField fieldUserName;
    private JPasswordField fieldPassword;
    private JCheckBox checkStorePassword;
    private JButton btnOk;
    private JButton btnCancel;
    private JPanel contentPanel;
    private JLabel labelLoginName;
    private JLabel labelPassword;
    private JLabel labelWarning;
    private JCheckBox checkUseProxy;
    private JCheckBox checkSocksProxy;
}
