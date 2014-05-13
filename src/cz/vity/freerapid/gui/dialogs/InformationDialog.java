package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JDirectoryChooser;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.ContentPanel;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.models.RecentsFilesComboModel;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Task;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
//TODO Sorry, there are currently no free download slots available on this server.
public class InformationDialog extends AppFrame implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(InformationDialog.class.getName());

    private final ManagerDirector director;
    private final DownloadFile file;
    private PresentationModel<DownloadFile> model;


    public InformationDialog(Frame owner, ManagerDirector director, DownloadFile file) throws HeadlessException {
        super(owner);
        this.director = director;
        this.file = file;

        this.setName("InformationDialog");
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
        return okButton;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return cancelButton;
    }

    @org.jdesktop.application.Action
    public void cancelBtnAction() {
        setResult(RESULT_CANCEL);
        if (model != null)
            model.triggerFlush();
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
        model = new PresentationModel<DownloadFile>(file);
        Bindings.bind(descriptionArea, model.getBufferedModel("description"));

        file.addPropertyChangeListener(this);
    }

    private void buildGUI() {
        new CompoundUndoManager(descriptionArea);

        comboPath.setModel(new RecentsFilesComboModel(UserProp.LAST_USED_SAVED_PATH, true));
        AutoCompleteDecorator.decorate(comboPath);

        comboPath.setSelectedItem(file.getSaveToDirectory().getAbsolutePath());
        progressBar.setFont(progressBar.getFont().deriveFont(Font.BOLD, 16.0F));
        progressBar.setStringPainted(true);

        Swinger.inputFocus(descriptionArea);
    }

    @org.jdesktop.application.Action
    public void okBtnAction() {
        if (!validateChanges())
            return;
        AppPrefs.storeProperty(UserProp.LAST_COMBO_PATH, comboPath.getSelectedItem().toString());
        final File outputDir = new File(comboPath.getEditor().getItem().toString());
        file.setSaveToDirectory(outputDir);
        setResult(RESULT_OK);
        if (model != null)
            model.triggerCommit();
        doClose();
    }

    @Override
    public void doClose() {
        file.removePropertyChangeListener(this);
        super.doClose();
        if (model != null) {
            model.setBean(null);
        }
        if (getModalResult() == RESULT_OK) {
            comboPath.addItem(comboPath.getSelectedItem());
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


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("InformationDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        iconLabel = new JLabel();
        pathLabel = new JLabel();
        JLabel labelFrom = new JLabel();
        fieldFrom = new JTextField();
        JLabel labelSize = new JLabel();
        fieldSize = new JTextField();
        JLabel labelDescription = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        descriptionArea = ComponentFactory.getTextArea();
        JPanel optionsPanel = new JPanel();
        JLabel saveToLabel = new JLabel();
        comboPath = new JComboBox();
        btnSelectPath = new JButton();
        progressBar = new JProgressBar();
        JLabel labelRemaining = new JLabel();
        remainingLabel = new JLabel();
        JLabel labelEstimateTime = new JLabel();
        estTimeLabel = new JLabel();
        JLabel labelCurrentSpeed = new JLabel();
        currentSpeedLabel = new JLabel();
        JLabel labelAverageSpeed = new JLabel();
        avgSpeedLabel = new JLabel();
        JXButtonPanel buttonBar = new JXButtonPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG_BORDER);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- iconLabel ----
                iconLabel.setName("iconLabel");

                //---- pathLabel ----
                pathLabel.setName("pathLabel");
                pathLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

                //---- labelFrom ----
                labelFrom.setName("labelFrom");

                //---- fieldFrom ----
                fieldFrom.setBorder(null);
                fieldFrom.setOpaque(false);
                fieldFrom.setName("fieldFrom");

                //---- labelSize ----
                labelSize.setName("labelSize");

                //---- fieldSize ----
                fieldSize.setBorder(null);
                fieldSize.setOpaque(false);

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
                                    FormFactory.DEFAULT_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormFactory.DEFAULT_COLSPEC
                            },
                            RowSpec.decodeSpecs("default")), optionsPanel);

                    optionsPanelBuilder.add(saveToLabel, cc.xy(1, 1));
                    optionsPanelBuilder.add(comboPath, cc.xy(3, 1));
                    optionsPanelBuilder.add(btnSelectPath, cc.xy(5, 1));
                }

                //---- progressBar ----
                progressBar.setFont(new Font("Tahoma", Font.BOLD, 16));

                //---- labelRemaining ----
                labelRemaining.setName("labelRemaining");

                //---- remainingLabel ----
                remainingLabel.setName("remainingLabel");
                remainingLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

                //---- labelEstimateTime ----
                labelEstimateTime.setName("labelEstimateTime");

                //---- estTimeLabel ----
                estTimeLabel.setName("estTimeLabel");
                estTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

                //---- labelCurrentSpeed ----
                labelCurrentSpeed.setName("labelCurrentSpeed");

                //---- currentSpeedLabel ----
                currentSpeedLabel.setName("currentSpeedLabel");
                currentSpeedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

                //---- labelAverageSpeed ----
                labelAverageSpeed.setName("labelAverageSpeed");

                //---- avgSpeedLabel ----
                avgSpeedLabel.setName("avgSpeedLabel");
                avgSpeedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                new ColumnSpec(Sizes.dluX(49)),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec("max(min;50dlu)")
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
                                new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(50)), FormSpec.DEFAULT_GROW),
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec("fill:max(pref;20dlu)"),
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(iconLabel, cc.xywh(1, 1, 1, 5));
                contentPanelBuilder.add(pathLabel, cc.xywh(3, 1, 7, 1));
                contentPanelBuilder.add(labelFrom, cc.xy(3, 3));
                contentPanelBuilder.add(fieldFrom, cc.xywh(5, 3, 5, 1));
                contentPanelBuilder.add(labelSize, cc.xy(3, 5));
                contentPanelBuilder.add(fieldSize, cc.xywh(5, 5, 3, 1));
                contentPanelBuilder.add(labelDescription, cc.xy(1, 7));
                contentPanelBuilder.add(scrollPane1, cc.xywh(1, 9, 9, 1));
                contentPanelBuilder.add(optionsPanel, cc.xywh(1, 11, 9, 1));
                contentPanelBuilder.add(progressBar, cc.xywh(1, 13, 9, 1));
                contentPanelBuilder.add(labelRemaining, cc.xy(1, 15));
                contentPanelBuilder.add(remainingLabel, cc.xywh(3, 15, 3, 1));
                contentPanelBuilder.add(labelEstimateTime, cc.xy(7, 15));
                contentPanelBuilder.add(estTimeLabel, cc.xy(9, 15));
                contentPanelBuilder.add(labelCurrentSpeed, cc.xy(1, 17));
                contentPanelBuilder.add(currentSpeedLabel, cc.xywh(3, 17, 3, 1));
                contentPanelBuilder.add(labelAverageSpeed, cc.xy(7, 17));
                contentPanelBuilder.add(avgSpeedLabel, cc.xy(9, 17));
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
                                FormFactory.UNRELATED_GAP_COLSPEC,
                                new ColumnSpec("max(pref;55dlu)"),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC
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
        updateFrom();
        updateSize();
        updateFileName();
        updateState();
    }

    private void updateState() {
        updateAvgSpeed();
        updateSpeed();
        updateDurationTime();
        updateEstimateTime();
        updateDownloaded();
        updateEnabled();
    }

    private void updateEnabled() {
        final DownloadState state = file.getState();
        final Action okAction = getActionMap().get("okBtnAction");
        final Action selectAction = getActionMap().get("btnSelectPathAction");
        final boolean enabled = state != DownloadState.COMPLETED && state != DownloadState.DELETED;
        okAction.setEnabled(enabled);
        selectAction.setEnabled(enabled);
        descriptionArea.setEditable(enabled);
        descriptionArea.setEnabled(enabled);
        comboPath.setEditable(enabled);
        comboPath.setEnabled(enabled);
    }

    private void updateDownloaded() {
        final int n = ContentPanel.getProgress(file);
        progressBar.setString(n + "%");
        progressBar.setValue(n);
    }

    private void updateSpeeds() {
        updateAvgSpeed();
        updateSpeed();
        updateEstimateTime();
        updateDurationTime();
    }

    private void updateSize() {
        final long fs = file.getFileSize();
        String value;
        if (fs >= 0) {
            value = ContentPanel.bytesToAnother(fs);
            value += "  (" + NumberFormat.getIntegerInstance().format(fs) + " B)";
        } else {
            value = "unknown";
        }

        fieldSize.setText(value);
    }

    private void updateAvgSpeed() {
        String value;
        if (file.getState() == DownloadState.DOWNLOADING) {
            if (file.getSpeed() >= 0) {
                value = ContentPanel.bytesToAnother((long) file.getAverageSpeed()) + "/s";
            } else value = "0 B/s";
        } else value = "";
        avgSpeedLabel.setText(getResourceMap().getString("textBold", value));
    }

    private void updateSpeed() {
        String value;
        if (file.getState() == DownloadState.DOWNLOADING) {
            if (file.getSpeed() >= 0) {
                value = ContentPanel.bytesToAnother(file.getSpeed()) + "/s";
            } else value = "0 B/s";
        } else value = "";
        currentSpeedLabel.setText(getResourceMap().getString("textBold", value));
    }

    private void updateDurationTime() {
        String value;
        if (DownloadState.isProcessState(file.getState())) {
            Task task = file.getTask();
            if (task != null)
                value = ContentPanel.secondsToHMin(task.getExecutionDuration(TimeUnit.SECONDS));
            else value = "";
        } else value = "";
        estTimeLabel.setText(getResourceMap().getString("textBold", value));
    }

    private void updateEstimateTime() {
        String value = "";
        final DownloadState state = file.getState();
        if (state == DownloadState.DOWNLOADING) {
            long hasToBeDownloaded = file.getFileSize() - file.getDownloaded();
            final float speed = file.getAverageSpeed();

            if (Float.compare(0, speed) != 0) {
                value = ContentPanel.secondsToHMin(Math.round(hasToBeDownloaded / speed));
            } else value = "Estimating...";
        } else if (state == DownloadState.WAITING) {
            value = "Waiting " + ContentPanel.secondsToHMin(file.getSleep());
        }
        remainingLabel.setText(getResourceMap().getString("textBold", value));
    }

    private void updateFileName() {
        final Icon icon = director.getFileTypeIconProvider().getIconImageByFileType(file.getFileType(), true);
        iconLabel.setIcon(icon);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        pathLabel.setText(getResourceMap().getString("textBold", file.getOutputFile().getAbsolutePath()));
    }

    private void updateFrom() {
        fieldFrom.setText(file.getFileUrl().toExternalForm());
    }

    private JLabel iconLabel;
    private JLabel pathLabel;
    private JTextField fieldFrom;
    private JTextField fieldSize;
    private JTextArea descriptionArea;
    private JComboBox comboPath;
    private JButton btnSelectPath;
    private JProgressBar progressBar;
    private JLabel remainingLabel;
    private JLabel currentSpeedLabel;
    private JLabel avgSpeedLabel;
    private JButton okButton;
    private JButton cancelButton;
    private JLabel estTimeLabel;

    public void propertyChange(PropertyChangeEvent evt) {
        if ("speed".equals(evt.getPropertyName())) {
            updateSpeeds();
        } else if ("averageSpeed".equals(evt.getPropertyName())) {
            updateSpeeds();
        } else if ("state".equals(evt.getPropertyName())) {
            updateState();
        } else if ("fileName".equals(evt.getPropertyName())) {
            updateFileName();
        } else if ("fileSize".equals(evt.getPropertyName())) {
            updateSize();
        } else if ("sleep".equals(evt.getPropertyName())) {
            updateEstimateTime();
            updateDurationTime();
        } else if ("downloaded".equals(evt.getPropertyName())) {
            updateDownloaded();
        }
    }
}
