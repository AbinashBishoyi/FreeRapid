package cz.vity.freerapid.swing;


import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationActionMap;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

/**
 * Pridava popmenu s cut/copy/paste/delete pro kazdou jtextcomponentu
 *
 * @author Vity
 */
public final class TextComponentContextMenuListener implements AWTEventListener {
    public void eventDispatched(AWTEvent event) {

        // interested only in mouseevents
        if (!(event instanceof MouseEvent))
            return;

        final MouseEvent me = (MouseEvent) event;

        // interested only in popuptriggers
        if (!me.isPopupTrigger())
            return;

        // me.getManagerComponent(...) retunrs the heavy weight component on which event occured
        if (me.getComponent() == null)
            return;
        final Component comp = SwingUtilities.getDeepestComponentAt(me.getComponent(), me.getX(), me.getY());

        // no popup shown by user code
        if (MenuSelectionManager.defaultManager().getSelectedPath().length > 0)
            return;
        // interested only in textcomponents
        if (!(comp instanceof JTextComponent))
            return;
        // create popup menu and show
        final JTextComponent tc = (JTextComponent) comp;
        //Swinger.inputFocus(tc);
        tc.grabFocus();
        final ApplicationContext context = Application.getInstance().getContext();
        final ApplicationActionMap map = context.getActionMap();
        final JPopupMenu menu = new JPopupMenu();

        menu.add(map.get("cut"));
        menu.add(map.get("copy"));
        menu.add(map.get("paste"));
        menu.add(map.get("delete"));
        menu.addSeparator();
        final Action action = map.get("select-all");
        //System.out.println("select-all action = " + action);
        menu.add(action);

        final Point pt = SwingUtilities.convertPoint(me.getComponent(), me.getPoint(), tc);
        menu.show(tc, pt.x, pt.y);
    }
}