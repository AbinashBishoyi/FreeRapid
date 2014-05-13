package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.gui.actions.URLTransferHandler;
import cz.vity.freerapid.gui.dialogs.InformationDialog;
import cz.vity.freerapid.gui.dialogs.MultipleSettingsDialog;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.MenuManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.FileState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.OSDesktop;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.PatternFilter;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.net.URL;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class ContentPanel extends JPanel implements ListSelectionListener, ListDataListener, PropertyChangeListener, ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ContentPanel.class.getName());

    private static final int COLUMN_CHECKED = 0;
    private static final int COLUMN_NAME = 1;
    private static final int COLUMN_PROGRESSBAR = 2;
    private static final int COLUMN_PROGRESS = 3;
    private static final int COLUMN_STATE = 4;
    private static final int COLUMN_SIZE = 5;
    private static final int COLUMN_SPEED = 6;
    private static final int COLUMN_AVERAGE_SPEED = 7;
    private static final int COLUMN_SERVICE = 8;
    private static final int COLUMN_PROXY = 9;

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
    private boolean validateLinksEnabled = false;
    private static final String VALIDATELINKS_ACTION_ENABLED_PROPERTY = "validateLinksEnabled";


    private JXTable table;
    private static String[] states;

    public ContentPanel(ApplicationContext context, ManagerDirector director) {
        this.context = context;
        this.director = director;
        this.manager = director.getDataManager();
        this.setName("contentPanel");

        readStates();

        Swinger.initActions(this, context);
        initComponents();
        setActions();

        final MainApp app = (MainApp) (context.getApplication());
        app.getMainFrame().addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {
                Swinger.inputFocus(table);
            }

            public void windowLostFocus(WindowEvent e) {

            }
        });

        manager.getDownloadFiles().addListDataListener(this);
        manager.addPropertyChangeListener(this);

    }

    private void readStates() {
        final DownloadState[] downloadStates = DownloadState.values();
        states = new String[downloadStates.length];
        int i = 0;
        for (DownloadState state : downloadStates) {
            states[i++] = context.getResourceMap().getString(state.name());
        }

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
        return Swinger.getSelectedRows(table);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void deleteFileAction() {
//        if (!isCompleteWithFilesEnabled())
//            return;
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        final String s = getFileList(files);
        final int result;
        final boolean confirm = AppPrefs.getProperty(UserProp.CONFIRM_FILE_DELETE, UserProp.CONFIRM_FILE_DELETE_DEFAULT);
        final boolean showedDialog;
        if (s.isEmpty() || (!confirm)) {
            showedDialog = false;
            result = Swinger.RESULT_OK;
        } else {
            result = Swinger.getChoiceOKCancel("message.areyousuredelete", s);
            showedDialog = true;
        }

        if (result == Swinger.RESULT_OK) {
            for (DownloadFile file : files) {
                final File outputFile = file.getOutputFile();
                if (outputFile != null)
                    outputFile.delete();
            }
            removeSelected(files, indexes, showedDialog);
            if (indexes.length > 0) {
                Arrays.sort(indexes);
                renewSelection(new int[]{indexes[0]});
            } else selectFirstIfNoSelection();
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

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void openDirectoryAction() {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            OSDesktop.openFile(file.getOutputFile().getParentFile());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = RESUME_ACTION_ENABLED_PROPERTY)
    public void resumeAction() {
        if (isResumeActionEnabled())
            manager.resumeSelected(getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = PAUSE_ACTION_ENABLED_PROPERTY)
    public void pauseAction() {
        manager.pauseSelected(getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = CANCEL_ACTION_ENABLED_PROPERTY)
    public void cancelAction() {
        if (!isCancelActionEnabled())
            return;
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        final String s = getFileList(files);
        final int result;
        final boolean confirm = AppPrefs.getProperty(UserProp.CONFIRM_FILE_DELETE, UserProp.CONFIRM_FILE_DELETE_DEFAULT);
        final boolean deleteFiles;
        if (confirm && !s.isEmpty()) {
            result = Swinger.getChoiceYesNoCancel("message.areyousuredelete", s);
            deleteFiles = result == Swinger.RESULT_YES;
        } else {
            result = Swinger.RESULT_YES;
            deleteFiles = true;
        }

        if (result == Swinger.RESULT_YES || result == Swinger.RESULT_NO) {
            manager.cancelSelected(getSelectedRows(), deleteFiles);
        }
    }

    private String getFileList(List<DownloadFile> files) {
        final StringBuilder builder = new StringBuilder();
        for (DownloadFile file : files) {
            if (file.getOutputFile() != null && file.getOutputFile().exists())
                builder.append('\n').append(Utils.shortenFileName(file.getOutputFile(), 60));
        }

        return builder.toString();
    }

    @org.jdesktop.application.Action(enabledProperty = REMOVECOMPLETED_ACTION_ENABLED_PROPERTY)
    public void removeCompletedAction() {
        final int[] rows = getSelectedRows();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.removeCompleted();
        renewSelection(rows);
        selectionModel.setValueIsAdjusting(false);
    }

    private void selectFirstIfNoSelection() {
        final int[] rows = getSelectedRows();
        if (rows.length == 0) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (getVisibleRowCount() > 0)
                        table.getSelectionModel().setSelectionInterval(0, 0);
                }
            });
        }
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
        scrollToVisible(true);
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void removeSelectedAction() {
        final int[] indexes = getSelectedRows();
        final List<DownloadFile> files = manager.getSelectionToList(indexes);
        removeSelected(files, indexes, false);
    }

    @org.jdesktop.application.Action(enabledProperty = NONEMPTY_ACTION_ENABLED_PROPERTY)
    public void removeInvalidLinksAction() {
        final int[] ints = getSelectedRows();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.removeInvalidLinks();
        renewSelection(ints);
        selectionModel.setValueIsAdjusting(false);
    }

    private void renewSelection(final int[] rows) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                for (int row : rows) {
                    final int i = table.convertRowIndexToView(row);
                    if (i != -1)
                        table.getSelectionModel().addSelectionInterval(i, i);
                }
                selectFirstIfNoSelection();
                scrollToVisible(true);
            }
        });
    }

    private void removeSelected(List<DownloadFile> files, int[] indexes, boolean quiet) {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);

        if (!quiet) {
            final boolean confirmRemove = AppPrefs.getProperty(UserProp.CONFIRM_REMOVE, UserProp.CONFIRM_REMOVE_DEFAULT);

            if (confirmRemove) {
                final boolean confirmDownloadingOnly = AppPrefs.getProperty(UserProp.CONFIRM_DOWNLOADING_REMOVE, UserProp.CONFIRM_DOWNLOADING_REMOVE_DEFAULT);
                boolean showDialog = false;
                if (confirmDownloadingOnly) {

                    for (DownloadFile file : files) {
                        if (DownloadState.DOWNLOADING == file.getState()) {
                            showDialog = true;
                            break;
                        }
                    }
                } else showDialog = true;

                if (showDialog) {
                    final int result = Swinger.getChoiceOKCancel("areYouSureYouWantToRemove");
                    if (result != Swinger.RESULT_OK)
                        return;
                }
            }
        }

        manager.removeSelected(files);
        selectionModel.setValueIsAdjusting(false);
        final int min = getArrayMin(indexes);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                int count = getVisibleRowCount();
                if (count > 0) {//pokud je neco videt
                    int index = count - 1; //vypoctem si posledni viditelnou
                    if (index > min) {
                        index = table.convertRowIndexToView(min); //pokud neni videt
                        if (index == -1)
                            index = count - 1;//nastavime posledni
                    }

                    selectionModel.addSelectionInterval(index, index);
                    scrollToVisible(true);
                }
            }
        });
    }

    private int getVisibleRowCount() {
        return table.getFilters().getOutputSize();
    }

    private int getArrayMin(final int[] indexes) {
        int min = Integer.MAX_VALUE;
        for (int i : indexes) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    @org.jdesktop.application.Action(enabledProperty = VALIDATELINKS_ACTION_ENABLED_PROPERTY)
    public void validateLinksAction() {
        manager.validateLinks(getSelectedRows());
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
            scrollToVisible(true);
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
        scrollToVisible(true);
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
        scrollToVisible(true);
    }

    private void scrollToVisible(final boolean up) {
        final int[] rows = table.getSelectedRows();
        final int length = rows.length;
        if (length > 0)
            table.scrollRowToVisible((up) ? rows[0] : rows[length - 1]);
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
        scrollToVisible(false);
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
        scrollToVisible(false);
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
            public void preferenceChange(final PreferenceChangeEvent evt) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        updateTableFromPreferences(evt);
                    }
                });
            }
        });
        updateGridLines();
    }

    private void updateTableFromPreferences(PreferenceChangeEvent evt) {
        if (UserProp.SHOW_GRID_HORIZONTAL.equals(evt.getKey()) || UserProp.SHOW_GRID_VERTICAL.equals(evt.getKey()))
            updateGridLines();
        else if (UserProp.SHOW_SERVICES_ICONS.equals(evt.getKey())) {
            table.repaint();
            table.packAll();
        }
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


    public boolean isValidateLinksEnabled() {
        return validateLinksEnabled;
    }

    public void setValidateLinksEnabled(boolean validateLinksEnabled) {
        boolean oldValue = this.validateLinksEnabled;
        this.validateLinksEnabled = validateLinksEnabled;
        firePropertyChange(VALIDATELINKS_ACTION_ENABLED_PROPERTY, oldValue, validateLinksEnabled);
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
        final boolean enabledCancel = this.manager.hasDownloadFilesStates(indexes, DownloadsActions.cancelEnabledStates);
        setCancelActionEnabled(enabledCancel);

        setSelectedEnabled(indexes.length > 0);

        final boolean allCompleted = this.manager.hasDownloadFilesStates(indexes, DownloadsActions.completedStates);

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
            setValidateLinksEnabled(false);
        } else {
            final boolean enabledResume = this.manager.hasAnyDownloadFilesStates(indexes, DownloadsActions.resumeEnabledStates);
            setResumeActionEnabled(enabledResume);

            final boolean enabledPause = this.manager.hasAnyDownloadFilesStates(indexes, DownloadsActions.pauseEnabledStates);
            setPauseActionEnabled(enabledPause);

            setValidateLinksEnabled(this.manager.hasAnyDownloadFilesStates(indexes, DownloadsActions.recheckExistingStates));

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
        table.setColumnSelectionAllowed(false);
        table.setSortable(false);
        //table.setColumnMargin(10);
        final WinampMoveStyle w = new WinampMoveStyle();
        table.addMouseListener(w);
        table.addMouseMotionListener(w);


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


        final ColorHighlighter highlighter = new ColorHighlighter(new HighlightPredicate() {
            public boolean isHighlighted(Component renderer, org.jdesktop.swingx.decorator.ComponentAdapter adapter) {
                return FileState.FILE_NOT_FOUND.equals(((DownloadFile) adapter.getValue(COLUMN_CHECKED)).getFileState());
            }
        }, new Color(0xFFD2D2), Color.BLACK);

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
        tableColumnModel.getColumn(COLUMN_PROGRESSBAR).setCellRenderer(new ProgressBarCellRenderer(context));
        final TableColumn column = tableColumnModel.getColumn(COLUMN_CHECKED);
        column.setMaxWidth(30);
        column.setWidth(30);
        column.setCellRenderer(new CheckedCellRenderer(context));

        ((TableColumnExt) tableColumnModel.getColumn(COLUMN_CHECKED)).setToolTipText(context.getResourceMap().getString("checkedColumnTooltip"));
        tableColumnModel.getColumn(COLUMN_PROGRESS).setCellRenderer(new ProgressCellRenderer());
        tableColumnModel.getColumn(COLUMN_STATE).setCellRenderer(new EstTimeCellRenderer(context));
        tableColumnModel.getColumn(COLUMN_SIZE).setCellRenderer(new SizeCellRenderer(context));
        tableColumnModel.getColumn(COLUMN_SPEED).setCellRenderer(new SpeedCellRenderer());
        tableColumnModel.getColumn(COLUMN_AVERAGE_SPEED).setCellRenderer(new AverageSpeedCellRenderer());
        tableColumnModel.getColumn(COLUMN_SERVICE).setCellRenderer(new ServiceCellRenderer(director));
        tableColumnModel.getColumn(COLUMN_PROXY).setCellRenderer(new ConnectionCellRenderer(context));

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    showPopMenu(e);
                else if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() >= 2) {
                    smartEnterAction();
                }
            }
        });

        table.packAll();

        final InputMap inputMap = table.getInputMap();
        final ActionMap actionMap = table.getActionMap();

        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_C), "copy");
        inputMap.put(SwingUtils.getCtrlAltKeyStroke(KeyEvent.VK_C), "copy");

        actionMap.put("copy", Swinger.getAction("copyContent"));

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_ESCAPE), "deleteItem");
        actionMap.put("deleteItem", Swinger.getAction("cancelAction"));

        inputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_DELETE), "deleteFileAction");
        actionMap.put("deleteFileAction", Swinger.getAction("deleteFileAction"));

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_SPACE), "resumeAction");
        actionMap.put("resumeAction", Swinger.getAction("resumeAction"));

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_ENTER), "smartEnterAction");
        actionMap.put("smartEnterAction", Swinger.getAction("smartEnterAction"));

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_HOME), "selectFirstRow");
        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_END), "selectLastRow");
        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_HOME), "selectFirstColumn");
        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_END), "selectLastColumn");

        inputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_HOME), "selectFirstRowExtendSelection");
        inputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_END), "selectLastRowExtendSelection");

        //inputMap.remove("find");
        inputMap.remove(KeyStroke.getKeyStroke("F8"));
        actionMap.remove("find");
        actionMap.remove("focusHeader");

