package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JDirectoryChooser;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.FRDUtils;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.models.RecentsFilesComboModel;
import cz.vity.freerapid.utilities.FileUtils;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.logging.Logger;

/**
 * @author Vity
 */

public class MultipleSettingsDialog extends AppFrame implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(MultipleSettingsDialog.class.getName());

    private final java.util.List<DownloadFile> files;
    private final DownloadFile file;
    private JLabel titleLabel;


    public MultipleSettingsDialog(Frame owner, java.util.List<DownloadFile> files) throws Exception {
        super(owner);
        this.files = files;
        this.file = files.get(0);

        this.setName("MultipleSettingsDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            doClose();
            throw e;
        }

    }


    @Override
    protected AbstractButton getBtnOK() {
        return okButton;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return cancelButton;
    }

    @org.jdesktop.application.Action
    public void cancelBtnAction() {
        setResult(RESULT_CANCEL);

        doClose();
    }


    private void build() {
        inject();
        buildGUI();
        buildModels();

        //final ActionMap actionMap = getActionMap();
        setAction(okButton, "okBtnAction");
        setAction(cancelButton, "cancelBtnAction");

        setAction(btnSelectPath, "btnSelectPathAction");

        updateInit();
    }

    private void buildModels() {

        for (DownloadFile downloadFile : files) {
            downloadFile.addPropertyChangeListener(this);
        }
    }

    private void buildGUI() {
        new CompoundUndoManager(descriptionArea);

        comboPath.setModel(new RecentsFilesComboModel(UserProp.LAST_USED_SAVED_PATH, true));
        //AutoCompleteDecorator.decorate(comboPath);

        comboPath.setSelectedItem(FileUtils.getAbsolutFile(file.getSaveToDirectory()).toString());
        Swinger.inputFocus(descriptionArea);

        descriptionArea.setText(file.getDescription());

        fieldSize.setOpaque(false);

        fieldSize.setBackground(this.getBackground());

        fieldSize.setEditable(false);

        descriptionArea.setFont(descriptionArea.getFont().deriveFont(11.0F));

        final StringBuilder builder = new StringBuilder();

        for (DownloadFile downloadFile : files) {
            builder.append(downloadFile.getFileName()).append("<br>");
        }
        titleLabel.setToolTipText(getResourceMap().getString("html", builder.toString()));
    }

    @org.jdesktop.application.Action
    public void okBtnAction() {
        if (!validateChanges())
            return;
        AppPrefs.storeProperty(UserProp.LAST_COMBO_PATH, comboPath.getSelectedItem().toString());
        final File outputDir = new File(comboPath.getEditor().getItem().toString());
        final File dir = FRDUtils.getAbsRelPath(outputDir);
        file.setSaveToDirectory(dir);

        final String desc = descriptionArea.getText();
        for (DownloadFile downloadFile : files) {
            downloadFile.setSaveToDirectory(dir);
            downloadFile.setDescription(desc);
        }
        setResult(RESULT_OK);

        doClose();
    }

    @Override
    public void doClose() {
        for (DownloadFile downloadFile : files) {
            downloadFile.removePropertyChangeListener(this);
        }

        super.doClose();

        if (getModalResult() == RESULT_OK) {
            comboPath.addItem(comboPath.getSelectedItem());
            ((RecentsFilesComboModel) comboPath.getModel()).setAsMRU(comboPath.getSelectedItem());
        }
    }

    private boolean validateChanges() {
        final String dir = (String) comboPath.getEditor().getItem();
        if (dir == null || !new File(dir).isDirectory()) {
            Swinger.showErrorMessage(this.getResourceMap(), "noDirectoryMessage");
            btnSelectPathAction();
            return false;
        }
        return true;
    }


    @org.jdesktop.application.Action
    public void btnSelectPathAction() {
        final JDirectoryChooser directoryChooser = new JDirectoryChooser(comboPath.getEditor().getItem().toString());
        if (directoryChooser.showDialog(this, getResourceMap().getString("SelectDirectory")) != JDirectoryChooser.CANCEL_OPTION) {
            comboPath.getEditor().setItem(directoryChooser.getSelectedFile().getAbsolutePath());
            Swinger.inputFocus(comboPath);
        }
    }


    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("MultipleSettingsDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel iconLabel = new JLabel();
        titleLabel = new JLabel();
        JLabel labelSize = new JLabel();
        fieldSize = new JTextField();
        JLabel labelDescription = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        descriptionArea = ComponentFactory.getTextArea();
        JPanel optionsPanel = new JPanel();
        JLabel saveToLabel = new JLabel();
        comboPath = new JComboBox();
        btnSelectPath = new JButton();
        JXButtonPanel buttonBar = new JXButtonPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- iconLabel ----
                iconLabel.setName("iconLabel");

                //---- titleLabel ----
                titleLabel.setName("titleLabel");
                titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

                //---- labelSize ----
                labelSize.setName("labelSize");

                //---- fieldSize ----
                fieldSize.setBorder(null);
                fieldSize.setOpaque(false);
                fieldSize.setEditable(false);

                //---- labelDescription ----
                labelDescription.setName("labelDescription");

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(descriptionArea);
                }

                //======== optionsPanel ========
                {

                    //---- saveToLabel ----
                    saveToLabel.setName("saveToLabel");
                    saveToLabel.setLabelFor(comboPath);

                    //---- comboPath ----
                    comboPath.setEditable(true);

                    //---- btnSelectPath ----
                    btnSelectPath.setName("btnSelectPath");

                    PanelBuilder optionsPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC
                            },
                            RowSpec.decodeSpecs("default")), optionsPanel);

                    optionsPanelBuilder.add(saveToLabel, cc.xy(1, 1));
                    optionsPanelBuilder.add(comboPath, cc.xy(3, 1));
                    optionsPanelBuilder.add(btnSelectPath, cc.xy(5, 1));
                }

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec(Sizes.dluX(49)),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                ColumnSpec.decode("max(min;70dlu)")
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.TOP, Sizes.PREFERRED, FormSpec.NO_GROW),
                                FormSpecs.RELATED_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(50)), FormSpec.DEFAULT_GROW),
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(iconLabel, cc.xywh(1, 1, 1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
                contentPanelBuilder.add(titleLabel, cc.xywh(3, 1, 5, 1));
                contentPanelBuilder.add(labelSize, cc.xy(3, 3));
                contentPanelBuilder.add(fieldSize, cc.xywh(5, 3, 3, 1));
                contentPanelBuilder.add(labelDescription, cc.xy(1, 7));
                contentPanelBuilder.add(scrollPane1, cc.xywh(1, 9, 7, 1));
                contentPanelBuilder.add(optionsPanel, cc.xywh(1, 11, 7, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

                //---- okButton ----
                okButton.setName("okButton");

                //---- cancelButton ----
                cancelButton.setName("cancelButton");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.UNRELATED_GAP_COLSPEC,
                                ColumnSpec.decode("max(pref;42dlu)"),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC
                        },
                        RowSpec.decodeSpecs("fill:pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{3, 5}});

                buttonBarBuilder.add(okButton, cc.xy(3, 1));
                buttonBarBuilder.add(cancelButton, cc.xy(5, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }


    private void updateInit() {
        updateSize();
        updateState();
    }

    private void updateState() {
        updateEnabled();
    }

    private void updateEnabled() {
        boolean enabled = true;
        for (DownloadFile downloadFile : files) {
            final DownloadState s = downloadFile.getState();
            if (s == DownloadState.COMPLETED || s == DownloadState.DELETED) {
                enabled = false;
                break;
            }
        }
        final Action okAction = getActionMap().get("okBtnAction");
        final Action selectAction = getActionMap().get("btnSelectPathAction");
        okAction.setEnabled(enabled);
        selectAction.setEnabled(enabled);
        descriptionArea.setEditable(enabled);
        //descriptionArea.setEnabled(enabled);
        comboPath.setEditable(enabled);
        comboPath.setEnabled(enabled);
    }


    private void updateSize() {
        long fs = 0;
        for (DownloadFile downloadFile : files) {
            final long fileSize = downloadFile.getFileSize();
            if (fileSize < 0) {
                fs = -1;
                break;
            } else fs += fileSize;
        }
        String value;
        if (fs >= 0) {
            value = ContentPanel.bytesToAnother(fs);
            if (fs >= 1024)
                value = value + "  (" + NumberFormat.getIntegerInstance().format(fs) + " B)";
        } else {
            value = getResourceMap().getString("unknown");
        }

        fieldSize.setText(value);
    }


    private JTextField fieldSize;
    private JTextArea descriptionArea;
    private JComboBox comboPath;
    private JButton btnSelectPath;
    private JButton okButton;
    private JButton cancelButton;


    public void propertyChange(PropertyChangeEvent evt) {
        if ("state".equals(evt.getPropertyName())) {
            updateState();
        } else if ("fileSize".equals(evt.getPropertyName())) {
            updateSize();
        }
    }
}