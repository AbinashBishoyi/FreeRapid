package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.list.ArrayListModel;
import cz.vity.freerapid.core.tasks.ConnectionSettings;
import cz.vity.freerapid.core.tasks.DownloadClient;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.OSDesktop;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
import java.util.Arrays;

/**
 * @author Vity
 */
public class ContentPanel extends JPanel implements ListSelectionListener, ListDataListener, PropertyChangeListener {
    private static final int COLUMN_ID = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_PROGRESSBAR = 2;
    private static final int COLUMN_PROGRESS = 3;
    private static final int COLUMN_STATE = 4;
    private static final int COLUMN_SIZE = 5;
    private static final int COLUMN_SPEED = 6;
    private static final int COLUMN_PROXY = 7;

    private final ApplicationContext context;

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


    private JXTable table;

    public ContentPanel(ApplicationContext context, DataManager manager) {
        this.context = context;
        this.manager = manager;
        this.setName("contentPanel");
        Swinger.initActions(this, context);
        initComponents();
        setActions();
        manager.getDownloadFiles().addListDataListener(this);
        manager.addPropertyChangeListener(this);
    }


    @org.jdesktop.application.Action(enabledProperty = COMPLETED_OK_ACTION_ENABLED_PROPERTY)
    public void openFileAction() {
        final int[] indexes = table.getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            OSDesktop.openFile(file.getOutputFile());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = COMPLETED_OK_ACTION_ENABLED_PROPERTY)
    public void deleteFileAction() {
        final int[] indexes = table.getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        StringBuilder builder = new StringBuilder();
        for (DownloadFile file : files) {
            builder.append('\n').append(file.getFileName());
        }
        final int result = Swinger.getChoiceOKCancel("message.areyousuredelete", builder.toString());
        if (result == Swinger.RESULT_OK) {
            for (DownloadFile file : files) {
                file.getOutputFile().delete();
            }
            this.removeSelectedAction();
        }
    }

