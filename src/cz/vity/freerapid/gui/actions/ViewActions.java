package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.dialogs.DownloadHistoryDialog;
import cz.vity.freerapid.gui.dialogs.SpeedMeterDialog;
import cz.vity.freerapid.gui.dialogs.UserPreferencesDialog;
import cz.vity.freerapid.gui.managers.ContentPanel;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.os.OSCommand;
import cz.vity.freerapid.utilities.os.SystemCommander;
import cz.vity.freerapid.utilities.os.SystemCommanderFactory;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.util.logging.Logger;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */

public class ViewActions extends AbstractBean {
    private final static Logger logger = Logger.getLogger(ViewActions.class.getName());

    private MainApp app;

    private boolean showCompleted;
    private final static String SHOW_COMPLETED_PROPERTY = "showCompleted";

    private boolean clipboardMonitoringSelected;
    private static final String CLIPBOARD_MONITORING_PROPERTY = "clipboardMonitoringSelected";

    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);
        setShowCompleted(AppPrefs.getProperty(UserProp.SHOW_COMPLETED, true));
        setClipboardMonitoringSelected(AppPrefs.getProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT));
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.AUTOSHUTDOWN.equals(evt.getKey())) {
                    updateSelectedAction();
                }
            }
        });
    }

    private void updateSelectedAction() {
        final String action = app.getManagerDirector().getMenuManager().getSelectedShutDownAction();
        app.getContext().getActionMap().get(action).putValue(AbstractAction.SELECTED_KEY, Boolean.TRUE);
    }

    @Action
    public void showStatusBar() {
        //final boolean selected = ((AbstractButton) e.getSource()).isSelected();
    }

    @Action
    public void showToolbar() {

    }

    protected MainApp getApp() {
        return app;
    }

//    public boolean isEnabled() {
//        System.out.println("isenabled");
//        return true;
//    }
//
//    public void setEnabled(boolean value) {
//        System.out.println("set enabled");
//    }
//
//    public boolean isSelectedShowMenu() {
//        System.out.println("check selected");
//        return ((SingleFrameApplication)ApplicationContext.getInstance().getApplication()).getMainFrame().getJMenuBar().isVisible();
//    }

    //

    @Action
    public void showDownloadHistoryAction() {
        final ManagerDirector managerDirector = app.getManagerDirector();
        final DownloadHistoryDialog dialog = new DownloadHistoryDialog(app.getMainFrame(), managerDirector);
//        dialog.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
//        dialog.setModal(false);
        app.show(dialog);
    }

    @Action
    public void showSpeedMonitor() {
        final ManagerDirector managerDirector = app.getManagerDirector();
        final SpeedMeterDialog dialog = new SpeedMeterDialog(app.getMainFrame(), managerDirector);
//        dialog.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
//        dialog.setModal(false);
        app.show(dialog);
    }

    @Action
    public void options() {
        try {
            final UserPreferencesDialog dialog = new UserPreferencesDialog(app.getMainFrame(), app.getContext());
            app.prepareDialog(dialog, true);
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    @Action(selectedProperty = ViewActions.CLIPBOARD_MONITORING_PROPERTY)
    public void monitorClipboardAction() {
        AppPrefs.storeProperty(UserProp.CLIPBOARD_MONITORING, isClipboardMonitoringSelected());
    }

    @Action(selectedProperty = ViewActions.SHOW_COMPLETED_PROPERTY)
    public void showCompletedAction() {
        //setShowCompleted(!isShowCompleted());
        AppPrefs.storeProperty(UserProp.SHOW_COMPLETED, isShowCompleted());
        final ManagerDirector managerDirector = app.getManagerDirector();
        final ContentPanel panel = managerDirector.getContentManager().getContentPanel();
        panel.updateFilters();
    }

    public boolean isShowCompleted() {
        return showCompleted;
    }

    public boolean getShowCompleted() {
        return showCompleted;
    }

    public void setShowCompleted(boolean showCompleted) {
        boolean oldValue = this.showCompleted;
        this.showCompleted = showCompleted;
        firePropertyChange("showCompleted", oldValue, showCompleted);
    }

    public boolean isClipboardMonitoringSelected() {
        return clipboardMonitoringSelected;
    }

    public void setClipboardMonitoringSelected(boolean clipboardMonitoringSelected) {
        boolean oldValue = this.clipboardMonitoringSelected;
        this.clipboardMonitoringSelected = clipboardMonitoringSelected;
        firePropertyChange("clipboardMonitoringSelected", oldValue, clipboardMonitoringSelected);
    }

    @Action
    public void shutdownDisabledAction() {
        setShutdownProperty(UserProp.AUTOSHUTDOWN_DISABLED);
    }

    private void setShutdownProperty(final int autoshutdownType) {
        AppPrefs.storeProperty(UserProp.AUTOSHUTDOWN, autoshutdownType);
    }

    @Action
    public void shutdownQuitAction() {
        setShutdownProperty(UserProp.AUTOSHUTDOWN_CLOSE);
    }

    @Action
    public void shutdownHibernateAction() {
        updateShutdown(OSCommand.HIBERNATE, UserProp.AUTOSHUTDOWN_HIBERNATE);
    }

    @Action
    public void shutdownShutdownAction() {
        updateShutdown(OSCommand.SHUTDOWN, UserProp.AUTOSHUTDOWN_SHUTDOWN);
    }

    @Action
    public void shutdownStandByAction() {
        updateShutdown(OSCommand.STANDBY, UserProp.AUTOSHUTDOWN_STANDBY);
    }

    @Action
    public void shutdownRebootAction() {
        updateShutdown(OSCommand.REBOOT, UserProp.AUTOSHUTDOWN_REBOOT);
    }

    private void updateShutdown(OSCommand command, int propertyShutdownType) {
        final SystemCommander utils = SystemCommanderFactory.getInstance().getSystemCommanderInstance(app.getContext());
        if (!utils.isSupported(command)) {
            setShutdownProperty(UserProp.AUTOSHUTDOWN_DISABLED);
            Swinger.showErrorMessage(app.getContext().getResourceMap(), "systemCommandNotSupported", command.toString().toLowerCase());
        } else
            setShutdownProperty(propertyShutdownType);
    }

}
