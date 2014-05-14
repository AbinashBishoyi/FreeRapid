package cz.vity.freerapid.gui.managers;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.BoundedRangeAdapter;
import com.jgoodies.binding.adapter.PreferencesAdapter;
import com.jgoodies.binding.beans.PropertyAdapter;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ConverterFactory;
import com.jgoodies.binding.value.DelayedReadValueModel;
import com.jgoodies.binding.value.ValueModel;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.gui.MyPreferencesAdapter;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.swing.TrayIconSupport;
import cz.vity.freerapid.swing.components.MemoryIndicator;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;
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

    private JLabel clipboardMonitoring;
    private MemoryIndicator indicator;
    private PropertyChangeListener taskPCL;

    private Task activeTask = null;
    private JSlider slider;

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


    public JXStatusBar getStatusBar() {
        if (statusbar == null) {
            statusbar = new JXStatusBar();

            trayIconSupport = app.getTrayIconSupport();
            defaultIconImage = (Utils.isWindows()) ? resourceMap.getImageIcon("trayIconImageWin").getImage() : resourceMap.getImageIcon("trayIconImage").getImage();
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

            clipboardMonitoring = new JLabel();
            clipboardMonitoring.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    final boolean b = AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT);
                    final Action action = app.getContext().getActionMap().get("monitorClipboardAction");
                    action.putValue(Action.SELECTED_KEY, !b);
                    action.actionPerformed(new ActionEvent(this, 0, ""));
                }
            });

            taskPCL = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if ("progress".equals(e.getPropertyName())) {
                        progress.setIndeterminate(false);
                        progress.setValue((Integer) e.getNewValue());
                    } else if ("message".equals(e.getPropertyName())) {
                        progress.setStringPainted(true);
                        final String s = (String) e.getNewValue();
                        progress.setString(s);
                        progress.setToolTipText(s);
                    }
                }
            };


            clipboardMonitoring.setName("labelClipboardMonitoring");
            resourceMap.injectComponent(clipboardMonitoring);


            statusbar.setName("statusbarPanel");
            infoLabel = new JLabel();
            progress = new JProgressBar();

            //  progress.setStringPainted(false);
            indicator = new MemoryIndicator();
            indicator.setPreferredSize(new Dimension(100, 15));
            infoLabel.setPreferredSize(new Dimension(345, 15));
            clipboardMonitoring.setPreferredSize(new Dimension(17, 15));
            progress.setPreferredSize(new Dimension(progress.getPreferredSize().width + 35, 15));
            progress.setVisible(false);
            director.getMenuManager().getMenuBar().addPropertyChangeListener("selectedText", this);
            statusbar.add(infoLabel, JXStatusBar.Constraint.ResizeBehavior.FIXED);

            final JPanel speedLimitPanel = new JPanel();
            speedLimitPanel.setLayout(new BoxLayout(speedLimitPanel, BoxLayout.LINE_AXIS));
            //speedLimitPanel.setSize(80, 15);
            speedLimitPanel.setPreferredSize(new Dimension(170, 15));
            speedLimitPanel.setBorder(null);
            final JCheckBox speedLimiterEnabled = new JCheckBox();
            speedLimiterEnabled.setName("speedLimiterEnabled");
            speedLimiterEnabled.setBorder(null);
            speedLimiterEnabled.setText(null);
            final ValueModel valueModel = bind(speedLimiterEnabled, UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT);
            speedLimitPanel.add(speedLimiterEnabled);
            final PreferencesAdapter speedAdapter = new PreferencesAdapter(AppPrefs.getPreferences(), UserProp.SPEED_LIMIT, UserProp.SPEED_LIMIT_DEFAULT);
            slider = new JSlider(new BoundedRangeAdapter(speedAdapter, 0, 0, 250));
            slider.setName("speedSlider");
            slider.setPreferredSize(new Dimension(100, 15));
            slider.setMaximumSize(new Dimension(100, 15));
            slider.setFont(slider.getFont().deriveFont(6F));
            slider.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 3));
            slider.setSnapToTicks(true);

            speedLimitPanel.add(slider);
            final JLabel labelSpeed = new JLabel();
            labelSpeed.setName("labelSpeed");
            labelSpeed.setLabelFor(slider);

            bindSpeedSlider(slider);

            Bindings.bind(labelSpeed, ConverterFactory.createStringConverter(speedAdapter, NumberFormat.getIntegerInstance()));
            labelSpeed.setAlignmentX(JLabel.RIGHT_ALIGNMENT);
            labelSpeed.setPreferredSize(new Dimension(32, 15));
            speedLimitPanel.add(labelSpeed);
            labelSpeed.setBackground(Color.green);
            final JLabel labelSpeedUnit = new JLabel();
            labelSpeedUnit.setLabelFor(slider);
            labelSpeedUnit.setName("labelSpeedUnit");
            speedLimitPanel.add(labelSpeedUnit);

            slider.setSize(45, 12);

            PropertyConnector.connectAndUpdate(valueModel, slider, "enabled");
            PropertyConnector.connectAndUpdate(valueModel, labelSpeed, "enabled");
            PropertyConnector.connectAndUpdate(valueModel, labelSpeedUnit, "enabled");

            statusbar.add(speedLimitPanel, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(progress, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(clipboardMonitoring, JXStatusBar.Constraint.ResizeBehavior.FIXED);
            statusbar.add(Box.createGlue(), JXStatusBar.Constraint.ResizeBehavior.FILL);
            //statusbar.add(Box.createGlue(), JXStatusBar.Constraint.ResizeBehavior.FILL);
            context.getTaskMonitor().addPropertyChangeListener(this);

            dataManager.getDownloadFiles().addListDataListener(this);

            dataManager.getProcessManager().addPropertyChangeListener("downloading", this);

            final DelayedReadValueModel delayedReadValueModel = new DelayedReadValueModel(new PropertyAdapter<DataManager>(dataManager, "speed"), 50, true);
            delayedReadValueModel.addPropertyChangeListener(new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent evt) {
                    final String s = evt.getPropertyName();
                    System.out.println("s2 = " + s);
                }
            });
            delayedReadValueModel.addValueChangeListener(new PropertyChangeListener() {

                public void propertyChange(PropertyChangeEvent evt) {
                    final String s = evt.getPropertyName();
                    System.out.println("s = " + s);
                }
            });

            //dataManager.addPropertyChangeListener("speed", this);
            //dataManager.addPropertyChangeListener("speed", this);
            dataManager.addPropertyChangeListener("completed", this);
            dataManager.addPropertyChangeListener("state", this);

            AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
                public void preferenceChange(final PreferenceChangeEvent evt) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            checkPropertyChange(evt);
                        }
                    });
                }
            });
            //final ContentPanel panel = director.getDockingManager().getContentPanel();
            updateInfoStatus();
            updateClipboardMonitoring();
            updateMemoryIndicator();
        }
        return statusbar;
    }

    private void checkPropertyChange(PreferenceChangeEvent evt) {
        final String key = evt.getKey();
        if (UserProp.SHOWINFO_IN_TITLE.equals(key)) {
            updateInfoStatus();
        } else if (UserProp.ANIMATE_ICON.equals(key)) {
            if (!AppPrefs.getProperty(UserProp.ANIMATE_ICON, UserProp.ANIMATE_ICON_DEFAULT))
                trayIconSupport.setImage(defaultIconImage);
            else
                updateIconAnimation();
        } else if (UserProp.CLIPBOARD_MONITORING.equals(key)) {
            updateClipboardMonitoring();
        } else if (UserProp.SHOW_MEMORY_INDICATOR.equals(key)) {
            updateMemoryIndicator();
        } else
        if (UserProp.GLOBAL_SPEED_SLIDER_MAX.equals(key) || UserProp.GLOBAL_SPEED_SLIDER_MAX.equals(key) || UserProp.GLOBAL_SPEED_SLIDER_STEP.equals(key)) {
            bindSpeedSlider(slider);
        }
    }

    private void updateMemoryIndicator() {
        final boolean memoryIndicator = AppPrefs.getProperty(UserProp.SHOW_MEMORY_INDICATOR, UserProp.SHOW_MEMORY_INDICATOR_DEFAULT);
        indicator.setVisible(memoryIndicator);
        if (memoryIndicator)
            statusbar.add(indicator, JXStatusBar.Constraint.ResizeBehavior.FIXED);
        else
            statusbar.remove(indicator);
    }

    private void updateClipboardMonitoring() {
        clipboardMonitoring.setEnabled(AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT));
    }


    private void setStatusBarVisible(boolean visible) {
        getStatusBar().setVisible(visible);
        //AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, visible); //ulozeni uzivatelskeho nastaveni
    }


    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        if ("value".equals(propertyName) || "completed".equals(propertyName)) {
            updateInfoStatus();
        } else if ("started".equals(propertyName) || "done".equals(propertyName) || "message".equals(propertyName)) {
            //final Task task = (Task) evt.getSource();
            if (!(evt.getSource() instanceof DownloadTask))
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
            task.removePropertyChangeListener(taskPCL);
            activeTask = null;
        } else if ("started".equals(propertyName)) {
            if (activeTask != null)
                task.removePropertyChangeListener(taskPCL);
            activeTask = task;
            progress.setStringPainted(false);
            progress.setVisible(true);
            progress.setToolTipText(null);
            progress.setIndeterminate(!task.isProgressPropertyValid());
            task.addPropertyChangeListener(taskPCL);
        }
    }


    private void updateInfoStatus() {
        final int completed = dataManager.getCompleted();
        final int size = dataManager.getDownloadFiles().size();
        final int speed = dataManager.getCurrentSpeed();
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

    private void bindSpeedSlider(JSlider slider) {
        int minimum = AppPrefs.getProperty(UserProp.GLOBAL_SPEED_SLIDER_MIN, UserProp.GLOBAL_SPEED_SLIDER_MIN_DEFAULT);
        int maximum = AppPrefs.getProperty(UserProp.GLOBAL_SPEED_SLIDER_MAX, UserProp.GLOBAL_SPEED_SLIDER_MAX_DEFAULT);
        int step = AppPrefs.getProperty(UserProp.GLOBAL_SPEED_SLIDER_STEP, UserProp.GLOBAL_SPEED_SLIDER_STEP_DEFAULT);
//        minimum = Math.min(minimum, maximum);
//        maximum = Math.max(minimum, maximum);
//        if (step > maximum - minimum) {
//            step = Math.max(maximum - minimum / 10, 1);
//        }
        slider.setMinimum(minimum);
        slider.setMaximum(maximum);
        slider.setMinorTickSpacing(step);
    }

    private ValueModel bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
        final ValueModel valueModel = new MyPreferencesAdapter(key, defaultValue);
        Bindings.bind(checkBox, valueModel);
        return valueModel;
    }


}
