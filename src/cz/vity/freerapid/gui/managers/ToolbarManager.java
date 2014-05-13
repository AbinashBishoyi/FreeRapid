package cz.vity.freerapid.gui.managers;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.swing.ToolbarSeparator;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Sprava toolbaru Vytvari a ovlada cely toolbar
 *
 * @author Vity
 */
public class ToolbarManager implements PropertyChangeListener {
    /**
     * hlavni panel v kterem je toolbar umisten
     */
    private final JPanel toolbarPanel = new JPanel(new BorderLayout());
    /**
     * preferovana velikost buttonu v toolbaru
     */
    private final static Dimension buttonDimension = new Dimension(69, 68);
    /**
     * velikost mezery mezi buttony ruzneho typu
     */
    //private static final int STRUT_SIZE = 8;

    /**
     * samotny toolbar
     */
    private JToolBar toolbar = new JToolBar("mainToolbar");
    //private JXBusyLabel labelWorkingProgress;

    /**
     * Konstruktor - naplni toolbar buttony
     */

    public ToolbarManager(ManagerDirector directorManager, ApplicationContext context) {
        toolbarPanel.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 0));
        final Action action = context.getActionMap().get("showToolbar");
        action.putValue(Action.SELECTED_KEY, true); //defaultni hodnota
        //odchyt udalosti z akce pro zmenu viditelnosti toolbaru
        action.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                if (Action.SELECTED_KEY.equals(evt.getPropertyName())) {
                    setToolBarVisible((Boolean) evt.getNewValue());
                }
            }
        });
        createToolbar();
    }

    public void propertyChange(PropertyChangeEvent evt) {
        final String propertyName = evt.getPropertyName();
        if ("done".equals(propertyName)) {
            //setWorkingProgress(false);
        } else if ("started".equals(propertyName)) {
            //setWorkingProgress(true);
        }
    }


    private void createToolbar() {
        toolbarPanel.add(toolbar);
        toolbarPanel.setPreferredSize(new Dimension(400, 50));
        toolbar.setFocusable(false);
        toolbar.setFloatable(false);
        toolbar.add(getButton(Swinger.getAction("addNewLinksAction")));
        toolbar.add(new ToolbarSeparator());
        toolbar.add(getButton(Swinger.getAction("resumeAction")));
        toolbar.add(getButton(Swinger.getAction("pauseAction")));
        toolbar.add(getButton(Swinger.getAction("cancelAction")));
        toolbar.add(new ToolbarSeparator());
        toolbar.add(getButton(Swinger.getAction("topAction")));
        toolbar.add(getButton(Swinger.getAction("upAction")));
        toolbar.add(getButton(Swinger.getAction("downAction")));
        toolbar.add(getButton(Swinger.getAction("bottomAction")));
//        toolbar.add(new ToolbarSeparator());
//        toolbar.add(getButton(Swinger.getAction("quit")));
        toolbar.add(Box.createGlue());
//        this.labelWorkingProgress = new JXBusyLabel();
//        this.labelWorkingProgress.setName("labelWorkingProgress");
//        labelWorkingProgress.setBorder(BorderFactory.createEmptyBorder(2, 10, 2, 10));
//        setWorkingProgress(false);
//        toolbar.add(labelWorkingProgress);
    }

    private void setToolBarVisible(boolean visible) {
        toolbarPanel.setVisible(visible);
        //toolbar.setVisible(visible);
        //  AppPrefs.storeProperty(AppPrefs.SHOW_TOOLBAR, visible); //ulozeni uzivatelskeho nastaveni, ale jen do hashmapy
    }

    /**
     * Vraci hlavni panel toolbaru jako komponentu
     *
     * @return komponenta toolbar
     */
    public JComponent getComponent() {
        return toolbarPanel;
    }

    private static AbstractButton getToggleButton(final Action action) {
        final JToggleButton button = new JToggleButton(action);
        return setButtonProperties(button, action);
    }

    private static AbstractButton getButton(final Action action) {
        final JButton button = new JButton(action);
        return setButtonProperties(button, action);
    }


    private static AbstractButton setButtonProperties(AbstractButton button, Action action) {
        button.setRolloverEnabled(true);
        button.setIconTextGap(-2);
        final Object desc = action.getValue(Action.SHORT_DESCRIPTION);
        String s = (String) action.getValue(Action.NAME);
        if (s != null && s.endsWith("..."))
            s = s.substring(0, s.length() - 3);
        button.setText(s);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setMinimumSize(buttonDimension);

        button.setPreferredSize(buttonDimension);
        button.setMaximumSize(buttonDimension);
        final Object keystroke = action.getValue(Action.ACCELERATOR_KEY);
        if (desc != null && keystroke != null) {
            button.setToolTipText(desc.toString() + " (" + SwingUtils.keyStroke2String((KeyStroke) keystroke) + ")");
        }

        button.setMnemonic(0);
        button.setFocusable(false);
        return button;
    }


    private void setWorkingProgress(final boolean enabled) {
        final JXFrame jxFrame = (JXFrame) (MainApp.getInstance(MainApp.class).getMainFrame());
        jxFrame.setWaiting(enabled);
        //labelWorkingProgress.setBusy(enabled);
    }

}
