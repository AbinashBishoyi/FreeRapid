package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.gui.dialogs.DownloadHistoryDialog;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import org.jdesktop.application.Action;

import java.awt.*;

/**
 * @author Vity
 */

public class ViewActions {

    private MainApp app;

    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);
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
        final DownloadHistoryDialog dialog = new DownloadHistoryDialog(managerDirector, app.getMainFrame());
        dialog.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
        dialog.setModal(false);
        app.prepareDialog(dialog, true);
    }

}
