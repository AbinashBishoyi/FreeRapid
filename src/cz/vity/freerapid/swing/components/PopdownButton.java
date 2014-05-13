package cz.vity.freerapid.swing.components;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Source taken from http://explodingpixels.wordpress.com/2008/11/10/prevent-popup-menu-dismissal/
 */
public class PopdownButton extends JToggleButton {

    private JToggleButton fButton;

    private JPopupMenu fPopupMenu;

    public PopdownButton() {
        // setup the default button state.
        fButton = this;
        this.fPopupMenu = new JPopupMenu();

        fButton.putClientProperty("JButton.buttonType", "textured");

        // install a mouse listener on the button to hide and show the popup
        // menu as appropriate.
        fButton.addMouseListener(createButtonMouseListener());

        // add a popup menu listener to update the button's selection state
        // when the menu is being dismissed.
        fPopupMenu.addPopupMenuListener(createPopupMenuListener());

        // install a special client property on the button to prevent it from
        // closing of the popup when the down arrow is pressed.
        JComboBox box = new JComboBox();
        Object preventHide = box.getClientProperty("doNotCancelPopup");
        fButton.putClientProperty("doNotCancelPopup", preventHide);
    }

    private MouseListener createButtonMouseListener() {
        return new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                // if the popup menu is currently showing, then hide it.
                // else if the popup menu is not showing, then show it.
                if (fPopupMenu.isShowing()) {
                    hidePopupMenu();
                } else {
                    showPopupMenu();
                }
            }
        };
    }

    private PopupMenuListener createPopupMenuListener() {
        return new PopupMenuListener() {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                // no implementation.
            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                // no implementation.
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
                // the popup menu has been canceled externally (either by
                // pressing escape or clicking off of the popup menu). update
                // the button's state to reflect the menu dismissal.
                fButton.setSelected(false);
            }
        };
    }

    private void hidePopupMenu() {
        fPopupMenu.setVisible(false);
    }

    private void showPopupMenu() {
        // show the menu below the button, and slightly to the right.
        fPopupMenu.show(fButton, 5, fButton.getHeight());
    }

    public JPopupMenu getPopupMenu() {
        return fPopupMenu;
    }
}
