package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.tasks.CheckForNewVersionTask;
import cz.vity.freerapid.gui.dialogs.AboutDialog;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import org.jdesktop.application.Action;

import java.awt.event.ActionEvent;

/**
 * @author Vity
 */

public class HelpActions {
    public static final String CONTEXT_DIALOG_HELPPROPERTY = "contextDialogHelp";

    private MainApp app;
    public static final String CONTEXT_DIALOG_HELP_ACTION = "contextDialogHelpAction";


    public HelpActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void help() {
        Swinger.showInformationDialog(Swinger.getResourceMap().getString("notImplementedYet"));
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
    public void contextDialogHelpAction(ActionEvent event) {
        //final String context = ((JComponent) event.getSource()).getClientProperty(CONTEXT_DIALOG_HELPPROPERTY).toString();
        final String context = event.getActionCommand();
        Browser.openBrowser(context);
    }

    @Action
    public void about() {
        final AboutDialog aboutDialog = new AboutDialog(app.getMainFrame());
        app.prepareDialog(aboutDialog, true);
    }

}
