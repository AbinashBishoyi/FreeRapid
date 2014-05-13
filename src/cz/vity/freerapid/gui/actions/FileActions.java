package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.gui.dialogs.NewLinksDialog;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.Action;
import org.jdesktop.beans.AbstractBean;

import javax.swing.*;
import java.util.List;

/**
 * FileActions = Menu soubory
 *
 * @author Vity
 */

public class FileActions extends AbstractBean {

    private MainApp app;


    public FileActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @Action
    public void addNewLinksAction() {
        final ManagerDirector managerDirector = app.getManagerDirector();
        final DataManager dataManager = managerDirector.getDataManager();
        final NewLinksDialog dialog = new NewLinksDialog(dataManager, app.getMainFrame());
        app.prepareDialog(dialog, true);
        if (dialog.getModalResult() == NewLinksDialog.RESULT_OK) {
            final List<DownloadFile> files = dialog.getDownloadFiles();
            dataManager.addToList(files);
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    if (!dialog.isStartPaused())
                        dataManager.addToQueue(files);
                    managerDirector.getDockingManager().getContentPanel().selectAdded(files);
                }
            });

        }
    }

}
