package cz.vity.freerapid.gui.dialogs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JDirectoryChooser;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.components.EditorPaneLinkDetector;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class NewLinksDialog extends AppDialog {
    private final static Logger logger = Logger.getLogger(NewLinksDialog.class.getName());
    private EditorPaneLinkDetector urlsArea;
    private boolean startPaused = false;

    public NewLinksDialog(Frame owner) throws HeadlessException {
        super(owner, true);
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

    private void buildGUI() {
        AutoCompleteDecorator.decorate(comboPath);
        CompoundUndoManager undoManager = new CompoundUndoManager(urlsArea);
        urlsArea.setPreferredSize(new Dimension(130, 100));
        urlsArea.setURLs("http://rapidshare.com/files/132012635/Private_Triple_X_01.pdf");
        comboPath.addItem("c:\\");
        comboPath.setSelectedIndex(0);

//        urlsArea.getDocument().addUndoableEditListener(
//        new UndoableEditListener() {
//          public void undoableEditHappened(UndoableEditEvent e) {
//            undoManager.addEdit(e.getEdit());
//          }
//        });

    }

    @Action
    public void okBtnAction() {
        if (!validateStart())
            return;
        setResult(RESULT_OK);
        doClose();
    }

    private boolean validateStart() {
        if (urlsArea.getURLs().isEmpty()) {
            Swinger.showErrorMessage(this.getResourceMap(), "noURLMessage");
            Swinger.inputFocus(this.urlsArea);
            return false;
        }
        final String dir = (String) comboPath.getEditor().getItem();
        if (dir == null || !new File(dir).isDirectory()) {
            Swinger.showErrorMessage(this.getResourceMap(), "noDirectoryMessage");
            btnSelectPathAction();
            return false;
        }
        return true;
    }

    public List<DownloadFile> getDownloadFiles() {
        final File directory = getDirectory();
        final Collection<URL> urlList = urlsArea.getURLs();
        final LinkedHashSet<URL> urlLinkedHashSet = new LinkedHashSet<URL>(urlList);
        List<DownloadFile> result = new ArrayList<DownloadFile>();
        for (URL url : urlLinkedHashSet) {
            result.add(new DownloadFile(url, directory));
        }
        return result;
        //final URL[] urls = urlLinkedHashSet.toArray(new URL[urlLinkedHashSet.size()]);
    }

    private File getDirectory() {
        return new File(comboPath.getEditor().getItem().toString());
    }


    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        JLabel labelLinks = new JLabel();
        JScrollPane scrollPane1 = new JScrollPane();
        urlsArea = ComponentFactory.getURLsEditorPane();
        JLabel labelSaveTo = new JLabel();
        comboPath = new JComboBox();
        btnSelectPath = new JButton();
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
                                FormFactory.DEFAULT_ROWSPEC
                        }), contentPanel);

                contentPanelBuilder.add(labelLinks, cc.xy(1, 1));
                contentPanelBuilder.add(scrollPane1, cc.xywh(1, 3, 5, 1));
                contentPanelBuilder.add(labelSaveTo, cc.xy(1, 5));
                contentPanelBuilder.add(comboPath, cc.xy(3, 5));
                contentPanelBuilder.add(btnSelectPath, cc.xy(5, 5));
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
                                new ColumnSpec("max(pref;60px)"),
                                FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                                new ColumnSpec("max(pref;60px)"),
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
}