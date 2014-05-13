package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.dialogs.DownloadHistoryDialog;
import cz.vity.freerapid.gui.managers.ContentPanel;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import org.jdesktop.application.AbstractBean;
import org.jdesktop.application.Action;

/**
 * @author Vity
 */

public class ViewActions extends AbstractBean {

    private MainApp app;

    private boolean showCompleted;
    private final static String SHOW_COMPLETED_PROPERTY = "showCompleted";

    public ViewActions() {
        app = MainApp.getInstance(MainApp.class);
        setShowCompleted(AppPrefs.getProperty(UserProp.SHOW_COMPLETED, true));
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
//        dialog.setModalExclusionType(Dialog.ModalExclusionType.TOOLKIT_EXCLUDE);
//        dialog.setModal(false);
        app.show(dialog);
    }

    @Action(selectedProperty = ViewActions.SHOW_COMPLETED_PROPERTY)
    public void showCompletedAction() {
        //setShowCompleted(!isShowCompleted());
        AppPrefs.storeProperty(UserProp.SHOW_COMPLETED, isShowCompleted());
        final ManagerDirector managerDirector = app.getManagerDirector();
        final ContentPanel panel = managerDirector.getDockingManager().getContentPanel();
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
}
