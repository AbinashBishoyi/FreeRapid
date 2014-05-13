package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.actions.URLTransferHandler;
import cz.vity.freerapid.gui.dialogs.InformationDialog;
import cz.vity.freerapid.gui.dialogs.MultipleSettingsDialog;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.HttpDownloadClient;
import cz.vity.freerapid.plugins.webclient.HttpFile;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.OSDesktop;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.PatternFilter;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class ContentPanel extends JPanel implements ListSelectionListener, ListDataListener, PropertyChangeListener, ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ContentPanel.class.getName());

    private static final int COLUMN_NAME = 0;
    private static final int COLUMN_PROGRESSBAR = 1;
    private static final int COLUMN_PROGRESS = 2;
    private static final int COLUMN_STATE = 3;
    private static final int COLUMN_SIZE = 4;
    private static final int COLUMN_SPEED = 5;
    private static final int COLUMN_AVERAGE_SPEED = 6;
    private static final int COLUMN_SERVICE = 7;
    private static final int COLUMN_PROXY = 8;

    private final ApplicationContext context;
    private final ManagerDirector director;

    private final DataManager manager;

    private boolean cancelActionEnabled = false;

    private static final String CANCEL_ACTION_ENABLED_PROPERTY = "cancelActionEnabled";
    private boolean resumeActionEnabled = false;
    private static final String RESUME_ACTION_ENABLED_PROPERTY = "resumeActionEnabled";
    private boolean removeCompletedActionEnabled = false;
    private static final String REMOVECOMPLETED_ACTION_ENABLED_PROPERTY = "removeCompletedActionEnabled";
    private boolean pauseActionEnabled = false;
    private static final String PAUSE_ACTION_ENABLED_PROPERTY = "pauseActionEnabled";
    private boolean completeWithFilesEnabled = false;
    private static final String COMPLETED_OK_ACTION_ENABLED_PROPERTY = "completeWithFilesEnabled";
    private boolean selectedEnabled = false;
    private static final String SELECTED_ACTION_ENABLED_PROPERTY = "selectedEnabled";
    private boolean nonEmptyEnabled = false;
    private static final String NONEMPTY_ACTION_ENABLED_PROPERTY = "nonEmptyEnabled";


    private JXTable table;

    public ContentPanel(ApplicationContext context, ManagerDirector director) {
        this.context = context;
        this.director = director;
        this.manager = director.getDataManager();
        this.setName("contentPanel");
        Swinger.initActions(this, context);
        initComponents();
        setActions();
        manager.getDownloadFiles().addListDataListener(this);
        manager.addPropertyChangeListener(this);

    }


    @org.jdesktop.application.Action(enabledProperty = COMPLETED_OK_ACTION_ENABLED_PROPERTY)
    public void openFileAction() {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            OSDesktop.openFile(file.getOutputFile());
        }
    }

    private int[] getSelectedRows() {
        final int[] ints = table.getSelectedRows();

        for (int i = 0; i < ints.length; i++) {
            ints[i] = table.convertRowIndexToModel(ints[i]);
        }
        return ints;
    }

    @org.jdesktop.application.Action(enabledProperty = COMPLETED_OK_ACTION_ENABLED_PROPERTY)
    public void deleteFileAction() {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        StringBuilder builder = new StringBuilder();
        for (DownloadFile file : files) {
            builder.append('\n').append(file.getOutputFile());
        }

        final int result = Swinger.getChoiceOKCancel("message.areyousuredelete", builder.toString());
        if (result == Swinger.RESULT_OK) {
            for (DownloadFile file : files) {
                file.getOutputFile().delete();
            }
            this.removeSelectedAction();
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void downloadInformationAction() throws Exception {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        if (files.isEmpty())
            return;

        final MainApp app = (MainApp) context.getApplication();
        final JFrame owner = app.getMainFrame();
        if (files.size() == 1) {
            final InformationDialog dialog = new InformationDialog(owner, director, files.get(0));
            app.show(dialog);
        } else {
            final MultipleSettingsDialog dialog = new MultipleSettingsDialog(owner, files);
            app.show(dialog);
        }
    }

    @org.jdesktop.application.Action(enabledProperty = COMPLETED_OK_ACTION_ENABLED_PROPERTY)
    public void openDirectoryAction() {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            OSDesktop.openFile(file.getOutputFile().getParentFile());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = RESUME_ACTION_ENABLED_PROPERTY)
    public void resumeAction() {
        manager.resumeSelected(getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = PAUSE_ACTION_ENABLED_PROPERTY)
    public void pauseAction() {
        manager.pauseSelected(getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = CANCEL_ACTION_ENABLED_PROPERTY)
    public void cancelAction() {
        if (isCancelActionEnabled())
            manager.cancelSelected(getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = REMOVECOMPLETED_ACTION_ENABLED_PROPERTY)
    public void removeCompletedAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.removeCompleted();
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action(enabledProperty = NONEMPTY_ACTION_ENABLED_PROPERTY)
    public void selectAllAction() {
        table.selectAll();
    }

    @org.jdesktop.application.Action(enabledProperty = NONEMPTY_ACTION_ENABLED_PROPERTY)
    public void invertSelectionAction() {
        final int[] indexes = getSelectedRows();
        final int count = table.getModel().getRowCount();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        if (indexes.length > 1)
            Arrays.sort(indexes);
        for (int i = 0; i < count; i++) {
            if (Arrays.binarySearch(indexes, i) < 0) {
                int index = table.convertRowIndexToView(i);
                selectionModel.addSelectionInterval(index, index);
            }
        }
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void removeSelectedAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.removeSelected(indexes);
        selectionModel.setValueIsAdjusting(false);
        final int min = getArrayMin(indexes);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int count = table.getFilters().getOutputSize();
                if (count > 0) {//pokud je neco videt
                    int index = count - 1; //vypoctem si posledni viditelnou
                    if (index > min) {
                        index = table.convertRowIndexToView(min); //pokud neni videt
                        if (index == -1)
                            index = count - 1;//nastavime posledni
                    }

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

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void sortbyNameAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        int[] indexes = getSelectedRows();
        if (indexes.length == 1) {
            table.selectAll();
            indexes = getSelectedRows();
        }
        final int resultIndex = manager.sortByName(indexes);
        selectionModel.setValueIsAdjusting(false);
        if (resultIndex != -1) {
            int index = table.convertRowIndexToView(resultIndex);
            selectionModel.setSelectionInterval(index, index + indexes.length - 1);
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void topAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveTop(indexes);
        selectionModel.setValueIsAdjusting(false);
        selectionModel.setSelectionInterval(0, indexes.length - 1);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void upAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveUp(indexes);
        selectionModel.clearSelection();
        for (int index : indexes) {
            index = table.convertRowIndexToView(index);
            selectionModel.addSelectionInterval(index, index);
        }
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void downAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveDown(indexes);
        selectionModel.clearSelection();
        for (int index : indexes) {
            index = table.convertRowIndexToView(index);
            selectionModel.addSelectionInterval(index, index);
        }
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void bottomAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveBottom(indexes);
        selectionModel.setValueIsAdjusting(false);
        final int rowCount = table.getRowCount();
        selectionModel.setSelectionInterval(rowCount - indexes.length, rowCount - 1);
    }


    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void openInBrowser() {
        final java.util.List<DownloadFile> files = manager.getSelectionToList(getSelectedRows());
        for (HttpFile file : files) {
            Browser.openBrowser(file.getFileUrl().toExternalForm());
        }
    }

    private void setActions() {
        initTable();

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.SHOW_GRID_HORIZONTAL.equals(evt.getKey()) || UserProp.SHOW_GRID_VERTICAL.equals(evt.getKey()))
                    updateGridLines();
            }
        });
        updateGridLines();
    }

    private void updateGridLines() {
        final boolean horizontal = AppPrefs.getProperty(UserProp.SHOW_GRID_HORIZONTAL, UserProp.SHOW_GRID_HORIZONTAL_DEFAULT);
        final boolean vertical = AppPrefs.getProperty(UserProp.SHOW_GRID_VERTICAL, UserProp.SHOW_GRID_VERTICAL_DEFAULT);
        table.setShowGrid(horizontal, vertical);
    }


    public void updateFilters() {
        if (!AppPrefs.getProperty(UserProp.SHOW_COMPLETED, true)) {
            table.setFilters(new FilterPipeline(new StateFilter()));
        } else table.setFilters(null);

    }

    private static class StateFilter extends PatternFilter {
        public StateFilter() {
            super("", 0, 0);

        }

        @Override
        public boolean test(int row) {
            DownloadFile file = (DownloadFile) getInputValue(row, COLUMN_STATE);
            return file != null && file.getState() != DownloadState.COMPLETED;
        }
    }

    public boolean isPauseActionEnabled() {
        return pauseActionEnabled;
    }

    public void setPauseActionEnabled(boolean pauseActionEnabled) {
        boolean oldValue = this.pauseActionEnabled;
        this.pauseActionEnabled = pauseActionEnabled;
        firePropertyChange(PAUSE_ACTION_ENABLED_PROPERTY, oldValue, pauseActionEnabled);
    }

    public boolean isCancelActionEnabled() {
        return cancelActionEnabled;
    }

    public void setCancelActionEnabled(boolean cancelActionEnabled) {
        boolean oldValue = this.cancelActionEnabled;
        this.cancelActionEnabled = cancelActionEnabled;
        firePropertyChange(CANCEL_ACTION_ENABLED_PROPERTY, oldValue, cancelActionEnabled);
    }

    public boolean isResumeActionEnabled() {
        return resumeActionEnabled;
    }

    public void setResumeActionEnabled(boolean resumeActionEnabled) {
        boolean oldValue = this.resumeActionEnabled;
        this.resumeActionEnabled = resumeActionEnabled;
        firePropertyChange(RESUME_ACTION_ENABLED_PROPERTY, oldValue, resumeActionEnabled);
    }

    public boolean isRemoveCompletedActionEnabled() {
        return removeCompletedActionEnabled;
    }

    public void setRemoveCompletedActionEnabled(boolean removeCompletedActionEnabled) {
        boolean oldValue = this.removeCompletedActionEnabled;
        this.removeCompletedActionEnabled = removeCompletedActionEnabled;
        firePropertyChange(REMOVECOMPLETED_ACTION_ENABLED_PROPERTY, oldValue, removeCompletedActionEnabled);
    }

    public boolean isCompleteWithFilesEnabled() {
        return completeWithFilesEnabled;
    }

    public void setCompletedWithFilesEnabled(boolean completedEnabled) {
        boolean oldValue = this.completeWithFilesEnabled;
        this.completeWithFilesEnabled = completedEnabled;
        firePropertyChange(COMPLETED_OK_ACTION_ENABLED_PROPERTY, oldValue, completedEnabled);
    }

    public boolean isSelectedEnabled() {
        return selectedEnabled;
    }

    public void setSelectedEnabled(boolean selectedEnabled) {
        boolean oldValue = this.selectedEnabled;
        this.selectedEnabled = selectedEnabled;
        firePropertyChange(SELECTED_ACTION_ENABLED_PROPERTY, oldValue, selectedEnabled);
    }

    public boolean isNonEmptyEnabled() {
        return nonEmptyEnabled;
    }

    public void setNonEmptyEnabled(boolean nonEmptyEnabled) {
        boolean oldValue = this.nonEmptyEnabled;
        this.nonEmptyEnabled = nonEmptyEnabled;
        firePropertyChange(NONEMPTY_ACTION_ENABLED_PROPERTY, oldValue, nonEmptyEnabled);
    }

    private void updateActions() {
        final int[] indexes = getSelectedRows();
        final boolean enabledCancel = this.manager.hasDownloadFilesStates(indexes, DownloadState.cancelEnabledStates);
        setCancelActionEnabled(enabledCancel);

        setSelectedEnabled(indexes.length > 0);

        final boolean allCompleted = this.manager.hasDownloadFilesStates(indexes, DownloadState.completedStates);

        if (allCompleted) {
            boolean valid = true;
            final java.util.List<DownloadFile> files = this.manager.getSelectionToList(indexes);
            for (DownloadFile file : files) {
                if (!file.getOutputFile().exists()) {
                    valid = false;
                    break;
                }
            }
            setCompletedWithFilesEnabled(valid);


            setResumeActionEnabled(false);
            setPauseActionEnabled(false);
        } else {
            final boolean enabledResume = this.manager.hasAnyDownloadFilesStates(indexes, DownloadState.resumeEnabledStates);
            setResumeActionEnabled(enabledResume);

            final boolean enabledPause = this.manager.hasAnyDownloadFilesStates(indexes, DownloadState.pauseEnabledStates);
            setPauseActionEnabled(enabledPause);

            setCompletedWithFilesEnabled(false);
        }
        setNonEmptyEnabled(table.getModel().getRowCount() > 0);
    }


    private void initTable() {
        table.setName("mainTable");
        final String[] columns = (String[]) context.getResourceMap().getObject("mainTableColumns", String[].class);
        table.setModel(new CustomTableModel(manager.getDownloadFiles(), columns));
        table.setAutoCreateColumnsFromModel(false);
        table.setEditable(false);
        table.setColumnControlVisible(true);
        table.setSortable(false);
        table.setColumnMargin(10);

        table.setTransferHandler(new URLTransferHandler(director) {
            @Override
            protected void doDropAction(final List<URL> files) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        Swinger.getAction("addNewLinksAction").actionPerformed(new ActionEvent(files, 0, null));
                    }
                });
            }
        });

        table.getSelectionModel().addListSelectionListener(this);

        final TableColumnModel tableColumnModel = table.getColumnModel();
        table.createDefaultColumnsFromModel();
