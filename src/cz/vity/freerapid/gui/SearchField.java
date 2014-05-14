package cz.vity.freerapid.gui;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.search.SearchItem;
import cz.vity.freerapid.swing.ComponentFactory;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Rectangle2D;
import java.util.Collections;

/**
 * @author Vity
 */
public class SearchField extends JTextField implements FocusListener, PopupMenuListener {
    private String emptyString;
    private static final int PREFFERED_HEIGHT = 25;
    private JToggleButton btn;
    private SearchItem selectedItem = null;
    private java.util.List<SearchItem> searchItemList = Collections.emptyList();

    public SearchField(ApplicationContext context) {
        super();
        this.addFocusListener(this);
        emptyString = "";
        btn = new JToggleButton("");
        btn.setSize(PREFFERED_HEIGHT - 6, PREFFERED_HEIGHT - 6);
        btn.setMinimumSize(btn.getSize());
        btn.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        btn.setFocusable(false);
        this.add(btn);

        btn.setCursor(Cursor.getDefaultCursor());

        final Border border = this.getBorder();
        //noinspection SuspiciousNameCombination
        this.setBorder(BorderFactory.createCompoundBorder(border, BorderFactory.createEmptyBorder(0, PREFFERED_HEIGHT, 0, 0)));
        btn.setLocation((border == null) ? 2 : border.getBorderInsets(this).left, this.getInsets().top);
        final Dimension preferredSize = new Dimension(AppPrefs.getProperty(UserProp.SEARCH_FIELD_WIDTH, UserProp.SEARCH_FIELD_WIDTH_DEFAULT), PREFFERED_HEIGHT);
        this.setPreferredSize(preferredSize);
        this.setMaximumSize(preferredSize);
        this.setMinimumSize(preferredSize);
        this.addFocusListener(new ComponentFactory.SelectAllOnFocusListener());
        btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                doPopmenu();
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (hasFocus() && e.isAltDown() && e.getKeyCode() == KeyEvent.VK_DOWN) {
                    doPopmenu();
                    e.consume();
                }
            }
        });
        Swinger.initActions(this, context);
    }

    @org.jdesktop.application.Action
    public void manageSearchEngines() {
        Swinger.showInformationDialog(Swinger.getResourceMap().getString("howToManageSearchEngines"));
        Browser.openBrowser(Consts.SEARCH_ENGINES_URL);
    }

    private void doPopmenu() {
        if (searchItemList == null) {
            return;
        }
        final JPopupMenu popmenu = new JPopupMenu();
        for (SearchItem item : searchItemList) {
            popmenu.add(new SelectSearchEngineAction(item));
        }
        if (!searchItemList.isEmpty()) {
            popmenu.addSeparator();
        }
        popmenu.add(Swinger.getAction("manageSearchEngines"));
        popmenu.show(getParent(), getLocation().x, getLocation().y + getHeight());
    }


    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (!hasFocus() && getText().isEmpty() && emptyString != null && !emptyString.isEmpty()) {
            g.setColor(Color.GRAY);
            final Font f = this.getFont().deriveFont(Font.ITALIC);
            g.setFont(f);
            final Rectangle2D bounds = g.getFontMetrics(f).getStringBounds(emptyString, g);
            g.drawString(emptyString, getInsets().left, getHeight() + getInsets().top - getInsets().bottom - (int) (bounds.getHeight() / 2));
        }
    }


    public void popupMenuCanceled(PopupMenuEvent e) {

    }

    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

    }

    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        btn.getModel().setSelected(false);
    }

    @Override
    public void focusGained(FocusEvent e) {
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        focusGained(e);
    }

    public String getEmptyString() {
        return emptyString;
    }

    public void setEmptyString(String emptyString) {
        this.emptyString = emptyString;
        repaint();
    }

    public java.util.List<SearchItem> getSearchItemList() {
        return searchItemList;
    }

    public void setSearchItemList(java.util.List<SearchItem> searchItemList) {
        if (searchItemList == null) {
            throw new IllegalArgumentException("Search list cannot be null");
        }
        String idDefault = AppPrefs.getProperty(UserProp.SEARCH_FIELD_SEARCH_ENGINE, UserProp.SEARCH_FIELD_SEARCH_ENGINE_DEFAULT);
        this.searchItemList = searchItemList;
        for (SearchItem item : searchItemList) {
            if (item.getId().equals(idDefault)) {
                setSelectedItem(item);
                return;
            }
        }
        if (searchItemList.isEmpty()) {
            setSelectedItem(null);
        } else {
            setSelectedItem(searchItemList.get(0));
        }
    }


    public SearchItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(SearchItem selectedItem) {
        this.selectedItem = selectedItem;
        final String id;
        if (selectedItem == null) {
            setEmptyString("");
            btn.setIcon(null);
            id = null;
        } else {
            id = selectedItem.getId();
            btn.setIcon(selectedItem.getImage());
            setEmptyString(selectedItem.getSearchDescription().getShortName());
        }
        AppPrefs.storeProperty(UserProp.SEARCH_FIELD_SEARCH_ENGINE, id);
    }

    private class SelectSearchEngineAction extends AbstractAction {
        private final SearchItem item;

        private SelectSearchEngineAction(SearchItem item) {
            super(item.getSearchDescription().getShortName(), item.getImage());
            this.putValue(Action.SHORT_DESCRIPTION, item.getSearchDescription().getShortName());
            this.putValue(Action.LONG_DESCRIPTION, item.getSearchDescription().getDescription());
            this.item = item;
        }

        public void actionPerformed(ActionEvent e) {
            SearchField.this.setSelectedItem(item);
            Swinger.inputFocus(SearchField.this);
        }
    }
}
