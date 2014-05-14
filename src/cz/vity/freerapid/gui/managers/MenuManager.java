package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.beans.PropertyConnector;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.actions.FileActions;
import cz.vity.freerapid.gui.actions.HelpActions;
import cz.vity.freerapid.gui.actions.OptionsActions;
import cz.vity.freerapid.gui.actions.ViewActions;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.binding.BindUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;


/**
 * Sprava a vytvoreni hlavniho menu
 *
 * @author Vity
 */
public class MenuManager {
    public final static String MENU_SEPARATOR = "---";
    private final static String SELECTED_TEXT_PROPERTY = "selectedText";
    private final static String RADIO = "*";
    private final static String RADIO2 = "*2";
    private final static String CHECKED = "!";

    private final ApplicationContext context;
    private final ManagerDirector director;
    private final FileActions fileActions;
    private final OptionsActions optionsActions;
    private JMenuBar menuBar;
    private JMenu useConnections;

    public FileActions getFileActions() {
        return fileActions;
    }

    public MenuManager(final ApplicationContext context, ManagerDirector director) {
        super();
        this.context = context;
        this.director = director;
        fileActions = new FileActions(context);
        optionsActions = new OptionsActions();
        Swinger.initActions(fileActions, context);
        Swinger.initActions(optionsActions, context);
        Swinger.initActions(new ViewActions(), context);
        Swinger.initActions(new HelpActions(), context);
    }

