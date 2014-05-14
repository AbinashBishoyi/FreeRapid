package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.SearchField;
import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.gui.actions.URLTransferHandler;
import cz.vity.freerapid.gui.content.comparators.*;
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
import cz.vity.freerapid.swing.components.FindTableAction;
import cz.vity.freerapid.utilities.*;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.sort.SortController;
import org.jdesktop.swingx.table.TableColumnExt;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.TableCellEditor;
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
import java.util.*;
import java.util.List;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @version 1.0
 * @author Vity
 */
public class ContentPanel extends JPanel implements ListSelectionListener, ListDataListener, PropertyChangeListener, ClipboardOwner {
    private final static Logger logger = Logger.getLogger(ContentPanel.class.getName());

    private static final int COLUMN_CHECKED = 0;
    static final int COLUMN_NAME = 1;
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
    private boolean removeCompletedAndDeletedActionEnabled = false;
    private static final String REMOVECOMPLETEDANDDELETED_ACTION_ENABLED_PROPERTY = "removeCompletedAndDeletedActionEnabled";
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


    private boolean removeInvalidLinksActionEnabled = false;
    private static final String REMOVEINVALID_ACTION_ENABLED_PROPERTY = "removeInvalidLinksActionEnabled";

    private JXTable table;
    private static String[] states;
    private static final NumberFormat integerInstance = NumberFormat.getIntegerInstance();
    private static final NumberFormat numberFormatInstance = NumberFormat.getInstance();

    private static final String MOVEENABLED_ACTION_ENABLED_PROPERTY = "moveEnabled";
    private boolean moveEnabled = false;

