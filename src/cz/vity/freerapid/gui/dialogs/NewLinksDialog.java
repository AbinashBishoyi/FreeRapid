package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JDirectoryChooser;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.FRDUtils;
import cz.vity.freerapid.gui.actions.URLTransferHandler;
import cz.vity.freerapid.gui.dialogs.filechooser.OpenSaveDialogFactory;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.EditorPaneLinkDetector;
import cz.vity.freerapid.swing.models.RecentsFilesComboModel;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class NewLinksDialog extends AppDialog implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(NewLinksDialog.class.getName());

    private final ManagerDirector director;
    private final DataManager dataManager;
    private final PluginsManager pluginsManager;
    private EditorPaneLinkDetector urlsArea;
    private boolean startPaused = false;

    public NewLinksDialog(ManagerDirector director, Frame owner) throws HeadlessException {
        super(owner, true);
        this.director = director;
        this.dataManager = director.getDataManager();
        this.pluginsManager = director.getPluginsManager();
        this.setName("NewLinksDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }


    @Override
    protected AbstractButton getBtnOK() {
        return okButton;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return cancelButton;
    }

    private void build() {
        inject();
        buildGUI();

        setAction(okButton, "okBtnAction");
        setAction(cancelButton, "cancelBtnAction");
        setAction(btnPasteFromClipboard, "btnPasteFromClipboardAction");
        setAction(btnSelectPath, "btnSelectPathAction");
        setAction(btnStartPaused, "btnStartPausedAction");

        final String desc = getResourceMap().getString("btnSelectPathAction.description");
        btnSelectPath.getAccessibleContext().setAccessibleName(desc);
        btnSelectPath.getAccessibleContext().setAccessibleDescription(desc);
        btnSelectPath.setToolTipText(desc);

        pack();
        locateOnOpticalScreenCenter(this);
    }

    @Action
    public void btnPasteFromClipboardAction() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                final String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                final List<URL> urlList = URLTransferHandler.textURIListToFileList(data, getApp().getManagerDirector().getPluginsManager(), false);
                final List<String> result = new LinkedList<String>();
                for (URL url : urlList) {
                    result.add(url.toExternalForm());
                }
                urlsArea.setURLs(result);
            } catch (Exception e) {
                //ignore
            }
        }
    }

    @Action
    public void cancelBtnAction() {
        doClose();
    }

    @Action
    public void btnSelectPathAction() {
        final String item = (String) comboPath.getEditor().getItem();
        File dir = null;
        final String directoryChooserTitle = getResourceMap().getString("directoryChooserTitle");
        if (AppPrefs.getProperty(UserProp.SELECT_DIR_DIALOG_OVERRIDE, UserProp.SELECT_DIR_DIALOG_OVERRIDE_DEFAULT)) {
            dir = OpenSaveDialogFactory.getInstance(MainApp.getAContext()).getDirChooser((item == null) ? null : new File(item), directoryChooserTitle);
        } else {
            final JDirectoryChooser directoryChooser = new JDirectoryChooser(item);
            directoryChooser.setDialogTitle(directoryChooserTitle);
            directoryChooser.setControlButtonsAreShown(true);
            if (directoryChooser.showDialog(this, getResourceMap().getString("SelectDirectory")) != JDirectoryChooser.CANCEL_OPTION) {
                dir = directoryChooser.getSelectedFile();
            }
        }
        if (dir != null) {
            comboPath.getEditor().setItem(dir.getAbsolutePath());
            Swinger.inputFocus(comboPath);
        }

    }

    @Action
    public void btnStartPausedAction() {
        if (!validateStart())
            return;
        setResult(RESULT_OK);
        saveLastSaveToPath();
        startPaused = true;
        doClose();
    }

    @Override
    public void doClose() {
        super.doClose();
        if (getModalResult() == RESULT_OK) {
            comboPath.addItem(comboPath.getSelectedItem());
        }
    }

    private void buildGUI() {
        new CompoundUndoManager(urlsArea);
        new CompoundUndoManager(descriptionArea);
        urlsArea.setPreferredSize(new Dimension(230, 100));
        urlsArea.getParent().setPreferredSize(new Dimension(230, 100));

        comboPath.setModel(new RecentsFilesComboModel(UserProp.LAST_USED_SAVED_PATH, true));

        comboPath.setSelectedItem(AppPrefs.getProperty(UserProp.LAST_COMBO_PATH, ""));

        descriptionArea.setFont(descriptionArea.getFont().deriveFont(11.0F));
        descriptionArea.setComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));

        final KeyAdapter adapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB) {
                    if (!e.isShiftDown())
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusNextComponent();
                    else
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().focusPreviousComponent();
                    e.consume();
                }
            }
        };
        urlsArea.addKeyListener(adapter);
        descriptionArea.addKeyListener(adapter);

        setTransferHandler(new URLTransferHandler(director) {
            @Override
            protected void doDropAction(List<URL> files) {
                urlsArea.setURLList(files);
            }
        });
    }

    @Action
    public void okBtnAction() {
        if (!validateStart())
            return;
        saveLastSaveToPath();
        setResult(RESULT_OK);
        doClose();
    }

    private void saveLastSaveToPath() {
        AppPrefs.storeProperty(UserProp.LAST_COMBO_PATH, comboPath.getSelectedItem().toString());
    }

    private boolean validateStart() {
        final List<URL> urlList = urlsArea.getURLs();
        if (urlList.isEmpty()) {
            Swinger.showErrorMessage(getResourceMap(), "noURLMessage");
            Swinger.inputFocus(urlsArea);
            return false;
        }

        //check directory where the downloads are going to be saved to
        final String dir = (String) comboPath.getEditor().getItem();
        if (dir == null || dir.isEmpty()) {
            Swinger.showErrorMessage(getResourceMap(), "noDirectoryMessage");
            btnSelectPathAction();
            return false;
        }
        final File outputDir = new File(Utils.isWindows() ? dir.trim() : dir);
        if (!outputDir.isDirectory()) {
            final int choiceYesNo = Swinger.getChoiceYesNo(getResourceMap().getString("directoryCreateMessage"));
            if (choiceYesNo == Swinger.RESULT_YES) {
                if (!outputDir.mkdirs()) {
                    Swinger.showErrorMessage(getResourceMap(), "directoryCreatingFailed", outputDir.getAbsolutePath());
                    btnSelectPathAction();
                    return false;
                } else {
                    if (!outputDir.isDirectory()) {
                        Swinger.showErrorMessage(getResourceMap(), "itsNotDirectory", outputDir.getAbsolutePath());
                        btnSelectPathAction();
                        return false;
                    }
                }
            } else {
                btnSelectPathAction();
                return false;
            }
        }

        //check if links are supported by plugins
        final List<URL> notSupportedList = new ArrayList<URL>();
        for (URL url : urlList) {
            if (!pluginsManager.isSupported(url)) {
                notSupportedList.add(url);
            }
        }
        if (!notSupportedList.isEmpty()) {
            final int result = Swinger.getChoiceYesNo(getResourceMap().getString("notSupportedByPlugins", urlListToString(notSupportedList)));
            if (result == Swinger.RESULT_YES) {
                final List<URL> newList = removeAll(urlList, notSupportedList);
                urlsArea.setText("");
                urlsArea.setURLList(newList);
                if (newList.isEmpty()) {
                    Swinger.showErrorMessage(getResourceMap(), "noURLMessage");
                    Swinger.inputFocus(urlsArea);
                    return false;
                }
            } else {
                return false;
            }
        }

        //check if links to be added already exist on the main list
        final List<URL> alreadyOnList = new ArrayList<URL>();
        synchronized (dataManager.getLock()) {
            for (final DownloadFile file : dataManager.getDownloadFiles()) {
                alreadyOnList.add(file.getFileUrl());
            }
        }
        final List<URL> removeList = getCommonElements(urlList, alreadyOnList);
        if (!removeList.isEmpty()) {
            final int result = Swinger.getChoiceYesNoCancel(getResourceMap().getString("alreadyContainsMessage", urlListToString(removeList)));
            switch (result) {
                case Swinger.RESULT_YES:
                    return true;
                case Swinger.RESULT_NO:
                    final List<URL> newList = removeAll(urlList, removeList);
                    urlsArea.setText("");
                    urlsArea.setURLList(newList);
                    if (newList.isEmpty()) {
                        Swinger.showErrorMessage(getResourceMap(), "noURLMessage");
                        Swinger.inputFocus(urlsArea);
                        return false;
                    }
                    return true;
                default:
                    return false;
            }
        }

        return true;
    }

    public List<DownloadFile> getDownloadFiles() {
        final File saveToDirectory = FRDUtils.getAbsRelPath(getDirectory());
        final String description = descriptionArea.getText();
        final List<DownloadFile> result = new ArrayList<DownloadFile>();
        for (final URL url : urlsArea.getURLs()) {
            result.add(new DownloadFile(url, saveToDirectory, description));
        }
        return result;
    }

    private File getDirectory() {
        final String o = (String) comboPath.getEditor().getItem();
        return new File((Utils.isWindows()) ? o.trim() : o);
    }

    public boolean isStartPaused() {
        return startPaused;
    }

    public void setURLs(List<URL> urlList) {
        urlsArea.setURLList(urlList);
    }

    /**
     * Workaround for performance issue concerning {@link URL#equals(Object) URL.equals()}.
     * Also removes duplicates.
     *
     * @param target This is where the items will be removed from.
     * @param toRemove Items to remove.
     * @return List containing the elements which exist in {@code target} but not in {@code toRemove}.
     */
    private static List<URL> removeAll(final List<URL> target, final List<URL> toRemove) {
        final Map<String, URL> map = new LinkedHashMap<String, URL>(target.size());
        for (final URL u : target) {
            map.put(u.toString(), u);
        }
        for (final URL u : toRemove) {
            map.remove(u.toString());
        }
        final List<URL> result = new ArrayList<URL>(map.size());
        result.addAll(map.values());
        return result;
    }

    /**
     * Returns the common elements in two Lists.
     *
     * @param list1 List 1
     * @param list2 List 2
     * @return List containing the elements which exist in both lists passed as arguments.
     */
    private static List<URL> getCommonElements(final List<URL> list1, final List<URL> list2) {
        final Map<String, URL> map = new LinkedHashMap<String, URL>(list1.size());
        final List<URL> commonElements = new ArrayList<URL>();
        for (final URL u : list1) {
            map.put(u.toString(), u);
        }
        for (final URL u : list2) {
            if (map.containsKey(u.toString())) {
                commonElements.add(u);
            }
        }
        return commonElements;
    }

    private String urlListToString(final List<URL> urls) {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0, n = Math.min(urls.size(), 20); i < n; i++) {
            builder.append('\n').append(urls.get(i));
        }
        if (urls.size() > 20) {
            builder.append('\n').append(getResourceMap().getString("andOtherURLs", urls.size() - 20));
        }
        return builder.toString();
    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
    }

    @SuppressWarnings({"deprecation"})
    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("NewLinksDialog");
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelLinks = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        urlsArea = ComponentFactory.getURLsEditorPane();
        JLabel labelSaveTo = new JLabel();
        comboPath = new JComboBox();
        btnSelectPath = new JButton();
        JLabel labelDescription = new JLabel();
        JScrollPane scrollPane2 = new JScrollPane();
        descriptionArea = ComponentFactory.getTextArea();
        JXButtonPanel buttonBar = new JXButtonPanel();
        btnPasteFromClipboard = new JButton();
        okButton = new JButton();
        btnStartPaused = new JButton();
        cancelButton = new JButton();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(Borders.DIALOG);
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {

                //---- labelLinks ----
                labelLinks.setName("labelLinks");
                labelLinks.setLabelFor(urlsArea);

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(urlsArea);
                }

                //---- labelSaveTo ----
                labelSaveTo.setName("labelSaveTo");
                labelSaveTo.setLabelFor(comboPath);

                //---- comboPath ----
                comboPath.setEditable(true);

                //---- btnSelectPath ----
                btnSelectPath.setName("btnSelectPath");

                //---- labelDescription ----
                labelDescription.setName("labelDescription");
                labelDescription.setLabelFor(descriptionArea);

                //======== scrollPane2 ========
                {
                    scrollPane2.setViewportView(descriptionArea);
                }

                PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.DEFAULT_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                FormSpecs.MIN_COLSPEC
                        },
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(55)), FormSpec.DEFAULT_GROW)
                        }), contentPanel);

                contentPanelBuilder.add(labelLinks, cc.xy(1, 1));
                contentPanelBuilder.add(scrollPane1, cc.xywh(1, 3, 5, 1));
                contentPanelBuilder.add(labelSaveTo, cc.xy(1, 5));
                contentPanelBuilder.add(comboPath, cc.xy(3, 5));
                contentPanelBuilder.add(btnSelectPath, cc.xy(5, 5));
                contentPanelBuilder.add(labelDescription, cc.xy(1, 7));
                contentPanelBuilder.add(scrollPane2, cc.xywh(3, 7, 3, 1));
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

                //---- btnPasteFromClipboard ----
                btnPasteFromClipboard.setName("btnPasteFromClipboard");

                //---- okButton ----
                okButton.setName("okButton");

                //---- btnStartPaused ----
                btnStartPaused.setName("btnStartPaused");

                //---- cancelButton ----
                cancelButton.setName("cancelButton");

                PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                        new ColumnSpec[]{
                                FormSpecs.PREF_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormSpecs.UNRELATED_GAP_COLSPEC,
                                FormSpecs.PREF_COLSPEC,
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                ColumnSpec.decode("max(pref;50dlu)"),
                                FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                ColumnSpec.decode("max(pref;50dlu)"),
                        },
                        RowSpec.decodeSpecs("fill:pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{5, 9}});

                buttonBarBuilder.add(btnPasteFromClipboard, cc.xy(1, 1));
                buttonBarBuilder.add(okButton, cc.xy(5, 1));
                buttonBarBuilder.add(btnStartPaused, cc.xy(7, 1));
                buttonBarBuilder.add(cancelButton, cc.xy(9, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
    }

    private JComboBox comboPath;
    private JButton btnSelectPath;
    private JButton btnPasteFromClipboard;
    private JButton okButton;
    private JButton btnStartPaused;
    private JButton cancelButton;
    private JTextArea descriptionArea;

}