    private void init() {

        final Object[] fileMenuActionNames = {
                "addNewLinksAction",
                MENU_SEPARATOR,
                "importLinksAction",
                "exportSelectedLinksAction",
                "exportAllLinksAction",
                MENU_SEPARATOR,
                "quit"
        };

        final Object[] removeActionNames = {
                "removeCompletedAction",
                "removeCompletedAndDeletedAction",
                "removeInvalidLinksAction",
                "removeSelectedAction",
        };

        final Object[] downloadActionNames = {
                "downloadInformationAction",
                MENU_SEPARATOR,
                "openFileAction",
                "deleteFileAction",
                "openDirectoryAction",
                "renameAction",
                MENU_SEPARATOR,
                "resumeAction",
                "pauseAction",
                "cancelAction",
                "retryAllErrorAction",
                MENU_SEPARATOR,
                createMenu("removeMenu", removeActionNames),
                MENU_SEPARATOR,
                "validateLinksAction",
                MENU_SEPARATOR,
                "selectAllAction",
                "invertSelectionAction",
                MENU_SEPARATOR,
                "sortbyNameAction",
                MENU_SEPARATOR,
                "topAction",
                "upAction",
                "downAction",
                "bottomAction",
        };

        final Object[] shutdownActionNames = {
                RADIO2 + "shutdownDisabledAction",
                MENU_SEPARATOR,
                RADIO2 + "shutdownQuitAction",
                RADIO2 + "shutdownHibernateAction",
                RADIO2 + "shutdownStandByAction",
                RADIO2 + "shutdownRebootAction",
                RADIO2 + "shutdownShutdownAction",
        };

        JMenu shutDownMenu = createMenu("autoShutdownMenu", shutdownActionNames);


        final Object[] optionsMenuActionNames = {
                "options",
                MENU_SEPARATOR,
                CHECKED + "monitorClipboardAction",
                MENU_SEPARATOR,
                CHECKED + "quietModeAction",
                MENU_SEPARATOR,
                CHECKED + "globalSpeedLimitAction",
                MENU_SEPARATOR,
                createConnectionsMenu(),
                MENU_SEPARATOR,
                shutDownMenu,
        };

        final Object[] viewMenuActionNames = {
                "showDownloadHistoryAction",
                MENU_SEPARATOR,
                CHECKED + "showCompletedAction",
                MENU_SEPARATOR,
                CHECKED + "showToolbar",
                CHECKED + "showStatusBar",
                //          "showSpeedMonitor"
        };


        final Object[] helpMenuActionNames = {
                "help",
                MENU_SEPARATOR,
                "showDemo",
                "visitHomepage",
                "visitForum",
                "checkPluginStatuses",
                MENU_SEPARATOR,
                "checkForNewPlugins",
                "checkForNewVersion",
                MENU_SEPARATOR,
                "openLogFile",
                "browseToLogFile",
                MENU_SEPARATOR,
                "paypalSupportAction",
                MENU_SEPARATOR,
                "about"
        };

        final boolean isWindows = Utils.isWindows();

        MenuSelectionManager.defaultManager().addChangeListener(
                new ChangeListener() {
                    @Override
                    public void stateChanged(ChangeEvent evt) {
                        // Get the selected menu or menu item
                        MenuSelectionManager msm = (MenuSelectionManager) evt.getSource();

                        MenuElement[] path = msm.getSelectedPath();
//                        for (MenuElement menuElement : path) {
//                            System.out.println("menuElement = " + menuElement);
//                        }
                        // To interpret path, see
                        // e813 Getting the Currently Selected Menu or Menu Item
                        final StringBuilder builder = new StringBuilder();
                        for (MenuElement menuElement : path) {
                            if (menuElement instanceof ComboPopup)
                                return;
                            final Component component = menuElement.getComponent();

                            if (component instanceof JMenuItem) {
                                JMenuItem menuItem = (JMenuItem) component;
                                final Action action = menuItem.getAction();
                                if (action == null)
                                    continue;
                                final String longDescription = (String) action.getValue(Action.LONG_DESCRIPTION);
                                if (longDescription != null) {
                                    if (builder.length() > 0)
                                        builder.append(" - ");
                                    builder.append(longDescription);
                                } else {
                                    final Object shortDescription = action.getValue(Action.SHORT_DESCRIPTION);
                                    if (shortDescription != null) {
                                        if (builder.length() > 0)
                                            builder.append(" - ");
                                        builder.append(shortDescription);
                                    }
                                }
                            } else if (component instanceof JComponent) {
                                final JComponent comp = (JComponent) component;
                                final String text = comp.getToolTipText();
                                if (text != null)
                                    builder.append(text);
                            }
                        }
                        menuBar.putClientProperty(SELECTED_TEXT_PROPERTY, (path.length != 0) ? builder.toString() : "cancel");

                    }
                }
        );
        menuBar.add(createMenu("fileMenu", fileMenuActionNames));
        menuBar.add(createMenu("downloadsMenu", downloadActionNames));
        menuBar.add(createMenu("connectionsMenu", optionsMenuActionNames));
        menuBar.add(createMenu("viewMenu", viewMenuActionNames));
        menuBar.add(createMenu("helpMenu", helpMenuActionNames));
        menuBar.putClientProperty(SELECTED_TEXT_PROPERTY, "");

        final ApplicationActionMap map = context.getActionMap();

        PropertyConnector.connectAndUpdate(BindUtils.getPrefsValueModel(UserProp.SHOW_COMPLETED, UserProp.SHOW_COMPLETED_DEFAULT), map.get("showCompletedAction"), "selected");
        PropertyConnector.connectAndUpdate(BindUtils.getPrefsValueModel(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT), map.get("globalLimitSpeedAction"), "selected");
        PropertyConnector.connectAndUpdate(BindUtils.getPrefsValueModel(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT), map.get("monitorClipboardAction"), "selected");
        PropertyConnector.connectAndUpdate(BindUtils.getPrefsValueModel(UserProp.QUIET_MODE_ENABLED, UserProp.QUIET_MODE_ENABLED_DEFAULT), map.get("quietModeAction"), "selected");

        final JRootPane rootPane = director.getMainFrame().getRootPane();
        rootPane.registerKeyboardAction(Swinger.getAction("showDownloadHistoryAction"), SwingUtils.getCtrlKeyStroke(KeyEvent.VK_H), JComponent.WHEN_IN_FOCUSED_WINDOW);

        if (isWindows)
            selectAutoShutDownMenu(map);

        if (AppPrefs.getProperty(UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED, UserProp.AUTOSHUTDOWN_DISABLED_WHEN_EXECUTED_DEFAULT)) {
            AppPrefs.storeProperty(UserProp.AUTOSHUTDOWN, UserProp.AUTOSHUTDOWN_DISABLED);
        }
    }

    private void selectAutoShutDownMenu(ApplicationActionMap map) {
        final String action = getSelectedShutDownAction();
        map.get(action).putValue(Action.SELECTED_KEY, Boolean.TRUE);
    }

    public String getSelectedShutDownAction() {
        final int property = AppPrefs.getProperty(UserProp.AUTOSHUTDOWN, UserProp.AUTOSHUTDOWN_DEFAULT);
        final String action;
        switch (property) {
            case UserProp.AUTOSHUTDOWN_CLOSE:
                action = "shutdownQuitAction";
                break;
            case UserProp.AUTOSHUTDOWN_HIBERNATE:
                action = "shutdownHibernateAction";
                break;
            case UserProp.AUTOSHUTDOWN_REBOOT:
                action = "shutdownRebootAction";
                break;
            case UserProp.AUTOSHUTDOWN_SHUTDOWN:
                action = "shutdownShutdownAction";
                break;
            case UserProp.AUTOSHUTDOWN_STANDBY:
                action = "shutdownStandByAction";
                break;
            default:
                action = "shutdownDisabledAction";
                break;
        }
        return action;
    }

//    private void disableOnLinux(ApplicationActionMap map, String... actions) {
//        final boolean b = Utils.isWindows();
//        if (b)
//            return;
//        for (String action : actions) {
//            map.get(action).setEnabled(false);
//        }
//    }