    static {
        numberFormatInstance.setMaximumFractionDigits(2);
        numberFormatInstance.setMinimumFractionDigits(1);
    }

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
        openFilesInSystem(false);
    }

    private void openFilesInSystem(final boolean storeFile) {
        final int[] indexes = getSelectedRows();
        final List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            if (storeFile) {
                if (file.getStoreFile() != null && file.getStoreFile().length() > 0) {
                    OSDesktop.openFile(file.getStoreFile());
                }
            } else
                OSDesktop.openFile(file.getOutputFile());
        }
    }

    public int[] getSelectedRows() {
        return Swinger.getSelectedRows(table);
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
            int index = Swinger.convertRowIndexToView(table, resultIndex);
            selectionModel.setSelectionInterval(index, index + indexes.length - 1);
            scrollToVisible(true);
        }
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
                File outputFile = file.getOutputFile();
                if (outputFile != null) {
                    FileUtils.deleteFileWithRecycleBin(outputFile);
                }
                outputFile = file.getStoreFile();
                if (outputFile != null) {
                    FileUtils.deleteFileWithRecycleBin(outputFile);
                }
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
            OSDesktop.openDirectoryForFile(file.getOutputFile());
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void searchSubtitlesAction() {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        String subLanguage = AppPrefs.getProperty(UserProp.SEARCH_SUBTITLES_LANGUAGE, Locale.getDefault().getISO3Language());
        //http://en.wikipedia.org/wiki/List_of_ISO_639-2_codes
        //duplicate code problem
        Map<String, String> remapLang = new HashMap<String, String>();
        remapLang.put("ces", "cze");
        remapLang.put("fra", "fre");
        remapLang.put("nld", "dut");
        remapLang.put("fas", "per");
        remapLang.put("slk", "slo");
        if (remapLang.containsKey(subLanguage)) {
            subLanguage = remapLang.get(subLanguage);
        }
        final String weblanguage = Locale.getDefault().getLanguage();
        for (DownloadFile file : files) {
            final long fs = file.getFileSize();
            if (fs <= 0) {
                continue;
            }
            final String url = String.format("http://www.opensubtitles.org/%s/search/moviebytesize-%s/sublanguageid-%s", weblanguage, fs, subLanguage);
            Browser.openBrowser(url);
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

    @org.jdesktop.application.Action(enabledProperty = NONEMPTY_ACTION_ENABLED_PROPERTY)
    public void retryAllErrorAction() {
        if (isResumeActionEnabled())
            manager.retryAllError();
    }

    private String getFileList(final List<DownloadFile> files) {
        final List<DownloadFile> existingFiles = new ArrayList<DownloadFile>();
        for (DownloadFile file : files) {
            if (file.getOutputFile() != null && file.getDownloaded() > 0 && file.getOutputFile().exists()) {
                existingFiles.add(file);
            }
        }
        final StringBuilder builder = new StringBuilder();
        for (int i = 0, n = Math.min(existingFiles.size(), 20); i < n; i++) {
            builder.append('\n').append(Utils.shortenFileName(existingFiles.get(i).getOutputFile()));
        }
        if (existingFiles.size() > 20) {
            builder.append('\n').append(context.getResourceMap().getString("andOtherFiles", existingFiles.size() - 20));
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

    @org.jdesktop.application.Action(enabledProperty = REMOVECOMPLETEDANDDELETED_ACTION_ENABLED_PROPERTY)
    public void removeCompletedAndDeletedAction() {
        final int[] rows = getSelectedRows();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.removeCompletedAndDeleted();
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
                int index = Swinger.convertRowIndexToView(table, i);
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

    @org.jdesktop.application.Action(enabledProperty = REMOVEINVALID_ACTION_ENABLED_PROPERTY)
    public void removeInvalidLinksAction() {
        final int[] rows = getSelectedRows();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        manager.removeInvalidLinks();
        renewSelection(rows);
        selectionModel.setValueIsAdjusting(false);
    }

    private void renewSelection(final int[] rows) {
        table.clearSelection();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                final int visibleRowCount = getVisibleRowCount();
                if (visibleRowCount <= 0)
                    return;
                for (int row : rows) {
                    int i = Swinger.convertRowIndexToView(table, row);
                    if (i != -1) {
                        if (i >= visibleRowCount)
                            i = visibleRowCount - 1;
                        table.getSelectionModel().addSelectionInterval(i, i);
                    }
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
                    if (min < index) {
                        index = min;
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
        return table.getRowSorter().getViewRowCount();
    }

    private int getArrayMin(final int[] indexes) {
        int min = Integer.MAX_VALUE;
        for (int i : indexes) {
            i = Swinger.convertRowIndexToView(table, i);
            if (min > i && i != -1) {
                min = i;
            }
        }
        return min;//nejmensi z View
    }

    @org.jdesktop.application.Action(enabledProperty = VALIDATELINKS_ACTION_ENABLED_PROPERTY)
    public void validateLinksAction() {
        manager.validateLinks(getSelectedRows());
    }

    @org.jdesktop.application.Action(enabledProperty = MOVEENABLED_ACTION_ENABLED_PROPERTY)
    public void topAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveTop(indexes);
        selectionModel.setValueIsAdjusting(false);
        selectionModel.setSelectionInterval(0, indexes.length - 1);
        scrollToVisible(true);
    }

    @org.jdesktop.application.Action(enabledProperty = MOVEENABLED_ACTION_ENABLED_PROPERTY)
    public void upAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveUp(indexes);
//        table.getRowSorter().modelStructureChanged();
        selectionModel.clearSelection();
        for (int index : indexes) {
            index = Swinger.convertRowIndexToView(table, index);
            selectionModel.addSelectionInterval(index, index);
        }
        selectionModel.setValueIsAdjusting(false);
        scrollToVisible(true);
    }

    private void scrollToVisible(final boolean up) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final int[] rows = table.getSelectedRows();
                final int length = rows.length;
                if (length > 0) {
                    table.scrollRowToVisible((up) ? rows[0] : rows[length - 1]);
                }
            }
        });
    }

    @org.jdesktop.application.Action(enabledProperty = MOVEENABLED_ACTION_ENABLED_PROPERTY)
    public void downAction() {
        final ListSelectionModel selectionModel = table.getSelectionModel();
        selectionModel.setValueIsAdjusting(true);
        final int[] indexes = getSelectedRows();
        manager.moveDown(indexes);
//        table.getRowSorter().modelStructureChanged();
        selectionModel.clearSelection();
        for (int index : indexes) {
            index = Swinger.convertRowIndexToView(table, index);
            selectionModel.addSelectionInterval(index, index);
        }
        selectionModel.setValueIsAdjusting(false);
        scrollToVisible(false);
    }

    @org.jdesktop.application.Action(enabledProperty = MOVEENABLED_ACTION_ENABLED_PROPERTY)
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
            Browser.openBrowser(file.getFileUrl().toExternalForm().replaceAll("%23", "#"));
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


    @SuppressWarnings({"unchecked"})
    public void updateFilters() {
        final SortController rowSorter = (SortController) table.getRowSorter();

        if (!AppPrefs.getProperty(UserProp.SHOW_COMPLETED, true)) {
            rowSorter.setRowFilter(new StateFilter());
        } else rowSorter.setRowFilter(null);
    }

    private boolean isCancelledExisting() {
        final int[] indexes = getSelectedRows();
        final java.util.List<DownloadFile> files = manager.getSelectionToList(indexes);
        for (DownloadFile file : files) {
            final File outputFile = file.getOutputFile();
            if (!(outputFile.exists() && outputFile.length() == file.getFileSize() && file.getFileSize() > 0))
                return false;
        }
        return true;
    }

    private static class StateFilter extends RowFilter<Object, Object> {

        @Override
        public boolean include(Entry entry) {
            DownloadFile file = (DownloadFile) entry.getValue(COLUMN_STATE);
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

    public boolean isRemoveCompletedAndDeletedActionEnabled() {
        return removeCompletedAndDeletedActionEnabled;
    }

    public void setRemoveCompletedAndDeletedActionEnabled(boolean removeCompletedAndDeletedActionEnabled) {
        boolean oldValue = this.removeCompletedAndDeletedActionEnabled;
        this.removeCompletedAndDeletedActionEnabled = removeCompletedAndDeletedActionEnabled;
        firePropertyChange(REMOVECOMPLETEDANDDELETED_ACTION_ENABLED_PROPERTY, oldValue, removeCompletedAndDeletedActionEnabled);
    }

    public boolean isRemoveInvalidLinksActionEnabled() {
        return removeInvalidLinksActionEnabled;
    }

    public void setRemoveInvalidLinksActionEnabled(boolean removeInvalidLinksActionEnabled) {
        boolean oldValue = this.removeInvalidLinksActionEnabled;
        this.removeInvalidLinksActionEnabled = removeInvalidLinksActionEnabled;
        firePropertyChange(REMOVEINVALID_ACTION_ENABLED_PROPERTY, oldValue, removeInvalidLinksActionEnabled);
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

    public boolean isMoveEnabled() {
        return moveEnabled;
    }

    public void setMoveEnabled(boolean moveEnabled) {
        boolean oldValue = this.moveEnabled;
        this.moveEnabled = moveEnabled;
        firePropertyChange(MOVEENABLED_ACTION_ENABLED_PROPERTY, oldValue, moveEnabled);
    }

    public boolean isNonEmptyEnabled() {
        return nonEmptyEnabled;
    }

    public void setNonEmptyEnabled(boolean nonEmptyEnabled) {
        boolean oldValue = this.nonEmptyEnabled;
        this.nonEmptyEnabled = nonEmptyEnabled;
        firePropertyChange(NONEMPTY_ACTION_ENABLED_PROPERTY, oldValue, nonEmptyEnabled);
    }

    private boolean isSorted() {
        if (table.isSortable()) {
            final List<? extends RowSorter.SortKey> keys = table.getRowSorter().getSortKeys();
            if (!keys.isEmpty()) {
                for (RowSorter.SortKey key : keys) {
                    if (key.getSortOrder() != SortOrder.UNSORTED)
                        return true;
                }
            }
        }
        return false;
    }

    private void updateActions() {
        final int[] indexes = getSelectedRows();
        final boolean enabledCancel = this.manager.hasDownloadFilesStates(indexes, DownloadsActions.cancelEnabledStates);
        setCancelActionEnabled(enabledCancel);

        setSelectedEnabled(indexes.length > 0);
        setMoveEnabled(isSelectedEnabled() && !isSorted());

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

            final boolean enabledPause = this.manager.isPausable(indexes);

            setPauseActionEnabled(enabledPause);

            setValidateLinksEnabled(this.manager.hasAnyDownloadFilesStates(indexes, DownloadsActions.recheckExistingStates));

            setCompletedWithFilesEnabled(false);
        }
        setNonEmptyEnabled(table.getModel().getRowCount() > 0);
    }

    @SuppressWarnings({"unchecked"})
    private void initTable() {
        table.setName("mainTable");
        final String[] columns = (String[]) context.getResourceMap().getObject("mainTableColumns", String[].class);
        table.setModel(new CustomTableModel(manager.getDownloadFiles(), columns));
        table.setAutoCreateColumnsFromModel(false);

//        table.setHorizontalScrollEnabled(false);
        table.setAutoResizeMode(AppPrefs.getProperty(UserProp.TABLE_COLUMNS_RESIZE, UserProp.TABLE_COLUMNS_RESIZE_DEFAULT));
        table.setEditable(true);
        table.setAutoStartEditOnKeyStroke(false);
        table.setColumnControlVisible(true);
        table.setColumnSelectionAllowed(false);
        final DefaultRowSorter sorter = (DefaultRowSorter) table.getRowSorter();

        //table.setColumnMargin(10);
        final WinampMoveStyle w = new WinampMoveStyle();
        table.addMouseListener(w);
        table.addMouseMotionListener(w);
        table.setSortsOnUpdates(true);

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
        colName.setCellEditor(new RenameFileNameEditor());
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
        tableColumnModel.getColumn(COLUMN_SPEED).setCellRenderer(new SpeedCellRenderer(context));
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

        final boolean b = AppPrefs.getProperty(UserProp.TABLE_SORTABLE, UserProp.TABLE_SORTABLE_DEFAULT);
        table.setSortable(b);
        if (b) {
            final SortController rowSorter = (SortController) table.getRowSorter();
            rowSorter.setSortOrderCycle(SortOrder.ASCENDING, SortOrder.DESCENDING, SortOrder.UNSORTED);
            ((DefaultRowSorter) rowSorter).setMaxSortKeys(1);
            sorter.setComparator(COLUMN_NAME, new NameColumnComparator());
            sorter.setComparator(COLUMN_AVERAGE_SPEED, new AvgSpeedColumnComparator());
            sorter.setComparator(COLUMN_CHECKED, new CheckedColumnComparator());
            sorter.setComparator(COLUMN_PROGRESS, new ProgressColumnComparator());
            sorter.setComparator(COLUMN_PROXY, new ConnectionColumnComparator());
            sorter.setComparator(COLUMN_SERVICE, new ServiceColumnComparator());
            sorter.setComparator(COLUMN_SIZE, new SizeColumnComparator());
            sorter.setComparator(COLUMN_SPEED, new SpeedColumnComparator());
            sorter.setComparator(COLUMN_STATE, new EstTimeColumnComparator());
            sorter.setComparator(COLUMN_PROGRESSBAR, new ProgressBarColumnComparator());
        }
        table.setUpdateSelectionOnSort(b);


        table.packAll();

        final InputMap inputMap = table.getInputMap();
        final ActionMap actionMap = table.getActionMap();

        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_K), "searchFieldAction");
        actionMap.put("searchFieldAction", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                final SearchField field = director.getToolbarManager().getSearchField();
                if (field.isVisible()) {
                    Swinger.inputFocus(field);
                }
            }
        });

        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_C), "copy");
        inputMap.put(SwingUtils.getCtrlAltKeyStroke(KeyEvent.VK_C), "copy");

        actionMap.put("copy", Swinger.getAction("copyContent"));

        if (AppPrefs.getProperty(UserProp.CANCEL_ON_ESCAPE, UserProp.CANCEL_ON_ESCAPE_DEFAULT)) {
            inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_ESCAPE), "deleteItem");
            actionMap.put("deleteItem", Swinger.getAction("cancelAction"));
        }

        inputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_DELETE), "deleteFileAction");
        actionMap.put("deleteFileAction", Swinger.getAction("deleteFileAction"));

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_SPACE), "resumeAction");
        actionMap.put("resumeAction", Swinger.getAction("resumeAction"));


        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_CONTEXT_MENU), "contextMenu");
        actionMap.put("contextMenu", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                final Point point = new Point();
                final Rectangle rect = table.getVisibleRect();

                if (Swinger.getSelectedRows(table).length > 0) {
                    final int row = table.getSelectionModel().getLeadSelectionIndex();
                    final Rectangle cellRect = table.getCellRect(row, 0, true);
                    cellRect.y += cellRect.height - 1;
                    if (cellRect.y > rect.y && rect.y + rect.height > cellRect.y)
                        point.y = cellRect.y;
                    else if (cellRect.y < rect.y)
                        point.y = rect.y;
                    else
                        point.y = rect.y + rect.height;
                    point.x = cellRect.x;
                } else {
                    point.x = table.getCellRect(0, 0, true).x;
                }
                showPopMenu(new MouseEvent(table, 0, 0, 0, point.x, point.y, 1, true));
            }
        });

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_ENTER), "smartEnterAction");
        actionMap.put("smartEnterAction", Swinger.getAction("smartEnterAction"));

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_HOME), "selectFirstRow");
        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_END), "selectLastRow");
        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_HOME), "selectFirstColumn");
        inputMap.put(SwingUtils.getCtrlKeyStroke(KeyEvent.VK_END), "selectLastColumn");

        inputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_HOME), "selectFirstRowExtendSelection");
        inputMap.put(SwingUtils.getShiftKeyStroke(KeyEvent.VK_END), "selectLastRowExtendSelection");

        inputMap.put(SwingUtils.getKeyStroke(KeyEvent.VK_F2), "renameAction");
        actionMap.put("renameAction", Swinger.getAction("renameAction"));

        //inputMap.remove("find");
        inputMap.remove(KeyStroke.getKeyStroke("F8"));
        actionMap.remove("find");
        actionMap.remove("focusHeader");

