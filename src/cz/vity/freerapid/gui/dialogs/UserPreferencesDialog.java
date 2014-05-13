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
import cz.vity.freerapid.core.*;
import cz.vity.freerapid.gui.MyPreferencesAdapter;
import cz.vity.freerapid.gui.MyPresentationModel;
import cz.vity.freerapid.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.MenuManager;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.swing.*;
import cz.vity.freerapid.swing.components.PopdownButton;
import cz.vity.freerapid.swing.models.RecentsFilesComboModel;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import cz.vity.freerapid.utilities.os.OSCommand;
import cz.vity.freerapid.utilities.os.SystemCommander;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.net.URL;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class UserPreferencesDialog extends AppDialog implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(UserPreferencesDialog.class.getName());
    private MyPresentationModel model;
    private static final String CARD_PROPERTY = "card";
    private static final String LAF_PROPERTY = "lafFakeProperty";
    private static final String LNG_PROPERTY = "lngFakeProperty";
    @SuppressWarnings({"FieldAccessedSynchronizedAndUnsynchronized"})
    private ResourceMap bundle;
    private final ApplicationContext context;
    private ClientManager clientManager;
    private boolean updateDefaultConnection;
    private ManagerDirector managerDirector;
    private Trigger trigger;
    private boolean pluginTableWasChanged;
    private static final String PLUGIN_OPTIONS_ENABLED_PROPERTY = "pluginOptionsEnabled";
    private boolean pluginOptionsEnabled;


    private static enum Card {
        CARD1, CARD2, CARD3, CARD4, CARD5, CARD6
    }

    public UserPreferencesDialog(Frame owner, ApplicationContext context) throws Exception {
        super(owner, true);
        this.context = context;
        updateDefaultConnection = false;
        pluginTableWasChanged = false;
        managerDirector = ((MainApp) context.getApplication()).getManagerDirector();
        clientManager = managerDirector.getClientManager();
        setPluginOptionsEnabled(false);
        //managerDirector.getPluginsManager().getAvailablePlugins()
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
        setAction(btnSelectConnectionProxy, "btnSelectConnectionProxy");
        setAction(btnCreateDesktopShortcut, "createDesktopShortcut");
        setAction(btnCreateQuickLaunchShortcut, "createQuickLaunchShortcut");
        setAction(btnCreateStartMenuShortcut, "createStartMenuShortcut");
        setAction(btnCreateStartupShortcut, "createStartupShortcut");

        setAction(btnPluginOptions, "btnPluginOptionsAction");
        setAction(btnResetDefaultPluginServer, "btnResetDefaultPluginServerAction");
        setAction(btnUpdatePlugins, "btnUpdatePluginsAction");


        initPluginTable();

        setDefaultValues();
        Card card;
        try {
            card = Card.valueOf(AppPrefs.getProperty(FWProp.USER_SETTINGS_SELECTED_CARD, Card.CARD1.toString()));
        } catch (IllegalArgumentException e) {
            card = Card.CARD1;
        }
        showCard(card);
        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);


    }

    @org.jdesktop.application.Action
    public void btnSelectProxyListAction() {
        final File[] files = OpenSaveDialogFactory.getInstance(context).getChooseProxyList();
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
        addButton(map.get("pluginsBtnAction"), Card.CARD6, group);
        addButton(map.get("miscBtnAction"), Card.CARD5, group);

        setAction(btnProxyListPathSelect, "btnSelectProxyListAction");

        buildPopmenuButton(popmenuButton.getPopupMenu());

    }

    private void initPluginTable() {
        pluginTable.setName("pluginTable");
        pluginTable.setAutoCreateColumnsFromModel(false);
        pluginTable.setColumnControlVisible(true);

        pluginTable.setHorizontalScrollEnabled(true);

        pluginTable.setSortable(true);
        pluginTable.setColumnMargin(10);
        pluginTable.setRolloverEnabled(true);

        pluginTable.setShowGrid(true, true);
        pluginTable.setEditable(true);

        pluginTable.setColumnSelectionAllowed(false);

        pluginTable.createDefaultColumnsFromModel();
        final TableModel tableModel = pluginTable.getModel();
        final PluginMetaDataTableModel customTableModel = (PluginMetaDataTableModel) tableModel;
        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                UserPreferencesDialog.this.model.setBuffering(true);
                if (e.getType() == TableModelEvent.UPDATE) {
                    pluginTableWasChanged = true;
                    if (e.getColumn() == PluginMetaDataTableModel.COLUMN_ACTIVE) {
                        final PluginMetaData data = customTableModel.getObject(e.getFirstRow());
                        updatePremium(data);
                    }
                }
            }
        });

        final ListSelectionModel selectionModel = pluginTable.getSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                final int index = e.getLastIndex();
                if (index != -1) {
                    final PluginMetaData data = customTableModel.getObject(pluginTable.convertRowIndexToModel(selectionModel.getMinSelectionIndex()));
                    setPluginOptionsEnabled(data.isOptionable());
                } else setPluginOptionsEnabled(false);
            }
        });

        pluginTable.setSortOrder(PluginMetaDataTableModel.COLUMN_ID, org.jdesktop.swingx.decorator.SortOrder.ASCENDING);

        TableColumn tableColumn = Swinger.updateColumn(pluginTable, "X", PluginMetaDataTableModel.COLUMN_ACTIVE, 22, 22, null);
        tableColumn.setWidth(22);
        tableColumn.setMaxWidth(22);
        //((DefaultCellEditor)tableColumn.getCellEditor()).setToolTipText("asasdasd");
        tableColumn = Swinger.updateColumn(pluginTable, "U", PluginMetaDataTableModel.COLUMN_UPDATE, 22, 22, null);
        tableColumn.setWidth(22);
        tableColumn.setMaxWidth(22);
        pluginTable.setRolloverEnabled(true);
        //final GrayHighlighter grayHighlighter = new GrayHighlighter();
        Swinger.updateColumn(pluginTable, "ID", PluginMetaDataTableModel.COLUMN_ID, -1, 70, new GrayHighlighter());
        Swinger.updateColumn(pluginTable, "Version", PluginMetaDataTableModel.COLUMN_VERSION, -1, 40, new GrayHighlighter());
        Swinger.updateColumn(pluginTable, "Services", PluginMetaDataTableModel.COLUMN_SERVICES, -1, 100, new GrayHighlighter());
        Swinger.updateColumn(pluginTable, "Author", PluginMetaDataTableModel.COLUMN_AUTHOR, -1, -1, new GrayHighlighter());
        Swinger.updateColumn(pluginTable, "WWW", PluginMetaDataTableModel.COLUMN_WWW, -1, -1, SwingXUtils.getHyperLinkTableCellRenderer());

        pluginTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!pluginTable.hasFocus())
                    Swinger.inputFocus(pluginTable);
                if (SwingUtilities.isRightMouseButton(e))
                    SwingUtils.showPopMenu(popmenuButton.getPopupMenu(), e, pluginTable, UserPreferencesDialog.this);
            }
        });

        final InputMap tableInputMap = pluginTable.getInputMap();
        final ActionMap tableActionMap = pluginTable.getActionMap();
        final ActionMap actionMap = getActionMap();

        tableInputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_C), "copy");
        tableActionMap.put("copy", actionMap.get("copyContent"));

        final KeyStroke ctrlF = SwingUtils.getCtrlKeyStroke(KeyEvent.VK_F);
        tableInputMap.remove(ctrlF);

        pluginTable.getParent().setPreferredSize(new Dimension(230, 150));

        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_HOME), "selectFirstRowExtendSelection");
        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_END), "selectLastRowExtendSelection");

