package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.gui.actions.FileActions;
import cz.vity.freerapid.gui.actions.HelpActions;
import cz.vity.freerapid.gui.actions.ViewActions;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.basic.ComboPopup;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


/**
 * Sprava a vytvoreni hlavniho menu
 *
 * @author Vity
 */
public class MenuManager {
    /**
     * hlavni komponenta pro menu
     */
    private JMenuBar menuBar;
    private final ApplicationContext context;
    private final ManagerDirector director;
    /**
     *
     */
    private static final String SELECTED_TEXT_PROPERTY = "selectedText";
    private static final String MENU_SEPARATOR = "---";
    private final FileActions fileActions;
    private final static String RADIO = "*";
    private final static String RADIO2 = "*2";
    private final static String CHECKED = "!";
    private ViewActions viewActions;

//    private final static String AUTOSEARCH_PROPERTY = "autosearch";
//    private boolean isAutoSearchEnabled = false;

    public FileActions getFileActions() {
        return fileActions;
    }

///    private JMenu autosearchSubmenu = null;

    public MenuManager(final ApplicationContext context, ManagerDirector director) {
        super();
        this.context = context;
        this.director = director;
        fileActions = new FileActions();
        Swinger.initActions(fileActions, context);
        viewActions = new ViewActions();
        Swinger.initActions(viewActions, context);
        Swinger.initActions(new HelpActions(), context);
    }

    private void init() {

        final Object[] fileMenuActionNames = {
                "addNewLinksAction",
                MENU_SEPARATOR,
                "quit"
        };

        final Object[] downloadActionNames = {
                "downloadInformationAction",
                MENU_SEPARATOR,
                "openFileAction",
                "deleteFileAction",
                "openDirectoryAction",
                MENU_SEPARATOR,
                "resumeAction",
                "pauseAction",
                "cancelAction",
                MENU_SEPARATOR,
                "removeCompletedAction",
                MENU_SEPARATOR,
                "selectAllAction",
                "invertSelectionAction",
                MENU_SEPARATOR,
                "topAction",
                "upAction",
                "downAction",
                "bottomAction",
                MENU_SEPARATOR,
                "removeSelectedAction"
        };

        final Object[] optionsMenuActionNames = {
                createConnectionsMenu(),
                MENU_SEPARATOR,
                "options"
        };

        final Object[] viewMenuActionNames = {
                CHECKED + "showToolbar",
                CHECKED + "showStatusBar",
                MENU_SEPARATOR,
                CHECKED + "showCompletedAction",
                MENU_SEPARATOR,
                "showDownloadHistoryAction",
                //          "showSpeedMonitor"
        };


        final Object[] helpMenuActionNames = {
                "help",
                MENU_SEPARATOR,
                "checkForNewVersion",
                "visitHomepage",
                MENU_SEPARATOR,
                "about"
        };

        MenuSelectionManager.defaultManager().addChangeListener(
                new ChangeListener() {
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
                            if (!(menuElement.getComponent() instanceof JMenuItem))
                                continue;

                            JMenuItem menuItem = (JMenuItem) menuElement.getComponent();
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

        context.getActionMap().get("showCompletedAction").putValue(AbstractAction.SELECTED_KEY, viewActions.isShowCompleted());
//        final MainApp app = (MainApp) context.getApplication();

    }

    private JMenu createConnectionsMenu() {
        final JMenu useConnections = new JMenu();
        useConnections.setName("useConnectionsMenu");

        final ClientManager clientManager = director.getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getAvailableConnections();
        for (final ConnectionSettings settings : connectionSettingses) {
            final JCheckBoxMenuItem item = new JCheckBoxMenuItem(settings.toString());
            item.setSelected(settings.isEnabled());
            item.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    settings.setEnabled(!settings.isEnabled());
                    director.getDataManager().checkQueue();
                }
            });
            useConnections.add(item);
        }
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

    private static JMenu createMenu(String menuName, Object[] actionNames) {
        JMenu menu = new JMenu();
        return processMenu(menu, menuName, actionNames);
    }

    private static JMenu processMenu(JMenu menu, String menuName, Object[] actionNames) {
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
                menuItem.setAction(Swinger.getAction(action));
                menuItem.setToolTipText(null);//showed in statusbar
                menu.add(menuItem);
            }
        }
        return menu;
    }

    public static JPopupMenu processMenu(JPopupMenu menu, String menuName, Object[] actionNames) {
        menu.setName(menuName);
        for (Object actionName : actionNames) {
            if (MENU_SEPARATOR.equals(actionName)) {
                menu.addSeparator();
            } else {
                JMenuItem menuItem = new JMenuItem();
                menuItem.setAction(Swinger.getAction(actionName));
                menu.add(menuItem);
            }
        }
        return menu;
    }
}
