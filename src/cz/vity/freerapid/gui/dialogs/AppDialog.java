package cz.vity.freerapid.gui.dialogs;

import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.gui.actions.HelpActions;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public abstract class AppDialog extends JDialog {
    private final static Logger logger = Logger.getLogger(AppDialog.class.getName());

    public final static int RESULT_OK = 0;
    final static int RESULT_CANCEL = 1;
    int result = RESULT_CANCEL;
    private ActionMap actionMap = null;

//    private final boolean closeOnCancel = true;

    public AppDialog(Dialog owner, boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    public AppDialog(final Frame owner, final boolean modal) throws HeadlessException {
        super(owner, modal);
    }

    public void doClose() {
        if (actionMap != null)
            actionMap.remove(this);
        dispose();
    }

    public final int getModalResult() {
        return result;
    }

    protected AbstractButton getBtnCancel() {
        return null;
    }

    protected AbstractButton getBtnOK() {
        return null;
    }

    public MainApp getApp() {
        return MainApp.getInstance(MainApp.class);
    }

    protected void inject() {
        Application application = Application.getInstance(Application.class);
        ApplicationContext context = application.getContext();
        context.getResourceMap(getClass()).injectComponents(this);

        this.getContentPane().applyComponentOrientation(ComponentOrientation.getOrientation(Locale.getDefault()));
//        ActionMap actionMap = context.getActionMap(this.getClass(),
//                this);
//        ApplicationAction action = (ApplicationAction) actionMap.get("ok");
////        ActionMap formMap = context.getActionMap(form.getClass(),
////                form);
////        javax.swing.Action delegate = formMap.get("apply");
////        action.setProxy(delegate);
//
//        okayButton.setAction(action);

    }


    protected final JRootPane createRootPane() {


        final ActionListener escapeActionListener = new ActionListener() {
            public void actionPerformed(final ActionEvent actionEvent) {
                //     if (closeOnCancel) {
                doCancel(actionEvent);
                //doCancelButtonAction();
                //   }
            }
        };
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                windowIsClosing();
            }
        });

        final ActionListener okButtonListener = new ActionListener() {

            public void actionPerformed(final ActionEvent actionEvent) {
                if (AppDialog.this.getFocusOwner() instanceof AbstractButton) {
                    final AbstractButton button = ((AbstractButton) AppDialog.this.getFocusOwner());
                    if (button instanceof JToggleButton) {
                        final JToggleButton toggleButton = (JToggleButton) button;
                        if (!toggleButton.isSelected()) {
                            doButtonAction(button, actionEvent);
                            return;
                        }
                    } else {
                        doButtonAction(button, actionEvent);
                        return;
                    }
                }
                final AbstractButton button = getBtnOK();
                if (button != null) {
//                    if (catchOKButtonFocus) {
//                        //AppDialog.this.getContentPane().getFocusTraversalPolicy().
//                        //Swinger.inputFocus(button);
//                    } else {
                    actionEvent.setSource(button);
                    doButtonAction(button, actionEvent);
//                    }
                }
            }
        };
        final JRootPane rootPane = new JRootPane();
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(escapeActionListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        rootPane.registerKeyboardAction(okButtonListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, InputEvent.CTRL_MASK);//textarea
        rootPane.registerKeyboardAction(okButtonListener, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);

        return rootPane;
    }

    protected void windowIsClosing() {

    }

    private void doCancel(ActionEvent actionEvent) {
        final AbstractButton button = getBtnCancel();
        if (button != null) {
            doButtonAction(button, actionEvent);
        }
    }

    private static void doButtonAction(final AbstractButton button, final ActionEvent actionEvent) {
        final Action action = button.getAction();
        if (action != null && action.isEnabled()) {
            button.doClick();
//            action.actionPerformed(actionEvent);
        }

    }


    void setResult(int result) {
        this.result = result;
    }

    protected ResourceMap getResourceMap() {
        return Swinger.getResourceMap(this.getClass(), AppDialog.class);
    }

    /**
     * Locates the given component on the screen's center.
     *
     * @param component the component to be centered
     */
    protected static void locateOnOpticalScreenCenter(Component component) {
        Dimension paneSize = component.getSize();
        Dimension screenSize = component.getToolkit().getScreenSize();
        component.setLocation(
                (screenSize.width - paneSize.width) / 2,
                (int) ((screenSize.height - paneSize.height) * 0.45));
    }

    protected String[] getList(final String key, final int valueCount) {
        final ResourceMap resourceMap = getResourceMap();
        final String[] list = new String[valueCount];
        for (int i = 0; i < valueCount; i++) {
            list[i] = resourceMap.getString(key + "_" + i);
        }
        return list;
    }

    protected boolean validateNonEmpty(final JTextComponent component) {
        final Document doc = component.getDocument();
        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            LogUtils.processException(logger, e);
            return false;
        }
        return validateNonEmpty(text);
    }

    protected boolean validateNonEmpty(JTextField field, String value) {
        if (!validateNonEmpty(value)) {
            Swinger.inputFocus(field);
            return false;
        }
        return true;
    }


    protected boolean validateNonEmpty(final String text) {
        return text.trim().length() > 0;
    }

    protected Action setAction(AbstractButton button, String actionCode) {
        if (button == null) {
            throw new IllegalArgumentException("Button component cannot be null");
        }

        final Action action = getActionMap().get(actionCode);
        if (action == null) {
            throw new IllegalArgumentException("Action with actionCode " + actionCode + " was not found");
        }

        button.setAction(action);
        final Object keystroke = action.getValue(Action.ACCELERATOR_KEY);
        if (keystroke != null) {
            registerKeyboardAction(action);
            final Object desc = action.getValue(Action.SHORT_DESCRIPTION);
            if (desc != null) {
                action.putValue(Action.SHORT_DESCRIPTION, desc.toString() + " (" + SwingUtils.keyStroke2String((KeyStroke) keystroke) + ")");
            }
        }
        return action;
    }

    public ActionMap getActionMap() {
        if (this.actionMap == null)
            return actionMap = Swinger.getActionMap(this.getClass(), this);
        else
            return actionMap;
    }

    protected void setContextHelp(AbstractButton btnHelp, String contextHelp) {
        final Action helpAction = getActionMap().get(HelpActions.CONTEXT_DIALOG_HELP_ACTION);
        btnHelp.setAction(helpAction);
        btnHelp.setActionCommand(contextHelp);
        btnHelp.putClientProperty(HelpActions.CONTEXT_DIALOG_HELPPROPERTY, contextHelp);
        rootPane.registerKeyboardAction(helpAction, contextHelp, (KeyStroke) helpAction.getValue(Action.ACCELERATOR_KEY), JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    protected void registerKeyboardAction(String action) {
        registerKeyboardAction(getActionMap().get(action));
    }


    protected void registerKeyboardAction(Action action) {
        registerKeyboardAction(action, (KeyStroke) action.getValue(Action.ACCELERATOR_KEY));
    }

    protected void registerKeyboardAction(Action action, KeyStroke keystroke) {
        rootPane.registerKeyboardAction(action, keystroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
    }
}