//        registerKeyboardAction(focusFilterAction, ctrlF);

    }

    private void buildPopmenuButton(final JPopupMenu popupMenu) {
        final MenuManager menuManager = managerDirector.getMenuManager();
        final ActionMap actionMap = getActionMap();
        final JMenu updatesMenu = menuManager.createMenu("updatesMenu", actionMap, "selectAllUpdatesAction", "deSelectAllUpdatesAction");
        getResourceMap().injectComponent(updatesMenu);
        final JMenu activityMenu = menuManager.createMenu("activityMenu", actionMap, "selectAllActivityAction", "deSelectAllActivityAction");
        getResourceMap().injectComponent(activityMenu);
        final Object[] objects = {"copyPluginListAction", "copyPluginListWithVersionAction", "copySupportedSitesListAction", MenuManager.MENU_SEPARATOR, activityMenu, updatesMenu};
        menuManager.processMenu(popupMenu, "", actionMap, objects);
    }


    @org.jdesktop.application.Action()
    public void copyContent() {
        final int[] rows = Swinger.getSelectedRows(pluginTable);
        if (rows.length <= 0)
            return;

        final PluginMetaDataTableModel tableModel = (PluginMetaDataTableModel) pluginTable.getModel();

        final int selCol = pluginTable.convertColumnIndexToModel(pluginTable.getColumnModel().getSelectionModel().getLeadSelectionIndex());
        if (selCol == PluginMetaDataTableModel.COLUMN_ACTIVE || selCol == PluginMetaDataTableModel.COLUMN_UPDATE)
            return;
        Object value;
        if (selCol == PluginMetaDataTableModel.COLUMN_WWW)
            value = tableModel.getObject(rows[0]).getWWW();
        else value = tableModel.getValueAt(rows[0], selCol);

        if (value != null)
            SwingUtils.copyToClipboard(value.toString(), this);
    }


    private void updatePremium(PluginMetaData data) {
        if (!data.isEnabled())
            return;
        final java.util.List<PluginMetaData> dataList = managerDirector.getPluginsManager().getSupportedPlugins();
        final String s = data.getServices();
        for (PluginMetaData metaData : dataList) {
            if (metaData.getServices().equals(s) && !data.equals(metaData))
                metaData.setEnabled(false);
        }
    }

    @org.jdesktop.application.Action
    public void btnSelectConnectionProxy() {
        final ConnectDialog connectDialog = new ConnectDialog(this);
        this.getApp().prepareDialog(connectDialog, true);
        if (connectDialog.getModalResult() == ConnectDialog.RESULT_OK) {
            model.setBuffering(true);
            updateDefaultConnection = true;
        }
    }

    @org.jdesktop.application.Action(enabledProperty = PLUGIN_OPTIONS_ENABLED_PROPERTY)
    public void btnPluginOptionsAction() {
        final int selectedRow = pluginTable.getSelectedRow();
        final int i = pluginTable.convertRowIndexToModel(selectedRow);
        final PluginMetaData data = ((PluginMetaDataTableModel) pluginTable.getModel()).getObject(i);
        final ShareDownloadService service = managerDirector.getPluginsManager().getPluginInstance(data.getId());
        try {
            service.showOptions();
        } catch (Exception e) {
            //ignore
        }
    }

    @org.jdesktop.application.Action
    public void btnResetDefaultPluginServerAction() {
        comboPluginServers.getModel().setSelectedItem(Consts.PLUGIN_CHECK_UPDATE_URL);
    }

    @org.jdesktop.application.Action
    public void btnUpdatePluginsAction() {

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
            case CARD5:
                actionName = "miscBtnAction";
                break;
            case CARD6:
                actionName = "pluginsBtnAction";
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

        trigger = new Trigger();

        model = new MyPresentationModel(null, trigger);


        final ArrayListModel<PluginMetaData> plugins = new ArrayListModel<PluginMetaData>(managerDirector.getPluginsManager().getSupportedPlugins());


        pluginTable.setModel(new PluginMetaDataTableModel(plugins, getList("pluginTableColumns")));

        bindBasicComponents();

        final RecentsFilesComboModel listComboModel = new RecentsFilesComboModel(10, UserProp.PLUGIN_CHECK_URL_LIST, false, false);
        comboPluginServers.setModel(listComboModel);

        comboPluginServers.setSelectedItem(AppPrefs.getProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, Consts.PLUGIN_CHECK_UPDATE_URL));

        final JTextField field = (JTextField) comboPluginServers.getEditor().getEditorComponent();
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            public void removeUpdate(DocumentEvent e) {
                changedUpdate(e);
            }

            public void changedUpdate(DocumentEvent e) {
                model.setBuffering(true);
            }
        });

