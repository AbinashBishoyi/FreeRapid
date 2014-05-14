package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.CheckForNewVersionTask;
import cz.vity.freerapid.gui.dialogs.AboutDialog;
import cz.vity.freerapid.gui.managers.UpdateManager;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogFileHandler;
import cz.vity.freerapid.utilities.OSDesktop;
import org.jdesktop.application.Action;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */

public class HelpActions {
    public static final String CONTEXT_DIALOG_HELPPROPERTY = "contextDialogHelp";
    public static final String CONTEXT_DIALOG_HELP_ACTION = "contextDialogHelpAction";

    private MainApp app;

    public HelpActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void paypalSupportAction() {
        Browser.openBrowser(AppPrefs.getProperty(UserProp.PAYPAL, UserProp.PAYPAL_DEFAULT));
    }

    @Action
    public void help() {
        Browser.openBrowser(AppPrefs.getProperty(UserProp.HELP_URL, Consts.HELP_WEBURL));
    }

    @Action
    public void checkForNewVersion() {
        app.getContext().getTaskService().execute(new CheckForNewVersionTask(true));
    }

    @Action
    public void visitHomepage() {
        Browser.showHomepage();
    }

    @Action
    public void visitForum() {
        Browser.openBrowser(Consts.FORUM_URL);
    }

    @Action
    public void checkPluginStatuses() {
        Browser.openBrowser(AppPrefs.getProperty(UserProp.PLUGINSSTATUS_URL, Consts.PLUGINSSTATUS_URL));
    }

    @Action
    public void showDemo() {
        Browser.openBrowser(AppPrefs.getProperty(UserProp.DEMO_URL, Consts.DEMO_WEBURL));
    }

    @Action
    public void contextDialogHelpAction(ActionEvent event) {
        final String context = event.getActionCommand();
        Browser.openBrowser(context);
    }

    @Action
    public void about() {
        final AboutDialog aboutDialog = new AboutDialog(app.getMainFrame());
        app.prepareDialog(aboutDialog, true);
    }

    @Action
    public void checkForNewPlugins() {
        final UpdateManager updateManager = app.getManagerDirector().getUpdateManager();
        updateManager.checkUpdate(false);
    }

    @Action
    public void czoSupportAction() {
        Browser.openBrowser("http://cos.root.cz/hlasovani/");
    }

    @Action
    public void openLogFile() {
        OSDesktop.openFile(LogFileHandler.getLogFile());
    }

    @Action
    public void browseToLogFile() {
        OSDesktop.openDirectoryForFile(LogFileHandler.getLogFile());
    }

}
