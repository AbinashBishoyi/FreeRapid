package cz.vity.freerapid.swing.components;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * @author Vity
 */
abstract class FindOnDemandAction extends AbstractAction implements DocumentListener, KeyListener {
    protected JPanel searchPanel;
    protected JTextField searchField;
    protected JPopupMenu popup;
    private final ResourceMap map;
    private boolean initialized = false;

    public FindOnDemandAction(ResourceMap map) {
        super("Incremental Search"); //NOI18N
        this.map = map;
    }

    private void initPanels() {
        searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        searchField = new JTextField();
        popup = new JPopupMenu();

        searchPanel.setBackground(UIManager.getColor("ToolTip.background")); //NOI18N
        searchField.setOpaque(false);
        JLabel label = new JLabel();
        label.setName("labelQuickSearch");
        label.setFont(new Font("DialogInput", Font.BOLD, 12)); // for readability
        map.injectComponent(label);
        searchPanel.add(label);
        searchPanel.add(searchField);
        label.setLabelFor(searchField);
        popup.setName("quickSearchPopup");
        map.injectComponent(popup);
        searchField.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
        popup.setBorder(BorderFactory.createLineBorder(Color.black));
        popup.add(searchPanel);
        initialized = true;

        searchField.setFont(new Font("DialogInput", Font.PLAIN, 12)); // for readability

        // when the window containing the "comp" has registered Esc key
        // then on pressing Esc instead of search popup getting closed
        // the event is sent to the window. to overcome this we
        // register an action for Esc.
        searchField.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                popup.setVisible(false);
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_FOCUSED);
    }

    protected JComponent comp = null;
    protected boolean ignoreCase;

    /*-------------------------------------------------[ ActionListener ]---------------------------------------------------*/

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == searchField)
            popup.setVisible(false);
        else {
            if (!initialized)
                initPanels();
            comp = (JComponent) ae.getSource();
            ignoreCase = !((ae.getModifiers() & ActionEvent.SHIFT_MASK) != 0);

            searchField.removeActionListener(this);
            searchField.removeKeyListener(this);
            searchField.getDocument().removeDocumentListener(this);
            initSearch(ae);
            searchField.addActionListener(this);
            searchField.addKeyListener(this);
            searchField.getDocument().addDocumentListener(this);

            Rectangle rect = comp.getVisibleRect();
            popup.show(comp, rect.x, rect.y - popup.getPreferredSize().height - 5);
            Swinger.inputFocus(searchField);
        }
    }

    // can be overridden by subclasses to change initial search text etc.
    @SuppressWarnings({"UnusedDeclaration"})
    protected void initSearch(ActionEvent ae) {
        searchField.setText(""); //NOI18N
        searchField.setForeground(Color.black);
    }

    private void changed(Position.Bias bias) {
        // note: popup.pack() doesn't work for first character insert
        popup.setVisible(false);
        popup.setVisible(true);

        searchField.requestFocus();
        searchField.setForeground(changed(comp, searchField.getText(), bias) ? Color.black : Color.red);
    }

    // should search for given text and select item and
    // return true if search is successfull
    protected abstract boolean changed(JComponent comp, String text, Position.Bias bias);

    /*-------------------------------------------------[ DocumentListener ]---------------------------------------------------*/

    public void insertUpdate(DocumentEvent e) {
        changed(null);
    }

    public void removeUpdate(DocumentEvent e) {
        changed(null);
    }

    public void changedUpdate(DocumentEvent e) {
    }

    /*-------------------------------------------------[ KeyListener ]---------------------------------------------------*/

    protected boolean shiftDown = false;
    protected boolean controlDown = false;

    public void keyPressed(KeyEvent ke) {
        if (ke.getSource() != searchField)
            return;
        shiftDown = ke.isShiftDown();
        controlDown = ke.isControlDown();

        switch (ke.getKeyCode()) {
            case KeyEvent.VK_UP:
                changed(Position.Bias.Backward);
                break;
            case KeyEvent.VK_DOWN:
                changed(Position.Bias.Forward);
                break;
        }
    }

    public void keyTyped(KeyEvent e) {
        if (AppPrefs.getProperty(UserProp.SEARCH_ON_TYPE, UserProp.SEARCH_ON_TYPE_DEFAULT)) {
            if (e.getSource().equals(searchField))
                return;
            if (e.isActionKey())
                return;
            final String s = String.valueOf(e.getKeyChar());
            if ((e.getModifiers() == 0 || e.isShiftDown()) && (Character.isLetterOrDigit(e.getKeyChar()) || s.matches("\\p{Punct}"))) {
                actionPerformed(new ActionEvent(e.getSource(), e.getID(), "press"));
                searchField.setText(s);
            }
        }
    }

    public void keyReleased(KeyEvent e) {
    }

    /*-------------------------------------------------[ Installation ]---------------------------------------------------*/

    public void install(JComponent comp) {
        comp.addKeyListener(this);
        comp.registerKeyboardAction(this, KeyStroke.getKeyStroke('F', KeyEvent.CTRL_MASK), JComponent.WHEN_FOCUSED);
        comp.registerKeyboardAction(this, KeyStroke.getKeyStroke('F', KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK), JComponent.WHEN_FOCUSED);
    }

    public void uninstall(JComponent comp) {
        comp.removeKeyListener(this);
    }
}