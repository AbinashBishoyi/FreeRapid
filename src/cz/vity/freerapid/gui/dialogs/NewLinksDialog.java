package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JDirectoryChooser;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.actions.URLTransferHandler;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.EditorPaneLinkDetector;
import cz.vity.freerapid.swing.models.RecentsFilesComboModel;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class NewLinksDialog extends AppDialog implements ClipboardOwner {
    private final static Logger logger = Logger.getLogger(NewLinksDialog.class.getName());
    private EditorPaneLinkDetector urlsArea;
    private boolean startPaused = false;
    private final DataManager dataManager;
    private final List<URL> removeList;
    private PluginsManager pluginsManager;

    public NewLinksDialog(ManagerDirector director, Frame owner) throws HeadlessException {
        super(owner, true);
        this.dataManager = director.getDataManager();
        this.pluginsManager = director.getPluginsManager();
        this.setName("NewLinksDialog");
        try {
            initComponents();
            build();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
        removeList = new ArrayList<URL>();
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

        //final ActionMap actionMap = getActionMap();
        setAction(okButton, "okBtnAction");
        setAction(cancelButton, "cancelBtnAction");
        setAction(btnPasteFromClipboard, "btnPasteFromClipboardAction");
        setAction(btnSelectPath, "btnSelectPathAction");
        setAction(btnStartPaused, "btnStartPausedAction");

        pack();
        locateOnOpticalScreenCenter(this);
    }

    @Action
    public void btnPasteFromClipboardAction() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        if (clipboard.isDataFlavorAvailable(DataFlavor.stringFlavor)) {
            try {
                final String data = (String) clipboard.getData(DataFlavor.stringFlavor);
                urlsArea.setURLs(data);
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
        final JDirectoryChooser directoryChooser = new JDirectoryChooser(comboPath.getEditor().getItem().toString());
        if (directoryChooser.showDialog(this, getResourceMap().getString("SelectDirectory")) != JDirectoryChooser.CANCEL_OPTION) {
            comboPath.getEditor().setItem(directoryChooser.getSelectedFile().getAbsolutePath());
            Swinger.inputFocus(comboPath);
        }
    }

    @Action
    public void btnStartPausedAction() {
        if (!validateStart())
            return;
        setResult(RESULT_OK);
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
        urlsArea.setPreferredSize(new Dimension(130, 100));
        //urlsArea.setURLs("http://www.filefactory.com/file/a3f880/n/KOW_-_Monica_divx_002");
        comboPath.setModel(new RecentsFilesComboModel(UserProp.LAST_USED_SAVED_PATH, true));
        AutoCompleteDecorator.decorate(comboPath);
        if (comboPath.getModel().getSize() > 0) {
            comboPath.setSelectedIndex(0);
        }

        this.setTransferHandler(new URLTransferHandler() {
            protected void doDropAction(List<URL> files) {
                urlsArea.setURLList(files);
            }
        });
    }

    @Action
    public void okBtnAction() {
        if (!validateStart())
            return;
        setResult(RESULT_OK);
        doClose();
    }

    private boolean validateStart() {
        List<URL> urlList = urlsArea.getURLs();
        if (urlList.isEmpty()) {
            Swinger.showErrorMessage(this.getResourceMap(), "noURLMessage");
            Swinger.inputFocus(this.urlsArea);
            return false;
        }
        StringBuilder builder = new StringBuilder();
        final List<String> stringList = urlsArea.getURLsAsStringList();
        final List<URL> notSupportedList = new ArrayList<URL>();


        final String dir = (String) comboPath.getEditor().getItem();
        if (dir == null || !new File(dir).isDirectory()) {
            Swinger.showErrorMessage(this.getResourceMap(), "noDirectoryMessage");
            btnSelectPathAction();
            return false;
        }

        for (URL url : urlList) {
            final String s = url.toExternalForm();
            if (!pluginsManager.isSupported(url)) {
                notSupportedList.add(url);
                builder.append('\n').append(s);
            }
        }


        if (!notSupportedList.isEmpty()) {
            final int result = Swinger.getChoiceYesNo(getResourceMap().getString("notSupportedByPlugins", builder.toString()));
            if (result == Swinger.RESULT_YES) {
                urlList.removeAll(notSupportedList);
                urlsArea.setText("");
                urlsArea.setURLList(urlList);
            } else {
                return false;
            }
        }

        if (urlList.isEmpty()) {
            Swinger.showErrorMessage(this.getResourceMap(), "noURLMessage");
            Swinger.inputFocus(this.urlsArea);
            return false;
        }

        final List<String> onTheList = new ArrayList<String>();
        removeList.clear();
        builder = new StringBuilder();
        synchronized (this.dataManager.getLock()) {
            final ArrayListModel<DownloadFile> files = this.dataManager.getDownloadFiles();
            for (DownloadFile file : files) {
                final URL urlAddress = file.getFileUrl();
                final String url = urlAddress.toString();
                onTheList.add(url);
            }
        }
        try {
            for (String s : stringList) {
                if (onTheList.contains(s)) {
                    removeList.add(new URL(s));
                    builder.append('\n').append(s);
                }
            }
        } catch (MalformedURLException e) {
            LogUtils.processException(logger, e);
        }

        if (!removeList.isEmpty()) {
            final int result = Swinger.getChoiceYesNoCancel(getResourceMap().getString("alreadyContainsMessage", builder.toString()));
            switch (result) {
                case Swinger.RESULT_NO:
                    return true;
                case Swinger.RESULT_YES:
                    removeList.clear();
                    return true;
                default:
                    removeList.clear();
                    return false;
            }
        }
        return true;
    }

    public List<DownloadFile> getDownloadFiles() {
        final File directory = getDirectory();
        final Collection<URL> urlList = urlsArea.getURLs();
        urlList.removeAll(removeList);
        final LinkedHashSet<URL> urlLinkedHashSet = new LinkedHashSet<URL>(urlList);
        List<DownloadFile> result = new ArrayList<DownloadFile>();
        final String description = this.descriptionArea.getText();
        for (URL url : urlLinkedHashSet) {
            result.add(new DownloadFile(url, directory, description));
        }
        return result;
    }

    private File getDirectory() {
        return new File(comboPath.getEditor().getItem().toString());
    }


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
            dialogPane.setBorder(Borders.DIALOG_BORDER);
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
                                FormFactory.DEFAULT_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.MIN_COLSPEC
                        },
                        new RowSpec[]{
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
                                FormFactory.LINE_GAP_ROWSPEC,
                                FormFactory.DEFAULT_ROWSPEC,
                                FormFactory.LINE_GAP_ROWSPEC,
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
                                FormFactory.PREF_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                                FormFactory.UNRELATED_GAP_COLSPEC,
                                FormFactory.PREF_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.PREF_COLSPEC,
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                FormFactory.DEFAULT_COLSPEC
                        },
                        RowSpec.decodeSpecs("fill:pref")), buttonBar);
                ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{5, 7, 9}});

                buttonBarBuilder.add(btnPasteFromClipboard, cc.xy(1, 1));
                buttonBarBuilder.add(okButton, cc.xy(5, 1));
                buttonBarBuilder.add(btnStartPaused, cc.xy(7, 1));
                buttonBarBuilder.add(cancelButton, cc.xy(9, 1));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    public boolean isStartPaused() {
        return startPaused;
    }

    private JComboBox comboPath;
    private JButton btnSelectPath;
    private JButton btnPasteFromClipboard;
    private JButton okButton;
    private JButton btnStartPaused;
    private JButton cancelButton;
    private JTextArea descriptionArea;

    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }

    public void setURLs(List<URL> urlList) {
        urlsArea.setURLList(urlList);
    }
}