    @org.jdesktop.application.Action(enabledProperty = COMPLETED_OK_ACTION_ENABLED_PROPERTY)
    public void openDirectoryAction() {
        final int[] indexes = table.getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            OSDesktop.openFile(file.getOutputFile().getParentFile());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = RESUME_ACTION_ENABLED_PROPERTY)
    public void resumeAction() {
        manager.resumeSelected(table.getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = PAUSE_ACTION_ENABLED_PROPERTY)
    public void pauseAction() {
        manager.pauseSelected(table.getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = CANCEL_ACTION_ENABLED_PROPERTY)
    public void cancelAction() {
        manager.cancelSelected(table.getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = REMOVECOMPLETED_ACTION_ENABLED_PROPERTY)
    public void removeCompletedAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.removeCompleted();
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action
    public void selectAllAction() {
        table.selectAll();
    }

    @org.jdesktop.application.Action
    public void invertSelectionAction() {
        final int[] indexes = table.getSelectedRows();
        final int count = table.getModel().getRowCount();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        selectionModel.clearSelection();
        if (indexes.length > 1)
            Arrays.sort(indexes);
        for (int i = 0; i < count; i++) {
            if (Arrays.binarySearch(indexes, i) < 0)
                selectionModel.addSelectionInterval(i, i);
        }
        selectionModel.setValueIsAdjusting(false);
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

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void topAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = table.getSelectedRows();
        manager.moveTop(indexes);
        selectionModel.setValueIsAdjusting(false);
        selectionModel.setSelectionInterval(0, indexes.length - 1);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void upAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = table.getSelectedRows();
        manager.moveUp(indexes);
        selectionModel.clearSelection();
        for (int index : indexes) {
            selectionModel.addSelectionInterval(index, index);
        }
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void downAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = table.getSelectedRows();
        manager.moveDown(indexes);
        selectionModel.clearSelection();
        for (int index : indexes) {
            selectionModel.addSelectionInterval(index, index);
        }
        selectionModel.setValueIsAdjusting(false);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void bottomAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = table.getSelectedRows();
        manager.moveBottom(indexes);
        selectionModel.setValueIsAdjusting(false);
        final int rowCount = table.getRowCount();
        selectionModel.setSelectionInterval(rowCount - indexes.length, rowCount - 1);
    }


    private void setActions() {
        initTable();
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

    private void updateActions() {
        final int[] indexes = table.getSelectedRows();
        final boolean enabledCancel = this.manager.hasDownloadFilesStates(indexes, DownloadState.COMPLETED, DownloadState.ERROR, DownloadState.DOWNLOADING, DownloadState.GETTING, DownloadState.WAITING, DownloadState.PAUSED);
        setCancelActionEnabled(enabledCancel);

        setSelectedEnabled(indexes.length > 0);

        final boolean allCompleted = this.manager.hasDownloadFilesStates(indexes, DownloadState.COMPLETED);

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
            final boolean enabledResume = this.manager.hasDownloadFilesStates(indexes, DownloadState.ERROR, DownloadState.CANCELLED, DownloadState.PAUSED);
            setResumeActionEnabled(enabledResume);

            final boolean enabledPause = this.manager.hasDownloadFilesStates(indexes, DownloadState.ERROR, DownloadState.GETTING, DownloadState.QUEUED, DownloadState.WAITING, DownloadState.DOWNLOADING);
            setPauseActionEnabled(enabledPause);

            setCompletedWithFilesEnabled(false);
        }
    }


    private void initTable() {
        table.setName("mainTable");
        table.setModel(new CustomTableModel(manager.getDownloadFiles(), new String[]{"ID", "Name", "Progress", "Completition", "Est. time", "Size", "Speed", "Connection"}));
        table.setAutoCreateColumnsFromModel(false);
        table.setEditable(false);
        table.setColumnControlVisible(true);
        table.setSortable(false);
        table.setColumnMargin(10);
        table.setShowGrid(false, false);

        table.getSelectionModel().addListSelectionListener(this);

        final TableColumnModel tableColumnModel = table.getColumnModel();
        table.createDefaultColumnsFromModel();
        final TableColumn columnID = tableColumnModel.getColumn(COLUMN_ID);
        columnID.setCellRenderer(new IDCellRenderer());
        columnID.setMaxWidth(30);
        columnID.setWidth(30);
        final TableColumn colName = tableColumnModel.getColumn(COLUMN_NAME);
        colName.setCellRenderer(new NameURLCellRenderer());
        colName.setWidth(150);
        colName.setMinWidth(50);
        tableColumnModel.getColumn(COLUMN_PROGRESSBAR).setCellRenderer(new ProgressBarCellRenderer());
        tableColumnModel.getColumn(COLUMN_PROGRESS).setCellRenderer(new ProgressCellRenderer());
        tableColumnModel.getColumn(COLUMN_STATE).setCellRenderer(new EstTimeCellRenderer());
        tableColumnModel.getColumn(COLUMN_SIZE).setCellRenderer(new SizeCellRenderer());
        tableColumnModel.getColumn(COLUMN_SPEED).setCellRenderer(new SpeedCellRenderer());
        tableColumnModel.getColumn(COLUMN_PROXY).setCellRenderer(new ConnectionCellRenderer());

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    showPopMenu(e);
                else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
                    if (ContentPanel.this.isCompleteWithFilesEnabled())
                        openFileAction();
                }
            }
        });

    }

    private void showPopMenu(MouseEvent e) {
        final JPopupMenu popup = new JPopupMenu();
        final ApplicationActionMap map = this.context.getActionMap();
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
        popup.add(map.get("topAction"));
        popup.add(map.get("upAction"));
        popup.add(map.get("downAction"));
        popup.add(map.get("bottomAction"));
        popup.addSeparator();
        popup.add(map.get("removeSelectedAction"));
        popup.show(this, e.getX(), e.getY());
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
        updateActions();
    }

    public void intervalAdded(ListDataEvent e) {
        if (isInInterval(table.getSelectedRows(), e.getIndex0(), e.getIndex1())) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    updateActions();
                }
            });
        }
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

    public void selectAdded(java.util.List<DownloadFile> files) {
        table.getSelectionModel().setSelectionInterval(0, files.size() - 1);
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

    private static int getProgress(DownloadFile downloadFile) {
        final long downloaded = downloadFile.getDownloaded();
        final long fileSize = downloadFile.getFileSize();
        if (downloaded == 0 || fileSize == 0)
            return 0;
        return (int) (((downloaded / (float) fileSize) * 100));
    }


    private static class IDCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            this.setHorizontalAlignment(RIGHT);
            return super.getTableCellRendererComponent(table, row, isSelected, hasFocus, row, column);
        }
    }

    private static class NameURLCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            value = downloadFile.getFileUrl();
            super.getTableCellRendererComponent(table, " " + value, isSelected, hasFocus, row, column);
            this.setForeground(Color.BLUE);
            if (value != null)
                this.setToolTipText(value.toString());
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
                this.setToolTipText("Average speed " + bytesToAnother((long) downloadFile.getAverageSpeed()) + "/s");
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

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final DownloadState state = downloadFile.getState();
            this.setHorizontalAlignment(CENTER);
            if (state == DownloadState.DOWNLOADING) {
                long hasToBeDownloaded = downloadFile.getFileSize() - downloadFile.getDownloaded();
                final float speed = downloadFile.getAverageSpeed();

                if (Float.compare(0, speed) != 0) {
                    value = secondsToHMin(Math.round(hasToBeDownloaded / speed));
                } else value = stateToString(state);
            } else if (state == DownloadState.WAITING) {
                value = String.format("%s (%s)", stateToString(state), secondsToHMin(downloadFile.getSleep()));
            } else value = stateToString(state);
            if (state == DownloadState.ERROR) {
                this.setToolTipText(downloadFile.getErrorMessage());
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
            } else if (state == DownloadState.CANCELLED || state == DownloadState.ERROR || state == DownloadState.DELETED) {
                this.setBackground(BG_RED);
            } else if (state == DownloadState.PAUSED) {
                this.setBackground(Color.BLACK);
            } else if (state == DownloadState.QUEUED) {
                this.setBackground(BG_ORANGE);
            } else if (state == DownloadState.COMPLETED) {
                // this.setBackground(Color.GREEN);
            } else
                this.setBackground(Color.BLACK);
            this.setValue(getProgress(downloadFile));
            return this;
        }
    }

    private static class ConnectionCellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            final DownloadFile downloadFile = (DownloadFile) value;
            final DownloadTask task = downloadFile.getTask();
            if (task != null) {
                final DownloadClient client = task.getClient();
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


    private static String secondsToHMin(int seconds) {
        int min = seconds / 60;
        int hours = min / 60;
        min = min - hours * 60;
        seconds = seconds - min * 60 - hours * 3600;
        if (hours > 0) {
            return String.format("%02dh:%02dm:%02ds", hours, min, seconds);
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

}
