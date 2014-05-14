package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.dialogs.userprefs.UserPreferencesDialog;
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
 * @author ntoskrnl
 */
public class OptionsActions extends AbstractBean {
    private final static Logger logger = Logger.getLogger(OptionsActions.class.getName());

    private final MainApp app;

    private final static String REFRESH_PROXY_LIST_ACTION_ENABLED_PROPERTY = "refreshProxyListActionEnabled";
    private boolean refreshProxyListActionEnabled = false;

    public OptionsActions() {
        app = MainApp.getInstance(MainApp.class);

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(final PreferenceChangeEvent evt) {
                if (UserProp.USE_PROXY_LIST.equals(evt.getKey())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            setRefreshProxyListActionEnabled(Boolean.parseBoolean(evt.getNewValue()));
                        }
                    });
                }
            }
        });
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
    public void quietModeAction() {
        AppPrefs.negateProperty(UserProp.QUIET_MODE_ENABLED, UserProp.QUIET_MODE_ENABLED_DEFAULT);
    }

    @Action
    public void globalSpeedLimitAction() {
        AppPrefs.negateProperty(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT);
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
        } else {
            setShutdownProperty(propertyShutdownType);
        }
    }

    @Action
    public void globalLimitSpeedAction() {
        AppPrefs.negateProperty(UserProp.SPEED_LIMIT_ENABLED, UserProp.SPEED_LIMIT_ENABLED_DEFAULT);
    }

    @Action(enabledProperty = REFRESH_PROXY_LIST_ACTION_ENABLED_PROPERTY)
    public void refreshProxyList() {
        app.getManagerDirector().getClientManager().updateConnectionSettings();
    }

    public void setRefreshProxyListActionEnabled(boolean refreshProxyListActionEnabled) {
        boolean oldValue = this.refreshProxyListActionEnabled;
        this.refreshProxyListActionEnabled = refreshProxyListActionEnabled;
        firePropertyChange(REFRESH_PROXY_LIST_ACTION_ENABLED_PROPERTY, oldValue, this.refreshProxyListActionEnabled);
    }

    public boolean isRefreshProxyListActionEnabled() {
        return refreshProxyListActionEnabled;
    }
}