//        final TableColumn columnID = tableColumnModel.getColumn(COLUMN_ID);
//        columnID.setCellRenderer(new IDCellRenderer());
//        columnID.setMaxWidth(30);
//        columnID.setWidth(30);
        final TableColumn colName = tableColumnModel.getColumn(COLUMN_NAME);
        colName.setCellRenderer(new NameURLCellRenderer(director.getFileTypeIconProvider()));
        colName.setWidth(150);
        colName.setMinWidth(50);
        tableColumnModel.getColumn(COLUMN_PROGRESSBAR).setCellRenderer(new ProgressBarCellRenderer());
        tableColumnModel.getColumn(COLUMN_PROGRESS).setCellRenderer(new ProgressCellRenderer());
        tableColumnModel.getColumn(COLUMN_STATE).setCellRenderer(new EstTimeCellRenderer());
        tableColumnModel.getColumn(COLUMN_SIZE).setCellRenderer(new SizeCellRenderer());
        tableColumnModel.getColumn(COLUMN_SPEED).setCellRenderer(new SpeedCellRenderer());
        tableColumnModel.getColumn(COLUMN_AVERAGE_SPEED).setCellRenderer(new AverageSpeedCellRenderer());
        tableColumnModel.getColumn(COLUMN_SERVICE).setCellRenderer(new ServiceCellRenderer());
        tableColumnModel.getColumn(COLUMN_PROXY).setCellRenderer(new ConnectionCellRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    showPopMenu(e);
                else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
                    if (ContentPanel.this.isCompleteWithFilesEnabled())
                        openFileAction();
                    else if (isSelectedEnabled()) {
                        try {
                            downloadInformationAction();
                        } catch (Exception ex) {
                            LogUtils.processException(logger, ex);
                        }
                    }
                }
            }
        });

        final InputMap inputMap = table.getInputMap();
        final ActionMap actionMap = table.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("control C"), "copy");
        inputMap.put(KeyStroke.getKeyStroke("control alt C"), "copy");

        actionMap.put("copy", Swinger.getAction("copyContent"));

        inputMap.put(KeyStroke.getKeyStroke("ESCAPE"), "deleteItem");
        actionMap.put("deleteItem", Swinger.getAction("cancelAction"));

        inputMap.put(KeyStroke.getKeyStroke("shift DELETE"), "deleteFileAction");
        actionMap.put("deleteFileAction", Swinger.getAction("deleteFileAction"));