//        field.getDocument(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                model.setBuffering(true);
//            }
//        }).;

        field.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {

            }

            public void focusLost(FocusEvent e) {
                final String s = field.getText();
                if (s != null && !s.trim().isEmpty()) {
                    listComboModel.addElement(s);
                }
            }
        });


        final ActionMap map = getActionMap();
        final javax.swing.Action actionOK = map.get("okBtnAction");
        PropertyConnector connector = PropertyConnector.connect(model, PresentationModel.PROPERTYNAME_BUFFERING, actionOK, "enabled");
        connector.updateProperty2();
    }

    private void bindBasicComponents() {

        bind(checkAllowOnlyOneInstance, FWProp.ONEINSTANCE, FWProp.ONE_INSTANCE_DEFAULT);
        bind(checkForNewVersion, FWProp.NEW_VERSION, true);
        bind(checkContinueInterrupted, UserProp.DOWNLOAD_ON_APPLICATION_START, true);
        bind(checkUseHistory, UserProp.USE_HISTORY, UserProp.USE_HISTORY_DEFAULT);
        bind(checkAutoShutDownDisabledWhenExecuted, UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED, UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED_DEFAULT);

        bind(spinnerMaxConcurrentDownloads, UserProp.MAX_DOWNLOADS_AT_A_TIME, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT, 1, 9, 1);
        bind(spinnerErrorAttemptsCount, UserProp.ERROR_ATTEMPTS_COUNT, UserProp.MAX_DOWNLOADS_AT_A_TIME_DEFAULT, -1, 999, 1);
        bind(spinnerAutoReconnectTime, UserProp.AUTO_RECONNECT_TIME, UserProp.AUTO_RECONNECT_TIME_DEFAULT, 1, 10000, 10);

        bind(checkProcessFromTop, UserProp.START_FROM_FROM_TOP, UserProp.START_FROM_FROM_TOP_DEFAULT);

        ValueModel valueModel = bind(checkUseProxyList, UserProp.USE_PROXY_LIST, false);
        PropertyConnector.connectAndUpdate(valueModel, fieldProxyListPath, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, btnProxyListPathSelect, "enabled");

        bind(fieldProxyListPath, UserProp.PROXY_LIST_PATH, "");

        bind(checkPlaySoundWhenComplete, UserProp.PLAY_SOUNDS_OK, true);
        bind(checkPlaySoundInCaseOfError, UserProp.PLAY_SOUNDS_FAILED, true);

        bind(checkDecoratedFrames, FWProp.DECORATED_FRAMES, false);
        bind(checkHideWhenMinimized, FWProp.MINIMIZE_TO_TRAY, false);

        bind(checkAnimateIcon, UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT);
        bind(checkShowTitle, UserProp.SHOWINFO_IN_TITLE, UserProp.SHOWINFO_IN_TITLE_DEFAULT);

        bind(checkConfirmExiting, UserProp.CONFIRM_EXITING, UserProp.CONFIRM_EXITING_DEFAULT);
        bind(checkConfirmFileDeletion, UserProp.CONFIRM_FILE_DELETE, UserProp.CONFIRM_FILE_DELETE_DEFAULT);
        final ValueModel confirmRemove = bind(checkConfirmFileRemove, UserProp.CONFIRM_REMOVE, UserProp.CONFIRM_REMOVE_DEFAULT);
        bind(checkConfirmDownloadingRemoveOnly, UserProp.CONFIRM_DOWNLOADING_REMOVE, UserProp.CONFIRM_DOWNLOADING_REMOVE_DEFAULT);

        PropertyConnector.connectAndUpdate(confirmRemove, checkConfirmDownloadingRemoveOnly, "enabled");

        bind(checkShowHorizontalLinesInTable, UserProp.SHOW_GRID_HORIZONTAL, UserProp.SHOW_GRID_HORIZONTAL_DEFAULT);
        bind(checkShowVerticalLinesInTable, UserProp.SHOW_GRID_VERTICAL, UserProp.SHOW_GRID_VERTICAL_DEFAULT);

        bind(checkGenerateTXTDescription, UserProp.GENERATE_DESCRIPTION_BY_FILENAME, UserProp.GENERATE_DESCRIPTION_BY_FILENAME_DEFAULT);
        bind(checkGenerateDescIon, UserProp.GENERATE_DESCRIPT_ION_FILE, UserProp.GENERATE_DESCRIPT_ION_FILE_DEFAULT);
        bind(checkGenerateHidden, UserProp.GENERATE_DESCRIPTION_FILES_HIDDEN, UserProp.GENERATE_DESCRIPTION_FILES_HIDDEN_DEFAULT);


        bind(check4PluginUpdatesAutomatically, UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY, UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY_DEFAULT);
        bind(checkDownloadNotExistingPlugins, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS_DEFAULT);
        bind(checkConfirmUpdating, UserProp.CONFIRM_UPDATING_PLUGINS, UserProp.CONFIRM_UPDATING_PLUGINS_DEFAULT);

        bind(checkPrepareFile, UserProp.ANTI_FRAGMENT_FILES, UserProp.ANTI_FRAGMENT_FILES_DEFAULT);

        bind(checkCloseToTray, FWProp.MINIMIZE_ON_CLOSE, FWProp.MINIMIZE_ON_CLOSE_DEFAULT);

        final ValueModel useDefault = bind(checkUseDefaultConnection, UserProp.USE_DEFAULT_CONNECTION, UserProp.USE_DEFAULT_CONNECTION_DEFAULT);
        PropertyConnector.connectAndUpdate(useDefault, btnSelectConnectionProxy, "enabled");


        valueModel = bind(checkShowIconInSystemTray, FWProp.SHOW_TRAY, true);

        PropertyConnector.connectAndUpdate(valueModel, checkAnimateIcon, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, checkCloseToTray, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, checkHideWhenMinimized, "enabled");

        bind(comboFileExists, UserProp.FILE_ALREADY_EXISTS, UserProp.FILE_ALREADY_EXISTS_DEFAULT, "fileAlreadyExistsOptions");
        bind(comboRemoveCompleted, UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT, "removeCompletedOptions");

        bindLaFCombobox();

        bindLngCombobox();
    }

    private void bindLaFCombobox() {
        final LookAndFeels lafs = LookAndFeels.getInstance();
        final ListModel listModel = new ArrayListModel<LaF>(lafs.getAvailableLookAndFeels());
        final LookAndFeelAdapter adapter = new LookAndFeelAdapter(LAF_PROPERTY, lafs.getSelectedLaF());
        final SelectionInList<String> inList = new SelectionInList<String>(listModel, model.getBufferedModel(adapter));
        Bindings.bind(comboLaF, inList);
    }

    private void bindLngCombobox() {
        final ListModel listModel = new ArrayListModel<SupportedLanguage>(Lng.getSupportedLanguages());
        final LanguageAdapter adapter = new LanguageAdapter(LNG_PROPERTY, Lng.getSelectedLanguage());
        final SelectionInList<String> inList = new SelectionInList<String>(listModel, model.getBufferedModel(adapter));
        Bindings.bind(comboLng, inList);

        comboLng.setRenderer(new LanguageComboCellRenderer(context));
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
        final boolean updateProxyConnectionList = isBuffering(UserProp.PROXY_LIST_PATH) || isBuffering(UserProp.USE_PROXY_LIST);
        updateDefaultConnection = updateDefaultConnection || isBuffering(UserProp.USE_DEFAULT_CONNECTION);

        AppPrefs.storeProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, comboPluginServers.getSelectedItem().toString());

        trigger.triggerCommit();
