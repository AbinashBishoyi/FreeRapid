package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.PresentationModel;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.Trigger;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.JButtonBar;
import com.l2fprod.common.swing.plaf.blue.BlueishButtonBarUI;
import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.gui.dialogs.AppDialog;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.swing.binding.MyPresentationModel;
import cz.vity.freerapid.swing.components.EnhancedToolbar;
import org.jdesktop.application.Action;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swinghelper.buttonpanel.JXButtonPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class UserPreferencesDialog extends AppDialog {

    private static final Logger logger = Logger.getLogger(UserPreferencesDialog.class.getName());
    private static final String CARD_PROPERTY = "card";
    private final ManagerDirector managerDirector;
    private final HashMap<Card, UserPreferencesTab> tabs = new LinkedHashMap<Card, UserPreferencesTab>();
    private MyPresentationModel model;
    private Trigger trigger;
    private boolean updateQueue = false;

    static enum Card {
        CARD1, CARD2, CARD3, CARD4, CARD5, CARD6
    }

    public UserPreferencesDialog(Frame owner, ApplicationContext context) throws Exception {
        super(owner, true);
        setName("UserPreferencesDialog");
        managerDirector = ((MainApp) context.getApplication()).getManagerDirector();
        try {
            initComponents();
            build();
        } catch (Exception e) {
            doClose();
            throw e;
        }
    }

    @Override
    public String[] getList(final String key, final int valueCount) {
        return super.getList(key, valueCount);
    }

    @Override
    public ResourceMap getResourceMap() {
        return super.getResourceMap();
    }

    @Override
    public void registerKeyboardAction(javax.swing.Action action) {
        super.registerKeyboardAction(action);
    }

    MyPresentationModel getModel() {
        return model;
    }

    void setUpdateQueue() {
        updateQueue = true;
    }

    @Override
    protected AbstractButton getBtnCancel() {
        return btnCancel;
    }

    @Override
    protected AbstractButton getBtnOK() {
        return btnOK;
    }

    private void build() throws CloneNotSupportedException {
        inject();
        init();
        Card card;
        try {
            card = Card.valueOf(AppPrefs.getProperty(FWProp.USER_SETTINGS_SELECTED_CARD, Card.CARD1.toString()));
        } catch (IllegalArgumentException e) {
            card = Card.CARD1;
        }
        showCard(card);
        pack();
        setResizable(true);
        locateOnOpticalScreenCenter(this);
    }

    private void init() {
        toolbar.setUI(new BlueishButtonBarUI());
        final ActionMap map = getActionMap();
        ButtonGroup group = new ButtonGroup();
        addButton(map.get("generalBtnAction"), Card.CARD1, group);
        addButton(map.get("connectionsBtnAction"), Card.CARD2, group);
        addButton(map.get("soundBtnAction"), Card.CARD3, group);
        addButton(map.get("viewsBtnAction"), Card.CARD4, group);
        addButton(map.get("pluginsBtnAction"), Card.CARD6, group);
        addButton(map.get("miscBtnAction"), Card.CARD5, group);
        trigger = new Trigger();
        model = new MyPresentationModel(null, trigger);
        final javax.swing.Action actionOK = map.get("okBtnAction");
        PropertyConnector connector = PropertyConnector.connect(model, PresentationModel.PROPERTY_BUFFERING, actionOK, "enabled");
        connector.updateProperty2();

        setAction(btnOK, "okBtnAction");
        setAction(btnCancel, "cancelBtnAction");
    }

    private void addButton(javax.swing.Action action, final Card card, ButtonGroup group) {
        final JToggleButton button = new JToggleButton(action);
        final Dimension size = button.getPreferredSize();
        final Dimension dim = new Dimension(83, size.height + 8);
        button.setFont(button.getFont().deriveFont((float) 10));
        button.setForeground(Color.BLACK);
        button.setMinimumSize(dim);
        button.setPreferredSize(dim);
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.BOTTOM);
        button.setOpaque(false);
        toolbar.add(button);
        button.putClientProperty(CARD_PROPERTY, card);
        group.add(button);
    }

    void showCard(Card card) {
        assert card != null;
        final UserPreferencesTab tab = tabs.get(card);
        if (!tab.isInitialized()) {
            tab.init();
            tab.setInitialized(true);
        }

        final CardLayout cardLayout = (CardLayout) panelCard.getLayout();
        cardLayout.show(panelCard, card.toString());
        AppPrefs.storeProperty(FWProp.USER_SETTINGS_SELECTED_CARD, card.toString());
        String actionName;
        switch (card) {
            case CARD1:
                actionName = "generalBtnAction";
                break;
            case CARD2:
                actionName = "connectionsBtnAction";
                break;
            case CARD3:
                actionName = "soundBtnAction";
                break;
            case CARD4:
                actionName = "viewsBtnAction";
                break;
            case CARD5:
                actionName = "miscBtnAction";
                break;
            case CARD6:
                actionName = "pluginsBtnAction";
                break;
            default:
                assert false;
                return;
        }
        javax.swing.Action action = getActionMap().get(actionName);
        assert action != null;
        action.putValue(javax.swing.Action.SELECTED_KEY, Boolean.TRUE);
    }

    @Action
    public void okBtnAction() {
        if (!validated())
            return;
        for (final UserPreferencesTab tab : tabs.values()) {
            if (tab.isInitialized()) {
                tab.apply();
            }
        }
        trigger.triggerCommit();
        if (updateQueue)
            managerDirector.getDataManager().getProcessManager().queueUpdated();
        doClose();
    }

    private boolean validated() {
        for (final UserPreferencesTab tab : tabs.values()) {
            if (!tab.isInitialized()) {
                continue;
            }
            if (!tab.validated()) {
                return false;
            }
        }
        return true;
    }

    @Action
    public void cancelBtnAction() {
        for (final UserPreferencesTab tab : tabs.values()) {
            if (tab.isInitialized()) {
                tab.cancel();
            }
        }
        doClose();
    }

    @Action
    public void generalBtnAction(ActionEvent e) {
        showCard(e);
    }

    @Action
    public void connectionsBtnAction(ActionEvent e) {
        showCard(e);
    }

    @Action
    public void soundBtnAction(ActionEvent e) {
        showCard(e);
    }

    @Action
    public void viewsBtnAction(ActionEvent e) {
        showCard(e);
    }

    @Action
    public void miscBtnAction(ActionEvent e) {
        showCard(e);
    }

    @Action
    public void pluginsBtnAction(ActionEvent e) {
        showCard(e);
    }

    private void showCard(ActionEvent e) {
        showCard((Card) ((JComponent) e.getSource()).getClientProperty(CARD_PROPERTY));
    }

    @Override
    public void doClose() {
        logger.fine("Closing UserPreferencesDialog");
        try {
            if (model != null)
                model.release();
        } finally {
            super.doClose();
        }
    }

    private void initComponents() {
        JPanel dialogPane = new JPanel();
        JPanel contentPanel = new JPanel();
        btnOK = new JButton();
        btnOK.setName("btnOK");
        btnCancel = new JButton();
        btnCancel.setName("btnCancel");
        panelCard = new JPanel();
        toolbar = new EnhancedToolbar();
        JXButtonPanel buttonBar = new JXButtonPanel();
        CellConstraints cc = new CellConstraints();

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new BorderLayout());

                //======== buttonBar ========
                {
                    buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 4dlu, 4dlu, 4dlu"));
                    buttonBar.setCyclic(true);

                    PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormSpecs.GLUE_COLSPEC,
                                    ColumnSpec.decode("max(pref;42dlu)"),
                                    FormSpecs.RELATED_GAP_COLSPEC,
                                    FormSpecs.PREF_COLSPEC
                            },
                            RowSpec.decodeSpecs("pref")), buttonBar);
                    ((FormLayout) buttonBar.getLayout()).setColumnGroups(new int[][]{{2, 4}});

                    buttonBarBuilder.add(btnOK, cc.xy(2, 1));
                    buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
                }
                contentPanel.add(buttonBar, BorderLayout.SOUTH);

                //======== panelCard ========
                {
                    final GeneralTab generalTab = new GeneralTab(this);
                    final ConnectionsTab connectionsTab = new ConnectionsTab(this, managerDirector);
                    final AlertsTab alertsTab = new AlertsTab(this);
                    final ViewsTab viewsTab = new ViewsTab(this);
                    final PluginsTab pluginsTab = new PluginsTab(this, managerDirector);
                    final MiscTab miscTab = new MiscTab(this);

                    tabs.put(Card.CARD1, generalTab);
                    tabs.put(Card.CARD2, connectionsTab);
                    tabs.put(Card.CARD3, alertsTab);
                    tabs.put(Card.CARD4, viewsTab);
                    tabs.put(Card.CARD6, pluginsTab);
                    tabs.put(Card.CARD5, miscTab);
                    for (final UserPreferencesTab tab : tabs.values()) {
                        tab.build(cc);
                    }


                    panelCard.setLayout(new CardLayout());
                    panelCard.add(generalTab, "CARD1");
                    panelCard.add(connectionsTab, "CARD2");
                    panelCard.add(alertsTab, "CARD3");
                    panelCard.add(viewsTab, "CARD4");
                    panelCard.add(pluginsTab, "CARD6");
                    panelCard.add(miscTab, "CARD5");
                }
                contentPanel.add(panelCard, BorderLayout.CENTER);
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);
            dialogPane.add(toolbar, BorderLayout.NORTH);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);

    }

    private JButton btnOK;
    private JButton btnCancel;
    private JPanel panelCard;
    private JButtonBar toolbar;

}
