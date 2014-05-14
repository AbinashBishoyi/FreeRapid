package cz.vity.freerapid.swing;

import cz.vity.freerapid.core.*;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class TrayIconSupport implements PropertyChangeListener {
    private final static Logger logger = Logger.getLogger(TrayIconSupport.class.getName());
    private TrayIcon trayIcon;
    private boolean enabled;
    private WindowAdapter windowAdapter;
    private static final String TITLE_PROPERTY = "title";
    private String toolTip;

    public TrayIconSupport() {
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (FWProp.SHOW_TRAY.equals(evt.getKey())) {
                    setVisibleByDefault();
                }
            }
        });
    }

    public synchronized void setVisible(boolean visible) {
        if (visible && !isEnabled()) {
            enable();
        } else if (isEnabled()) disable();
    }

    public synchronized void setVisibleByDefault() {
        setVisible(AppPrefs.getProperty(FWProp.SHOW_TRAY, true));
    }

    private synchronized void enable() {
        if (!SystemTray.isSupported()) {
            logger.log(Level.WARNING, "Cannot enable tray icon - Tray icon is not supported on this system");
            return;
        }
        final MainApp app = MainApp.getInstance(MainApp.class);
        final ApplicationContext context = app.getContext();
        final ResourceMap map = context.getResourceMap();

        final JFrame frame = app.getMainFrame();

        windowAdapter = new WindowAdapter() {
            @Override
            public void windowIconified(WindowEvent e) {
                if (minimizeToTray())
                    frame.setVisible(false);
            }
        };

        frame.addWindowListener(windowAdapter);

        if (minimizeToTray() && frame.getExtendedState() == JFrame.ICONIFIED)
            frame.setVisible(false);

        Image image = (Utils.isWindows()) ? Swinger.getResourceMap().getImageIcon("trayIconImageWin").getImage() : frame.getIconImage();
        frame.addPropertyChangeListener(TITLE_PROPERTY, this);
        MouseAdapter mouseListener = new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (e.getClickCount() == 2) {
                        windowPlay(frame, true);
                    } else if (e.getClickCount() == 1) {
                        windowPlay(frame, false);
                    }
                    e.consume();
                }
            }
        };

        PopupMenu popup = buildPopmenu(app, map);

        final Font font = frame.getFont().deriveFont((float) 11);
        popup.setFont(font);
        this.trayIcon = new TrayIcon(image, frame.getTitle(), popup);

        trayIcon.setImageAutoSize(AppPrefs.getProperty(UserProp.TRAY_ICON_AUTOIMAGESIZE, UserProp.TRAY_ICON_AUTOIMAGESIZE_DEFAULT));
        trayIcon.addMouseListener(mouseListener);
        try {
            SystemTray tray = SystemTray.getSystemTray();
            tray.add(trayIcon);
            setEnabled(true);
        } catch (AWTException e) {
            logger.log(Level.WARNING, "Cannot enable Tray icon - Tray icon is not supported on this system");
        }
    }

    private boolean minimizeToTray() {
        return AppPrefs.getProperty(FWProp.SHOW_TRAY, true) && AppPrefs.getProperty(FWProp.MINIMIZE_TO_TRAY, false);
    }

    private void windowPlay(final JFrame frame, boolean toFront) {
        int state = frame.getExtendedState();
        if ((state & JFrame.ICONIFIED) == 1) {
            frame.setExtendedState(state &= ~Frame.ICONIFIED);
            frame.setVisible(true);
            frame.toFront();
        } else {
            if (!toFront) {
                // Set the iconified bit
                state |= Frame.ICONIFIED;
                // Iconify the frame
                frame.setExtendedState(state);
            }
        }
    }

    private void windowRestore(final JFrame frame) {
        int state = frame.getExtendedState();
        frame.setExtendedState(state &= ~Frame.ICONIFIED);
        frame.setVisible(true);
        frame.toFront();
    }

    private PopupMenu buildPopmenu(final MainApp app, ResourceMap map) {
        PopupMenu popup = new PopupMenu();

        MenuItem defaultItem = new MenuItem(map.getString("trayQuit"));
        MenuItem restoreItem = new MenuItem(map.getString("trayRestore"));

        final CheckboxMenuItem clipboardMonitoring = new CheckboxMenuItem(map.getString("monitorClipboardActionTray"));
        clipboardMonitoring.setState(AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT));
        clipboardMonitoring.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                final boolean b = clipboardMonitoring.getState();
                AppPrefs.storeProperty(UserProp.CLIPBOARD_MONITORING, b);
            }
        });

        final CheckboxMenuItem quietMode = new CheckboxMenuItem(map.getString("quietModeActionTray"));
        quietMode.setState(QuietMode.getInstance().isEnabled());
        quietMode.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                final boolean b = quietMode.getState();
                QuietMode.getInstance().setEnabled(b);
            }
        });

        final CheckboxMenuItem hideWhenMinimizedItem = new CheckboxMenuItem(map.getString("trayHideWhenMinimized"));
        hideWhenMinimizedItem.setState(AppPrefs.getProperty(FWProp.MINIMIZE_TO_TRAY, false));

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (FWProp.MINIMIZE_TO_TRAY.equals(evt.getKey())) {
                    hideWhenMinimizedItem.setState(AppPrefs.getProperty(FWProp.MINIMIZE_TO_TRAY, false));
                } else if (UserProp.CLIPBOARD_MONITORING.equals(evt.getKey())) {
                    clipboardMonitoring.setState(AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT));
                } else if (UserProp.QUIET_MODE_ENABLED.equals(evt.getKey())) {
                    quietMode.setState(QuietMode.getInstance().isEnabled());
                }
            }
        });

        defaultItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                app.exit(e);
            }
        });

        restoreItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        windowRestore(app.getMainFrame());
                    }
                });
            }
        });

        hideWhenMinimizedItem.addItemListener(new ItemListener() {
            public void itemStateChanged(ItemEvent e) {
                final boolean b = hideWhenMinimizedItem.getState();
                AppPrefs.storeProperty(FWProp.MINIMIZE_TO_TRAY, b);
            }
        });

        popup.add(restoreItem);
        popup.addSeparator();
        popup.add(clipboardMonitoring);
        popup.add(quietMode);
        popup.addSeparator();
        popup.add(hideWhenMinimizedItem);
        popup.addSeparator();
        popup.add(defaultItem);
        return popup;
    }

    public synchronized void disable() {
        if (!SystemTray.isSupported()) {
            logger.log(Level.WARNING, "Cannot disable Tray icon - Tray icon is not supported on this system");
            return;
        }
        final SystemTray tray = SystemTray.getSystemTray();
        tray.remove(trayIcon);
        final MainApp app = MainApp.getInstance(MainApp.class);
        final JFrame frame = app.getMainFrame();
        windowRestore(frame);
        frame.removeWindowFocusListener(windowAdapter);
        frame.removePropertyChangeListener(TITLE_PROPERTY, this);
        trayIcon = null;
        setEnabled(false);
    }

    private boolean isEnabled() {
        return enabled;
    }

    private void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (toolTip != null)
            if (isEnabled() && TITLE_PROPERTY.equals(evt.getPropertyName()) && trayIcon != null) {
                final JFrame w = (JFrame) evt.getSource();
                trayIcon.setToolTip(w.getTitle());
            }
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
        if (isEnabled()) {
            trayIcon.setToolTip(toolTip);
        }
    }

    public void setImage(Image iconImage) {
        if (isEnabled()) {
            trayIcon.setImage(iconImage);
        }
    }

}
