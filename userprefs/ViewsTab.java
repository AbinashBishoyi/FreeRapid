package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.swing.LaF;
import cz.vity.freerapid.swing.LookAndFeels;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import java.util.logging.Logger;

/**
 * @author ntoskrnl
 */
public class ViewsTab extends UserPreferencesTab {

    private static final Logger logger = Logger.getLogger(ViewsTab.class.getName());
    private static final String LAF_PROPERTY = "lafFakeProperty";
    private LaF backupLaF;

    ViewsTab(final UserPreferencesDialog dialog) {
        super(dialog);
    }

    @Override
    public void init() {
        backupLaF = LookAndFeels.getInstance().getSelectedLaF();
        bindLaFCombobox();

        bind(checkDecoratedFrames, FWProp.DECORATED_FRAMES, false);
        bind(checkHideWhenMinimized, FWProp.MINIMIZE_TO_TRAY, false);
        bind(checkAnimateIcon, UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT);
        bind(checkShowTitle, UserProp.SHOWINFO_IN_TITLE, UserProp.SHOWINFO_IN_TITLE_DEFAULT);
        bind(checkShowHorizontalLinesInTable, UserProp.SHOW_GRID_HORIZONTAL, UserProp.SHOW_GRID_HORIZONTAL_DEFAULT);
        bind(checkShowVerticalLinesInTable, UserProp.SHOW_GRID_VERTICAL, UserProp.SHOW_GRID_VERTICAL_DEFAULT);
        bind(checkServiceAsIconOnly, UserProp.SHOW_SERVICES_ICONS, UserProp.SHOW_SERVICES_ICONS_DEFAULT);
        bind(checkSlimLinesInHistory, UserProp.SLIM_LINES_IN_HISTORY, UserProp.SLIM_LINES_IN_HISTORY_DEFAULT);
        bind(checkBringToFrontWhenPasted, UserProp.BRING_TO_FRONT_WHEN_PASTED, UserProp.BRING_TO_FRONT_WHEN_PASTED_DEFAULT);
        bind(checkCloseToTray, FWProp.MINIMIZE_ON_CLOSE, FWProp.MINIMIZE_ON_CLOSE_DEFAULT);
        bind(checkShowToolbarText, UserProp.SHOW_TEXT_TOOLBAR, UserProp.SHOW_TEXT_TOOLBAR_DEFAULT);

        ValueModel valueModel = bind(checkShowIconInSystemTray, FWProp.SHOW_TRAY, true);
        PropertyConnector.connectAndUpdate(valueModel, checkAnimateIcon, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, checkCloseToTray, "enabled");
        PropertyConnector.connectAndUpdate(valueModel, checkHideWhenMinimized, "enabled");

        setAction(btnApplyLookAndFeel, "applyLookAndFeelAction");
    }

    private void bindLaFCombobox() {
        final LookAndFeels lafs = LookAndFeels.getInstance();
        final ListModel listModel = new ArrayListModel<LaF>(lafs.getAvailableLookAndFeels());
        final LookAndFeelAdapter adapter = new LookAndFeelAdapter(LAF_PROPERTY, lafs.getSelectedLaF());
        final SelectionInList<String> inList = new SelectionInList<String>(listModel, dialog.getModel().getBufferedModel(adapter));
        Bindings.bind(comboLaF, inList);
    }

    @Action
    public void applyLookAndFeelAction() {
        LaF laf = (LaF) comboLaF.getSelectedItem();
        applyLookAndFeel(laf);
    }

    private void applyLookAndFeel(LaF laf) {
        final LaF selectedLaF = LookAndFeels.getInstance().getSelectedLaF();
        if (laf != null) {
            if (!selectedLaF.equals(laf)) {
                updateLookAndFeel(laf);
            }
        }
    }

    private void updateLookAndFeel(LaF laf) {
        boolean successful;
        try {
            final LookAndFeels lafManager = LookAndFeels.getInstance();
            successful = lafManager.loadLookAndFeel(laf, true);
            lafManager.storeSelectedLaF(laf);
        } catch (Exception ex) {
            LogUtils.processException(logger, ex);
            Swinger.showErrorDialog(resourceMap, "changeLookAndFeelActionFailed", ex);
            successful = false;
        }
        if (successful) {
            Swinger.showInformationDialog(resourceMap.getString("message_changeLookAndFeelActionSet"));
        }
    }

    @Override
    public void apply() {
        applyLookAndFeelAction();
        AppPrefs.removeProperty(LAF_PROPERTY);
    }

    @Override
    public void cancel() {
        applyLookAndFeel(backupLaF);
        AppPrefs.removeProperty(LAF_PROPERTY);
    }

