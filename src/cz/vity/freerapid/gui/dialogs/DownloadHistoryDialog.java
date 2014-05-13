package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.gui.managers.FileHistoryItem;
import cz.vity.freerapid.gui.managers.FileHistoryManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.OSDesktop;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class DownloadHistoryDialog extends AppDialog implements ClipboardOwner, ListSelectionListener {
    private final static Logger logger = Logger.getLogger(DownloadHistoryDialog.class.getName());
    private FileHistoryManager manager;
    private static final int COLUMN_DATE = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_DESCRIPTION = 2;
    private static final int COLUMN_SIZE = 3;
    private static final int COLUMN_URL = 4;

    private boolean selectedEnabled = false;
    private static final String SELECTED_ACTION_ENABLED_PROPERTY = "selectedEnabled";
    private final ManagerDirector director;


    public DownloadHistoryDialog(ManagerDirector director, Frame owner) throws HeadlessException {
        super(owner, true);
        this.director = director;
        this.manager = director.getFileHistoryManager();
        this.setName("DownloadHistoryDialog");
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

    private void build() {
        inject();
        buildGUI();

        //final ActionMap actionMap = getActionMap();
        setAction(okButton, "okBtnAction");
        setAction(clearHistoryBtn, "clearHistoryBtnAction");

        pack();
        locateOnOpticalScreenCenter(this);
    }

    private void initTable() {
        table.setName("historyTable");
        table.setModel(new CustomTableModel(manager.getItems(), new String[]{"Date", "File name", "Description", "Size", "URL"}));
        table.setAutoCreateColumnsFromModel(false);
        table.setEditable(false);
        table.setColumnControlVisible(true);
        table.setSortable(false);
        table.setColumnMargin(10);
        table.setShowGrid(false, false);

        table.getSelectionModel().addListSelectionListener(this);

//        final TableColumnModel tableColumnModel = table.getColumnModel();
        table.createDefaultColumnsFromModel();
        Swinger.updateColumn(table, "Date", COLUMN_DATE, 40, new DateCellRenderer());
        Swinger.updateColumn(table, "Name", COLUMN_NAME, 150, new FileNameCellRenderer());
        Swinger.updateColumn(table, "Description", COLUMN_DESCRIPTION, 170, new DescriptionCellRenderer());
        Swinger.updateColumn(table, "Size", COLUMN_SIZE, 40, new SizeCellRenderer());
        Swinger.updateColumn(table, "URL", COLUMN_URL, -1, new URLCellRenderer());


        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    showPopMenu(e);
                else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
                    openFileAction();
                }
            }
        });

        table.getInputMap().put(KeyStroke.getKeyStroke("control C"), "copy");
        table.getActionMap().put("copy", Swinger.getAction("copyContent"));
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void copyContent() {
        final java.util.List<FileHistoryItem> files = manager.getSelectionToList(table.getSelectedRows());
        StringBuilder builder = new StringBuilder();
        for (FileHistoryItem file : files) {
            builder.append(file.toString()).append('\n');
        }
        final StringSelection stringSelection = new StringSelection(builder.toString().trim());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void openFileAction() {
        final int[] indexes = table.getSelectedRows();
        final java.util.List<FileHistoryItem> files = manager.getSelectionToList(indexes);
        for (FileHistoryItem file : files) {
            OSDesktop.openFile(file.getOutputFile());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void openInBrowser() {
        final java.util.List<FileHistoryItem> files = manager.getSelectionToList(table.getSelectedRows());
        for (FileHistoryItem file : files) {
            Browser.openBrowser(file.getUrl().toExternalForm());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void openDirectoryAction() {
        final int[] indexes = table.getSelectedRows();
        final java.util.List<FileHistoryItem> files = manager.getSelectionToList(indexes);
        for (FileHistoryItem file : files) {
            if (file.getOutputFile().exists())
                OSDesktop.openFile(file.getOutputFile().getParentFile());
        }
    }

    @Action
    public void cancelBtnAction() {
        doClose();
    }

    private void buildGUI() {
        initTable();
    }

    @Action
    public void okBtnAction() {
        doClose();
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("DownloadHistoryDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JPanel panel1 = new JPanel();
        combobox = new JComboBox();
        JLabel labelFilter = new JLabel();
        fieldFilter = new JTextField();
        JScrollPane scrollPane2 = new JScrollPane();
        table = new JXTable();
        JXButtonPanel buttonBar = new JXButtonPanel();
        clearHistoryBtn = new JButton();
        okButton = new JButton();
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

                //======== panel1 ========
                {
                    panel1.setBorder(new TitledBorder(""));

                    //---- labelFilter ----
                    labelFilter.setName("labelFilter");
                    labelFilter.setLabelFor(fieldFilter);

                    PanelBuilder panel1Builder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    new ColumnSpec("max(pref;80dlu)"),
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormFactory.DEFAULT_COLSPEC,
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(Sizes.dluX(100)),
                                    FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                            },
                            RowSpec.decodeSpecs("default")), panel1);

                    panel1Builder.add(combobox, cc.xy(1, 1));
                    panel1Builder.add(labelFilter, cc.xy(3, 1));
                    panel1Builder.add(fieldFilter, cc.xy(5, 1));
                }

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(table);
                }

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        ColumnSpec.decodeSpecs("default:grow"),
                        new RowSpec[]{
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                        }), contentPanel);

                contentPanelBuilder.add(panel1, cc.xy(1, 1));
                contentPanelBuilder.add(scrollPane2, cc.xy(1, 3));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

                //---- clearHistoryBtn ----
                clearHistoryBtn.setName("clearHistoryBtn");

                //---- okButton ----
                okButton.setName("okButton");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormFactory.UNRELATED_GAP_COLSPEC,
                                new ColumnSpec("max(pref;55dlu)")
                        },
                        RowSpec.decodeSpecs("fill:pref")), buttonBar);

                buttonBarBuilder.add(clearHistoryBtn, cc.xy(1, 1));
                buttonBarBuilder.add(okButton, cc.xy(5, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        updateActions();
    }

    private void updateActions() {
        final int[] indexes = table.getSelectedRows();
        setSelectedEnabled(indexes.length > 0);
    }

    public boolean isSelectedEnabled() {
        return selectedEnabled;
    }

    public void setSelectedEnabled(boolean selectedEnabled) {
        boolean oldValue = this.selectedEnabled;
        this.selectedEnabled = selectedEnabled;
        firePropertyChange(SELECTED_ACTION_ENABLED_PROPERTY, oldValue, selectedEnabled);
    }


    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void deleteFileAction() {
        final int[] indexes = table.getSelectedRows();
        final java.util.List<FileHistoryItem> files = manager.getSelectionToList(indexes);
        StringBuilder builder = new StringBuilder();
        for (FileHistoryItem file : files) {
            builder.append('\n').append(file.getFileName());
        }
        final int result = Swinger.getChoiceOKCancel("message.areyousuredelete", builder.toString());
        if (result == Swinger.RESULT_OK) {
            for (FileHistoryItem file : files) {
                file.getOutputFile().delete();
            }
            this.removeSelectedAction();
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void removeSelectedAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = table.getSelectedRows();
        manager.removeSelected(indexes);
        selectionModel.setValueIsAdjusting(false);
        final int min = getArrayMin(indexes);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final int count = table.getRowCount();
                if (table.getRowCount() > 0) {
                    int index = Math.min(count - 1, min);
                    selectionModel.addSelectionInterval(index, index);
                }
            }
        });
    }

    private int getArrayMin(int[] indexes) {
        int min = Integer.MAX_VALUE;
        for (int i : indexes) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }


    private static class CustomTableModel extends AbstractTableModel implements ListDataListener {
        private final ArrayListModel<FileHistoryItem> model;
        private final String[] columns;


        public CustomTableModel(ArrayListModel<FileHistoryItem> model, String[] columns) {
            super();
            this.model = model;
            this.columns = columns;
            model.addListDataListener(this);
        }

        public int getRowCount() {
            return model.getSize();
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return false;
        }

        @Override
        public String getColumnName(int column) {
            return this.columns[column];
        }

        public int getColumnCount() {
            return this.columns.length;
        }

        public Object getValueAt(int rowIndex, int columnIndex) {
            return model.get(rowIndex);
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

    private void showPopMenu(MouseEvent e) {
        final JPopupMenu popup = new JPopupMenu();
        final ActionMap map = this.getActionMap();
        popup.add(map.get("openFileAction"));
        popup.add(map.get("deleteFileAction"));
        popup.add(map.get("openDirectoryAction"));
        popup.addSeparator();
        popup.add(map.get("copyContent"));
        popup.add(map.get("openInBrowser"));

        popup.show(this, e.getX(), e.getY());
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void clearHistoryBtnAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.clearHistory();
        selectionModel.setValueIsAdjusting(false);
    }


    private static class DateCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class FileNameCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class DescriptionCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class SizeCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class URLCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private JComboBox combobox;
    private JTextField fieldFilter;
    private JXTable table;
    private JButton clearHistoryBtn;
    private JButton okButton;

}