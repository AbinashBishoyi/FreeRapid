package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.gui.dialogs.DownloadHistoryDialog;
import cz.vity.freerapid.gui.dialogs.SpeedMeterDialog;
import cz.vity.freerapid.gui.dialogs.UserPreferencesDialog;
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


    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);
        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.AUTOSHUTDOWN.equals(evt.getKey())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            updateSelectedAction();
                        }
                    });
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
        AppPrefs.negateProperty(UserProp.SHOW_STATUSBAR, UserProp.SHOW_STATUSBAR_DEFAULT);
    }

    @Action
    public void showToolbar() {
        AppPrefs.negateProperty(UserProp.SHOW_TOOLBAR, UserProp.SHOW_TOOLBAR_DEFAULT);
    }

    protected MainApp getApp() {
        return app;
    }

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
            app.getAppPrefs().store();
        } catch (Exception e) {
            LogUtils.processException(logger, e);
        }
    }

    @Action
    public void monitorClipboardAction() {
        AppPrefs.negateProperty(UserProp.CLIPBOARD_MONITORING, UserProp.CLIPBOARD_MONITORING_DEFAULT);
    }

    @Action
    public void globalLimitSpeedAction() {
        AppPrefs.negateProperty(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT);
    }

    @Action
    public void showCompletedAction() {
        AppPrefs.negateProperty(UserProp.SHOW_COMPLETED, UserProp.SHOW_COMPLETED_DEFAULT);
        final ManagerDirector managerDirector = app.getManagerDirector();
        final ContentPanel panel = managerDirector.getContentManager().getContentPanel();
        panel.updateFilters();
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