    @Override
    public void build(final CellConstraints cc) {
        JPanel panelAppearance = new JPanel();
        JPanel panelSystemTray = new JPanel();

        comboLaF = new JComboBox();
        JLabel labelLaF = new JLabel();
        labelLaF.setName("labelLaF");
        labelLaF.setLabelFor(comboLaF);
        btnApplyLookAndFeel = new JButton();
        btnApplyLookAndFeel.setName("btnApplyLookAndFeel");
        JLabel labelRequiresRestart2 = new JLabel();
        labelRequiresRestart2.setName("labelRequiresRestart2");
        checkDecoratedFrames = new JCheckBox();
        checkDecoratedFrames.setName("checkDecoratedFrames");
        checkShowHorizontalLinesInTable = new JCheckBox();
        checkShowHorizontalLinesInTable.setName("checkShowHorizontalLinesInTable");
        checkShowVerticalLinesInTable = new JCheckBox();
        checkShowVerticalLinesInTable.setName("checkShowVerticalLinesInTable");
        checkShowTitle = new JCheckBox();
        checkShowTitle.setName("checkShowTitle");
        checkShowToolbarText = new JCheckBox();
        checkShowToolbarText.setName("checkShowToolbarText");
        checkServiceAsIconOnly = new JCheckBox();
        checkServiceAsIconOnly.setName("checkServiceAsIconOnly");
        checkSlimLinesInHistory = new JCheckBox();
        checkSlimLinesInHistory.setName("checkSlimLinesInHistory");
        checkBringToFrontWhenPasted = new JCheckBox();
        checkBringToFrontWhenPasted.setName("checkBringToFrontWhenPasted");

        checkShowIconInSystemTray = new JCheckBox();
        checkShowIconInSystemTray.setName("checkShowIconInSystemTray");
        checkAnimateIcon = new JCheckBox();
        checkAnimateIcon.setName("checkAnimateIcon");
        checkCloseToTray = new JCheckBox();
        checkCloseToTray.setName("checkCloseToTray");
        checkHideWhenMinimized = new JCheckBox();
        checkHideWhenMinimized.setName("checkHideWhenMinimized");

        this.setBorder(Borders.TABBED_DIALOG_BORDER);

        //======== panelAppearance ========
        {
            panelAppearance.setBorder(new CompoundBorder(
                    new TitledBorder(null, resourceMap.getString("panelAppearance.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    Borders.DLU2_BORDER));

            PanelBuilder panelAppearanceBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.PREF_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.PREF_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.PREF_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                    },
                    new RowSpec[]{
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                    }), panelAppearance);

            panelAppearanceBuilder.add(labelLaF, cc.xy(3, 1));
            panelAppearanceBuilder.add(comboLaF, cc.xy(5, 1));
            panelAppearanceBuilder.add(btnApplyLookAndFeel, cc.xy(7, 1));
            panelAppearanceBuilder.add(labelRequiresRestart2, cc.xy(9, 1));
            panelAppearanceBuilder.add(checkDecoratedFrames, cc.xywh(3, 2, 7, 1));
            panelAppearanceBuilder.add(checkShowHorizontalLinesInTable, cc.xywh(3, 5, 7, 1));
            panelAppearanceBuilder.add(checkShowVerticalLinesInTable, cc.xywh(3, 6, 7, 1));
            panelAppearanceBuilder.add(checkShowTitle, cc.xywh(3, 7, 7, 1));
            panelAppearanceBuilder.add(checkShowToolbarText, cc.xywh(3, 8, 7, 1));
            panelAppearanceBuilder.add(checkServiceAsIconOnly, cc.xywh(3, 9, 7, 1));
            panelAppearanceBuilder.add(checkSlimLinesInHistory, cc.xywh(3, 10, 7, 1));
            panelAppearanceBuilder.add(checkBringToFrontWhenPasted, cc.xywh(3, 11, 7, 1));
        }

        //======== panel System tray ========
        {
            panelSystemTray.setBorder(new CompoundBorder(
                    new TitledBorder(null, resourceMap.getString("panelSystemTray.border"), TitledBorder.LEADING, TitledBorder.TOP),
                    Borders.DLU2_BORDER));

            PanelBuilder panelSystemTrayBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.PREF_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                    },
                    new RowSpec[]{
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,

                    }), panelSystemTray);

            panelSystemTrayBuilder.add(checkShowIconInSystemTray, cc.xy(3, 1));
            panelSystemTrayBuilder.add(checkAnimateIcon, cc.xy(5, 1));
            panelSystemTrayBuilder.add(checkCloseToTray, cc.xy(3, 2));
            panelSystemTrayBuilder.add(checkHideWhenMinimized, cc.xy(5, 2));
        }

        PanelBuilder thisBuilder = new PanelBuilder(new FormLayout(
                ColumnSpec.decodeSpecs("default:grow"),
                new RowSpec[]{
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        FormFactory.DEFAULT_ROWSPEC,
                        FormFactory.RELATED_GAP_ROWSPEC,
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                }), this);

        thisBuilder.add(panelAppearance, cc.xy(1, 1));
        thisBuilder.add(panelSystemTray, cc.xy(1, 3));
    }

    private JComboBox comboLaF;
    private JButton btnApplyLookAndFeel;
    private JCheckBox checkDecoratedFrames;
    private JCheckBox checkShowHorizontalLinesInTable;
    private JCheckBox checkShowVerticalLinesInTable;
    private JCheckBox checkShowTitle;
    private JCheckBox checkShowToolbarText;
    private JCheckBox checkServiceAsIconOnly;
    private JCheckBox checkSlimLinesInHistory;
    private JCheckBox checkBringToFrontWhenPasted;
    private JCheckBox checkShowIconInSystemTray;
    private JCheckBox checkAnimateIcon;
    private JCheckBox checkCloseToTray;
    private JCheckBox checkHideWhenMinimized;

}
