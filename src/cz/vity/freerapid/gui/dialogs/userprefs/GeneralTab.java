package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.list.ArrayListModel;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.*;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Utils;
import cz.vity.freerapid.utilities.os.OSCommand;
import cz.vity.freerapid.utilities.os.SystemCommander;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * @author ntoskrnl
 */
public class GeneralTab extends UserPreferencesTab {

    private static final String LNG_PROPERTY = "lngFakeProperty";

    GeneralTab(final UserPreferencesDialog dialog) {
        super(dialog);
    }

    @Override
    public void init() {
        bindLngCombobox();

        bind(checkAllowOnlyOneInstance, FWProp.ONEINSTANCE, FWProp.ONE_INSTANCE_DEFAULT);
        bind(checkForNewVersion, FWProp.NEW_VERSION, true);
        bind(checkContinueInterrupted, UserProp.DOWNLOAD_ON_APPLICATION_START, true);
        bind(checkUseHistory, UserProp.USE_HISTORY, UserProp.USE_HISTORY_DEFAULT);
        bind(checkAutoShutDownDisabledWhenExecuted, UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED, UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED_DEFAULT);
        bind(checkProcessFromTop, UserProp.START_FROM_TOP, UserProp.START_FROM_TOP_DEFAULT);
        bind(checkAutoStartDownloadsFromDecrypter, UserProp.AUTO_START_DOWNLOADS_FROM_DECRYPTER, UserProp.AUTO_START_DOWNLOADS_FROM_DECRYPTER_DEFAULT);
        bind(checkEnableDirectDownloads, UserProp.ENABLE_DIRECT_DOWNLOADS, UserProp.ENABLE_DIRECT_DOWNLOADS_DEFAULT);
        bind(checkForFileExistenceBeforeDownload, UserProp.TEST_FILE, UserProp.TEST_FILE_DEFAULT);
        bind(checkRecheckFilesOnStart, UserProp.RECHECK_FILES_ON_START, UserProp.RECHECK_FILES_ON_START_DEFAULT);
        bind(checkPreventStandbyWhileDownloading, UserProp.PREVENT_STANDBY_WHILE_DOWNLOADING, UserProp.PREVENT_STANDBY_WHILE_DOWNLOADING_DEFAULT);
        bind(comboFileExists, UserProp.FILE_ALREADY_EXISTS, UserProp.FILE_ALREADY_EXISTS_DEFAULT, "fileAlreadyExistsOptions");
        bind(comboRemoveCompleted, UserProp.REMOVE_COMPLETED_DOWNLOADS, UserProp.REMOVE_COMPLETED_DOWNLOADS_DEFAULT, "removeCompletedOptions");

        setAction(btnCreateDesktopShortcut, "createDesktopShortcut");
        setAction(btnCreateQuickLaunchShortcut, "createQuickLaunchShortcut");
        setAction(btnCreateStartMenuShortcut, "createStartMenuShortcut");
        setAction(btnCreateStartupShortcut, "createStartupShortcut");
    }

    private void bindLngCombobox() {
        final List<SupportedLanguage> languageList = Lng.getSupportedLanguages();
        Collections.sort(languageList);
        final ListModel listModel = new ArrayListModel<SupportedLanguage>(languageList);
        final LanguageAdapter adapter = new LanguageAdapter(LNG_PROPERTY, Lng.getSelectedLanguage());
        final SelectionInList<String> inList = new SelectionInList<String>(listModel, dialog.getModel().getBufferedModel(adapter));
        Bindings.bind(comboLng, inList);
        comboLng.setRenderer(new LanguageComboCellRenderer(dialog.getApp().getContext().getResourceMap().getResourcesDir(), resourceMap));
    }

    @Action
    public void createDesktopShortcut() {
        createShortcut(OSCommand.CREATE_DESKTOP_SHORTCUT);
    }

    @Action
    public void createStartMenuShortcut() {
        createShortcut(OSCommand.CREATE_STARTMENU_SHORTCUT);
    }

    @Action
    public void createStartupShortcut() {
        createShortcut(OSCommand.CREATE_STARTUP_SHORTCUT);
    }

    @Action
    public void createQuickLaunchShortcut() {
        createShortcut(OSCommand.CREATE_QUICKLAUNCH_SHORTCUT);
    }

    private void createShortcut(final OSCommand command) {
        final ApplicationContext context = MainApp.getAContext();
        final SystemCommander utils = SystemCommanderFactory.getInstance().getSystemCommanderInstance(context);
        if (utils.isSupported(command)) {
            final boolean result = utils.createShortCut(command);
            if (!result)
                Swinger.showErrorMessage(resourceMap, "createShortCutFailed");
        } else {
            Swinger.showErrorMessage(context.getResourceMap(), "systemCommandNotSupported", command.toString().toLowerCase());
        }
    }

