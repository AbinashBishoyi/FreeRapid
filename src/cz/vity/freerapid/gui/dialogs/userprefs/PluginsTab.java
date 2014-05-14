package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.beans.BeanAdapter;
import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.MenuManager;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.SwingXUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.FindTableAction;
import cz.vity.freerapid.swing.components.PopdownButton;
import cz.vity.freerapid.swing.models.SimplePreferencesComboModel;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.ComponentAdapter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
public class PluginsTab extends UserPreferencesTab implements ClipboardOwner {
    private static final Logger logger = Logger.getLogger(PluginsTab.class.getName());

    private static final int MINIMUM_PRIORITY = 1;
    private static final int MAXIMUM_PRIORITY = 10000;
    private static final int MIN_FIRST_PLUGIN_COLUMN_WIDTH = 26;

    private static final String PLUGIN_OPTIONS_ENABLED_PROPERTY = "pluginOptionsEnabled";
    private boolean pluginOptionsEnabled;

    private boolean pluginTableWasChanged = false;

    private final ManagerDirector managerDirector;

    PluginsTab(final UserPreferencesDialog dialog, final ManagerDirector managerDirector) {
        super(dialog);
        this.managerDirector = managerDirector;
        setPluginOptionsEnabled(false);
    }

    @Override
    public void init() {
        buildPopmenuButton(popmenuButton.getPopupMenu());

        final ArrayListModel<PluginMetaData> plugins = new ArrayListModel<PluginMetaData>(managerDirector.getPluginsManager().getSupportedPlugins());
        pluginTable.setModel(new PluginMetaDataTableModel(plugins, dialog.getList("pluginTableColumns", 10)));
        if (!plugins.isEmpty()) { //select first row in plugin table
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    pluginTable.getSelectionModel().setSelectionInterval(0, 0);
                }
            });
        }

        bind(spinnerUpdateHour, UserProp.PLUGIN_UPDATE_CHECK_INTERVAL, UserProp.PLUGIN_UPDATE_CHECK_INTERVAL_DEFAULT, 1, 1000, 1);
        bind(check4PluginUpdatesAutomatically, UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY, UserProp.CHECK4_PLUGIN_UPDATES_AUTOMATICALLY_DEFAULT);
        bind(checkDownloadNotExistingPlugins, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS_DEFAULT);
        bindCombobox(comboHowToUpdate, UserProp.PLUGIN_UPDATE_METHOD, UserProp.PLUGIN_UPDATE_METHOD_DEFAULT, "comboHowToUpdate", 4);
        comboHowToUpdate.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                dialog.getModel().setBuffering(true);
            }
        });

        final SimplePreferencesComboModel listComboModel = new SimplePreferencesComboModel(10, UserProp.PLUGIN_CHECK_URL_LIST, false);
        comboPluginServers.setModel(listComboModel);
        comboPluginServers.setSelectedItem(AppPrefs.getProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, Consts.PLUGIN_CHECK_UPDATE_URL));
        final JTextField field = (JTextField) comboPluginServers.getEditor().getEditorComponent();
        field.getDocument().addDocumentListener(new DocumentListener() {
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
        field.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
            }

            @Override
            public void focusLost(FocusEvent e) {
                final String s = field.getText();
                if (s != null && !s.trim().isEmpty()) {
                    listComboModel.addElement(s);
                }
            }
        });

        setAction(btnPluginOptions, "btnPluginOptionsAction");
        setAction(btnResetDefaultPluginServer, "btnResetDefaultPluginServerAction");

        initPluginTable();
    }

    private void buildPopmenuButton(final JPopupMenu popupMenu) {
        final MenuManager menuManager = managerDirector.getMenuManager();
        final JMenu updatesMenu = menuManager.createMenu("updatesMenu", actionMap, "selectAllUpdatesAction", "deSelectAllUpdatesAction");
        resourceMap.injectComponent(updatesMenu);
        final JMenu activityMenu = menuManager.createMenu("activityMenu", actionMap, "selectAllActivityAction", "deSelectAllActivityAction");
        resourceMap.injectComponent(activityMenu);
        final JMenu cmMenu = menuManager.createMenu("clipboardMonitoringMenu", actionMap, "selectAllCMAction", "deSelectAllCMAction");
        resourceMap.injectComponent(cmMenu);
        final Object[] objects = {"copyPluginListAction", "copyPluginListWithVersionAction", "copySupportedSitesListAction", MenuManager.MENU_SEPARATOR, activityMenu, updatesMenu, cmMenu};
        menuManager.processMenu(popupMenu, "", actionMap, objects);
    }

    private void initPluginTable() {
        pluginTable.setName("pluginTable");
        pluginTable.setAutoCreateColumnsFromModel(false);
        pluginTable.setColumnControlVisible(true);

        pluginTable.setHorizontalScrollEnabled(true);

        pluginTable.setSortable(true);
        pluginTable.setSortsOnUpdates(true);
        pluginTable.setUpdateSelectionOnSort(true);

        pluginTable.setColumnMargin(10);
        pluginTable.setRolloverEnabled(true);

        pluginTable.setShowGrid(true, true);
        pluginTable.setEditable(true);

        ColorHighlighter first = new ColorHighlighter(new HighlightPredicate() {
            @Override
            public boolean isHighlighted(Component renderer, ComponentAdapter adapter) {
                return Boolean.FALSE.equals(adapter.getValue(PluginMetaDataTableModel.COLUMN_ACTIVE));
            }
        }, HighlighterFactory.GENERIC_GRAY, Color.BLACK);

        pluginTable.addHighlighter(first);

        pluginTable.setColumnSelectionAllowed(false);

        pluginTable.createDefaultColumnsFromModel();
        final TableModel tableModel = pluginTable.getModel();
        final PluginMetaDataTableModel customTableModel = (PluginMetaDataTableModel) tableModel;
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                dialog.getModel().setBuffering(true);
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
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (e.getValueIsAdjusting())
                    return;
                final int index = e.getLastIndex();
                if (index != -1) {
                    pluginDetailPanel.setVisible(true);
                    final PluginMetaData data = customTableModel.getObject(pluginTable.convertRowIndexToModel(selectionModel.getMinSelectionIndex()));

                    final BeanAdapter<PluginMetaData> beanModel = new BeanAdapter<PluginMetaData>(data, true);
                    beanModel.addBeanPropertyChangeListener(new PropertyChangeListener() {
                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            final int row = pluginTable.convertRowIndexToModel(selectionModel.getMinSelectionIndex());
                            ((PluginMetaDataTableModel) pluginTable.getModel()).fireTableRowsUpdated(row, row);
                        }
                    });
                    bind(pluginDetailPanel.getSpinnerPluginPriority(), 1, MINIMUM_PRIORITY, MAXIMUM_PRIORITY, 1, beanModel.getValueModel("pluginPriority"));
                    final int max = data.getMaxParallelDownloads();
                    bind(pluginDetailPanel.getSpinnerMaxPluginConnections(), 1, 1, max, 1, beanModel.getValueModel("maxAllowedDownloads"));
                    pluginDetailPanel.getSpinnerMaxPluginConnections().setEnabled(max > 1);
                    bind(pluginDetailPanel.getCheckboxClipboardMonitoring(), beanModel.getValueModel("clipboardMonitored"));
                    bind(pluginDetailPanel.getCheckboxPluginIsActive(), beanModel.getValueModel("enabled"));
                    bind(pluginDetailPanel.getCheckboxUpdatePlugins(), beanModel.getValueModel("updatesEnabled"));
                    pluginDetailPanel.getAuthorLabel().setText(data.getVendor());
                    pluginDetailPanel.getAuthorLabel().setToolTipText(data.getVendor());
                    pluginDetailPanel.getVersionLabel().setText(data.getVersion());
                    pluginDetailPanel.getServicesLabel().setText(data.getServices());
                    pluginDetailPanel.getServicesLabel().setToolTipText(data.getServices());
                    pluginDetailPanel.getTitleSeparator().setTitle(data.getId());
                    setPluginOptionsEnabled(data.isOptionable());
                } else {
                    setPluginOptionsEnabled(false);
                    pluginDetailPanel.setVisible(false);
                }
            }
        });
        pluginDetailPanel.setVisible(false);

        pluginTable.setSortOrder(PluginMetaDataTableModel.COLUMN_ID, SortOrder.ASCENDING);
        pluginTable.setTerminateEditOnFocusLost(true);
        pluginTable.setAutoStartEditOnKeyStroke(true);

        TableColumn tableColumn = Swinger.updateColumn(pluginTable, "X", PluginMetaDataTableModel.COLUMN_ACTIVE, MIN_FIRST_PLUGIN_COLUMN_WIDTH, MIN_FIRST_PLUGIN_COLUMN_WIDTH, null);
        tableColumn.setWidth(MIN_FIRST_PLUGIN_COLUMN_WIDTH);
        tableColumn.setMaxWidth(MIN_FIRST_PLUGIN_COLUMN_WIDTH);
        tableColumn = Swinger.updateColumn(pluginTable, "U", PluginMetaDataTableModel.COLUMN_UPDATE, MIN_FIRST_PLUGIN_COLUMN_WIDTH, MIN_FIRST_PLUGIN_COLUMN_WIDTH, null);
        tableColumn.setWidth(MIN_FIRST_PLUGIN_COLUMN_WIDTH);
        tableColumn.setMaxWidth(MIN_FIRST_PLUGIN_COLUMN_WIDTH);

        tableColumn = Swinger.updateColumn(pluginTable, "C", PluginMetaDataTableModel.COLUMN_CLIPBOARD_MONITORED, MIN_FIRST_PLUGIN_COLUMN_WIDTH, MIN_FIRST_PLUGIN_COLUMN_WIDTH, null);
        tableColumn.setWidth(MIN_FIRST_PLUGIN_COLUMN_WIDTH);
        tableColumn.setMaxWidth(MIN_FIRST_PLUGIN_COLUMN_WIDTH);

        pluginTable.setRolloverEnabled(true);

        Swinger.updateColumn(pluginTable, "ID", PluginMetaDataTableModel.COLUMN_ID, -1, 70, null);
        Swinger.updateColumn(pluginTable, "Version", PluginMetaDataTableModel.COLUMN_VERSION, -1, 40, null);
        Swinger.updateColumn(pluginTable, "Services", PluginMetaDataTableModel.COLUMN_SERVICES, -1, 100, null);
        Swinger.updateColumn(pluginTable, "Author", PluginMetaDataTableModel.COLUMN_AUTHOR, -1, -1, null);
        Swinger.updateColumn(pluginTable, "MaxDownloads", PluginMetaDataTableModel.COLUMN_MAX_PARALEL_DOWNLOADS, -1, -1, new PluginConnectionAllowedRenderer());
        Swinger.updateColumn(pluginTable, "Priority", PluginMetaDataTableModel.COLUMN_PRIORITY, -1, -1, null);
        Swinger.updateColumn(pluginTable, "WWW", PluginMetaDataTableModel.COLUMN_WWW, -1, -1, SwingXUtils.getHyperLinkTableCellRenderer());

        final TableColumnModel tableColumnModel = pluginTable.getColumnModel();
        final SpinnerEditor spinnerEditor = new SpinnerEditor();
        tableColumnModel.getColumn(PluginMetaDataTableModel.COLUMN_MAX_PARALEL_DOWNLOADS).setCellEditor(spinnerEditor);
        tableColumnModel.getColumn(PluginMetaDataTableModel.COLUMN_PRIORITY).setCellEditor(spinnerEditor);

        pluginTable.getColumnExt(PluginMetaDataTableModel.COLUMN_WWW).setVisible(false);

        pluginTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!pluginTable.hasFocus())
                    Swinger.inputFocus(pluginTable);
                if (e.getClickCount() >= 2 && SwingUtilities.isLeftMouseButton(e)) {
                    btnPluginOptionsAction();
                } else if (SwingUtilities.isRightMouseButton(e))
                    SwingUtils.showPopMenu(popmenuButton.getPopupMenu(), e, pluginTable, dialog);
            }
        });

        final InputMap tableInputMap = pluginTable.getInputMap();
        final ActionMap tableActionMap = pluginTable.getActionMap();

        tableInputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_C), "copy");
        tableActionMap.put("copy", actionMap.get("copyContent"));

        final KeyStroke ctrlF = SwingUtils.getCtrlKeyStroke(KeyEvent.VK_F);
        tableInputMap.remove(ctrlF);

        pluginTable.getParent().setPreferredSize(new Dimension(230, 100));

        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_HOME), "selectFirstRowExtendSelection");
        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_END), "selectLastRowExtendSelection");

        setAction(pluginDetailPanel.getBtnPriorityDown(), "priorityDownAction");
        setAction(pluginDetailPanel.getBtnPriorityUp(), "priorityUpAction");

        new FindTableAction(resourceMap, PluginMetaDataTableModel.COLUMN_ID) {
            @Override
            protected Object getObject(int index, int column) {
                return pluginTable.getModel().getValueAt(index, column);
            }
        }.install(pluginTable);

        ((DefaultRowSorter<?, ?>) pluginTable.getRowSorter()).setComparator(PluginMetaDataTableModel.COLUMN_PRIORITY, Collections.reverseOrder());
    }

    private void updatePremium(PluginMetaData data) {
        if (!data.isEnabled())
            return;
        final List<PluginMetaData> dataList = managerDirector.getPluginsManager().getSupportedPlugins();
        final String s = data.getServices();
        for (PluginMetaData metaData : dataList) {
            if (metaData.getServices().equals(s) && !data.equals(metaData)) {
                metaData.setEnabled(false);
            }
        }
    }

    @SuppressWarnings("unused")
    public boolean isPluginOptionsEnabled() {
        return pluginOptionsEnabled;
    }

    public void setPluginOptionsEnabled(boolean pluginOptionsEnabled) {
        final boolean oldValue = this.pluginOptionsEnabled;
        this.pluginOptionsEnabled = pluginOptionsEnabled;
        firePropertyChange(PLUGIN_OPTIONS_ENABLED_PROPERTY, oldValue, pluginOptionsEnabled);
    }

    @Action
    public void priorityUpAction() {
        final int[] rows = Swinger.getSelectedRows(pluginTable);
        if (rows.length <= 0) {
            return;
        }
        pluginTable.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(PluginMetaDataTableModel.COLUMN_PRIORITY, SortOrder.ASCENDING)));
        final PluginMetaData data = ((PluginMetaDataTableModel) pluginTable.getModel()).getMetaValueAt(rows[0]);
        final List<PluginMetaData> dataList = getSortedPriorityPluginList();
        final int i = dataList.indexOf(data);
        if (i == -1 || dataList.size() - 1 == i) {
            return;
        }
        final PluginMetaData lowerPriorityPlugin = dataList.get(i + 1);
        data.setPluginPriority(Math.min(MAXIMUM_PRIORITY, lowerPriorityPlugin.getPluginPriority() + 1));
    }

    @Action
    public void priorityDownAction() {
        final int[] rows = Swinger.getSelectedRows(pluginTable);
        if (rows.length <= 0) {
            return;
        }
        pluginTable.getRowSorter().setSortKeys(Arrays.asList(new RowSorter.SortKey(PluginMetaDataTableModel.COLUMN_PRIORITY, SortOrder.ASCENDING)));
        final PluginMetaData data = ((PluginMetaDataTableModel) pluginTable.getModel()).getMetaValueAt(rows[0]);
        final List<PluginMetaData> dataList = getSortedPriorityPluginList();
        final int i = dataList.indexOf(data);
        if (i == -1 || i == 0) {
            return;
        }
        final PluginMetaData higherPriorityPlugin = dataList.get(i - 1);
        data.setPluginPriority(Math.max(MINIMUM_PRIORITY, higherPriorityPlugin.getPluginPriority() - 1));

    }

    private List<PluginMetaData> getSortedPriorityPluginList() {
        List<PluginMetaData> datas = getSupportedPlugins();
        Collections.sort(datas, new PluginsManager.PriorityComparator());
        return datas;
    }

    @Action
    public void copyPluginListAction() {
        copyPluginList(false);
    }

    private void copyPluginList(final boolean withVersion) {
        List<PluginMetaData> datas = getSortedPluginList();
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

    private List<PluginMetaData> getSortedPluginList() {
        List<PluginMetaData> datas = getSupportedPlugins();
        Collections.sort(datas);
        return datas;
    }

    private List<PluginMetaData> getSupportedPlugins() {
        return managerDirector.getPluginsManager().getSupportedPlugins();
    }

    @Action
    public void copyPluginListWithVersionAction() {
        copyPluginList(true);
    }

    @Action
    public void copySupportedSitesListAction() {
        final List<PluginMetaData> dataList = getSortedPluginList();
        StringBuilder builder = new StringBuilder();
        final String lineSeparator = Utils.getSystemLineSeparator();
        for (PluginMetaData data : dataList) {
            builder.append(data.getServices());
            builder.append(lineSeparator);
        }
        SwingUtils.copyToClipboard(builder.toString().trim(), this);
    }

    @Action
    public void copyContent() {
        final int[] rows = Swinger.getSelectedRows(pluginTable);
        if (rows.length <= 0)
            return;
        final int selCol = pluginTable.convertColumnIndexToModel(pluginTable.getColumnModel().getSelectionModel().getLeadSelectionIndex());
        if (selCol == PluginMetaDataTableModel.COLUMN_ACTIVE || selCol == PluginMetaDataTableModel.COLUMN_UPDATE)
            return;
        final PluginMetaDataTableModel tableModel = (PluginMetaDataTableModel) pluginTable.getModel();
        final Object value = tableModel.getValueAt(rows[0], selCol);
        if (value != null) {
            SwingUtils.copyToClipboard(value.toString(), this);
        }
    }

    @Action(enabledProperty = PLUGIN_OPTIONS_ENABLED_PROPERTY)
    public void btnPluginOptionsAction() {
        final int selectedRow = pluginTable.getSelectedRow();
        if (selectedRow == -1)
            return;
        final int i = pluginTable.convertRowIndexToModel(selectedRow);
        if (i == -1)
            return;
        final PluginMetaData data = ((PluginMetaDataTableModel) pluginTable.getModel()).getObject(i);
        final ShareDownloadService service = managerDirector.getPluginsManager().getPluginInstance(data.getId());
        try {
            service.showOptions();
            dialog.getModel().setBuffering(true);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    @Action
    public void btnResetDefaultPluginServerAction() {
        comboPluginServers.getModel().setSelectedItem(Consts.PLUGIN_CHECK_UPDATE_URL);
    }

    @Action
    public void selectAllUpdatesAction() {
        checkOrUncheckPlugin(Boolean.TRUE, PluginMetaDataTableModel.COLUMN_UPDATE);
    }

    @Action
    public void deSelectAllUpdatesAction() {
        checkOrUncheckPlugin(Boolean.FALSE, PluginMetaDataTableModel.COLUMN_UPDATE);
    }

    @Action
    public void selectAllActivityAction() {
        checkOrUncheckPlugin(Boolean.TRUE, PluginMetaDataTableModel.COLUMN_ACTIVE);
    }

    @Action
    public void deSelectAllActivityAction() {
        checkOrUncheckPlugin(Boolean.FALSE, PluginMetaDataTableModel.COLUMN_ACTIVE);
    }

    @Action
    public void selectAllCMAction() {
        checkOrUncheckPlugin(Boolean.TRUE, PluginMetaDataTableModel.COLUMN_CLIPBOARD_MONITORED);
    }

    @Action
    public void deSelectAllCMAction() {
        checkOrUncheckPlugin(Boolean.FALSE, PluginMetaDataTableModel.COLUMN_CLIPBOARD_MONITORED);
    }

    private void checkOrUncheckPlugin(Object value, int columnIndex) {
        final PluginMetaDataTableModel tableModel = (PluginMetaDataTableModel) pluginTable.getModel();
        final int count = tableModel.getRowCount();
        for (int i = 0; i < count; i++) {
            tableModel.setValueAt(value, i, columnIndex);
        }
    }

    @Override
    public boolean validated() {
        final Object item = comboPluginServers.getSelectedItem();
        if (item != null && !item.toString().isEmpty()) {
            try {
                new URI(item.toString());
            } catch (URISyntaxException e) {
                pluginTabbedPane.setSelectedIndex(1);
                dialog.showCard(UserPreferencesDialog.Card.CARD6);
                Swinger.inputFocus(comboPluginServers);
                Swinger.showErrorMessage(resourceMap, "invalidURL", item.toString());
                return false;
            }
        }
        return true;
    }

    @Override
    public void apply() {
        AppPrefs.storeProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, comboPluginServers.getSelectedItem().toString());
        ((SimplePreferencesComboModel) comboPluginServers.getModel()).store();
        if (pluginTableWasChanged) {
            managerDirector.getPluginsManager().updatePluginSettings();
            dialog.setUpdateQueue();
        }
    }

    @Override
    public void build(final CellConstraints cc) {
        JPanel pluginPanelSettings = new JPanel();
        JPanel pluginPanelUpdates = new JPanel();
        pluginTabbedPane = new JTabbedPane();

        JScrollPane scrollPane1 = new JScrollPane();
        JXButtonPanel pluginsButtonPanel = new JXButtonPanel();
        pluginTable = new JXTable();
        pluginTabbedPane.setName("pluginTabbedPane");
        pluginDetailPanel = new PluginDetailPanel();
        resourceMap.injectComponents(pluginDetailPanel);
        JLabel labelPluginInfo = new JLabel();
        labelPluginInfo.setName("labelPluginInfo");
        popmenuButton = ComponentFactory.getPopdownButton();
        popmenuButton.setName("popmenuButton");
        btnPluginOptions = new JButton();
        btnPluginOptions.setName("btnPluginOptions");

        check4PluginUpdatesAutomatically = new JCheckBox();
        check4PluginUpdatesAutomatically.setName("check4PluginUpdatesAutomatically");
        comboHowToUpdate = new JComboBox();
        JLabel labelAfterDetectUpdate = new JLabel();
        labelAfterDetectUpdate.setName("labelAfterDetectUpdate");
        labelAfterDetectUpdate.setLabelFor(comboHowToUpdate);
        checkDownloadNotExistingPlugins = new JCheckBox();
        checkDownloadNotExistingPlugins.setName("checkDownloadNotExistingPlugins");
        JLabel labelCheckForUpdateEvery = new JLabel();
        labelCheckForUpdateEvery.setName("labelCheckForUpdateEvery");
        spinnerUpdateHour = new JSpinner();
        JLabel labelHours = new JLabel();
        labelHours.setName("labelHours");
        comboPluginServers = new JComboBox();
        comboPluginServers.setEditable(true);
        JLabel labelUpdateFromServer = new JLabel();
        labelUpdateFromServer.setName("labelUpdateFromServer");
        labelUpdateFromServer.setLabelFor(comboPluginServers);
        btnResetDefaultPluginServer = new JButton();
        btnResetDefaultPluginServer.setName("btnResetDefaultPluginServer");
        JLabel labelManualCheck = new JLabel();
        labelManualCheck.setName("labelManualCheck");

        this.setBorder(Borders.TABBED_DIALOG);

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
                pluginPanelSettings.add(pluginDetailPanel, BorderLayout.EAST);

                //======== pluginsButtonPanel ========
                {
                    pluginsButtonPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

                    PanelBuilder pluginsButtonPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.UNRELATED_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.UNRELATED_GAP_COLSPEC
                            },
                            RowSpec.decodeSpecs("default")), pluginsButtonPanel);

                    pluginsButtonPanelBuilder.add(labelPluginInfo, cc.xy(3, 1));
                    pluginsButtonPanelBuilder.add(popmenuButton, cc.xy(5, 1));
                    pluginsButtonPanelBuilder.add(btnPluginOptions, cc.xy(7, 1));
                }
                pluginPanelSettings.add(pluginsButtonPanel, BorderLayout.SOUTH);
            }
            pluginTabbedPane.addTab(resourceMap.getString("pluginPanelSettings.tab.title"), pluginPanelSettings);

            //======== pluginPanelUpdates ========
            {
                pluginPanelUpdates.setBorder(new CompoundBorder(
                        new EmptyBorder(4, 4, 4, 4),
                        new TitledBorder(resourceMap.getString("pluginPanelUpdates.border"))));

                PanelBuilder pluginPanelUpdatesBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(Sizes.bounded(Sizes.MINIMUM, Sizes.dluX(30), Sizes.dluX(30))),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.dluX(50), Sizes.dluX(75)), FormSpec.DEFAULT_GROW),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.UNRELATED_GAP_COLSPEC
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.UNRELATED_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.UNRELATED_GAP_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }), pluginPanelUpdates);

                pluginPanelUpdatesBuilder.add(check4PluginUpdatesAutomatically, cc.xywh(1, 1, 5, 1));
                pluginPanelUpdatesBuilder.add(labelAfterDetectUpdate, cc.xywh(1, 3, 3, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                pluginPanelUpdatesBuilder.add(comboHowToUpdate, cc.xywh(5, 3, 1, 1, CellConstraints.LEFT, CellConstraints.DEFAULT));
                pluginPanelUpdatesBuilder.add(checkDownloadNotExistingPlugins, cc.xywh(1, 5, 5, 1));
                pluginPanelUpdatesBuilder.add(labelCheckForUpdateEvery, cc.xywh(1, 7, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                pluginPanelUpdatesBuilder.add(spinnerUpdateHour, cc.xy(3, 7));
                pluginPanelUpdatesBuilder.add(labelHours, cc.xy(5, 7));
                pluginPanelUpdatesBuilder.add(labelUpdateFromServer, cc.xywh(1, 9, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
                pluginPanelUpdatesBuilder.add(comboPluginServers, cc.xywh(3, 9, 3, 1));
                pluginPanelUpdatesBuilder.add(btnResetDefaultPluginServer, cc.xy(7, 9));
                pluginPanelUpdatesBuilder.add(labelManualCheck, cc.xywh(1, 13, 7, 1));
            }
            pluginTabbedPane.addTab(resourceMap.getString("pluginPanelUpdates.tab.title"), pluginPanelUpdates);

        }

        PanelBuilder thisBuilder = new PanelBuilder(new FormLayout(
                ColumnSpec.decodeSpecs("default:grow"),
                new RowSpec[]{
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("5px")
                }), this);

        thisBuilder.add(pluginTabbedPane, cc.xy(1, 1));
    }

    private JTabbedPane pluginTabbedPane;
    private PluginDetailPanel pluginDetailPanel;
    private JXTable pluginTable;
    private JButton btnPluginOptions;
    private PopdownButton popmenuButton;
    private JCheckBox check4PluginUpdatesAutomatically;
    private JComboBox comboHowToUpdate;
    private JCheckBox checkDownloadNotExistingPlugins;
    private JSpinner spinnerUpdateHour;
    private JComboBox comboPluginServers;
    private JButton btnResetDefaultPluginServer;

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    private static class SpinnerEditor extends AbstractCellEditor implements TableCellEditor {
        private final JSpinner spinner = new JSpinner();

        public SpinnerEditor() {
            final SpinnerNumberModel model = new SpinnerNumberModel();
            spinner.setModel(model);
            spinner.setEditor(new JSpinner.NumberEditor(spinner));
            model.setMinimum(1);
            spinner.setFocusable(true);
            for (Component tmpComponent : spinner.getComponents()) {
                tmpComponent.setFocusable(true);
                tmpComponent.addFocusListener(new FocusAdapter() {
                    @Override
                    public void focusLost(FocusEvent fe) {
                    }
                });
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            row = table.convertRowIndexToModel(row);
            assert row >= 0;
            final PluginMetaData data = ((PluginMetaDataTableModel) table.getModel()).getMetaValueAt(row);
            final SpinnerNumberModel model = (SpinnerNumberModel) spinner.getModel();
            column = table.convertColumnIndexToModel(column);
            spinner.setEnabled(true);
            if (column == PluginMetaDataTableModel.COLUMN_MAX_PARALEL_DOWNLOADS) {
                final int maxParallel = data.getMaxParallelDownloads();
                if (maxParallel == 1) {
                    spinner.setEnabled(false);
                }
                model.setMaximum(maxParallel);
            } else {
                model.setMaximum(MAXIMUM_PRIORITY);
            }
            spinner.setValue(value);
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            return spinner.getValue();
        }
    }

    private static class PluginConnectionAllowedRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value == null) {
                value = table.getValueAt(row, column);
            }
            this.setHorizontalAlignment(RIGHT);
            final PluginMetaData data = ((PluginMetaDataTableModel) table.getModel()).getMetaValueAt(row);
            if (data.getMaxAllowedDownloads() < data.getMaxParallelDownloads()) {
                this.setForeground(Color.GREEN);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

}
