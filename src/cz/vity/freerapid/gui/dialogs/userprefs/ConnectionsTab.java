package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.FRDUtils;
import cz.vity.freerapid.gui.dialogs.ConnectDialog;
import cz.vity.freerapid.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.FileUtils;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;

/**
 * @author ntoskrnl
 */
public class ConnectionsTab extends UserPreferencesTab {

    private final ManagerDirector managerDirector;
    private boolean updateDefaultConnection = false;

    ConnectionsTab(final UserPreferencesDialog dialog, final ManagerDirector managerDirector) {
        super(dialog);
        this.managerDirector = managerDirector;
    }

    @Override
    public void init() {
        final ValueModel useProxyList = bind(checkUseProxyList, UserProp.USE_PROXY_LIST, UserProp.USE_PROXY_LIST_DEFAULT);
        PropertyConnector.connectAndUpdate(useProxyList, fieldProxyListPath, "enabled");
        PropertyConnector.connectAndUpdate(useProxyList, actionMap.get("btnSelectProxyListAction"), "enabled");

        String property = AppPrefs.getProperty(UserProp.PROXY_LIST_PATH, "");
        if (!property.isEmpty()) {
            property = FileUtils.getAbsolutPath(property);
        }
        fieldProxyListPath.setText(property);
        fieldProxyListPath.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                dialog.getModel().setBuffering(true);
            }
        });

        final ValueModel useDefault = bind(checkUseDefaultConnection, UserProp.USE_DEFAULT_CONNECTION, UserProp.USE_DEFAULT_CONNECTION_DEFAULT);
        PropertyConnector.connectAndUpdate(useDefault, actionMap.get("btnSelectConnectionProxy"), "enabled");

        bind(spinnerMaxConcurrentDownloads, UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT, 1, 1000000, 1);
        bind(spinnerErrorAttemptsCount, UserProp.ERROR_ATTEMPTS_COUNT, UserProp.ERROR_ATTEMPTS_COUNT_DEFAULT, -1, 999, 1);
        bind(spinnerAutoReconnectTime, UserProp.AUTO_RECONNECT_TIME, UserProp.AUTO_RECONNECT_TIME_DEFAULT, 1, 10000, 10);

        bind(spinnerGlobalSpeedSliderMin, UserProp.GLOBAL_SPEED_SLIDER_MIN, UserProp.GLOBAL_SPEED_SLIDER_MIN_DEFAULT, 1, Integer.MAX_VALUE, 5);
        bind(spinnerGlobalSpeedSliderMax, UserProp.GLOBAL_SPEED_SLIDER_MAX, UserProp.GLOBAL_SPEED_SLIDER_MAX_DEFAULT, 1, Integer.MAX_VALUE, 5);
        final int intSpeedSliderStepMax = (Integer) spinnerGlobalSpeedSliderMax.getValue() - (Integer) spinnerGlobalSpeedSliderMin.getValue();
        bind(spinnerGlobalSpeedSliderStep, UserProp.GLOBAL_SPEED_SLIDER_STEP, UserProp.GLOBAL_SPEED_SLIDER_STEP_DEFAULT, 1, intSpeedSliderStepMax < 1 ? 1 : intSpeedSliderStepMax, 1);

        final ChangeListener changeListenerSpeedSlider = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final SpinnerNumberModel spinnerModel = (SpinnerNumberModel) spinnerGlobalSpeedSliderStep.getModel();
                spinnerModel.setMaximum((Integer) spinnerGlobalSpeedSliderMax.getValue() - (Integer) spinnerGlobalSpeedSliderMin.getValue());
                if ((Integer) spinnerModel.getMaximum() < 1) {
                    spinnerModel.setMaximum(1);
                }
                if ((Integer) spinnerModel.getMaximum() < (Integer) spinnerModel.getValue()) {
                    spinnerModel.setValue(spinnerModel.getMaximum());
                }
            }
        };
        spinnerGlobalSpeedSliderMin.addChangeListener(changeListenerSpeedSlider);
        spinnerGlobalSpeedSliderMax.addChangeListener(changeListenerSpeedSlider);

        spinnerGlobalSpeedSliderMin.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((Integer) spinnerGlobalSpeedSliderMin.getValue() > (Integer) spinnerGlobalSpeedSliderMax.getValue()) {
                    spinnerGlobalSpeedSliderMax.setValue(spinnerGlobalSpeedSliderMin.getValue());
                }
            }
        });
        spinnerGlobalSpeedSliderMax.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if ((Integer) spinnerGlobalSpeedSliderMin.getValue() > (Integer) spinnerGlobalSpeedSliderMax.getValue()) {
                    spinnerGlobalSpeedSliderMin.setValue(spinnerGlobalSpeedSliderMax.getValue());
                }
            }
        });

        fieldFileSpeedLimiterValues.setText(AppPrefs.getProperty(UserProp.SPEED_LIMIT_SPEEDS, UserProp.SPEED_LIMIT_SPEEDS_DEFAULT));
        fieldFileSpeedLimiterValues.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                dialog.getModel().setBuffering(true);
            }
        });

        setAction(btnSelectConnectionProxy, "btnSelectConnectionProxy");
        setAction(btnProxyListPathSelect, "btnSelectProxyListAction");
    }

    @Action
    public void btnSelectConnectionProxy() {
        final ConnectDialog connectDialog = new ConnectDialog(dialog);
        MainApp.getInstance(MainApp.class).prepareDialog(connectDialog, true);
        if (connectDialog.getModalResult() == ConnectDialog.RESULT_OK) {
            dialog.getModel().setBuffering(true);
            updateDefaultConnection = true;
        }
    }

    @Action
    public void btnSelectProxyListAction() {
        final File[] files = OpenSaveDialogFactory.getInstance(MainApp.getAContext()).getChooseProxyList();
        if (files.length > 0) {
            fieldProxyListPath.setText(files[0].getAbsolutePath());
            Swinger.inputFocus(fieldProxyListPath);
        }
    }

    @Override
    public void apply() {
        final boolean updateProxyConnectionList = isBuffering(UserProp.USE_PROXY_LIST) || isBuffering(UserProp.PROXY_LIST_PATH) || !AppPrefs.getProperty(UserProp.PROXY_LIST_PATH, "").equals(fieldProxyListPath.getText());
        updateDefaultConnection = updateDefaultConnection || isBuffering(UserProp.USE_DEFAULT_CONNECTION);

        AppPrefs.storeProperty(UserProp.SPEED_LIMIT_SPEEDS, fieldFileSpeedLimiterValues.getText());

        String property = fieldProxyListPath.getText();
        if (!property.isEmpty()) {
            property = FRDUtils.getAbsRelPath(property).getPath();
        }
        AppPrefs.storeProperty(UserProp.PROXY_LIST_PATH, property);

        if (updateDefaultConnection || updateProxyConnectionList) {
            managerDirector.getClientManager().updateConnectionSettings();
            dialog.setUpdateQueue();
        }
    }

    private boolean isBuffering(final String property) {
        return dialog.getModel().getBufferedModel(property).isBuffering();
    }

    @Override
    public void build(final CellConstraints cc) {
        JPanel panelConnections1 = new JPanel();
        JPanel panelProxySettings = new JPanel();
        JPanel panelErrorHandling = new JPanel();
        JPanel panelGlobalSpeedLimiter = new JPanel();
        JPanel panelFileSpeedLimiter = new JPanel();

        JLabel labelMaxConcurrentDownloads = new JLabel();
        labelMaxConcurrentDownloads.setName("labelMaxConcurrentDownloads");
        spinnerMaxConcurrentDownloads = new JSpinner();
        spinnerMaxConcurrentDownloads.setModel(new SpinnerNumberModel(0, 0, 5, 1));
        checkUseDefaultConnection = new JCheckBox();
        checkUseDefaultConnection.setName("checkUseDefaultConnection");
        btnSelectConnectionProxy = new JButton();
        btnSelectConnectionProxy.setName("btnSelectConnectionProxy");

        checkUseProxyList = new JCheckBox();
        checkUseProxyList.setName("checkUseProxyList");
        fieldProxyListPath = new JTextField();
        btnProxyListPathSelect = new JButton();
        btnProxyListPathSelect.setName("btnProxyListPathSelect");
        JLabel labelTextFileFormat = new JLabel();
        labelTextFileFormat.setName("labelTextFileFormat");

        JLabel labelErrorAttemptsCount = new JLabel();
        labelErrorAttemptsCount.setName("labelErrorAttemptsCount");
        spinnerErrorAttemptsCount = new JSpinner();
        spinnerErrorAttemptsCount.setModel(new SpinnerNumberModel(0, 0, 10, 1));
        JLabel labelNoAutoreconnect = new JLabel();
        labelNoAutoreconnect.setName("labelNoAutoreconnect");
        JLabel labelAutoReconnectTime = new JLabel();
        labelAutoReconnectTime.setName("labelAutoReconnectTime");
        spinnerAutoReconnectTime = new JSpinner();
        spinnerAutoReconnectTime.setModel(new SpinnerNumberModel(0, 0, 1000, 5));
        JLabel labelSeconds = new JLabel();
        labelSeconds.setName("labelSeconds");

        JLabel labelSpeedSliderMinValue = new JLabel();
        labelSpeedSliderMinValue.setName("labelSpeedSliderMinValue");
        JLabel labelSpeedSliderMaxValue = new JLabel();
        labelSpeedSliderMaxValue.setName("labelSpeedSliderMaxValue");
        JLabel labelSpeedSliderStep = new JLabel();
        labelSpeedSliderStep.setName("labelSpeedSliderStep");
        spinnerGlobalSpeedSliderMin = new JSpinner();
        spinnerGlobalSpeedSliderMin.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 5));
        spinnerGlobalSpeedSliderMax = new JSpinner();
        spinnerGlobalSpeedSliderMax.setModel(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 5));
        spinnerGlobalSpeedSliderStep = new JSpinner();
        spinnerGlobalSpeedSliderStep.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
        JLabel labelSpeedSliderKbps1 = new JLabel();
        labelSpeedSliderKbps1.setName("labelSpeedSliderKbps");
        JLabel labelSpeedSliderKbps2 = new JLabel();
        labelSpeedSliderKbps2.setName("labelSpeedSliderKbps");
        JLabel labelSpeedSliderKbps3 = new JLabel();
        labelSpeedSliderKbps3.setName("labelSpeedSliderKbps");

        JLabel labelFileSpeedLimiterValues = new JLabel();
        labelFileSpeedLimiterValues.setName("labelFileSpeedLimiterValues");
        fieldFileSpeedLimiterValues = new JTextField();
        fieldFileSpeedLimiterValues.setName("fieldFileSpeedLimiterValues");
        JLabel labelFileSpeedLimiterValuesDesc = new JLabel();
        labelFileSpeedLimiterValuesDesc.setName("labelFileSpeedLimiterValuesDesc");

        JLabel labelRequiresRestart = new JLabel();
        labelRequiresRestart.setName("labelRequiresRestart");
        labelRequiresRestart.setVisible(false);

        this.setBorder(Borders.TABBED_DIALOG);

        //======== panelConnections1 ========
        {
            panelConnections1.setBorder(new TitledBorder(null, resourceMap.getString("panelConnections1.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelConnections1Builder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC,
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            ColumnSpec.decode("max(pref;30dlu)"),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC,
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC,
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.DEFAULT_GROW),
                    },
                    new RowSpec[]{
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.UNRELATED_GAP_ROWSPEC,
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.NARROW_LINE_GAP_ROWSPEC
                    }), panelConnections1);

            panelConnections1Builder.add(labelMaxConcurrentDownloads, cc.xy(3, 1));
            panelConnections1Builder.add(spinnerMaxConcurrentDownloads, cc.xy(5, 1));
            panelConnections1Builder.add(checkUseDefaultConnection, cc.xyw(3, 3, 5));
            panelConnections1Builder.add(btnSelectConnectionProxy, cc.xy(9, 3));
        }

        //======== panelProxySettings ========
        {
            panelProxySettings.setBorder(new TitledBorder(null, resourceMap.getString("panelProxySettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelProxySettingsBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC,
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(200), FormSpec.DEFAULT_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC
                    },
                    new RowSpec[]{
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.NARROW_LINE_GAP_ROWSPEC
                    }), panelProxySettings);

            panelProxySettingsBuilder.add(checkUseProxyList, cc.xy(3, 1));
            panelProxySettingsBuilder.add(fieldProxyListPath, cc.xy(5, 1));
            panelProxySettingsBuilder.add(btnProxyListPathSelect, cc.xy(7, 1));
            panelProxySettingsBuilder.add(labelTextFileFormat, cc.xy(5, 2));
        }

        //======== panelErrorHandling ========
        {
            panelErrorHandling.setBorder(new TitledBorder(null, resourceMap.getString("panelErrorHandling.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelErrorHandlingBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC,
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            ColumnSpec.decode("max(pref;30dlu)"),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC
                    },
                    new RowSpec[]{
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.LINE_GAP_ROWSPEC,
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.LINE_GAP_ROWSPEC,
                            FormSpecs.NARROW_LINE_GAP_ROWSPEC
                    }), panelErrorHandling);

            panelErrorHandlingBuilder.add(labelErrorAttemptsCount, cc.xy(3, 1));
            panelErrorHandlingBuilder.add(spinnerErrorAttemptsCount, cc.xy(5, 1));
            panelErrorHandlingBuilder.add(labelNoAutoreconnect, cc.xy(7, 1));
            panelErrorHandlingBuilder.add(labelAutoReconnectTime, cc.xy(3, 3));
            panelErrorHandlingBuilder.add(spinnerAutoReconnectTime, cc.xy(5, 3));
            panelErrorHandlingBuilder.add(labelSeconds, cc.xy(7, 3));
        }

        //======== panelGlobalSpeedLimiter ========
        {
            panelGlobalSpeedLimiter.setBorder(new TitledBorder(null, resourceMap.getString("panelGlobalSpeedLimiter.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelGlobalSpeedLimiterBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC,
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            new ColumnSpec(Sizes.dluX(40)),
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
                            FormSpecs.NARROW_LINE_GAP_ROWSPEC
                    }), panelGlobalSpeedLimiter);

            panelGlobalSpeedLimiterBuilder.add(labelSpeedSliderMinValue, cc.xy(3, 1));
            panelGlobalSpeedLimiterBuilder.add(spinnerGlobalSpeedSliderMin, cc.xy(5, 1));
            panelGlobalSpeedLimiterBuilder.add(labelSpeedSliderKbps1, cc.xy(7, 1));
            panelGlobalSpeedLimiterBuilder.add(labelSpeedSliderMaxValue, cc.xy(3, 3));
            panelGlobalSpeedLimiterBuilder.add(spinnerGlobalSpeedSliderMax, cc.xy(5, 3));
            panelGlobalSpeedLimiterBuilder.add(labelSpeedSliderKbps2, cc.xy(7, 3));
            panelGlobalSpeedLimiterBuilder.add(labelSpeedSliderStep, cc.xy(3, 5));
            panelGlobalSpeedLimiterBuilder.add(spinnerGlobalSpeedSliderStep, cc.xy(5, 5));
            panelGlobalSpeedLimiterBuilder.add(labelSpeedSliderKbps3, cc.xy(7, 5));
        }

        //======== panelFileSpeedLimiter ========
        {
            panelFileSpeedLimiter.setBorder(new TitledBorder(null, resourceMap.getString("panelFileSpeedLimiter.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelFileSpeedLimiterBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
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
                            FormSpecs.NARROW_LINE_GAP_ROWSPEC
                    }), panelFileSpeedLimiter);

            panelFileSpeedLimiterBuilder.add(labelFileSpeedLimiterValues, cc.xy(3, 1));
            panelFileSpeedLimiterBuilder.add(fieldFileSpeedLimiterValues, cc.xy(3, 3));
            panelFileSpeedLimiterBuilder.add(labelFileSpeedLimiterValuesDesc, cc.xy(3, 5));
        }

        PanelBuilder thisBuilder = new PanelBuilder(new FormLayout(
                new ColumnSpec[]{
                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                },
                new RowSpec[]{
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC
                }), this);

        thisBuilder.add(panelConnections1, cc.xyw(1, 1, 3));
        thisBuilder.add(panelProxySettings, cc.xyw(1, 3, 3));
        thisBuilder.add(panelErrorHandling, cc.xyw(1, 5, 3));
        thisBuilder.add(panelGlobalSpeedLimiter, cc.xyw(1, 7, 1));
        thisBuilder.add(panelFileSpeedLimiter, cc.xyw(3, 7, 1));
        thisBuilder.add(labelRequiresRestart, cc.xyw(1, 9, 3));
    }

    private JSpinner spinnerMaxConcurrentDownloads;
    private JCheckBox checkUseDefaultConnection;
    private JButton btnSelectConnectionProxy;
    private JCheckBox checkUseProxyList;
    private JTextField fieldProxyListPath;
    private JButton btnProxyListPathSelect;
    private JSpinner spinnerErrorAttemptsCount;
    private JSpinner spinnerAutoReconnectTime;
    private JSpinner spinnerGlobalSpeedSliderMin;
    private JSpinner spinnerGlobalSpeedSliderMax;
    private JSpinner spinnerGlobalSpeedSliderStep;
    private JTextField fieldFileSpeedLimiterValues;

}
