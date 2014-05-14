package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.application.SubmitErrorInfo;
import cz.vity.freerapid.core.tasks.SubmitErrorInfoTask;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
import org.jdesktop.application.TaskEvent;
import org.jdesktop.application.TaskListener;

import javax.swing.*;
import java.awt.*;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class SubmitErrorDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(SubmitErrorDialog.class.getName());
    private final SubmitErrorInfo errorInfo;
    private PresentationModel<SubmitErrorInfo> model = null;


    public SubmitErrorDialog(final JFrame owner, final SubmitErrorInfo errorInfo) {
        super(owner, true);
        this.errorInfo = errorInfo;
        this.setName("SubmitErrorDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
        }
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOk;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Action
    public void cancelBtnAction() {
        model.triggerFlush();
        doClose();
    }

    @Action
    public void btnConnectionAction() {
        final ConnectDialog connectDialog = new ConnectDialog(this);
        this.getApp().prepareDialog(connectDialog, true);
    }

    @Action(block = Task.BlockingScope.APPLICATION)
    public Task<Void, Void> okBtnAction() {
        this.setResult(RESULT_OK);
        model.triggerCommit();

        this.setVisible(false);

        final SubmitErrorInfoTask errorInfoTask = new SubmitErrorInfoTask(errorInfo);
        errorInfoTask.addTaskListener(new TaskListener.Adapter<Void, Void>() {

            @Override
            public void failed(TaskEvent<Throwable> event) {
                showCheckYourConnection(event.getValue());
                setVisible(true);
            }

            @Override
            public void succeeded(TaskEvent<Void> event) {
                Swinger.showInformationDialog(getResourceMap().getString("infomessage_submit_succeed"));
                doClose();
            }
        });
        return errorInfoTask;
    }

    private void showCheckYourConnection(Throwable value) {
        if (value instanceof UnknownHostException) {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_submit_failed", value.getLocalizedMessage());
        }

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

        setAction(btnOk, "okBtnAction");
        setAction(btnCancel, "cancelBtnAction");
        setAction(btnConnection, "btnConnectionAction");
        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void buildGUI() {
        model = new PresentationModel<SubmitErrorInfo>(errorInfo, new Trigger());
        Bindings.bind(fieldName, model.getBufferedModel("name"));
        Bindings.bind(fieldEmail, model.getBufferedModel("email"));
        Bindings.bind(commentTextArea, model.getBufferedModel("comment"));
    }

//    private ActionMap getActionMap() {
//        return Swinger.getActionMap(this.getClass(), this);
//    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelName = new JLabel();
        fieldName = ComponentFactory.getTextField();
        JLabel labelEmail = new JLabel();
        fieldEmail = ComponentFactory.getTextField();
        JLabel labelComment = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        commentTextArea = ComponentFactory.getTextArea();
        JLabel labelDescribeInfo = new JLabel();
        JPanel buttonBar = new JPanel();
        btnConnection = new JButton();
        btnOk = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

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

                //---- labelName ----
                labelName.setLabelFor(fieldName);
                labelName.setName("labelName");

                //---- fieldName ----
                fieldName.setColumns(15);
                fieldName.setName("fieldName");

                //---- labelEmail ----
                labelEmail.setLabelFor(fieldEmail);
                labelEmail.setName("labelEmail");

                //---- fieldEmail ----
                fieldEmail.setColumns(15);
                fieldEmail.setName("fieldEmail");

                //---- labelComment ----
                labelComment.setLabelFor(commentTextArea);
                labelComment.setName("labelComment");

                //======== scrollPane1 ========
                {
                    scrollPane1.setName("scrollPane1");

                    //---- commentTextArea ----
                    commentTextArea.setRows(10);
                    commentTextArea.setLineWrap(true);
                    commentTextArea.setName("commentTextArea");
                    scrollPane1.setViewportView(commentTextArea);
                }

                //---- labelDescribeInfo ----
                labelDescribeInfo.setName("labelDescribeInfo");

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(labelName, cc.xy(1, 1));
                contentPanelBuilder.add(fieldName, cc.xy(3, 1));
                contentPanelBuilder.add(labelEmail, cc.xy(5, 1));
                contentPanelBuilder.add(fieldEmail, cc.xy(7, 1));
                contentPanelBuilder.add(labelComment, cc.xywh(1, 3, 1, 1, CellConstraints.DEFAULT, CellConstraints.TOP));
                contentPanelBuilder.add(scrollPane1, cc.xywh(3, 3, 5, 1));
                contentPanelBuilder.add(labelDescribeInfo, cc.xywh(3, 5, 5, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_PAD);
                buttonBar.setName("buttonBar");

                //---- button1 ----
                btnConnection.setName("btnConnection");

                //---- okButton ----
                btnOk.setName("okButton");

                //---- cancelButton ----

                btnCancel.setName("cancelButton");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                ColumnSpec.decode("max(min;10dlu):grow"),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.BUTTON_COLSPEC,
                                FormSpecs.RELATED_GAP_COLSPEC,
                                ComponentFactory.BUTTON_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);

                buttonBarBuilder.add(btnConnection, cc.xywh(2, 1, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
                buttonBarBuilder.add(btnOk, cc.xy(6, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(8, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private JTextField fieldName;
    private JTextField fieldEmail;
    private JTextArea commentTextArea;
    private JButton btnConnection;
    private JButton btnOk;
    private JButton btnCancel;

}
