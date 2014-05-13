package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonBarUI;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.MyPreferencesAdapter;
import cz.vity.freerapid.gui.MyPresentationModel;
import cz.vity.freerapid.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.vity.freerapid.swing.LaF;
import cz.vity.freerapid.swing.LookAndFeels;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Ladislav Vitasek
 */
public class UserPreferencesDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(UserPreferencesDialog.class.getName());
    private MyPresentationModel model;
    private static final String CARD_PROPERTY = "card";
    private static final String LAF_PROPERTY = "lafFakeProperty";
    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private ApplyPreferenceChangeListener prefListener = null;
    private ResourceMap bundle;

    private static enum Card {
        CARD1, CARD2, CARD3, CARD4
    }

    public UserPreferencesDialog(Frame owner) throws Exception {
        super(owner, true);
        this.setName("UserPreferencesDialog");
        bundle = getResourceMap();
        try {
            initComponents();
            build();
        } catch (Exception e) {
            doClose(); //dialog se pri fatalni chybe zavre
            throw e;
        }
    }


    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }

    private void build() throws CloneNotSupportedException {
        inject();
        buildGUI();
        buildModels();

        setAction(btnOK, "okBtnAction");
        setAction(btnCancel, "cancelBtnAction");


        setDefaultValues();
        showCard(Card.valueOf(AppPrefs.getProperty(FWProp.USER_SETTINGS_SELECTED_CARD, Card.CARD1.toString())));
        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);


    }

    @org.jdesktop.application.Action
    public void btnSelectProxyListAction() {
        final File[] files = OpenSaveDialogFactory.getInstance().getChooseProxyList();
        if (files.length > 0) {
            fieldProxyListPath.setText(files[0].getAbsolutePath());
            Swinger.inputFocus(fieldProxyListPath);
        }
    }

    private void buildGUI() {
        toolbar.setUI(new BlueishButtonBarUI());//nenechat to default?
        //  toolbar.setOrientation(JButtonBar.HORIZONTAL);
        //    toolbar.setUI(new BasicButtonBarUI());//nenechat to default?


        final ActionMap map = getActionMap();

        ButtonGroup group = new ButtonGroup();
        addButton(map.get("generalBtnAction"), Card.CARD1, group);
        addButton(map.get("connectionsBtnAction"), Card.CARD2, group);
        addButton(map.get("soundBtnAction"), Card.CARD3, group);
        addButton(map.get("viewsBtnAction"), Card.CARD4, group);

        setAction(btnProxyListPathSelect, "btnSelectProxyListAction");

    }


    private void addButton(javax.swing.Action action, final Card card, ButtonGroup group) {
        final JToggleButton button = new JToggleButton(action);
        final Dimension size = button.getPreferredSize();
        final Dimension dim = new Dimension(66, size.height);
        button.setFont(button.getFont().deriveFont((float) 10));
        button.setForeground(Color.BLACK);
        button.setMinimumSize(dim);
        button.setPreferredSize(dim);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setOpaque(false);
        toolbar.add(button);
        button.putClientProperty(CARD_PROPERTY, card);
        group.add(button);
    }

    private void showCard(Card card) {
        assert card != null;
        final CardLayout cardLayout = (CardLayout) panelCard.getLayout();
        cardLayout.show(panelCard, card.toString());
        AppPrefs.storeProperty(FWProp.USER_SETTINGS_SELECTED_CARD, card.toString());
        String actionName;
        switch (card) {
            case CARD1:
                actionName = "generalBtnAction";
                break;
            case CARD2:
                actionName = "connectionsBtnAction";
                break;
            case CARD3:
                actionName = "soundBtnAction";
                break;
            case CARD4:
                actionName = "viewsBtnAction";
                break;
            default:
                assert false;
                return;
        }
        javax.swing.Action action = getActionMap().get(actionName);
        assert action != null;
        action.putValue(javax.swing.Action.SELECTED_KEY, Boolean.TRUE);
    }


    private void buildModels() throws CloneNotSupportedException {

        model = new MyPresentationModel(null, new Trigger());

        prefListener = new ApplyPreferenceChangeListener();
        AppPrefs.getPreferences().addPreferenceChangeListener(prefListener);
        bindBasicComponents();

        final ActionMap map = getActionMap();
        final javax.swing.Action actionOK = map.get("okBtnAction");
        PropertyConnector connector = PropertyConnector.connect(model, PresentationModel.PROPERTYNAME_BUFFERING, actionOK, "enabled");
        connector.updateProperty2();

    }

    private void bindBasicComponents() {

        bind(checkAllowOnlyOneInstance, FWProp.ONEINSTANCE, true);
        bind(checkForNewVersion, FWProp.NEW_VERSION, true);
        bind(checkContinueInterrupted, UserProp.DOWNLOAD_ON_APPLICATION_START, true);
        bind(checkCloseWhenAllComplete, UserProp.CLOSE_WHEN_COMPLETED, false);

        bind(spinnerMaxConcurrentDownloads, UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT, 1, 9, 1);
        bind(spinnerErrorAttemptsCount, UserProp.ERROR_ATTEMPTS_COUNT, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT, 0, 20, 1);
        bind(spinnerAutoReconnectTime, UserProp.AUTO_RECONNECT_TIME, UserProp.AUTO_RECONNECT_TIME_DEFAULT, 10, 10000, 10);

        final ValueModel valueModel = bind(checkUseProxyList, UserProp.USE_PROXY_LIST, false);
        PropertyConnector.connectAndUpdate(valueModel, fieldProxyListPath, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, btnProxyListPathSelect, "enabled");

        bind(fieldProxyListPath, UserProp.PROXY_LIST_PATH, "");

        bind(checkPlaySoundWhenComplete, UserProp.PLAY_SOUNDS_OK, true);
        bind(checkPlaySoundInCaseOfError, UserProp.PLAY_SOUNDS_FAILED, true);

        bind(checkDecoratedFrames, FWProp.DECORATED_FRAMES, false);
        bind(checkHideWhenMinimized, FWProp.MINIMIZE_TO_TRAY, true);

        bind(checkShowIconInSystemTray, FWProp.SHOW_TRAY, true);

        bind(comboFileExists, UserProp.FILE_ALREADY_EXISTS, UserProp.FILE_ALREADY_EXISTS_DEFAULT, "fileAlreadyExistsOptions");

        bindLaFCombobox();
    }

    private void bindLaFCombobox() {
        final LookAndFeels lafs = LookAndFeels.getInstance();
        final ListModel listModel = new ArrayListModel<LaF>(lafs.getAvailableLookAndFeels());
        final LookAndFeelAdapter adapter = new LookAndFeelAdapter(LAF_PROPERTY, lafs.getSelectedLaF());
        final SelectionInList<String> inList = new SelectionInList<String>(listModel, model.getBufferedModel(adapter));
        Bindings.bind(comboLaF, inList);
    }


    private void bind(JSpinner spinner, String key, int defaultValue, int minValue, int maxValue, int step) {
        spinner.setModel(SpinnerAdapterFactory.createNumberAdapter(
                model.getBufferedPreferences(key, defaultValue),
                defaultValue,   // defaultValue
                minValue,   // minValue
                maxValue, // maxValue
                step)); // step
    }

    private ValueModel bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
        final ValueModel valueModel = model.getBufferedPreferences(key, defaultValue);
        Bindings.bind(checkBox, valueModel);
        return valueModel;
    }

    private void bind(final JTextField field, final String key, final Object defaultValue) {
        Bindings.bind(field, model.getBufferedPreferences(key, defaultValue), false);
    }

    private void bind(final JComboBox combobox, final String key, final Object defaultValue, final String propertyResourceMap) {
        final String[] stringList = getList(propertyResourceMap);
        if (stringList == null)
            throw new IllegalArgumentException("Property '" + propertyResourceMap + "' does not provide any string list from resource map.");
        bind(combobox, key, defaultValue, stringList);
    }

    private void bind(final JComboBox combobox, String key, final Object defaultValue, final String[] values) {
        if (values == null)
            throw new IllegalArgumentException("List of combobox values cannot be null!!");
        final MyPreferencesAdapter adapter = new MyPreferencesAdapter(key, defaultValue);
        final SelectionInList<String> inList = new SelectionInList<String>(values, new ValueHolder(values[(Integer) adapter.getValue()]), model.getBufferedModel(adapter));
        Bindings.bind(combobox, inList);

    }


    private void setDefaultValues() {

    }

    @org.jdesktop.application.Action
    public void okBtnAction() {
        model.triggerCommit();
//        final String s = AppPrefs.getProperty(UserProp.PROXY_LIST_PATH, "");
//        System.out.println("s = " + s);

        LaF laf = (LaF) comboLaF.getSelectedItem();
        LookAndFeels.getInstance().storeSelectedLaF(laf);

        doClose();
    }

    @org.jdesktop.application.Action
    public void cancelBtnAction() {
        doClose();
    }

    @org.jdesktop.application.Action(selectedProperty = "generalBtnActionSelected")
    public void generalBtnAction(ActionEvent e) {
        showCard(e);
    }

    @org.jdesktop.application.Action
    public void connectionsBtnAction(ActionEvent e) {
        showCard(e);
    }

    @org.jdesktop.application.Action
    public void soundBtnAction(ActionEvent e) {
        showCard(e);
    }

    private void showCard(ActionEvent e) {
        showCard((Card) ((JComponent) e.getSource()).getClientProperty(CARD_PROPERTY));
    }

    public boolean isGeneralBtnActionSelected() {
        return true;
    }


    @org.jdesktop.application.Action
    public void viewsBtnAction(ActionEvent e) {
        showCard(e);
    }


    @Override
    public void doClose() {
        AppPrefs.removeProperty(LAF_PROPERTY);
        logger.log(Level.FINE, "Closing UserPreferenceDialog.");
        try {
            synchronized (this) {
                if (prefListener != null) {
                    AppPrefs.getPreferences().removePreferenceChangeListener(prefListener);
                    prefListener = null;
                }
            }
            if (model != null)
                model.release();
        } finally {
            super.doClose();
        }
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("UserPreferencesDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnOK = new JButton();
        btnCancel = new JButton();
        panelCard = new JPanel();
        JPanel panelGeneral = new JPanel();
        JPanel panelApplicationSettings = new JPanel();
        checkForNewVersion = new JCheckBox();
        checkAllowOnlyOneInstance = new JCheckBox();
        JPanel panelDownloadsSettings = new JPanel();
        checkContinueInterrupted = new JCheckBox();
        checkCloseWhenAllComplete = new JCheckBox();
        JLabel labelIfFilenameExists = new JLabel();
        comboFileExists = new JComboBox();
        JPanel panelSoundSettings = new JPanel();
        JPanel panelSound = new JPanel();
        checkPlaySoundInCaseOfError = new JCheckBox();
        checkPlaySoundWhenComplete = new JCheckBox();
        JPanel panelViews = new JPanel();
        JPanel panelAppearance = new JPanel();
        JLabel labelLaF = new JLabel();
        comboLaF = new JComboBox();
        JLabel labelRequiresRestart2 = new JLabel();
        checkDecoratedFrames = new JCheckBox();
        checkShowIconInSystemTray = new JCheckBox();
        checkHideWhenMinimized = new JCheckBox();
        JPanel panelConnectionSettings = new JPanel();
        JPanel panelConnections1 = new JPanel();
        JLabel labelMaxConcurrentDownloads = new JLabel();
        spinnerMaxConcurrentDownloads = new JSpinner();
        JPanel panelProxySettings = new JPanel();
        checkUseProxyList = new JCheckBox();
        fieldProxyListPath = new JTextField();
        btnProxyListPathSelect = new JButton();
        JLabel labelTextFileFormat = new JLabel();
        JPanel panelErrorHandling = new JPanel();
        JLabel labelErrorAttemptsCount = new JLabel();
        spinnerErrorAttemptsCount = new JSpinner();
        JLabel labelNoAutoreconnect = new JLabel();
        JLabel labelAutoReconnectTime = new JLabel();
        spinnerAutoReconnectTime = new JSpinner();
        JLabel labelSeconds = new JLabel();
        JLabel labelRequiresRestart = new JLabel();
        toolbar = new JButtonBar();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setTitle(bundle.getString("this.title"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 4dlu, 4dlu, 4dlu"));
                    buttonBar.setCyclic(true);

                    //---- btnOK ----
                    btnOK.setName("btnOK");

                    //---- btnCancel ----
                    btnCancel.setName("btnCancel");

                    PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.GLUE_COLSPEC,
                                    new ColumnSpec("max(pref;42dlu)"),
                                    FormFactory.RELATED_GAP_COLSPEC,
                                    FormFactory.PREF_COLSPEC
                            },
                            RowSpec.decodeSpecs("pref")), buttonBar);
                    ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4}});

                    buttonBarBuilder.add(btnOK, cc.xy(2, 1));
                    buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
                }
                contentPanel.add(buttonBar, BorderLayout.SOUTH);

                //======== panelCard ========
                {
                    panelCard.setLayout(new CardLayout());

                    //======== panelGeneral ========
                    {
                        panelGeneral.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== panelApplicationSettings ========
                        {
                            panelApplicationSettings.setBorder(new TitledBorder(null, bundle.getString("panelApplicationSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- checkForNewVersion ----
                            checkForNewVersion.setName("checkForNewVersion");

                            //---- checkAllowOnlyOneInstance ----
                            checkAllowOnlyOneInstance.setName("checkAllowOnlyOneInstance");

                            PanelBuilder panelApplicationSettingsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default, default")), panelApplicationSettings);

                            panelApplicationSettingsBuilder.add(checkForNewVersion, cc.xy(3, 1));
                            panelApplicationSettingsBuilder.add(checkAllowOnlyOneInstance, cc.xy(3, 2));
                        }

                        //======== panelDownloadsSettings ========
                        {
                            panelDownloadsSettings.setBorder(new TitledBorder(null, bundle.getString("panelDownloadsSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- checkContinueInterrupted ----
                            checkContinueInterrupted.setName("checkContinueInterrupted");

                            //---- checkCloseWhenAllComplete ----
                            checkCloseWhenAllComplete.setName("checkCloseWhenAllComplete");

                            //---- labelIfFilenameExists ----
                            labelIfFilenameExists.setName("labelIfFilenameExists");

                            PanelBuilder panelDownloadsSettingsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelDownloadsSettings);

                            panelDownloadsSettingsBuilder.add(checkContinueInterrupted, cc.xywh(3, 1, 5, 1));
                            panelDownloadsSettingsBuilder.add(checkCloseWhenAllComplete, cc.xywh(3, 2, 5, 1));
                            panelDownloadsSettingsBuilder.add(labelIfFilenameExists, cc.xywh(3, 4, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                            panelDownloadsSettingsBuilder.add(comboFileExists, cc.xy(5, 4));
                        }

                        PanelBuilder panelGeneralBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                }), panelGeneral);

                        panelGeneralBuilder.add(panelApplicationSettings, cc.xy(1, 1));
                        panelGeneralBuilder.add(panelDownloadsSettings, cc.xy(1, 3));
                    }
                    panelCard.add(panelGeneral, "CARD1");

                    //======== panelSoundSettings ========
                    {
                        panelSoundSettings.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== panelSound ========
                        {
                            panelSound.setBorder(new TitledBorder(null, bundle.getString("panelSound.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- checkPlaySoundInCaseOfError ----
                            checkPlaySoundInCaseOfError.setName("checkPlaySoundInCaseOfError");

                            //---- checkPlaySoundWhenComplete ----
                            checkPlaySoundWhenComplete.setName("checkPlaySoundWhenComplete");

                            PanelBuilder panelSoundBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default, default")), panelSound);

                            panelSoundBuilder.add(checkPlaySoundInCaseOfError, cc.xy(3, 1));
                            panelSoundBuilder.add(checkPlaySoundWhenComplete, cc.xy(3, 2));
                        }

                        PanelBuilder panelSoundSettingsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelSoundSettings);

                        panelSoundSettingsBuilder.add(panelSound, cc.xy(1, 1));
                    }
                    panelCard.add(panelSoundSettings, "CARD3");

                    //======== panelViews ========
                    {
                        panelViews.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== panelAppearance ========
                        {
                            panelAppearance.setBorder(new CompoundBorder(
                                    new TitledBorder(null, bundle.getString("panelAppearance.border"), TitledBorder.LEADING, TitledBorder.TOP),
                                    Borders.DLU2_BORDER));

                            //---- labelLaF ----
                            labelLaF.setName("labelLaF");
                            labelLaF.setLabelFor(comboLaF);

                            //---- labelRequiresRestart2 ----
                            labelRequiresRestart2.setName("labelRequiresRestart2");

                            //---- checkDecoratedFrames ----
                            checkDecoratedFrames.setName("checkDecoratedFrames");

                            //---- checkShowIconInSystemTray ----
                            checkShowIconInSystemTray.setName("checkShowIconInSystemTray");

                            //---- checkHideWhenMinimized ----
                            checkHideWhenMinimized.setName("checkHideWhenMinimized");

                            PanelBuilder panelAppearanceBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.PREF_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.MIN_ROWSPEC,
                                            FormFactory.RELATED_GAP_ROWSPEC
                                    }), panelAppearance);

                            panelAppearanceBuilder.add(labelLaF, cc.xy(3, 1));
                            panelAppearanceBuilder.add(comboLaF, cc.xy(5, 1));
                            panelAppearanceBuilder.add(labelRequiresRestart2, cc.xy(7, 1));
                            panelAppearanceBuilder.add(checkDecoratedFrames, cc.xywh(3, 2, 5, 1));
                            panelAppearanceBuilder.add(checkShowIconInSystemTray, cc.xywh(3, 3, 5, 1));
                            panelAppearanceBuilder.add(checkHideWhenMinimized, cc.xywh(3, 4, 5, 1));
                        }

                        PanelBuilder panelViewsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                }), panelViews);

                        panelViewsBuilder.add(panelAppearance, cc.xy(1, 1));
                    }
                    panelCard.add(panelViews, "CARD4");

                    //======== panelConnectionSettings ========
                    {
                        panelConnectionSettings.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== panelConnections1 ========
                        {
                            panelConnections1.setBorder(new TitledBorder(null, bundle.getString("panelConnections1.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- labelMaxConcurrentDownloads ----
                            labelMaxConcurrentDownloads.setName("labelMaxConcurrentDownloads");

                            //---- spinnerMaxConcurrentDownloads ----
                            spinnerMaxConcurrentDownloads.setModel(new SpinnerNumberModel(0, 0, 5, 1));

                            PanelBuilder panelConnections1Builder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec("max(default;30dlu)")
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC
                                    }), panelConnections1);

                            panelConnections1Builder.add(labelMaxConcurrentDownloads, cc.xy(3, 1));
                            panelConnections1Builder.add(spinnerMaxConcurrentDownloads, cc.xy(5, 1));
                        }

                        //======== panelProxySettings ========
                        {
                            panelProxySettings.setBorder(new TitledBorder(null, bundle.getString("panelProxySettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- checkUseProxyList ----
                            checkUseProxyList.setName("checkUseProxyList");

                            //---- btnProxyListPathSelect ----
                            btnProxyListPathSelect.setName("btnProxyListPathSelect");

                            //---- labelTextFileFormat ----
                            labelTextFileFormat.setName("labelTextFileFormat");

                            PanelBuilder panelProxySettingsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(200), FormSpec.DEFAULT_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC
                                    }), panelProxySettings);

                            panelProxySettingsBuilder.add(checkUseProxyList, cc.xy(3, 1));
                            panelProxySettingsBuilder.add(fieldProxyListPath, cc.xy(5, 1));
                            panelProxySettingsBuilder.add(btnProxyListPathSelect, cc.xy(7, 1));
                            panelProxySettingsBuilder.add(labelTextFileFormat, cc.xy(5, 2));
                        }

                        //======== panelErrorHandling ========
                        {
                            panelErrorHandling.setBorder(new TitledBorder(null, bundle.getString("panelErrorHandling.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- labelErrorAttemptsCount ----
                            labelErrorAttemptsCount.setName("labelErrorAttemptsCount");

                            //---- spinnerErrorAttemptsCount ----
                            spinnerErrorAttemptsCount.setModel(new SpinnerNumberModel(0, 0, 10, 1));

                            //---- labelNoAutoreconnect ----
                            labelNoAutoreconnect.setName("labelNoAutoreconnect");

                            //---- labelAutoReconnectTime ----
                            labelAutoReconnectTime.setName("labelAutoReconnectTime");

                            //---- spinnerAutoReconnectTime ----
                            spinnerAutoReconnectTime.setModel(new SpinnerNumberModel(0, 0, 1000, 5));

                            //---- labelSeconds ----
                            labelSeconds.setName("labelSeconds");

                            PanelBuilder panelErrorHandlingBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec("max(pref;30dlu)"),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC
                                    }), panelErrorHandling);

                            panelErrorHandlingBuilder.add(labelErrorAttemptsCount, cc.xy(3, 1));
                            panelErrorHandlingBuilder.add(spinnerErrorAttemptsCount, cc.xy(5, 1));
                            panelErrorHandlingBuilder.add(labelNoAutoreconnect, cc.xy(7, 1));
                            panelErrorHandlingBuilder.add(labelAutoReconnectTime, cc.xy(3, 3));
                            panelErrorHandlingBuilder.add(spinnerAutoReconnectTime, cc.xy(5, 3));
                            panelErrorHandlingBuilder.add(labelSeconds, cc.xy(7, 3));
                        }

                        //---- labelRequiresRestart ----
                        labelRequiresRestart.setName("labelRequiresRestart");

                        PanelBuilder panelConnectionSettingsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelConnectionSettings);

                        panelConnectionSettingsBuilder.add(panelConnections1, cc.xy(1, 1));
                        panelConnectionSettingsBuilder.add(panelProxySettings, cc.xy(1, 3));
                        panelConnectionSettingsBuilder.add(panelErrorHandling, cc.xy(1, 5));
                        panelConnectionSettingsBuilder.add(labelRequiresRestart, cc.xy(1, 7));
                    }
                    panelCard.add(panelConnectionSettings, "CARD2");
                }
                contentPanel.add(panelCard, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
            dialogPane.add(toolbar, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    private JButton btnOK;
    private JButton btnCancel;
    private JPanel panelCard;

    private JCheckBox checkForNewVersion;
    private JCheckBox checkAllowOnlyOneInstance;
    private JCheckBox checkContinueInterrupted;
    private JCheckBox checkCloseWhenAllComplete;
    private JComboBox comboFileExists;
    private JCheckBox checkPlaySoundInCaseOfError;
    private JCheckBox checkPlaySoundWhenComplete;
    private JComboBox comboLaF;
    private JCheckBox checkDecoratedFrames;
    private JCheckBox checkShowIconInSystemTray;
    private JCheckBox checkHideWhenMinimized;
    private JSpinner spinnerMaxConcurrentDownloads;
    private JCheckBox checkUseProxyList;
    private JTextField fieldProxyListPath;
    private JButton btnProxyListPathSelect;
    private JSpinner spinnerErrorAttemptsCount;
    private JSpinner spinnerAutoReconnectTime;
    private JButtonBar toolbar;


    private class ApplyPreferenceChangeListener implements PreferenceChangeListener {

        public void preferenceChange(PreferenceChangeEvent evt) {
            //pozor, interne se vola ve zvlastnim vlakne, nikoli na EDT threadu
            final MainApp app = MainApp.getInstance(MainApp.class);
            final String key = evt.getKey();
            if (FWProp.SHOW_TRAY.equals(key)) {
                app.getTrayIconSupport().setVisibleByDefault();
            }
//            else if (LAF_PROPERTY.equals(key)) {
//                boolean succesful;
////                final ResourceMap map = getResourceMap();
//                laf = (LaF) comboLaF.getSelectedItem();
//                try {
////                    succesful = LookAndFeels.getInstance().loadLookAndFeel(laf, true);
//                    succesful = true;
//                    synchronized (UserPreferencesDialog.this) {
//                        AppPrefs.getPreferences().removePreferenceChangeListener(this);
//
//                        AppPrefs.removeProperty(LAF_PROPERTY);
//                        AppPrefs.getPreferences().addPreferenceChangeListener(this);
//                    }
//                } catch (Exception ex) {
//                    LogUtils.processException(logger, ex);
//                    Swinger.showErrorDialog("changeLookAndFeelActionFailed", ex);
////                    succesful = false;
//                }
//                if (!succesful) {
//
//                } //else {
//                    Swinger.showInformationDialog(map.getString("message_changeLookAndFeelActionSet"));
//                }
        }
//        }
    }


}
