package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.gui.dialogs.NewLinksDialog;
import cz.vity.freerapid.gui.managers.DataManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.Action;
import org.jdesktop.application.ProxyActions;
import org.jdesktop.beans.AbstractBean;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URL;
import java.util.List;

/**
 * FileActions = Menu soubory
 *
 * @author Vity
 */
@ProxyActions({"select-all", "copy", "cut", "paste"})
public class FileActions extends AbstractBean {

    private MainApp app;
    private NewLinksDialog dialog;


    public FileActions() {
        app = MainApp.getInstance(MainApp.class);
    }

    @SuppressWarnings({"unchecked"})
    @Action
    public void addNewLinksAction(ActionEvent event) {
        List<URL> urlList = null;
        if (event.getSource() instanceof List) {
            urlList = (List<URL>) event.getSource();
            if (urlList.isEmpty())
                return;
        }
        final ManagerDirector managerDirector = app.getManagerDirector();
        final DataManager dataManager = managerDirector.getDataManager();

        final boolean showing = dialog != null;
        if (!showing)
            dialog = new NewLinksDialog(managerDirector, app.getMainFrame());

        if (urlList != null)
            dialog.setURLs(urlList);
        if (!showing) {
            app.prepareDialog(dialog, true);
            if (dialog.getModalResult() == NewLinksDialog.RESULT_OK) {
                final List<DownloadFile> files = dialog.getDownloadFiles();
                dataManager.addToList(files);
                SwingUtilities.invokeLater(new Runnable() {
                    public void run() {
                        if (!dialog.isStartPaused())
                            dataManager.addToQueue(files);
                        managerDirector.getContentManager().getContentPanel().selectAdded(files);
                    }
                });
            }
            dialog = null;
        }
    }
    //TODO https://www.paypal.com/cgi-bin/webscr?cmd=_xclick&business=donations@wordrider.net&item_name=FreeRapid&no_shipping=1&cn=Optional comments&tax=0

}