//        paste();

        updateFilters();
        table.addHighlighter(highlighter);
    }

    @org.jdesktop.application.Action
    public void smartEnterAction() {
        if (this.isCompleteWithFilesEnabled())
            openFileAction();
        else if (isSelectedEnabled()) {
            try {
                downloadInformationAction();
            } catch (Exception ex) {
                LogUtils.processException(logger, ex);
            }
        }
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
        SwingUtils.copyToClipboard(builder.toString().trim(), this);
    }

    private void showPopMenu(MouseEvent e) {
        int[] selectedRows = getSelectedRows();//vraci model
        ListSelectionModel selectionModel = table.getSelectionModel();
        int rowNumber = table.rowAtPoint(e.getPoint());//vraci view
        if (rowNumber != -1) {
            if (selectedRows.length <= 0) {
                if (getVisibleRowCount() > 0) {
                    selectionModel.setSelectionInterval(rowNumber, rowNumber);//chce view
                }
            } else {
                Arrays.sort(selectedRows);
                if (Arrays.binarySearch(selectedRows, table.convertRowIndexToModel(rowNumber)) < 0) {
                    selectionModel.setValueIsAdjusting(true);
                    table.clearSelection();
                    selectionModel.setSelectionInterval(rowNumber, rowNumber);//chce view
                    selectionModel.setValueIsAdjusting(false);
                }
            }
        } else table.clearSelection();
        selectedRows = getSelectedRows();//znovu
        final JPopupMenu popup = new JPopupMenu();
        final ApplicationActionMap map = this.context.getActionMap();
        final MenuManager mm = director.getMenuManager();
        final JMenu removeMenu = mm.createMenu("removeMenu", map, "removeCompletedAction", "removeInvalidLinksAction", "removeSelectedAction");
        context.getResourceMap().injectComponent(removeMenu);
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
        popup.add(removeMenu);
        popup.addSeparator();
        popup.add(map.get("validateLinksAction"));
        popup.addSeparator();
        popup.add(map.get("selectAllAction"));
        popup.add(map.get("invertSelectionAction"));
//        final JMenu menu = new JMenu("Misc");
//        popup.add(menu);
        JMenu forceMenu = new JMenu();
        forceMenu.setName("forceDownloadMenu");
        context.getResourceMap().injectComponent(forceMenu);

//      menu.add(forceMenu);
        boolean forceEnabled = isSelectedEnabled() && this.manager.hasDownloadFilesStates(selectedRows, DownloadsActions.forceEnabledStates);
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
        scrollToVisible(true);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

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

    static int getProgress(final int max, final int timeToQueued) {
        return (int) ((timeToQueued / (float) max) * 100);
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

    static String stateToString(DownloadState state) {
        return states[state.ordinal()];
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


    private boolean isSelectedRow(int row) {
        final int[] ints = getSelectedRows();
        for (int i : ints) {
            if (i == row)
                return true;
        }
        return false;
    }

    private class WinampMoveStyle extends MouseAdapter {
        private boolean active = false;
        private int rowPosition;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (AppPrefs.getProperty(UserProp.DRAG_ON_RIGHT_MOUSE, UserProp.DRAG_ON_RIGHT_MOUSE_DEFAULT)) {
                    rowPosition = table.rowAtPoint(e.getPoint());
                    if (rowPosition != -1 && isSelectedRow(rowPosition)) {
                        table.setCursor(DragSource.DefaultMoveDrop);
                        active = true;
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (SwingUtilities.isRightMouseButton(e)) {
                if (active) {
                    table.setCursor(Cursor.getDefaultCursor());
                    active = false;
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (!active)
                return;
            final int position = table.rowAtPoint(e.getPoint());
            if (position == -1)
                return;
            if (rowPosition - position >= 1) {
                for (int i = rowPosition - position; i > 0; --i)
                    upAction();
                rowPosition = position;
            } else if (rowPosition - position <= -1) {
                for (int i = -1 * (rowPosition - position); i > 0; --i)
                    downAction();
                rowPosition = position;
            }
        }
    }


}
