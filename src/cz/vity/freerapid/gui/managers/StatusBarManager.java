package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.tasks.MoveFileTask;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.swing.components.MemoryIndicator;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

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

    /**
     * Konstruktor
     *
     * @param director spravce manazeru
     * @param context  aplikacni kontext
     */
    public StatusBarManager(ManagerDirector director, ApplicationContext context) {
        this.director = director;
        this.context = context;
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
            director.getDataManager().getDownloadFiles().addListDataListener(this);
            director.getDataManager().addPropertyChangeListener("speed", this);
            director.getDataManager().addPropertyChangeListener("completed", this);
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
        if ("started".equals(propertyName) || "done".equals(propertyName) || "message".equals(propertyName)) {
            if (evt.getSource() instanceof MoveFileTask)
                updateProgress(evt);
        } else if ("selectedText".equals(propertyName)) {
            final String s = (String) evt.getNewValue();
            if ("cancel".equals(s)) {
                updateInfoStatus();
            } else
                infoLabel.setText(s);
        } else if ("completed".equals(propertyName) || "speed".equals(propertyName)) {
            updateInfoStatus();
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
        final DataManager dataManager = director.getDataManager();
        final int completed = dataManager.getCompleted();
        final int size = dataManager.getDownloadFiles().size();
        final int speed = dataManager.getCurrentAllSpeed();
        final TrayIconSupport trayIconSupport = app.getTrayIconSupport();
        if (size >= 0) {
            final String speedFormatted = ContentPanel.bytesToAnother(speed);
            trayIconSupport.setToolTip(String.format("FreeRapid Downloader\n\nComplete downloads %d of %d\nCurrent speed: %s/s</html>", completed, size, speedFormatted));
            infoLabel.setText(String.format("Complete downloads %d of %d - Current speed: %s/s", completed, size, speedFormatted));
        } else {
            trayIconSupport.setToolTip(app.getMainFrame().getTitle());
            infoLabel.setText("Ready");
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