//        final String s = AppPrefs.getProperty(UserProp.PROXY_LIST_PATH, "");
//        System.out.println("s = " + s);

        final LaF selLaf = LookAndFeels.getInstance().getSelectedLaF();
        LaF laf = (LaF) comboLaF.getSelectedItem();
        if (laf != null) {
            if (!selLaf.equals(laf)) {
                updateLookAndFeel();
            }
        }

        final SupportedLanguage lng = Lng.getSelectedLanguage();
        if (!lng.equals(comboLng.getSelectedItem())) {
            updateLng();

        }

        ((RecentsFilesComboModel) comboPluginServers.getModel()).store();

        if (updateDefaultConnection && updateProxyConnectionList) {
            clientManager.updateConnectionSettings();
        } else {
            if (updateDefaultConnection)
                clientManager.updateDefaultConnection();
            if (updateProxyConnectionList)
                clientManager.updateProxyConnectionList();
        }

        if (pluginTableWasChanged) {
            managerDirector.getPluginsManager().updatePluginSettings();
        }

        doClose();
    }

    private boolean isBuffering(final String property) {
        return model.getBufferedModel(property).isBuffering();
    }

    private void updateLng() {
        AppPrefs.storeProperty(FWProp.SELECTED_LANGUAGE, ((SupportedLanguage) comboLng.getSelectedItem()).getLanguageCode());
        AppPrefs.storeProperty(FWProp.SELECTED_COUNTRY, ((SupportedLanguage) comboLng.getSelectedItem()).getCountry());
        Swinger.showInformationDialog(getResourceMap().getString("changeLanguageAfterRestart"));
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

    @org.jdesktop.application.Action
    public void miscBtnAction(ActionEvent e) {
        showCard(e);
    }

    @org.jdesktop.application.Action
    public void pluginsBtnAction(ActionEvent e) {
        showCard(e);
    }


    @org.jdesktop.application.Action
    public void copyPluginListAction() {
        copyPluginList(false);
    }

    private void copyPluginList(final boolean withVersion) {
        java.util.List<PluginMetaData> datas = getSortedPluginList();
        StringBuilder builder = new StringBuilder();
        final String lineSeparator = Utils.getSystemLineSeparator();
        for (PluginMetaData data : datas) {
            builder.append(data.getId());
            if (withVersion)
                builder.append(' ').append(data.getVersion());
            builder.append(lineSeparator);
        }
        SwingUtils.copyToClipboard(builder.toString().trim(), this);
    }

    private java.util.List<PluginMetaData> getSortedPluginList() {
        java.util.List<PluginMetaData> datas = getSupportedPlugins();
        Collections.sort(datas);
        return datas;
    }

    private java.util.List<PluginMetaData> getSupportedPlugins() {
        return managerDirector.getPluginsManager().getSupportedPlugins();
    }

    @org.jdesktop.application.Action
    public void copyPluginListWithVersionAction() {
        copyPluginList(true);
    }

    @org.jdesktop.application.Action
    public void copySupportedSitesListAction() {
        final java.util.List<PluginMetaData> dataList = getSortedPluginList();
        StringBuilder builder = new StringBuilder();
        final String lineSeparator = Utils.getSystemLineSeparator();
        for (PluginMetaData data : dataList) {
            builder.append(data.getServices());
            builder.append(lineSeparator);
        }
        SwingUtils.copyToClipboard(builder.toString().trim(), this);
    }

    private void checkOrUncheckPlugin(Object value, int columnIndex) {
        final PluginMetaDataTableModel tableModel = (PluginMetaDataTableModel) pluginTable.getModel();
        final int count = tableModel.getRowCount();
        for (int i = 0; i < count; i++) {
            tableModel.setValueAt(value, i, columnIndex);
        }
    }

    @org.jdesktop.application.Action
    public void selectAllUpdatesAction() {
        checkOrUncheckPlugin(Boolean.TRUE, PluginMetaDataTableModel.COLUMN_UPDATE);
    }

    @org.jdesktop.application.Action
    public void deSelectAllUpdatesAction() {
        checkOrUncheckPlugin(Boolean.FALSE, PluginMetaDataTableModel.COLUMN_UPDATE);
    }

    @org.jdesktop.application.Action
    public void selectAllActivityAction() {
        checkOrUncheckPlugin(Boolean.TRUE, PluginMetaDataTableModel.COLUMN_ACTIVE);
    }

    @org.jdesktop.application.Action
    public void deSelectAllActivityAction() {
        checkOrUncheckPlugin(Boolean.FALSE, PluginMetaDataTableModel.COLUMN_ACTIVE);
    }


    @Override
    public void doClose() {
        AppPrefs.removeProperty(LAF_PROPERTY);
        logger.log(Level.FINE, "Closing UserPreferenceDialog.");
        try {
            if (model != null)
                model.release();
        } finally {
            super.doClose();
        }
    }

    @org.jdesktop.application.Action
    public void createDesktopShortcut() {
        createShortcut(OSCommand.CREATE_DESKTOP_SHORTCUT);
    }

    @org.jdesktop.application.Action
    public void createStartMenuShortcut() {
        createShortcut(OSCommand.CREATE_STARTMENU_SHORTCUT);
    }

    @org.jdesktop.application.Action
    public void createStartupShortcut() {
        createShortcut(OSCommand.CREATE_STARTUP_SHORTCUT);
    }

    @org.jdesktop.application.Action
    public void createQuickLaunchShortcut() {
        createShortcut(OSCommand.CREATE_QUICKLAUNCH_SHORTCUT);
    }

    private void createShortcut(final OSCommand command) {
        final SystemCommander utils = SystemCommanderFactory.getInstance().getSystemCommanderInstance(context);
        if (utils.isSupported(command)) {
            final boolean result = utils.createShortCut(command);
            if (!result)
                Swinger.showErrorMessage(getResourceMap(), "createShortCutFailed");
        } else
            Swinger.showErrorMessage(context.getResourceMap(), "systemCommandNotSupported", command.toString().toLowerCase());
    }

    public boolean isPluginOptionsEnabled() {
        return pluginOptionsEnabled;
    }


    public void setPluginOptionsEnabled(boolean pluginOptionsEnabled) {
        final boolean oldValue = this.pluginOptionsEnabled;
        this.pluginOptionsEnabled = pluginOptionsEnabled;
        firePropertyChange(PLUGIN_OPTIONS_ENABLED_PROPERTY, oldValue, pluginOptionsEnabled);
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnSelectConnectionProxy = new JButton();
        btnSelectConnectionProxy.setName("btnSelectConnectionProxy");
        btnOK = new JButton();
        btnCancel = new JButton();
        btnCreateDesktopShortcut = new JButton();
        btnCreateStartMenuShortcut = new JButton();
        btnCreateQuickLaunchShortcut = new JButton();
        btnCreateStartupShortcut = new JButton();
        panelCard = new JPanel();
        JPanel panelGeneral = new JPanel();
        JPanel panelApplicationSettings = new JPanel();
        JPanel panelShortcutsSettings = new JPanel();
        checkForNewVersion = new JCheckBox();
        checkAllowOnlyOneInstance = new JCheckBox();
        JPanel panelDownloadsSettings = new JPanel();
        checkContinueInterrupted = new JCheckBox();

        checkConfirmExiting = new JCheckBox();
        checkConfirmFileDeletion = new JCheckBox();
        checkConfirmFileRemove = new JCheckBox();
        checkConfirmDownloadingRemoveOnly = new JCheckBox();

        checkConfirmExiting.setName("checkConfirmExiting");
        checkConfirmFileDeletion.setName("checkConfirmFileDeletion");
        checkConfirmFileRemove.setName("checkConfirmFileRemove");
        checkConfirmDownloadingRemoveOnly.setName("checkConfirmDownloadingRemoveOnly");

        checkAutoShutDownDisabledWhenExecuted = new JCheckBox();
        checkProcessFromTop = new JCheckBox();
        checkGenerateTXTDescription = new JCheckBox();
        checkGenerateDescIon = new JCheckBox();
        checkGenerateHidden = new JCheckBox();
        checkCloseToTray = new JCheckBox();
        checkUseHistory = new JCheckBox();
        checkUseDefaultConnection = new JCheckBox();
        checkGenerateTXTDescription.setName("checkGenerateTXTDescription");
        checkGenerateDescIon.setName("checkGenerateDescIon");
        checkGenerateHidden.setName("checkGenerateHidden");
        checkCloseToTray.setName("checkCloseToTray");
        checkUseDefaultConnection.setName("checkUseDefaultConnection");
        checkShowHorizontalLinesInTable = new JCheckBox();
        checkShowVerticalLinesInTable = new JCheckBox();
        checkShowHorizontalLinesInTable.setName("checkShowHorizontalLinesInTable");
        checkShowVerticalLinesInTable.setName("checkShowVerticalLinesInTable");
        checkUseHistory.setName("checkUseHistory");
        JLabel labelIfFilenameExists = new JLabel();
        JLabel labelLanguage = new JLabel();

        labelLanguage.setName("language");
        labelLanguage.setLabelFor(comboLng);
        comboLng = new JComboBox();
        checkConfirmUpdating = new JCheckBox();
        checkConfirmUpdating.setName("checkConfirmUpdating");

        comboRemoveCompleted = new JComboBox();
        comboRemoveCompleted.setName("comboRemoveCompleted");
        comboLng.setName("comboLng");
        comboFileExists = new JComboBox();

        JLabel labelRemoveCompleted = new JLabel();
        labelRemoveCompleted.setLabelFor(comboRemoveCompleted);
        labelRemoveCompleted.setName("labelRemoveCompleted");

        JPanel panelAlertSettings = new JPanel();
        JPanel panelMiscSettings = new JPanel();
        JPanel panelDescSettings = new JPanel();
        JPanel panelAdvancedSettings = new JPanel();

        JPanel panelSound = new JPanel();
        checkPlaySoundInCaseOfError = new JCheckBox();
        checkPlaySoundWhenComplete = new JCheckBox();
        JPanel panelViews = new JPanel();
        JPanel panelAppearance = new JPanel();
        JPanel panelSystemTray = new JPanel();
        JPanel panelConfirmation = new JPanel();
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
        checkAnimateIcon = new JCheckBox();
        checkShowTitle = new JCheckBox();
        checkPrepareFile = new JCheckBox();

        JPanel panelPlugins = new JPanel();
        JTabbedPane pluginTabbedPane = new JTabbedPane();
        pluginTabbedPane.setName("pluginTabbedPane");
        JPanel pluginPanelSettings = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane();
        pluginTable = new JXTable();
        JXButtonPanel pluginsButtonPanel = new JXButtonPanel();
        JLabel labelPluginInfo = new JLabel();
        btnPluginOptions = new JButton();
        JPanel pluginPanelUpdates = new JPanel();
        check4PluginUpdatesAutomatically = new JCheckBox();
        checkDownloadNotExistingPlugins = new JCheckBox();
        JLabel labelUpdateFromServer = new JLabel();
        comboPluginServers = new JComboBox();
        btnResetDefaultPluginServer = new JButton();
        btnUpdatePlugins = new JButton();

        popmenuButton = ComponentFactory.getPopdownButton();

        checkPrepareFile.setName("checkPrepareFile");

        //======== this ========
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
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec("max(default;70dlu)"),
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC
                                    }), panelApplicationSettings);

                            panelApplicationSettingsBuilder.add(checkForNewVersion, cc.xyw(3, 1, 4));
                            panelApplicationSettingsBuilder.add(checkAllowOnlyOneInstance, cc.xyw(3, 3, 4));
                            panelApplicationSettingsBuilder.add(checkUseHistory, cc.xyw(3, 5, 4));

                            panelApplicationSettingsBuilder.add(labelLanguage, cc.xyw(3, 7, 1));
                            panelApplicationSettingsBuilder.add(comboLng, cc.xyw(5, 7, 1));
                        }

                        //======== panelShortcutsSettings ========
                        {
                            panelShortcutsSettings.setBorder(new TitledBorder(null, bundle.getString("panelShortcutsSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            PanelBuilder panelApplicationSettingsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec("max(pref;30dlu)"),
//                                            FormFactory.GLUE_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
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
                                    }), panelShortcutsSettings);

                            panelApplicationSettingsBuilder.add(btnCreateDesktopShortcut, cc.xy(2, 1));
                            panelApplicationSettingsBuilder.add(btnCreateStartMenuShortcut, cc.xy(2, 3));
                            panelApplicationSettingsBuilder.add(btnCreateQuickLaunchShortcut, cc.xy(2, 5));
                            panelApplicationSettingsBuilder.add(btnCreateStartupShortcut, cc.xy(2, 7));
                        }

                        //======== panelDownloadsSettings ========
                        {
                            panelDownloadsSettings.setBorder(new TitledBorder(null, bundle.getString("panelDownloadsSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

                            //---- checkContinueInterrupted ----
                            checkContinueInterrupted.setName("checkContinueInterrupted");

                            //---- checkCloseWhenAllComplete ----
                            checkAutoShutDownDisabledWhenExecuted.setName("checkAutoShutDownDisabledWhenExecuted");

                            checkProcessFromTop.setName("checkProcessFromTop");
                            //---- labelIfFilenameExists ----
                            labelIfFilenameExists.setName("labelIfFilenameExists");

                            PanelBuilder panelDownloadsSettingsBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.LINE_GAP_ROWSPEC,
                                    }), panelDownloadsSettings);

                            panelDownloadsSettingsBuilder.add(checkContinueInterrupted, cc.xywh(3, 1, 7, 1));
                            panelDownloadsSettingsBuilder.add(checkProcessFromTop, cc.xywh(3, 2, 7, 1));
                            panelDownloadsSettingsBuilder.add(checkAutoShutDownDisabledWhenExecuted, cc.xywh(3, 3, 7, 1));
                            panelDownloadsSettingsBuilder.add(labelIfFilenameExists, cc.xywh(3, 5, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                            panelDownloadsSettingsBuilder.add(comboFileExists, cc.xy(5, 5));
                            panelDownloadsSettingsBuilder.add(labelRemoveCompleted, cc.xywh(7, 5, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                            panelDownloadsSettingsBuilder.add(comboRemoveCompleted, cc.xy(9, 5));
                        }

                        PanelBuilder panelGeneralBuilder = new PanelBuilder(new FormLayout(
                                new ColumnSpec[]{
                                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormFactory.DEFAULT_COLSPEC,
                                },
                                new RowSpec[]{
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                                        FormFactory.LINE_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                }), panelGeneral);

                        panelGeneralBuilder.add(panelApplicationSettings, cc.xy(1, 1));
                        panelGeneralBuilder.add(panelShortcutsSettings, cc.xy(3, 1));
                        panelGeneralBuilder.add(panelDownloadsSettings, cc.xyw(1, 3, 3));
                    }
                    panelCard.add(panelGeneral, "CARD1");

                    //======== panelAlertSettings ========
                    {
                        panelAlertSettings.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== panelSound ========
                        {
                            panelSound.setBorder(new CompoundBorder(
                                    new TitledBorder(null, bundle.getString("panelSound.border"), TitledBorder.LEADING, TitledBorder.TOP),
                                    Borders.DLU2_BORDER));

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
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelAlertSettings);

                        //======== panelConfirmation ========
                        {
                            panelConfirmation.setBorder(new CompoundBorder(
                                    new TitledBorder(null, bundle.getString("panelConfirmation.border"), TitledBorder.LEADING, TitledBorder.TOP),
                                    Borders.DLU2_BORDER));

                            PanelBuilder panelConfirmBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            new ColumnSpec(Sizes.dluX(9)),
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,

                                    }), panelConfirmation);

                            panelConfirmBuilder.add(checkConfirmExiting, cc.xyw(3, 1, 3));
                            panelConfirmBuilder.add(checkConfirmFileDeletion, cc.xy(7, 1));
                            panelConfirmBuilder.add(checkConfirmFileRemove, cc.xyw(3, 2, 3));
                            panelConfirmBuilder.add(checkConfirmDownloadingRemoveOnly, cc.xy(5, 3));
                        }
                        panelSoundSettingsBuilder.add(panelSound, cc.xy(1, 1));
                        panelSoundSettingsBuilder.add(panelConfirmation, cc.xy(1, 3));
                    }

                    panelCard.add(panelAlertSettings, "CARD3");

                    //======== panelMiscenallnousSettings ========
                    {
                        panelMiscSettings.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== panelDesc ========
                        {
                            panelDescSettings.setBorder(new TitledBorder(null, bundle.getString("panelDesc.border"), TitledBorder.LEADING, TitledBorder.TOP));


                            PanelBuilder panelDescBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default, default, default")), panelDescSettings);

                            panelDescBuilder.add(checkGenerateDescIon, cc.xy(3, 1));
                            panelDescBuilder.add(checkGenerateTXTDescription, cc.xy(3, 2));
                            panelDescBuilder.add(checkGenerateHidden, cc.xy(3, 3));
                        }

                        //======== panelAdvanced ========
                        {
                            panelAdvancedSettings.setBorder(new TitledBorder(null, bundle.getString("panelAdvanced.border"), TitledBorder.LEADING, TitledBorder.TOP));


                            PanelBuilder panelDescBuilder = new PanelBuilder(new FormLayout(
                                    new ColumnSpec[]{
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC
                                    },
                                    RowSpec.decodeSpecs("default")), panelAdvancedSettings);

                            panelDescBuilder.add(checkPrepareFile, cc.xy(3, 1));
                        }

                        PanelBuilder panelMiscSettingsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC
                                }), panelMiscSettings);

                        panelMiscSettingsBuilder.add(panelDescSettings, cc.xy(1, 1));
                        panelMiscSettingsBuilder.add(panelAdvancedSettings, cc.xy(1, 3));
                    }
                    panelCard.add(panelMiscSettings, "CARD5");

                    //======== panelPlugins ========
                    {
                        panelPlugins.setBorder(Borders.TABBED_DIALOG_BORDER);

                        //======== pluginTabbedPane ========
                        {

                            //======== pluginPanelSettings ========
                            {
                                pluginPanelSettings.setBorder(new CompoundBorder(
                                        new EmptyBorder(4, 4, 4, 4),
                                        new EtchedBorder()));
                                pluginPanelSettings.setLayout(new BorderLayout());

                                //======== scrollPane1 ========
                                {
                                    scrollPane1.setViewportView(pluginTable);
                                }
                                pluginPanelSettings.add(scrollPane1, BorderLayout.CENTER);

                                //======== pluginsButtonPanel ========
                                {
                                    pluginsButtonPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

                                    //---- labelPluginInfo ----
                                    labelPluginInfo.setName("labelPluginInfo");

                                    //---- popmenuButton ----
                                    popmenuButton.setName("popmenuButton");

                                    //---- btnPluginOptions ----
                                    btnPluginOptions.setName("btnPluginOptions");

                                    PanelBuilder pluginsButtonPanelBuilder = new PanelBuilder(new FormLayout(
                                            new ColumnSpec[]{
                                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                    FormFactory.DEFAULT_COLSPEC,
                                                    FormFactory.UNRELATED_GAP_COLSPEC,
                                                    FormFactory.DEFAULT_COLSPEC,
                                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                    FormFactory.UNRELATED_GAP_COLSPEC
                                            },
                                            RowSpec.decodeSpecs("default")), pluginsButtonPanel);

                                    pluginsButtonPanelBuilder.add(labelPluginInfo, cc.xy(3, 1));
                                    pluginsButtonPanelBuilder.add(popmenuButton, cc.xy(5, 1));
                                    pluginsButtonPanelBuilder.add(btnPluginOptions, cc.xy(7, 1));
                                }
                                pluginPanelSettings.add(pluginsButtonPanel, BorderLayout.SOUTH);
                            }
                            pluginTabbedPane.addTab(bundle.getString("pluginPanelSettings.tab.title"), pluginPanelSettings);

                            //======== pluginPanelUpdates ========
                            {
                                pluginPanelUpdates.setBorder(new CompoundBorder(
                                        new EmptyBorder(4, 4, 4, 4),
                                        new TitledBorder(bundle.getString("pluginPanelUpdates.border"))));

                                //---- check4PluginUpdatesAutomatically ----
                                check4PluginUpdatesAutomatically.setName("check4PluginUpdatesAutomatically");

                                //---- checkConfirmUpdating ----
                                checkConfirmUpdating.setName("checkConfirmUpdating");

                                //---- checkDownloadNotExistingPlugins ----
                                checkDownloadNotExistingPlugins.setName("checkDownloadNotExistingPlugins");

                                //---- labelUpdateFromServer ----
                                labelUpdateFromServer.setName("labelUpdateFromServer");
                                labelUpdateFromServer.setLabelFor(comboPluginServers);

                                //---- comboPluginServers ----
                                comboPluginServers.setEditable(true);

                                //---- btnResetDefaultPluginServer ----
                                btnResetDefaultPluginServer.setName("btnResetDefaultPluginServer");

                                PanelBuilder pluginPanelUpdatesBuilder = new PanelBuilder(new FormLayout(
                                        new ColumnSpec[]{
                                                FormFactory.DEFAULT_COLSPEC,
                                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.dluX(50), Sizes.dluX(75)), FormSpec.DEFAULT_GROW),
                                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                FormFactory.DEFAULT_COLSPEC,
                                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                                FormFactory.UNRELATED_GAP_COLSPEC
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
                                                new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                        }), pluginPanelUpdates);

                                pluginPanelUpdatesBuilder.add(check4PluginUpdatesAutomatically, cc.xywh(1, 1, 3, 1));
                                pluginPanelUpdatesBuilder.add(checkConfirmUpdating, cc.xywh(1, 3, 3, 1));
                                pluginPanelUpdatesBuilder.add(checkDownloadNotExistingPlugins, cc.xywh(1, 5, 3, 1));
                                pluginPanelUpdatesBuilder.add(labelUpdateFromServer, cc.xywh(1, 7, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                                pluginPanelUpdatesBuilder.add(comboPluginServers, cc.xy(3, 7));
                                pluginPanelUpdatesBuilder.add(btnResetDefaultPluginServer, cc.xy(5, 7));
                            }
                            pluginTabbedPane.addTab(bundle.getString("pluginPanelUpdates.tab.title"), pluginPanelUpdates);

                        }

                        PanelBuilder panelPluginsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec("5px")
                                }), panelPlugins);

                        panelPluginsBuilder.add(pluginTabbedPane, cc.xy(1, 1));
                    }
                    panelCard.add(panelPlugins, "CARD6");
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

                            checkShowTitle.setName("checkShowTitle");
                            checkProcessFromTop.setName("checkProcessFromTop");

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
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                    }), panelAppearance);

                            panelAppearanceBuilder.add(labelLaF, cc.xy(3, 1));
                            panelAppearanceBuilder.add(comboLaF, cc.xy(5, 1));
                            panelAppearanceBuilder.add(labelRequiresRestart2, cc.xy(7, 1));//1
                            panelAppearanceBuilder.add(checkDecoratedFrames, cc.xywh(3, 2, 5, 1));
                            panelAppearanceBuilder.add(checkShowHorizontalLinesInTable, cc.xywh(3, 5, 5, 1));
                            panelAppearanceBuilder.add(checkShowVerticalLinesInTable, cc.xywh(3, 6, 5, 1));
                            panelAppearanceBuilder.add(checkShowTitle, cc.xywh(3, 7, 5, 1));
                        }

                        //======== panel System tray ========
                        {
                            panelSystemTray.setBorder(new CompoundBorder(
                                    new TitledBorder(null, bundle.getString("panelSystemTray.border"), TitledBorder.LEADING, TitledBorder.TOP),
                                    Borders.DLU2_BORDER));

                            //---- checkShowIconInSystemTray ----
                            checkShowIconInSystemTray.setName("checkShowIconInSystemTray");

                            //---- checkHideWhenMinimized ----
                            checkHideWhenMinimized.setName("checkHideWhenMinimized");

                            checkAnimateIcon.setName("checkAnimateIcon");

                            PanelBuilder panelSystemTrayBuilder = new PanelBuilder(new FormLayout(
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

                                    }), panelSystemTray);

                            panelSystemTrayBuilder.add(checkShowIconInSystemTray, cc.xy(3, 1));
                            panelSystemTrayBuilder.add(checkAnimateIcon, cc.xy(5, 1));
                            panelSystemTrayBuilder.add(checkCloseToTray, cc.xy(3, 2));
                            panelSystemTrayBuilder.add(checkHideWhenMinimized, cc.xy(5, 2));
                        }


                        PanelBuilder panelViewsBuilder = new PanelBuilder(new FormLayout(
                                ColumnSpec.decodeSpecs("default:grow"),
                                new RowSpec[]{
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        FormFactory.DEFAULT_ROWSPEC,
                                        FormFactory.RELATED_GAP_ROWSPEC,
                                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                                }), panelViews);

                        panelViewsBuilder.add(panelAppearance, cc.xy(1, 1));
                        panelViewsBuilder.add(panelSystemTray, cc.xy(1, 3));
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
                                            new ColumnSpec("max(pref;30dlu)"),
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            FormFactory.DEFAULT_COLSPEC,
                                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.DEFAULT_GROW),
                                    },
                                    new RowSpec[]{
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.DEFAULT_ROWSPEC,
                                            FormFactory.NARROW_LINE_GAP_ROWSPEC
                                    }), panelConnections1);

                            panelConnections1Builder.add(labelMaxConcurrentDownloads, cc.xy(3, 1));
                            panelConnections1Builder.add(spinnerMaxConcurrentDownloads, cc.xy(5, 1));
                            panelConnections1Builder.add(checkUseDefaultConnection, cc.xyw(3, 2, 5));
                            panelConnections1Builder.add(btnSelectConnectionProxy, cc.xy(9, 2));
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
                    labelRequiresRestart.setVisible(false);
                    panelCard.add(panelConnectionSettings, "CARD2");
                }
                contentPanel.add(panelCard, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
            dialogPane.add(toolbar, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);

    }

    private JButton btnOK;
    private JButton btnCancel;
    private JButton btnCreateDesktopShortcut;
    private JButton btnCreateStartMenuShortcut;
    private JButton btnCreateQuickLaunchShortcut;
    private JButton btnCreateStartupShortcut;

    private JPanel panelCard;

    private JCheckBox checkForNewVersion;
    private JCheckBox checkAllowOnlyOneInstance;
    private JCheckBox checkContinueInterrupted;
    private JCheckBox checkAutoShutDownDisabledWhenExecuted;
    private JComboBox comboFileExists;
    private JComboBox comboRemoveCompleted;
    private JCheckBox checkPlaySoundInCaseOfError;
    private JCheckBox checkPlaySoundWhenComplete;
    private JComboBox comboLaF;
    private JComboBox comboLng;
    private JCheckBox checkDecoratedFrames;
    private JCheckBox checkAnimateIcon;
    private JCheckBox checkShowIconInSystemTray;
    private JCheckBox checkHideWhenMinimized;
    private JCheckBox checkGenerateTXTDescription;
    private JCheckBox checkGenerateDescIon;
    private JCheckBox checkGenerateHidden;
    private JCheckBox checkUseHistory;

    private JCheckBox checkConfirmExiting;
    private JCheckBox checkConfirmFileDeletion;
    private JCheckBox checkConfirmFileRemove;
    private JCheckBox checkConfirmDownloadingRemoveOnly;

    private JCheckBox checkShowHorizontalLinesInTable;
    private JCheckBox checkShowVerticalLinesInTable;
    private JCheckBox checkPrepareFile;
    private JCheckBox checkCloseToTray;
    private JCheckBox checkUseDefaultConnection;

    private JSpinner spinnerMaxConcurrentDownloads;
    private JCheckBox checkUseProxyList;
    private JCheckBox checkShowTitle;
    private JCheckBox checkProcessFromTop;

    private JTextField fieldProxyListPath;
    private JButton btnProxyListPathSelect;
    private JButton btnSelectConnectionProxy;
    private JSpinner spinnerErrorAttemptsCount;
    private JSpinner spinnerAutoReconnectTime;
    private JButtonBar toolbar;

    private JXTable pluginTable;
    private JButton btnPluginOptions;
    private JCheckBox check4PluginUpdatesAutomatically;
    private JCheckBox checkDownloadNotExistingPlugins;
    private JCheckBox checkConfirmUpdating;
    private JComboBox comboPluginServers;

    private JButton btnResetDefaultPluginServer;
    private JButton btnUpdatePlugins;

    private PopdownButton popmenuButton;


    private void updateLookAndFeel() {
        boolean succesful;
        final ResourceMap map = getResourceMap();
        LaF laf = (LaF) comboLaF.getSelectedItem();
        final LookAndFeels lafManager = LookAndFeels.getInstance();
        try {
            succesful = lafManager.loadLookAndFeel(laf, true);
            lafManager.storeSelectedLaF(laf);
            // succesful = true;
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            Swinger.showErrorDialog(map, "changeLookAndFeelActionFailed", ex);
            succesful = false;
        }
        if (succesful) {
            Swinger.showInformationDialog(map.getString("message_changeLookAndFeelActionSet"));
        }

    }


    private final static class LanguageComboCellRenderer extends DefaultListCellRenderer {
        private String path;
        private ResourceMap map;

        private LanguageComboCellRenderer(ApplicationContext context) {
            map = context.getResourceMap();
            path = (map.getResourcesDir() + map.getString("flagsPath")).trim();
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            final SupportedLanguage lng = (SupportedLanguage) value;

            assert lng != null;

            String s = lng.getIcon();
            if (s == null)
                s = map.getString("blank.gif");
            final URL resource = map.getClassLoader().getResource(path + s);
            final Component component = super.getListCellRendererComponent(list, lng.getName(), index, isSelected, cellHasFocus);
            if (resource != null) {
                this.setIcon(new ImageIcon(resource));
            }
            return component;
        }
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }


    private static class GrayHighlighter extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            row = table.convertRowIndexToModel(row);
            if (Boolean.FALSE.equals(table.getModel().getValueAt(row, PluginMetaDataTableModel.COLUMN_ACTIVE))) {
                comp.setForeground(Color.GRAY);
            } else
                comp.setForeground(Color.BLACK);
            return comp;
        }
    }
}
