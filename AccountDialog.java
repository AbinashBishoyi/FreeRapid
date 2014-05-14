package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import cz.vity.freerapid.plugins.webclient.hoster.PremiumAccount;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class AccountDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(AccountDialog.class.getName());
    private PresentationModel<PremiumAccount> model;
    private PremiumAccount account;

    public AccountDialog(Frame owner, String title, PremiumAccount account) throws HeadlessException {
        super(owner, true);
        this.setName("AccountDialog");
        if (account == null)
            this.account = new PremiumAccount();
        else
            this.account = account;
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        if (title != null)
            this.setTitle(this.getTitle() + " - " + title);
    }


    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    private void build() {
        inject();
        buildGUI();
        buildModels();
        setAction(btnOK, "okBtnAction");
        setAction(btnCancel, "cancelBtnAction");

        pack();
        locateOnOpticalScreenCenter(this);

    }

    private void buildModels() {
        model = new PresentationModel<PremiumAccount>(account);
        Bindings.bind(fieldUserName, model.getBufferedModel("username"));
        Bindings.bind(fieldPassword, model.getBufferedModel("password"));
    }

    private void buildGUI() {

    }

    @Action
    public void okBtnAction() {
        if (!validated())
            return;
        if (model != null)
            model.triggerCommit();
        setResult(RESULT_OK);
        doClose();
    }

    private boolean validated() {
        if (!Utils.hasValue(fieldUserName.getText())) {
            Swinger.showErrorMessage(getResourceMap(), "message_noUserName");
            Swinger.inputFocus(fieldUserName);
            return false;
        }
        if (fieldPassword.getPassword().length == 0) {
            Swinger.showErrorMessage(getResourceMap(), "message_noPassword");
            Swinger.inputFocus(fieldPassword);
            return false;
        }
        return true;
    }

    @Action
    public void cancelBtnAction() {
        if (model != null)
            model.triggerFlush();
        setResult(RESULT_CANCEL);
        doClose();
    }

    public PremiumAccount getAccount() {
        return account;
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {

        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelIcon = new JLabel();
        JLabel labelUserName = new JLabel();
        fieldUserName = ComponentFactory.getTextField();
        JLabel labelPassword = new JLabel();
        fieldPassword = ComponentFactory.getPasswordField();
        JPanel buttonBar = new JPanel();
        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();
        labelIcon.setName("labelIcon");

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(dialogPane, BorderLayout.CENTER);

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- labelUserName ----
                labelUserName.setName("labelUserName");
                labelUserName.setLabelFor(fieldUserName);

                //---- labelPassword ----
                labelPassword.setName("labelPassword");
                labelPassword.setLabelFor(fieldPassword);

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec("max(pref;35dlu)"),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec("min(pref;40dlu):grow"),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC
                        },
                        new RowSpec[]{
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.UNRELATED_GAP_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(labelIcon, cc.xywh(1, 1, 2, 7, CellConstraints.FILL, CellConstraints.CENTER));
                contentPanelBuilder.add(labelUserName, cc.xy(3, 1));
                contentPanelBuilder.add(fieldUserName, cc.xywh(3, 3, 3, 1));
                contentPanelBuilder.add(labelPassword, cc.xy(3, 5));
                contentPanelBuilder.add(fieldPassword, cc.xywh(3, 7, 3, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);

                //---- btnOK ----
                btnOK.setName("btnOK");

                //---- btnCancel ----
                btnCancel.setName("btnCancel");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec("55px:grow"),
                                ComponentFactory.BUTTON_COLSPEC,
                                FormFactory.RELATED_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC
                        },
                        RowSpec.decodeSpecs("default")), buttonBar);

                buttonBarBuilder.add(btnOK, cc.xy(2, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }

    }

    private JTextField fieldUserName;
    private JPasswordField fieldPassword;
    private JButton btnOK;
    private JButton btnCancel;


}