    @Override
    public void apply() {
        final SupportedLanguage lng = Lng.getSelectedLanguage();
        if (!lng.equals(comboLng.getSelectedItem())) {
            AppPrefs.storeProperty(FWProp.SELECTED_LANGUAGE, ((SupportedLanguage) comboLng.getSelectedItem()).getLanguageCode());
            AppPrefs.storeProperty(FWProp.SELECTED_COUNTRY, ((SupportedLanguage) comboLng.getSelectedItem()).getCountry());
            Swinger.showInformationDialog(resourceMap.getString("changeLanguageAfterRestart"));
        }
        AppPrefs.removeProperty(LNG_PROPERTY);
    }

    @Override
    public void cancel() {
        AppPrefs.removeProperty(LNG_PROPERTY);
    }

    @Override
    public void build(final CellConstraints cc) {
        JPanel panelApplicationSettings = new JPanel();
        JPanel panelShortcutsSettings = new JPanel();
        JPanel panelDownloadsSettings = new JPanel();

        checkForNewVersion = new JCheckBox();
        checkForNewVersion.setName("checkForNewVersion");
        checkAllowOnlyOneInstance = new JCheckBox();
        checkAllowOnlyOneInstance.setName("checkAllowOnlyOneInstance");
        checkUseHistory = new JCheckBox();
        checkUseHistory.setName("checkUseHistory");
        comboLng = new JComboBox();
        comboLng.setName("comboLng");
        JLabel labelLanguage = new JLabel();
        labelLanguage.setName("language");
        labelLanguage.setLabelFor(comboLng);

        btnCreateDesktopShortcut = new JButton();
        btnCreateStartMenuShortcut = new JButton();
        btnCreateQuickLaunchShortcut = new JButton();
        btnCreateStartupShortcut = new JButton();

        checkForFileExistenceBeforeDownload = new JCheckBox();
        checkForFileExistenceBeforeDownload.setName("checkForFileExistenceBeforeDownload");
        checkContinueInterrupted = new JCheckBox();
        checkContinueInterrupted.setName("checkContinueInterrupted");
        checkRecheckFilesOnStart = new JCheckBox();
        checkRecheckFilesOnStart.setName("checkRecheckFilesOnStart");
        checkProcessFromTop = new JCheckBox();
        checkProcessFromTop.setName("checkProcessFromTop");
        checkAutoStartDownloadsFromDecrypter = new JCheckBox();
        checkAutoStartDownloadsFromDecrypter.setName("checkAutoStartDownloadsFromDecrypter");
        checkEnableDirectDownloads = new JCheckBox();
        checkEnableDirectDownloads.setName("checkEnableDirectDownloads");
        checkAutoShutDownDisabledWhenExecuted = new JCheckBox();
        checkAutoShutDownDisabledWhenExecuted.setName("checkAutoShutDownDisabledWhenExecuted");
        checkPreventStandbyWhileDownloading = new JCheckBox();
        checkPreventStandbyWhileDownloading.setName("checkPreventStandbyWhileDownloading");
        checkPreventStandbyWhileDownloading.setEnabled(Utils.isWindows());
        JLabel labelIfFilenameExists = new JLabel();
        labelIfFilenameExists.setName("labelIfFilenameExists");
        comboFileExists = new JComboBox();
        comboRemoveCompleted = new JComboBox();
        comboRemoveCompleted.setName("comboRemoveCompleted");
        JLabel labelRemoveCompleted = new JLabel();
        labelRemoveCompleted.setLabelFor(comboRemoveCompleted);
        labelRemoveCompleted.setName("labelRemoveCompleted");

        this.setBorder(Borders.TABBED_DIALOG_BORDER);

        //======== panelApplicationSettings ========
        {
            panelApplicationSettings.setBorder(new TitledBorder(null, resourceMap.getString("panelApplicationSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelApplicationSettingsBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            ColumnSpec.decode("max(default;70dlu)"),
                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                    },
                    new RowSpec[]{
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC
                    }), panelApplicationSettings);

            panelApplicationSettingsBuilder.add(checkForNewVersion, cc.xyw(3, 1, 4));
            panelApplicationSettingsBuilder.add(checkAllowOnlyOneInstance, cc.xyw(3, 3, 4));
            panelApplicationSettingsBuilder.add(checkUseHistory, cc.xyw(3, 5, 4));
            panelApplicationSettingsBuilder.add(labelLanguage, cc.xyw(3, 7, 1));
            panelApplicationSettingsBuilder.add(comboLng, cc.xyw(5, 7, 1));
        }

        //======== panelShortcutsSettings ========
        {
            panelShortcutsSettings.setBorder(new TitledBorder(null, resourceMap.getString("panelShortcutsSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelApplicationSettingsBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            ColumnSpec.decode("max(pref;30dlu)"),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                    },
                    new RowSpec[]{
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                    }), panelShortcutsSettings);

            panelApplicationSettingsBuilder.add(btnCreateDesktopShortcut, cc.xy(2, 1));
            panelApplicationSettingsBuilder.add(btnCreateStartMenuShortcut, cc.xy(2, 3));
            panelApplicationSettingsBuilder.add(btnCreateQuickLaunchShortcut, cc.xy(2, 5));
            panelApplicationSettingsBuilder.add(btnCreateStartupShortcut, cc.xy(2, 7));
        }

        //======== panelDownloadsSettings ========
        {
            panelDownloadsSettings.setBorder(new TitledBorder(null, resourceMap.getString("panelDownloadsSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelDownloadsSettingsBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
                            new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                            FormFactory.DEFAULT_COLSPEC,
                            FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                            FormFactory.DEFAULT_COLSPEC,
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
                            FormFactory.LINE_GAP_ROWSPEC,
                            FormFactory.DEFAULT_ROWSPEC,
                            FormFactory.LINE_GAP_ROWSPEC,
                    }), panelDownloadsSettings);

            panelDownloadsSettingsBuilder.add(checkForFileExistenceBeforeDownload, cc.xywh(3, 1, 7, 1));
            panelDownloadsSettingsBuilder.add(checkContinueInterrupted, cc.xywh(3, 2, 7, 1));
            panelDownloadsSettingsBuilder.add(checkRecheckFilesOnStart, cc.xywh(3, 3, 7, 1));
            panelDownloadsSettingsBuilder.add(checkProcessFromTop, cc.xywh(3, 4, 7, 1));
            panelDownloadsSettingsBuilder.add(checkAutoStartDownloadsFromDecrypter, cc.xywh(3, 5, 7, 1));
            panelDownloadsSettingsBuilder.add(checkEnableDirectDownloads, cc.xywh(3, 6, 7, 1));
            panelDownloadsSettingsBuilder.add(checkAutoShutDownDisabledWhenExecuted, cc.xywh(3, 7, 7, 1));
            panelDownloadsSettingsBuilder.add(checkPreventStandbyWhileDownloading, cc.xywh(3, 8, 7, 1));
            panelDownloadsSettingsBuilder.add(labelIfFilenameExists, cc.xywh(3, 10, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
            panelDownloadsSettingsBuilder.add(comboFileExists, cc.xy(5, 10));
            panelDownloadsSettingsBuilder.add(labelRemoveCompleted, cc.xywh(7, 10, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
            panelDownloadsSettingsBuilder.add(comboRemoveCompleted, cc.xy(9, 10));
        }

        PanelBuilder thisBuilder = new PanelBuilder(new FormLayout(
                new ColumnSpec[]{
                        new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                        FormFactory.DEFAULT_COLSPEC,
                },
                new RowSpec[]{
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                        FormFactory.RELATED_GAP_ROWSPEC,
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
                        FormFactory.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                }), this);

        thisBuilder.add(panelApplicationSettings, cc.xy(1, 1));
        thisBuilder.add(panelShortcutsSettings, cc.xy(3, 1));
        thisBuilder.add(panelDownloadsSettings, cc.xyw(1, 3, 3));
    }

    private JCheckBox checkForNewVersion;
    private JCheckBox checkAllowOnlyOneInstance;
    private JCheckBox checkUseHistory;
    private JComboBox comboLng;
    private JButton btnCreateDesktopShortcut;
    private JButton btnCreateStartMenuShortcut;
    private JButton btnCreateQuickLaunchShortcut;
    private JButton btnCreateStartupShortcut;
    private JCheckBox checkForFileExistenceBeforeDownload;
    private JCheckBox checkContinueInterrupted;
    private JCheckBox checkRecheckFilesOnStart;
    private JCheckBox checkProcessFromTop;
    private JCheckBox checkAutoStartDownloadsFromDecrypter;
    private JCheckBox checkEnableDirectDownloads;
    private JCheckBox checkAutoShutDownDisabledWhenExecuted;
    private JCheckBox checkPreventStandbyWhileDownloading;
    private JComboBox comboFileExists;
    private JComboBox comboRemoveCompleted;

    private final static class LanguageComboCellRenderer extends DefaultListCellRenderer {
        private String path;
        private ResourceMap map;

        private LanguageComboCellRenderer(String resourceDir, ResourceMap map) {
            this.map = map;
            this.path = (resourceDir + map.getString("flagsPath")).trim();
        }

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            if (value == null)
                value = list.getModel().getElementAt(index);
            final SupportedLanguage lng = (SupportedLanguage) value;

            assert lng != null;

            String s = lng.getIcon();
            if (s == null)
                s = map.getString("blank.gif");
            final URL resource = map.getClassLoader().getResource(path + s);
            final Component component = super.getListCellRendererComponent(list, lng.getName(), index, isSelected, cellHasFocus);
            if (resource != null) {
                this.setIcon(new ImageIcon(resource));
            }
            this.getAccessibleContext().setAccessibleDescription(lng.getName());
            return component;
        }
    }

}
