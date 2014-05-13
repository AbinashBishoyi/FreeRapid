package cz.vity.freerapid.gui.actions;

import cz.vity.freerapid.core.MainApp;
import org.jdesktop.application.Action;

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
//    public void setSelectedShowMenu(final boolean selected) {
//        System.out.println("set selected");
//        final ManagerDirector mainPanel = ((MainApp) ApplicationContext.getInstance().getApplication()).getMainPanel();
//        mainPanel.getStatusBar().setVisible(selected);
//        AppPrefs.storeProperty(AppPrefs.SHOW_STATUSBAR, selected);
//    }

}
