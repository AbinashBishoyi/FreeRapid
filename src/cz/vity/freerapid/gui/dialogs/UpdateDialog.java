package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.gui.managers.UpdateManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.PluginMetaData;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.xmlimport.ver1.Plugin;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.MalformedURLException;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class UpdateDialog extends AppDialog implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(UpdateDialog.class.getName());

    private final ApplicationContext context;
    private final ManagerDirector managerDirector;
    private final java.util.List<Plugin> list;

    private static final int COLUMN_SELECTED = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_VERSION = 2;
    private static final int COLUMN_SERVICES = 3;
    private static final int COLUMN_AUTHOR = 4;
    private static final int COLUMN_PROGRESS = 5;
    private static final int COLUMN_STATUS = 6;
    private ArrayListModel<WrappedPluginData> listModel;


    public UpdateDialog(Frame owner, ApplicationContext context, ManagerDirector managerDirector, java.util.List<Plugin> list) throws HeadlessException {
        super(owner, true);
        this.context = context;
        this.managerDirector = managerDirector;
        this.list = list;


        this.setName("UpdateDialog");

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
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }


    private void build() {
        inject();
        buildGUI();

        //final ActionMap actionMap = getActionMap();
        setAction(btnOK, "okBtnAction");
        setAction(btnCancel, "btnCancelAction");
    }

    @org.jdesktop.application.Action
    public void okBtnAction() {
        doClose();
    }


    @org.jdesktop.application.Action
    public void btnCancelAction() {
        doClose();
    }

    @Action
    public void btnCloseAction() {
        doClose();
    }

    private void buildGUI() {
        initTable();

        labelServer.setText(AppPrefs.getProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, Consts.PLUGIN_CHECK_UPDATE_URL));
        labelUpdatesCount.setText(getResourceMap().getString("labelUpdatesCount", list.size()));

        Swinger.inputFocus(table);
    }


    public void propertyChange(PropertyChangeEvent evt) {

    }

    private void initTable() {
        table.setName("updatePluginsTable");
        initData();
        table.setModel(new CustomTableModel(listModel, getList("columns")));
        table.setAutoCreateColumnsFromModel(false);
        table.setEditable(true);
        table.setColumnControlVisible(true);
        table.setSortable(true);

        table.setRolloverEnabled(true);

        table.setColumnSelectionAllowed(false);

        table.createDefaultColumnsFromModel();
        Swinger.updateColumn(table, "Selected", COLUMN_SELECTED, 25, 40, null).setWidth(45);
        Swinger.updateColumn(table, "Name", COLUMN_NAME, -1, 70, null);
        Swinger.updateColumn(table, "Version", COLUMN_VERSION, -1, 50, null);
        Swinger.updateColumn(table, "Services", COLUMN_SERVICES, -1, 50, null);
        Swinger.updateColumn(table, "Author", COLUMN_AUTHOR, -1, 50, null);
        Swinger.updateColumn(table, "Progress", COLUMN_PROGRESS, -1, 70, new ProgressBarCellRenderer());
        Swinger.updateColumn(table, "Status", COLUMN_STATUS, -1, 80, null);


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!table.hasFocus())
                    Swinger.inputFocus(table);
