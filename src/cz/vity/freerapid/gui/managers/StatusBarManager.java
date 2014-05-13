package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.MoveFileTask;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.swing.components.MemoryIndicator;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * Sprava a vytvoreni Statusbaru
 *
 * @author Vity
 */
public class StatusBarManager implements PropertyChangeListener, ListDataListener {
    private JXStatusBar statusbar;
    private JLabel infoLabel;
    private final ManagerDirector director;
    private final ApplicationContext context;
    private JProgressBar progress;
    private MainApp app;
    private ResourceMap resourceMap;

    private DataManager dataManager;
    private Image defaultIconImage;
    private Image downloadingIconImage;
    private TrayIconSupport trayIconSupport;

    /**
     * Konstruktor
     *
     * @param director spravce manazeru
     * @param context  aplikacni kontext
     */
    public StatusBarManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
        resourceMap = context.getResourceMap();
        dataManager = director.getDataManager();
        app = (MainApp) context.getApplication();
    }

    /**
     * Vraci komponentu statusbaru
     *
     * @return
     */
    public JXStatusBar getStatusBar() {
        if (statusbar == null) {
            statusbar = new JXStatusBar();

            trayIconSupport = app.getTrayIconSupport();
            defaultIconImage = resourceMap.getImageIcon("trayIconImage").getImage();
            downloadingIconImage = resourceMap.getImageIcon("downloadingIconImage").getImage();

            final Action action = context.getActionMap().get("showStatusBar");
            action.putValue(Action.SELECTED_KEY, true); //defaultni hodnota
            action.addPropertyChangeListener(new PropertyChangeListener() {
                //odchyt udalosti z akce pro zmenu viditelnosti statusbaru
                public void propertyChange(PropertyChangeEvent evt) {
                    if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                        setStatusBarVisible((Boolean) evt.getNewValue());
                    }
                }
            });
            statusbar.setName("statusbarPanel");
            infoLabel = new JLabel();
            progress = new JProgressBar();
            //  progress.setStringPainted(false);
            final MemoryIndicator indicator = new MemoryIndicator();
            indicator.setPreferredSize(new Dimension(100, 15));
            infoLabel.setPreferredSize(new Dimension(330, 15));
            progress.setPreferredSize(new Dimension(progress.getPreferredSize().width * 2 / 3, 15));
            progress.setVisible(false);
            director.getMenuManager().getMenuBar().addPropertyChangeListener("selectedText", this);
            statusbar.add(infoLabel, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(progress, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(Box.createGlue(), JXStatusBar.Constraint.ResizeBehavior.FILL);
            statusbar.add(indicator, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            //statusbar.add(Box.createGlue(), JXStatusBar.Constraint.ResizeBehavior.FILL);
            context.getTaskMonitor().addPropertyChangeListener(this);

            dataManager.getDownloadFiles().addListDataListener(this);

            dataManager.getProcessManager().addPropertyChangeListener("downloading", this);

            dataManager.addPropertyChangeListener("speed", this);
            dataManager.addPropertyChangeListener("completed", this);
            dataManager.addPropertyChangeListener("state", this);
            AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
                public void preferenceChange(PreferenceChangeEvent evt) {
                    if (UserProp.SHOWINFO_IN_TITLE.equals(evt.getKey())) {
                        updateInfoStatus();
                    } else if (UserProp.ANIMATE_ICON.equals(evt.getKey())) {
                        if (!AppPrefs.getProperty(UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT))
                            trayIconSupport.setImage(defaultIconImage);
                        else
                            updateIconAnimation();
                    }
                }
            });
            //final ContentPanel panel = director.getDockingManager().getContentPanel();
            updateInfoStatus();
        }
        return statusbar;
    }


    private void setStatusBarVisible(boolean visible) {
        getStatusBar().setVisible(visible);
        //AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, visible); //ulozeni uzivatelskeho nastaveni
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        if ("speed".equals(propertyName) || "completed".equals(propertyName)) {
            updateInfoStatus();
        } else if ("started".equals(propertyName) || "done".equals(propertyName) || "message".equals(propertyName)) {
            if (evt.getSource() instanceof MoveFileTask)
                updateProgress(evt);
        } else if ("selectedText".equals(propertyName)) {
            final String s = (String) evt.getNewValue();
            if ("cancel".equals(s)) {
                updateInfoStatus();
            } else
                infoLabel.setText(s);
        } else if ("downloading".equals(propertyName)) {
            if (AppPrefs.getProperty(UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT))
                updateIconAnimation();
        }
    }

    private void updateIconAnimation() {
        final int downloading = dataManager.getDownloading();
        if (downloading == 0) {
            trayIconSupport.setImage(defaultIconImage);
        } else {
            trayIconSupport.setImage(downloadingIconImage);
        }
    }

    private void updateProgress(PropertyChangeEvent evt) {
        final Task task = (Task) evt.getSource();
        final String propertyName = evt.getPropertyName();
        if ("done".equals(propertyName)) {
            progress.setVisible(false);
        } else if ("started".equals(propertyName)) {
            final PropertyChangeListener taskPCL = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if ("progress".equals(e.getPropertyName())) {
                        progress.setVisible(true);
                        progress.setIndeterminate(false);
                        progress.setValue((Integer) e.getNewValue());
                    } else if ("message".equals(e.getPropertyName())) {
                        progress.setString((String) e.getNewValue());
                    }
                }
            };
            task.addPropertyChangeListener(taskPCL);
        }
    }


    private void updateInfoStatus() {
        final int completed = dataManager.getCompleted();
        final int size = dataManager.getDownloadFiles().size();
        final int speed = dataManager.getCurrentAllSpeed();
        final TrayIconSupport trayIconSupport = app.getTrayIconSupport();
        final boolean showInFrameTitle = AppPrefs.getProperty(UserProp.SHOWINFO_IN_TITLE, UserProp.SHOWINFO_IN_TITLE_DEFAULT);
        final String speedFormatted = ContentPanel.bytesToAnother(speed);
        int downloading = dataManager.getDownloading();
        if (showInFrameTitle) {
            final String s;
            if (downloading == 0) {
                s = resourceMap.getString("frameTitleInfoNoDownloads", completed, size);
            } else {
                if (speed == 0) {
                    s = resourceMap.getString("frameTitleInfo0Speed", completed, size, speedFormatted);
                } else
                    s = resourceMap.getString("frameTitleInfo", completed, size, speedFormatted);
            }

            app.getMainFrame().setTitle(s);
        } else {
            app.getMainFrame().setTitle(resourceMap.getString("Application.title"));
        }

        if (size >= 0) {
            trayIconSupport.setToolTip(resourceMap.getString("tooltipTrayInfo", completed, size, speedFormatted));
            infoLabel.setText(resourceMap.getString("statusBarInfo", completed, size, speedFormatted));
        } else {
            trayIconSupport.setToolTip(resourceMap.getString("Application.title"));
            infoLabel.setText(resourceMap.getString("statusBarInfoIdle"));
        }

    }

    public void intervalAdded(ListDataEvent e) {
        updateInfoStatus();
    }

    public void intervalRemoved(ListDataEvent e) {
        updateInfoStatus();
    }

    public void contentsChanged(ListDataEvent e) {

    }
}