//        paste();

        updateFilters();
    }

    public void paste() {
        final Action action = table.getActionMap().get("paste");
        action.actionPerformed(new ActionEvent(table, 0, "paste"));
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void copyContent() {
        final java.util.List<DownloadFile> files = manager.getSelectionToList(getSelectedRows());
        StringBuilder builder = new StringBuilder();
        for (DownloadFile file : files) {
            builder.append(file.toString()).append('\n');
        }
        final StringSelection stringSelection = new StringSelection(builder.toString().trim());
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, this);
    }

    private void showPopMenu(MouseEvent e) {
        final JPopupMenu popup = new JPopupMenu();
        final ApplicationActionMap map = this.context.getActionMap();
        popup.add(map.get("downloadInformationAction"));
        popup.addSeparator();
        popup.add(map.get("openFileAction"));
        popup.add(map.get("deleteFileAction"));
        popup.add(map.get("openDirectoryAction"));
        popup.addSeparator();
        popup.add(map.get("resumeAction"));
        popup.add(map.get("pauseAction"));
        popup.add(map.get("cancelAction"));
        popup.addSeparator();
        popup.add(map.get("removeCompletedAction"));
        popup.addSeparator();
        popup.add(map.get("selectAllAction"));
        popup.add(map.get("invertSelectionAction"));
        popup.addSeparator();
        popup.add(map.get("removeSelectedAction"));
//        final JMenu menu = new JMenu("Misc");
//        popup.add(menu);
        JMenu forceMenu = new JMenu("Force Download");
        forceMenu.setMnemonic('F');
//      menu.add(forceMenu);
        boolean forceEnabled = isSelectedEnabled() && this.manager.hasDownloadFilesStates(getSelectedRows(), DownloadState.forceEnabledStates);
        forceMenu.setEnabled(forceEnabled);
        final List<ConnectionSettings> connectionSettingses = director.getClientManager().getAvailableConnections();
        for (ConnectionSettings settings : connectionSettingses) {
            final ForceDownloadAction action = new ForceDownloadAction(settings);
            forceMenu.add(action);
            action.setEnabled(forceEnabled);
        }
        popup.addSeparator();
        popup.add(forceMenu);
        popup.addSeparator();
        popup.add(map.get("copyContent"));
        popup.add(map.get("openInBrowser"));

        final MouseEvent event = SwingUtilities.convertMouseEvent(table, e, this);
        popup.show(this, event.getX(), event.getY());
    }


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        JScrollPane scrollPane = new JScrollPane();
        table = new JXTable();

        //======== this ========
        setLayout(new BorderLayout());

        //======== scrollPane ========
        {
            scrollPane.setViewportView(table);
        }
        add(scrollPane, BorderLayout.CENTER);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    public void valueChanged(ListSelectionEvent e) {
        if (e.getValueIsAdjusting())
            return;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                updateActions();
            }
        });
    }

    public void intervalAdded(final ListDataEvent e) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if (isInInterval(getSelectedRows(), e.getIndex0(), e.getIndex1())) {
                    updateActions();
                }
            }
        });
    }

    public void intervalRemoved(ListDataEvent e) {
        intervalAdded(e);
    }

    public void contentsChanged(ListDataEvent e) {
        intervalAdded(e);
    }

    private boolean isInInterval(int[] indexes, int index0, int index1) {
        for (int i : indexes) {
            if (index0 >= i && i <= index1)
                return true;
        }
        return false;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if ("completed".equals(evt.getPropertyName())) {
            setRemoveCompletedActionEnabled(((Integer) evt.getNewValue()) > 0);
        }
    }

    public void selectAdded(final java.util.List<DownloadFile> files) {
        assert !files.isEmpty();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        final int index = manager.getDownloadFiles().indexOf(files.get(0));
        final int viewIndex = table.convertRowIndexToView(index);
        selectionModel.setSelectionInterval(viewIndex, viewIndex + files.size() - 1);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    private static class CustomTableModel extends AbstractTableModel implements ListDataListener {
        private final ArrayListModel<DownloadFile> model;
        private final String[] columns;


        public CustomTableModel(ArrayListModel<DownloadFile> model, String[] columns) {
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

    public static int getProgress(DownloadFile downloadFile) {
        final long downloaded = downloadFile.getDownloaded();
        final long fileSize = downloadFile.getFileSize();
        if (downloaded == 0 || fileSize == 0)
            return 0;
        return (int) (((downloaded / (float) fileSize) * 100));
    }

//    private static class IDCellRenderer extends DefaultTableCellRenderer {
//
//        @Override
//        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//            this.setHorizontalAlignment(RIGHT);
//            return super.getTableCellRendererComponent(table, row, isSelected, hasFocus, row, column);
//        }
//    }

    private static class NameURLCellRenderer extends DefaultTableCellRenderer {

        private final FileTypeIconProvider iconProvider;

        private NameURLCellRenderer(FileTypeIconProvider iconProvider) {
            this.iconProvider = iconProvider;

        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final String fn = downloadFile.getFileName();
            final String url = downloadFile.getFileUrl().toString();
            if (fn != null && !fn.isEmpty()) {
                value = fn;
            } else {
                value = url;
            }

            super.getTableCellRendererComponent(table, " " + value, isSelected, hasFocus, row, column);
            //this.setForeground(Color.BLUE);
            if (value != null) {
                this.setToolTipText(url);
                this.setIcon(iconProvider.getIconImageByFileType(downloadFile.getFileType(), false));
            }
            return this;
        }

    }

    private static class SizeCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final long fs = downloadFile.getFileSize();
            if (fs >= 0) {
                if (downloadFile.getDownloaded() != fs)
                    value = bytesToAnother(downloadFile.getDownloaded()) + " of " + bytesToAnother(fs);
                else
                    value = bytesToAnother(fs);
                this.setToolTipText(NumberFormat.getIntegerInstance().format(fs) + " B");
            } else {
                value = "unknown";
                this.setToolTipText(null);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class SpeedCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            if (downloadFile.getState() == DownloadState.DOWNLOADING) {
                if (downloadFile.getSpeed() >= 0) {
                    value = bytesToAnother(downloadFile.getSpeed()) + "/s";
                } else value = "0 B/s";
                //this.setToolTipText("Average speed " + bytesToAnother((long) downloadFile.getAverageSpeed()) + "/s");
            } else value = "";
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class ProgressCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            this.setHorizontalAlignment(CENTER);
            final int progress = getProgress(downloadFile);
            value = progress + "%";
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class EstTimeCellRenderer extends DefaultTableCellRenderer {
        private String tooltip;

        private EstTimeCellRenderer() {
            tooltip = Swinger.getResourceMap().getString("tooltip");
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final DownloadState state = downloadFile.getState();
            value = stateToString(state);
            this.setHorizontalAlignment(CENTER);
            this.setToolTipText(null);
            if (state == DownloadState.DOWNLOADING) {
                long hasToBeDownloaded = downloadFile.getFileSize() - downloadFile.getDownloaded();
                final float speed = downloadFile.getAverageSpeed();
                if (Float.compare(0, speed) != 0 && speed > 0) {
                    if (hasToBeDownloaded >= 0) {
                        value = secondsToHMin(Math.round(hasToBeDownloaded / speed));
                    }
                }
            } else if (state == DownloadState.WAITING) {
                if (downloadFile.getSleep() >= 0)
                    value = String.format("%s (%s)", stateToString(state), secondsToHMin(downloadFile.getSleep()));
                else value = "";
            }
            if (state == DownloadState.ERROR) {
                final String errorMessage = downloadFile.getErrorMessage();
                if (errorMessage != null) {
                    value = value + " - " + errorMessage.replaceAll("<.*?>", "");
                    this.setToolTipText(String.format(tooltip, errorMessage));
                }
            } else if (DownloadState.isProcessState(state)) {
                Task task = downloadFile.getTask();
                if (task != null)
                    this.setToolTipText("Elapsed time: " + secondsToHMin(task.getExecutionDuration(TimeUnit.SECONDS)));

            }

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class ProgressBarCellRenderer extends JProgressBar implements TableCellRenderer {
        private static final Color BG_RED = new Color(0xFFD0D0);
        private static final Color BG_ORANGE = new Color(0xFFEDD0);
        private static final Color BG_GREEN = new Color(0xD0FFE9);

        public ProgressBarCellRenderer() {
            super(0, 100);
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
            } else if (state == DownloadState.PAUSED) {
                this.setBackground(Color.BLACK);
            } else if (state == DownloadState.QUEUED) {
                this.setBackground(BG_ORANGE);
            } else if (state == DownloadState.COMPLETED) {
                // this.setBackground(Color.GREEN);
            } else
                this.setBackground(Color.BLACK);

            final int toQueued = downloadFile.getTimeToQueued();
            if (state == DownloadState.ERROR && toQueued >= 0) {
                final int max = downloadFile.getTimeToQueuedMax();
                this.setStringPainted(true);
                this.setString(toQueued + "/" + max);
                this.setValue(getProgress(max, toQueued));
                this.setToolTipText("Autoreconnect in " + toQueued + " seconds");
            } else {
                final int sleep = downloadFile.getSleep();
                if (state == DownloadState.WAITING && sleep >= 0) {
                    final int max = downloadFile.getTimeToQueuedMax();
                    this.setStringPainted(true);
                    this.setString(sleep + "/" + max);
                    this.setValue(getProgress(max, sleep));
                    this.setToolTipText("Attempt for downloading in " + sleep + " seconds");
                } else {
                    this.setToolTipText(null);
                    this.setStringPainted(false);
                    this.setValue(getProgress(downloadFile));
                }
            }
            return this;
        }

    }

    private static int getProgress(int max, int timeToQueued) {
        return (int) ((timeToQueued / (float) max) * 100);
    }

    private static class ConnectionCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final DownloadTask task = downloadFile.getTask();
            if (task != null) {
                final HttpDownloadClient client = task.getClient();
                final ConnectionSettings con = client.getSettings();
                if (con.isProxySet()) {
                    value = String.format("%s:%s", con.getProxyURL(), con.getProxyPort());
                    if (con.getUserName() != null) {
                        value = con.getUserName() + "@" + value;
                    }
                } else value = "Default";
            } else value = "";
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }


    public static String secondsToHMin(long seconds) {
        long min = seconds / 60;
        long hours = min / 60;
        min = min - hours * 60;
        seconds = seconds - min * 60 - hours * 3600;
        if (hours > 0) {
            return String.format("%02dh:%02dm", hours, min);
        } else if (min > 0) {
            return String.format("%02dm:%02ds", min, seconds);
        } else
            return String.format("%ds", seconds);
    }

    public static String bytesToAnother(long bytes) {
        final NumberFormat number = NumberFormat.getNumberInstance();
        number.setMaximumFractionDigits(2);
        number.setMinimumFractionDigits(1);
        if (bytes > (1024 * 1024)) {
            return String.format("%s MB", number.format((float) bytes / (1024 * 1024)));
        } else if (bytes > 1024) {
            return String.format("%s kB", number.format((float) bytes / 1024));
        } else {
            return String.format("%s B", NumberFormat.getIntegerInstance().format(bytes));
        }
    }

    private static String stateToString(DownloadState state) {
        return state.toString();
    }


    private class ForceDownloadAction extends AbstractAction {
        private final ConnectionSettings settings;

        public ForceDownloadAction(ConnectionSettings settings) {
            this.settings = settings;
            this.putValue(NAME, settings.toString());
        }

        public void actionPerformed(ActionEvent e) {
            manager.forceDownload(settings, getSelectedRows());
        }
    }


    private static class ServiceCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            value = downloadFile.getServiceName();
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private class AverageSpeedCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final DownloadState state = downloadFile.getState();
            if (state == DownloadState.DOWNLOADING) {
                if (downloadFile.getSpeed() >= 0) {
                    value = bytesToAnother((long) downloadFile.getAverageSpeed()) + "/s";
                } else value = "0 B/s";
            } else value = "";

            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }
}