    private JMenu createConnectionsMenu() {
        useConnections = new JMenu();
        useConnections.setName("useConnectionsMenu");

        updateConnectionSettings(director.getClientManager().getAvailableConnections());

        return useConnections;
    }

//    private void setMenuEnabled(boolean enabled) {
//        final int itemCount = autosearchSubmenu.getItemCount();
//        for (int i = 0; i < itemCount; i++) {
//            final JMenuItem item = autosearchSubmenu.getItem(i);
//            if (item != null) {
//                final Action action = item.getAction();
//                if (action != null) {
//                    action.setEnabled(enabled);
//                }
//            }
//        }
//        autosearchSubmenu.setEnabled(enabled);
//    }

    public JMenuBar getMenuBar() {
        if (menuBar == null) {
            this.menuBar = new JMenuBar();
            init();
        }
        return menuBar;
    }

    private JMenu createMenu(String menuName, Object[] actionNames) {
        JMenu menu = new JMenu();
        return processMenu(menu, menuName, context.getActionMap(), actionNames);
    }

    public JMenu createMenu(String menuName, final ActionMap actionMap, Object... actionNames) {
        JMenu menu = new JMenu();
        return processMenu(menu, menuName, actionMap, actionNames);
    }

    private JMenu processMenu(JMenu menu, String menuName, final ActionMap actionMap, Object... actionNames) {
        menu.setName(menuName);
        ButtonGroup group = new ButtonGroup();
        ButtonGroup group2 = new ButtonGroup();
        for (Object actionName : actionNames) {
            if (actionName instanceof JMenu) { //pokud se jedna o submenu
                menu.add((JMenu) actionName);
            } else if (MENU_SEPARATOR.equals(actionName)) {
                menu.addSeparator();
                group = new ButtonGroup();
            } else {
                final JMenuItem menuItem;
                String action = (String) actionName;
                if (action.startsWith(RADIO2)) {
                    action = action.substring(RADIO2.length());
                    menuItem = new JRadioButtonMenuItem();
                    group2.add(menuItem);
                } else if (action.startsWith(RADIO)) {
                    action = action.substring(RADIO.length());
                    menuItem = new JRadioButtonMenuItem();
                    group.add(menuItem);
                } else if (action.startsWith(CHECKED)) {
                    action = action.substring(CHECKED.length());
                    menuItem = new JCheckBoxMenuItem();
                } else
                    menuItem = new JMenuItem();
                menuItem.setAction(actionMap.get(action));
                menuItem.setToolTipText(null);//showed in statusbar
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public JPopupMenu processMenu(JPopupMenu menu, String menuName, ActionMap actionMap, Object[] actionNames) {
        menu.setName(menuName);

        for (Object actionName : actionNames) {
            if (actionName instanceof JMenu) { //pokud se jedna o submenu
                menu.add((JMenu) actionName);
            } else if (MENU_SEPARATOR.equals(actionName)) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(actionMap.get(actionName));
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public void updateConnectionSettings(List<ConnectionSettings> connectionSettingses) {
        useConnections.removeAll();

        final JMenuItem refreshProxyList = new JMenuItem();
        refreshProxyList.setName("refreshProxyList");
        optionsActions.setRefreshProxyListActionEnabled(AppPrefs.getProperty(UserProp.USE_PROXY_LIST, UserProp.USE_PROXY_LIST_DEFAULT));
        refreshProxyList.setAction(context.getActionMap().get("refreshProxyList"));
        useConnections.add(refreshProxyList);
        useConnections.add(new JSeparator());

        for (final ConnectionSettings settings : connectionSettingses) {
            final JCheckBoxMenuItem item = new JCheckBoxMenuItem(settings.toString());
            final PropertyConnector propertyConnector = PropertyConnector.connect(settings, "enabled", item, "selected");
            propertyConnector.updateProperty2();
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    director.getClientManager().setConnectionEnabled(settings, !settings.isEnabled());
                    director.getDataManager().checkQueue();
                }
            });
            useConnections.add(item);
        }
    }

}
