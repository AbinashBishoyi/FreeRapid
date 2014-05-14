package cz.vity.freerapid.gui.dialogs;


import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.UpdateManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.PopdownButton;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.application.Task;
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
import java.util.LinkedList;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class UpdateDialog extends AppDialog implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(UpdateDialog.class.getName());

    private final ManagerDirector managerDirector;

    private static final int COLUMN_SELECTED = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_VERSION = 2;
    private static final int COLUMN_SERVICES = 3;
    private static final int COLUMN_AUTHOR = 4;
    private static final int COLUMN_PROGRESS = 5;
    private static final int COLUMN_STATUS = 6;

    private ArrayListModel<WrappedPluginData> listModel = new ArrayListModel<WrappedPluginData>();
    private PopdownButton popmenuButton;


    public UpdateDialog(Frame owner, ManagerDirector managerDirector) throws HeadlessException {
        super(owner, true);
        this.managerDirector = managerDirector;
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
        setAction(btnOK, "okBtnAction");
        setAction(btnCancel, "btnCancelAction");
    }

    @Action
    public void okBtnAction() {
        getActionMap().get("okBtnAction").setEnabled(false);
        table.setEditable(false);
        Swinger.inputFocus(btnCancel);
        final UpdateManager updateManager = managerDirector.getUpdateManager();
        final Task task = updateManager.getDownloadPluginsTask(new LinkedList<WrappedPluginData>(listModel), false);
        if (task == null)
            return;
        updateManager.executeUpdateTask(task);
    }


    @Action
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

        Swinger.inputFocus(table);
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final DownloadFile downloadFile = (DownloadFile) evt.getSource();
                listModel.fireContentsChanged(getIndex(downloadFile));
            }
        });
    }

    private int getIndex(DownloadFile downloadFile) {
        int index = 0;
        for (WrappedPluginData data : listModel) {
            if (data.getHttpFile().equals(downloadFile))
                return index;
            ++index;
        }
        return -1;
    }

    private void initTable() {
        buildPopmenuButton(popmenuButton.getPopupMenu());

        table.setName("updatePluginsTable");
        table.setModel(new CustomTableModel(listModel, getList("columns", 7)));
        table.setAutoCreateColumnsFromModel(false);
        table.setEditable(true);
        table.setColumnControlVisible(true);
        table.setSortable(true);

        table.setRolloverEnabled(true);

        table.setColumnSelectionAllowed(false);

        table.createDefaultColumnsFromModel();

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        Swinger.updateColumn(table, "Selected", COLUMN_SELECTED, 25, 40, null).setMaxWidth(75);
        Swinger.updateColumn(table, "Name", COLUMN_NAME, -1, 70, null);
        Swinger.updateColumn(table, "Version", COLUMN_VERSION, -1, 50, null);
        Swinger.updateColumn(table, "Services", COLUMN_SERVICES, -1, 50, null);
        Swinger.updateColumn(table, "Author", COLUMN_AUTHOR, -1, 50, null);
        Swinger.updateColumn(table, "Progress", COLUMN_PROGRESS, -1, 70, new ProgressBarCellRenderer());
        Swinger.updateColumn(table, "Status", COLUMN_STATUS, -1, 80, null);


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!table.hasFocus()) {
                    Swinger.inputFocus(table);
                }
             if (SwingUtilities.isRightMouseButton(e))
                SwingUtils.showPopMenu(popmenuButton.getPopupMenu(), e, table, UpdateDialog.this);
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

    public void initData(java.util.List<WrappedPluginData> list) {
        cleanup();
        labelUpdatesCount.setText(getResourceMap().getString("labelUpdatesCount", list.size()));
        listModel.clear();
        for (WrappedPluginData wrappedPluginData : list) {
            wrappedPluginData.getHttpFile().addPropertyChangeListener(this);
            listModel.add(wrappedPluginData);
        }
    }

    @Override
    public void doClose() {
        super.doClose();
        cleanup();
    }

    private void cleanup() {
        for (WrappedPluginData data : listModel) {
            data.getHttpFile().removePropertyChangeListener(this);
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
        popmenuButton = ComponentFactory.getPopdownButton();
        popmenuButton.setName("popmenuButton");

        JXButtonPanel buttonBar = new JXButtonPanel();
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
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC
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
                buttonBar.setBorder(Borders.BUTTON_BAR_PAD);
                buttonBar.setCyclic(true);

                //---- btnOK ----
                btnOK.setName("btnOK");

                //---- btnCancel ----
                btnCancel.setName("btnCancel");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.GLUE_COLSPEC,
                                ColumnSpec.decode("max(pref;42dlu)"),
                                FormSpecs.RELATED_GAP_COLSPEC,
                                FormSpecs.PREF_COLSPEC
                        },
                        RowSpec.decodeSpecs("pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4, 6}});

                buttonBarBuilder.add(popmenuButton, cc.xy(2, 1));
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

        @Override
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

        @Override
        public int getColumnCount() {
            return this.columns.length;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            final WrappedPluginData item = model.get(rowIndex);
            switch (columnIndex) {
                case COLUMN_SELECTED:
                    return item.isSelected();
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
                if (item.isToBeDeleted()) {
                    return getResourceMap().getString("stateOldPlugin");
                }
                if (item.isNew()) {
                    return getResourceMap().getString("stateNew");
                } else
                    return getResourceMap().getString("stateNotActualized");
            } else if (state == DownloadState.CANCELLED) {
                return getResourceMap().getString("stateCancelled");
            } else if (state == DownloadState.ERROR) {
                return getResourceMap().getString("stateError") + ":" + item.getHttpFile().getErrorMessage();
            } else if (state == DownloadState.COMPLETED) {
                return getResourceMap().getString("stateActualized");
            }
            return null;
        }

        @Override
        public void intervalAdded(ListDataEvent e) {
            fireTableRowsInserted(e.getIndex0(), e.getIndex1());
        }

        @Override
        public void intervalRemoved(ListDataEvent e) {
            fireTableRowsDeleted(e.getIndex0(), e.getIndex1());
        }

        @Override
        public void contentsChanged(ListDataEvent e) {
            fireTableRowsUpdated(e.getIndex0(), e.getIndex1());
        }

    }


    @Action
    public void deSelectAll() {
        checkOrUncheckPlugin(Boolean.FALSE, COLUMN_SELECTED);
    }

    @Action
    public void selectAll() {
        checkOrUncheckPlugin(Boolean.TRUE, COLUMN_SELECTED);
    }

    private void buildPopmenuButton(final JPopupMenu popupMenu) {
        JMenuItem menuItem1 = new JMenuItem();
        menuItem1.setAction(getActionMap().get("selectAll"));
        popupMenu.add(menuItem1);
        JMenuItem menuItem2 = new JMenuItem();
        menuItem2.setAction(getActionMap().get("deSelectAll"));
        popupMenu.add(menuItem2);
    }    
    
    private void checkOrUncheckPlugin(Object value, int columnIndex) {
        final CustomTableModel tableModel = (CustomTableModel) this.table.getModel();
        final int count = tableModel.getRowCount();
        for (int i = 0; i < count; i++) {
            tableModel.setValueAt(value, i, columnIndex);
        }
    }


    private static class ProgressBarCellRenderer extends JProgressBar implements TableCellRenderer {

        public ProgressBarCellRenderer() {
            super(0, 100);
            final int h = this.getPreferredSize().height;
            this.setPreferredSize(new Dimension(70, h));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            this.setToolTipText(null);
            final int progress = ContentPanel.getProgress(downloadFile);
            this.setStringPainted(true);
            this.setString(progress + "%");
            this.setValue(progress);
            return this;
        }

    }


}
