package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.content.ContentPanel;
import cz.vity.freerapid.gui.dialogs.DownloadHistoryDialog;
import cz.vity.freerapid.gui.dialogs.SpeedMeterDialog;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Action;

import javax.swing.*;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;

/**
 * @author Vity
 */
public class ViewActions extends AbstractBean {

    private final MainApp app;

    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);

        AppPrefs.getPreferences().addPreferenceChangeListener(new PreferenceChangeListener() {
            @Override
            public void preferenceChange(PreferenceChangeEvent evt) {
                if (UserProp.AUTOSHUTDOWN.equals(evt.getKey())) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
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

    @Action
    public void showDownloadHistoryAction() {
        final ManagerDirector managerDirector = app.getManagerDirector();
        final DownloadHistoryDialog dialog = new DownloadHistoryDialog(app.getMainFrame(), managerDirector);
        app.show(dialog);
    }

    @Action
    public void showSpeedMonitor() {
        final ManagerDirector managerDirector = app.getManagerDirector();
        final SpeedMeterDialog dialog = new SpeedMeterDialog(app.getMainFrame(), managerDirector);
        app.show(dialog);
    }

    @Action
    public void showCompletedAction() {
        AppPrefs.negateProperty(UserProp.SHOW_COMPLETED, UserProp.SHOW_COMPLETED_DEFAULT);
        final ManagerDirector managerDirector = app.getManagerDirector();
        final ContentPanel panel = managerDirector.getContentManager().getContentPanel();
        panel.updateFilters();
    }

}
