package cz.vity.freerapid.swing.components;

import com.l2fprod.common.swing.JButtonBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

/**
 * @author Ladislav Vitasek
 */
public class EnhancedToolbar extends JButtonBar {
    private boolean isCyclic;
    private boolean isGroupSelectionFollowFocus;

    /**
     * {@inheritDoc}
     */
    public EnhancedToolbar() {
        super();
        init();
    }


    private void init() {
        setFocusTraversalPolicyProvider(true);
        setFocusTraversalPolicy(new JXButtonPanelFocusTraversalPolicy());
        ActionListener actionHandler = new ActionHandler();
        registerKeyboardAction(actionHandler, ActionHandler.FORWARD,
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        registerKeyboardAction(actionHandler, ActionHandler.FORWARD,
                KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        registerKeyboardAction(actionHandler, ActionHandler.BACKWARD,
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        setGroupSelectionFollowFocus(true);
        this.setCyclic(true);
    }

    /**
     * Returns whether arrow keys should support
     * cyclic focus traversal ordering for for this JXButtonPanel.
     */
    public boolean isCyclic() {
        return isCyclic;
    }

    /**
     * Sets whether arrow keys should support
     * cyclic focus traversal ordering for this JXButtonPanel.
     */
    public void setCyclic(boolean isCyclic) {
        this.isCyclic = isCyclic;
    }

    /**
     * Returns whether arrow keys should transfer button's
     * selection as well as focus for this JXButtonPanel.<p>
     * <p/>
     * Note: this property affects buttons which are added to a ButtonGroup
     */
    public boolean isGroupSelectionFollowFocus() {
        return isGroupSelectionFollowFocus;
    }

    /**
     * Sets whether arrow keys should transfer button's
     * selection as well as focus for this JXButtonPanel.<p>
     * <p/>
     * Note: this property affects buttons which are added to a ButtonGroup
     */
    public void setGroupSelectionFollowFocus(boolean groupSelectionFollowFocus) {
        isGroupSelectionFollowFocus = groupSelectionFollowFocus;
    }

    private static ButtonGroup getButtonGroup(AbstractButton button) {
        ButtonModel model = button.getModel();
        if (model instanceof DefaultButtonModel) {
            return ((DefaultButtonModel) model).getGroup();
        }
        return null;
    }

    private class ActionHandler implements ActionListener {
        private static final String FORWARD = "moveSelectionForward";
        private static final String BACKWARD = "moveSelectionBackward";

        public void actionPerformed(ActionEvent e) {
            FocusTraversalPolicy ftp = EnhancedToolbar.this.getFocusTraversalPolicy();

            if (ftp instanceof JXButtonPanelFocusTraversalPolicy) {
                JXButtonPanelFocusTraversalPolicy xftp =
                        (JXButtonPanelFocusTraversalPolicy) ftp;

                String actionCommand = e.getActionCommand();
                Component fo =
                        KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
                Component next;

                xftp.setAlternativeFocusMode(true);

                if (FORWARD.equals(actionCommand)) {
                    next = xftp.getComponentAfter(EnhancedToolbar.this, fo);
                } else if (BACKWARD.equals(actionCommand)) {
                    next = xftp.getComponentBefore(EnhancedToolbar.this, fo);
                } else {
                    throw new AssertionError("Unexpected action command: " + actionCommand);
                }

                xftp.setAlternativeFocusMode(false);

                if (fo instanceof AbstractButton) {
                    AbstractButton b = (AbstractButton) fo;
                    b.getModel().setPressed(false);
                }
                if (next != null) {
                    if (fo instanceof AbstractButton && next instanceof AbstractButton) {
                        ButtonGroup group = getButtonGroup((AbstractButton) fo);
                        AbstractButton nextButton = (AbstractButton) next;
                        if (group != getButtonGroup(nextButton)) {
                            return;
                        }
                        if (isGroupSelectionFollowFocus() && group != null &&
                                group.getSelection() != null && !nextButton.isSelected()) {
                            nextButton.doClick();
                        }
                        next.requestFocusInWindow();
                    }
                }
            }
        }
    }

    private class JXButtonPanelFocusTraversalPolicy extends LayoutFocusTraversalPolicy {
        private boolean isAlternativeFocusMode;

        public boolean isAlternativeFocusMode() {
            return isAlternativeFocusMode;
        }

        public void setAlternativeFocusMode(boolean alternativeFocusMode) {
            isAlternativeFocusMode = alternativeFocusMode;
        }

        protected boolean accept(Component c) {
            if (!isAlternativeFocusMode() && c instanceof AbstractButton) {
                AbstractButton button = (AbstractButton) c;
                ButtonGroup group = EnhancedToolbar.getButtonGroup(button);
                if (group != null && group.getSelection() != null
                        && !button.isSelected()) {
                    return false;
                }
            }
            return super.accept(c);
        }

        public Component getComponentAfter(Container aContainer, Component aComponent) {
            Component componentAfter = super.getComponentAfter(aContainer, aComponent);
            if (!isAlternativeFocusMode()) {
                return componentAfter;
            }
            if (EnhancedToolbar.this.isCyclic()) {
                return componentAfter == null ?
                        getFirstComponent(aContainer) : componentAfter;
            }
            if (aComponent == getLastComponent(aContainer)) {
                return aComponent;
            }
            return componentAfter;
        }

        public Component getComponentBefore(Container aContainer, Component aComponent) {
            Component componentBefore = super.getComponentBefore(aContainer, aComponent);
            if (!isAlternativeFocusMode()) {
                return componentBefore;
            }
            if (EnhancedToolbar.this.isCyclic()) {
                return componentBefore == null ?
                        getLastComponent(aContainer) : componentBefore;
            }
            if (aComponent == getFirstComponent(aContainer)) {
                return aComponent;
            }
            return componentBefore;
        }
    }
}