//                if (SwingUtilities.isRightMouseButton(e))
//                    showPopMenu(e);
            }
        });

        final InputMap tableInputMap = table.getInputMap();
        final ActionMap tableActionMap = table.getActionMap();
        final ActionMap actionMap = getActionMap();

        tableInputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_C), "copy");
        tableActionMap.put("copy", actionMap.get("copyContent"));

        final KeyStroke ctrlF = SwingUtils.getCtrlKeyStroke(KeyEvent.VK_F);
        tableInputMap.remove(ctrlF);

        table.getParent().setPreferredSize(new Dimension(450, 250));

        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_HOME), "selectFirstRowExtendSelection");
        tableInputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_END), "selectLastRowExtendSelection");
    }

    private void initData() {
        listModel = new ArrayListModel<WrappedPluginData>();
        final PluginsManager pluginsManager = managerDirector.getPluginsManager();
        final UpdateManager updateManager = managerDirector.getUpdateManager();
        final boolean downloadNotExisting = AppPrefs.getProperty(UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS, UserProp.DOWNLOAD_NOT_EXISTING_PLUGINS_DEFAULT);
        for (Plugin plugin : list) {
            final String id = plugin.getId();
            final boolean isNew = !pluginsManager.hasPlugin(id);
            if (!isNew) {
                final PluginMetaData data = pluginsManager.getPluginMetadata(id);
                if (!data.isUpdatesEnabled())
                    continue;
            }
            final DownloadFile httpFile;
            try {
                httpFile = updateManager.getDownloadFileInstance(plugin);
                httpFile.addPropertyChangeListener(this);
                final WrappedPluginData pluginData = new WrappedPluginData(downloadNotExisting || !isNew, httpFile, plugin);
                pluginData.setNew(isNew);
                listModel.add(pluginData);
            } catch (MalformedURLException e) {
                //ignore this malformed file
                LogUtils.processException(logger, e);
            }
        }
    }


    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("UpdateDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JScrollPane scrollPane1 = new JScrollPane();
        table = new JXTable();
        JPanel topPanel = new JPanel();
        JLabel labelUpdateServer = new JLabel();
        labelServer = new JLabel();
        labelUpdatesCount = new JLabel();
        JXButtonPanel buttonBar = new JXButtonPanel();
        progressBar = new JProgressBar();
        btnOK = new JButton();
        btnCancel = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout(4, 4));

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(table);
                }
                contentPanel.add(scrollPane1, BorderLayout.CENTER);

                //======== topPanel ========
                {

                    //---- labelUpdateServer ----
                    labelUpdateServer.setName("labelUpdateServer");

                    //---- server ----
                    labelServer.setName("server");

                    //---- labelUpdatesCount ----
                    labelUpdatesCount.setName("labelUpdatesCount");

                    PanelBuilder topPanelBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormFactory.DEFAULT_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormFactory.DEFAULT_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormFactory.DEFAULT_COLSPEC
                            },
                            RowSpec.decodeSpecs("default")), topPanel);

                    topPanelBuilder.add(labelUpdateServer, cc.xy(1, 1));
                    topPanelBuilder.add(labelServer, cc.xy(3, 1));
                    topPanelBuilder.add(labelUpdatesCount, cc.xy(7, 1));
                }
                contentPanel.add(topPanel, BorderLayout.NORTH);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
                buttonBar.setCyclic(true);

                //---- btnOK ----
                btnOK.setName("btnOK");

                //---- btnCancel ----
                btnCancel.setName("btnCancel");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.GLUE_COLSPEC,
                                new ColumnSpec("max(pref;42dlu)"),
                                FormFactory.RELATED_GAP_COLSPEC,
                                FormFactory.PREF_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{4, 6}});

                buttonBarBuilder.add(progressBar, cc.xy(2, 1));
                buttonBarBuilder.add(btnOK, cc.xy(4, 1));
                buttonBarBuilder.add(btnCancel, cc.xy(6, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private JXTable table;
    private JLabel labelServer;
    private JLabel labelUpdatesCount;
    private JProgressBar progressBar;
    private JButton btnOK;
    private JButton btnCancel;

    // JFormDesigner - End of variables declaration  //GEN-END:variables
    private class CustomTableModel extends AbstractTableModel implements ListDataListener {
        private final ArrayListModel<WrappedPluginData> model;
        private final String[] columns;


        public CustomTableModel(ArrayListModel<WrappedPluginData> model, String[] columns) {
            super();
            this.model = model;
            this.columns = columns;
            model.addListDataListener(this);
        }


        public int getRowCount() {
            return model.getSize();
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == COLUMN_SELECTED)
                return Boolean.class;
            return Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == COLUMN_SELECTED;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            if (columnIndex == COLUMN_SELECTED) {
                model.get(rowIndex).setSelected((Boolean) aValue);
                this.fireTableCellUpdated(rowIndex, columnIndex);
            }
        }


        @Override
        public String getColumnName(int column) {
            return this.columns[column];
        }

        public int getColumnCount() {
            return this.columns.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            final WrappedPluginData item = model.get(rowIndex);
            switch (columnIndex) {
                case COLUMN_SELECTED:
                    return item.getSelected();
                case COLUMN_NAME:
                    return item.getID();
                case COLUMN_VERSION:
                    return item.getVersion();
                case COLUMN_SERVICES:
                    return item.getServices();
                case COLUMN_AUTHOR:
                    return item.getAuthor();
                case COLUMN_PROGRESS:
                    return item.getHttpFile();
                case COLUMN_STATUS:
                    return getStatus(item);
                default:
                    assert false;
            }
            return item;
        }

        private Object getStatus(WrappedPluginData item) {
            final DownloadState state = item.getHttpFile().getState();
            if (state == DownloadState.PAUSED || state == DownloadState.QUEUED) {
                if (item.isNew()) {
                    return getResourceMap().getString("stateNew");
                } else
                    return getResourceMap().getString("stateNotActualized");
            } else if (state == DownloadState.CANCELLED) {
                return getResourceMap().getString("stateCancelled");
            } else if (state == DownloadState.ERROR) {
                return getResourceMap().getString("stateError") + ":" + item.getHttpFile().getErrorMessage();
            } else if (state == DownloadState.COMPLETED) {
                getResourceMap().getString("stateActualized");
            }
            return null;
        }

        public void intervalAdded(ListDataEvent e) {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        public void intervalRemoved(ListDataEvent e) {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
        }

        public void contentsChanged(ListDataEvent e) {
            fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
        }


    }


    private static class ProgressBarCellRenderer extends JProgressBar implements TableCellRenderer {
        private static final Color BG_RED = new Color(0xFFD0D0);
        private static final Color BG_ORANGE = new Color(0xFFEDD0);
        private static final Color BG_GREEN = new Color(0xD0FFE9);
        private static final Color BG_BLUE = new Color(0xb6e9ff);

        public ProgressBarCellRenderer() {
            super(0, 100);
            final int h = this.getPreferredSize().height;
            this.setPreferredSize(new Dimension(70, h));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final DownloadState state = downloadFile.getState();
            if (state == DownloadState.DOWNLOADING || state == DownloadState.GETTING || state == DownloadState.WAITING) {
                this.setBackground(BG_GREEN);
            } else
            if (state == DownloadState.CANCELLED || state == DownloadState.ERROR || state == DownloadState.DELETED) {
                this.setBackground(BG_RED);
            } else if (state == DownloadState.PAUSED || state == DownloadState.DISABLED) {
                this.setBackground(null);
            } else if (state == DownloadState.QUEUED) {
                this.setBackground(BG_ORANGE);
            } else if (state == DownloadState.SLEEPING) {
                this.setBackground(BG_BLUE);
            } else if (state == DownloadState.COMPLETED) {
                this.setBackground(null);
                // this.setBackground(Color.GREEN);
            } else
                this.setBackground(Color.BLACK);

            this.setToolTipText(null);
            final int progress = ContentPanel.getProgress(downloadFile);
            this.setStringPainted(true);
            this.setString(progress + "%");
            this.setValue(progress);
            return this;
        }

    }


}