//        paste();

        updateFilters();
        table.addHighlighter(highlighter);
        new FindTableAction(Swinger.getResourceMap(), COLUMN_NAME) {
            protected Object getObject(int index, int column) {
                final DownloadFile downloadFile = (DownloadFile) table.getModel().getValueAt(index, column);
                if (downloadFile == null) {
                    logger.warning("Download File is null");
                    return null;
                }
                final String fn = downloadFile.getFileName();
                final String url = downloadFile.getFileUrl().toString();
                final String value;
                if (fn != null && !fn.isEmpty()) {
                    value = fn;
                } else {
                    value = url;
                }
                return value;
            }
        }.install(table);

    }

    @org.jdesktop.application.Action
    public void smartEnterAction() {
        if (AppPrefs.getProperty(UserProp.OPEN_INCOMPLETE_FILES, UserProp.OPEN_INCOMPLETE_FILES_DEFAULT) && isSelectedEnabled()) {
            openFilesInSystem(true);
            return;
        }
        if (this.isCompleteWithFilesEnabled() || (isSelectedEnabled() && isCancelledExisting())) {
            openFileAction();
        } else if (isSelectedEnabled()) {
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
    public void renameAction() {
        final int[] selectedRows = table.getSelectedRows();
        if (selectedRows.length == 0) {
            return;
        }

        final int selectedRow = selectedRows[0];
        final int selectedColumn = table.convertColumnIndexToView(COLUMN_NAME);
        final DownloadFile valueAt = (DownloadFile) table.getValueAt(selectedRow, selectedColumn);
        if (valueAt.getFileName() == null) {
            return;
        }
        final String backup = valueAt.getFileName();
        final File originalOuputFile = valueAt.getOutputFile();
        final boolean wasExisting = originalOuputFile.exists();
        final boolean result = table.editCellAt(selectedRow, selectedColumn);//takes UI rows, not model
        if (!result)
            return;
        final TableCellEditor cellEditor = table.getCellEditor();
        if (cellEditor != null) {
            cellEditor.addCellEditorListener(new CellEditorListener() {
                @Override
                public void editingStopped(ChangeEvent e) {
                    final RenameFileNameEditor source = (RenameFileNameEditor) e.getSource();
                    cellEditor.removeCellEditorListener(this);
                    if (wasExisting) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                renameFile(source, originalOuputFile, backup, selectedRow);
                            }
                        });
                    } else
                        scrollToVisible(true);
                }

                @Override
                public void editingCanceled(ChangeEvent e) {
                    cellEditor.removeCellEditorListener(this);
                }
            });
        }
    }

    /**
     * Change file name physically on disk
     * @param source editor
     * @param originalOuputFile original name before editing
     * @param backupFileName original file name
     * @param row index of selected row
     */
    private void renameFile(RenameFileNameEditor source, File originalOuputFile, String backupFileName, int selectedRow) {
        boolean succeeded;
        final DownloadFile resultDownloadFile = (DownloadFile) source.getCellEditorValue();
        final File out = resultDownloadFile.getOutputFile();

        if (out.exists()) {
            final int answer = Swinger.showOptionDialog(context.getResourceMap(), true, JOptionPane.QUESTION_MESSAGE, "confirmMessage", "targetFileAlreadyExists", new String[]{"message.button.overwrite", Swinger.MESSAGE_BTN_CANCEL_CODE}, out);
            if (answer == 0) {
                succeeded = FileUtils.deleteFileWithRecycleBin(out);
                if (succeeded) {
                    succeeded = originalOuputFile.renameTo(out);
                }
            } else {
                succeeded = false;//cancel rename change
            }
        } else {
            succeeded = originalOuputFile.renameTo(out);
        }
        if (!succeeded) {
            resultDownloadFile.setFileName(backupFileName);
            table.setValueAt(resultDownloadFile, selectedRow, table.convertColumnIndexToView(COLUMN_NAME));
        } else {
            scrollToVisible(true);
        }
    }

    @org.jdesktop.application.Action(enabledProperty = SELECTED_ACTION_ENABLED_PROPERTY)
    public void copyContent() {
        final java.util.List<DownloadFile> files = manager.getSelectionToList(getSelectedRows());
        StringBuilder builder = new StringBuilder();
        for (DownloadFile file : files) {
            builder.append(file.toString().replaceAll("%23", "#")).append('\n');
        }
        SwingUtils.copyToClipboard(builder.toString().trim(), this);
    }

    private void showPopMenu(MouseEvent e) {
        int[] selectedRows = getSelectedRows();//vraci model
        if (selectedRows.length == 0)
            return;
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
        final JMenu removeMenu = mm.createMenu("removeMenu", map, "removeCompletedAction", "removeCompletedAndDeletedAction", "removeInvalidLinksAction", "removeSelectedAction");
        boolean removeEnabled = map.get("removeCompletedAction").isEnabled() || map.get("removeCompletedAndDeletedAction").isEnabled() || map.get("removeInvalidLinksAction").isEnabled() || map.get("removeSelectedAction").isEnabled();
        removeMenu.setEnabled(removeEnabled);

        final ResourceMap rMap = context.getResourceMap();
        rMap.injectComponent(removeMenu);
        JMenu speedLimitMenu = new JMenu();
        speedLimitMenu.setName("speedLimitMenu");
        rMap.injectComponent(speedLimitMenu);

        popup.add(map.get("downloadInformationAction"));
        popup.addSeparator();
        popup.add(map.get("openFileAction"));
        popup.add(map.get("deleteFileAction"));
        popup.add(map.get("openDirectoryAction"));
        popup.add(map.get("renameAction"));
        popup.addSeparator();
        popup.add(map.get("resumeAction"));
        popup.add(map.get("pauseAction"));
        popup.add(map.get("cancelAction"));
        popup.addSeparator();
        popup.add(removeMenu);
        popup.addSeparator();
        popup.add(map.get("validateLinksAction"));
        popup.add(speedLimitMenu);
        popup.addSeparator();
        popup.add(map.get("selectAllAction"));
        popup.add(map.get("invertSelectionAction"));
//        final JMenu menu = new JMenu("Misc");
//        popup.add(menu);
        JMenu forceMenu = new JMenu();
        forceMenu.setName("forceDownloadMenu");
        rMap.injectComponent(forceMenu);

        final String[] speedStrings = AppPrefs.getProperty(UserProp.SPEED_LIMIT_SPEEDS, UserProp.SPEED_LIMIT_SPEEDS_DEFAULT).split(",");
        final List<Integer> speeds = new ArrayList<Integer>(speedStrings.length);
        for (String s : speedStrings) {
            try {
                int num = (s.trim().isEmpty()) ? Integer.MAX_VALUE : Integer.parseInt(s.trim());
                if (num == 0)
                    num = -1;
                if (num >= -1)
                    speeds.add(num);
            } catch (NumberFormatException e1) {
                //ignore
            }
        }
        final ButtonGroup group = new ButtonGroup();
        final List<DownloadFile> files = this.manager.getSelectionToList(selectedRows);
        if (!files.isEmpty() && !speeds.contains(files.get(0).getSpeedLimit())) {
            speeds.add(files.get(0).getSpeedLimit());
        }

        for (int limit : speeds) {
            if (limit == Integer.MAX_VALUE) {
                speedLimitMenu.addSeparator();
            } else {
                String stValue = (limit == -1) ? rMap.getString("unlimitedSpeed") : rMap.getString("limitSpeed", limit);
                final JRadioButtonMenuItem radio = new JRadioButtonMenuItem(new SpeedLimitAction(stValue, limit));
                group.add(radio);
                radio.setAlignmentX(LEFT_ALIGNMENT);
                speedLimitMenu.add(radio);
                if (!files.isEmpty()) {
                    radio.setSelected(files.get(0).getSpeedLimit() == limit);
                }
            }
        }

        boolean forceEnabled = isSelectedEnabled() && this.manager.hasDownloadFilesStates(selectedRows, DownloadsActions.forceEnabledStates);
        forceMenu.setEnabled(forceEnabled);
        final List<ConnectionSettings> connectionSettingses = director.getClientManager().getAvailableConnections();
        boolean anyEnabled = false;
        for (ConnectionSettings settings : connectionSettingses) {
            final ForceDownloadAction action = new ForceDownloadAction(settings);
            forceMenu.add(action);
            action.setEnabled(forceEnabled);
            if (settings.isEnabled())
                anyEnabled = true;
        }

        forceMenu.setEnabled(forceEnabled && anyEnabled);

        popup.addSeparator();
        popup.add(forceMenu);
        popup.addSeparator();
        popup.add(map.get("copyContent"));
        popup.add(map.get("openInBrowser"));
        if (AppPrefs.getProperty(UserProp.SEARCH_SUBTITLES_ENABLED, UserProp.SEARCH_SUBTITLES_ENABLED_DEFAULT)) {
            popup.add(map.get("searchSubtitlesAction"));
        }

        final MouseEvent event = SwingUtilities.convertMouseEvent(table, e, this);
        popup.show(this, event.getX(), event.getY());
    }


    private void initComponents() {

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setName("contentScrollPane");
        table = new JXTable();
        table.setName("mainTable");

        //======== this ========
        setLayout(new BorderLayout());

        //======== scrollPane ========
        {
            scrollPane.setViewportView(table);
        }
        add(scrollPane, BorderLayout.CENTER);
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
        final String propertyName = evt.getPropertyName();
        if ("completed".equals(propertyName)) {
            setRemoveCompletedActionEnabled(((Integer) evt.getNewValue()) > 0);
            setRemoveCompletedAndDeletedActionEnabled(((Integer) evt.getNewValue()) > 0);
        } else if ("notFound".equals(propertyName)) {
            setRemoveInvalidLinksActionEnabled(((Integer) evt.getNewValue()) > 0);
        }
    }

    public void selectAdded(final java.util.List<DownloadFile> files) {
        assert !files.isEmpty();
        final ListSelectionModel selectionModel = table.getSelectionModel();
        final int index = manager.getDownloadFiles().indexOf(files.get(0));
        final int viewIndex = Swinger.convertRowIndexToView(table, index);
        selectionModel.setSelectionInterval(viewIndex, viewIndex + files.size() - 1);
        scrollToVisible(true);
    }

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    public static int getProgress(DownloadFile downloadFile) {
        final long downloaded = downloadFile.getDownloaded();
        final long fileSize = downloadFile.getFileSize();
        if (downloaded <= 0 || fileSize <= 0)
            return 0;
        int i = (int) (((downloaded / (float) fileSize) * 100));
        if (i < 0) i = 0;
        else if (i > 100) i = 100;
        return i;
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
        if (bytes > (1024 * 1024 * 1024)) {
            return numberFormatInstance.format((float) bytes / (1024 * 1024 * 1024F)) + " GB";
        } else if (bytes > (1024 * 1024)) {
            return numberFormatInstance.format((float) bytes / (1024 * 1024F)) + " MB";
        } else if (bytes >= 1024) {
            return numberFormatInstance.format((float) bytes / (1024F)) + " kB";
        } else {
            return String.format("%s B", integerInstance.format(bytes));
        }
    }

    public static String stateToString(DownloadState state) {
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

    private class SpeedLimitAction extends AbstractAction {
        private final int value;

        public SpeedLimitAction(String stringValue, int value) {
            this.value = value;
            this.putValue(NAME, stringValue);
        }

        public void actionPerformed(ActionEvent e) {
            manager.setSpeedLimit(getSelectedRows(), value);
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
                if (!table.getRowSorter().getSortKeys().isEmpty())
                    return;
                if (AppPrefs.getProperty(UserProp.DRAG_ON_RIGHT_MOUSE, UserProp.DRAG_ON_RIGHT_MOUSE_DEFAULT)) {
                    rowPosition = table.rowAtPoint(e.getPoint());
                    if (rowPosition != -1 && isSelectedRow(rowPosition)) {
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
            try {
                table.setCursor(DragSource.DefaultMoveDrop);
            } catch (Throwable ex) {
                //ignore JDK bug http://bugtracker.wordrider.net/task/957
            }
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
