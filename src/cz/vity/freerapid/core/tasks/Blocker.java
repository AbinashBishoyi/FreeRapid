package cz.vity.freerapid.core.tasks;

import org.jdesktop.application.*;
import org.jdesktop.swingx.JXFrame;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

class ScreenInputBlocker extends Task.InputBlocker {
    private static final Logger logger = Logger.getLogger(ScreenInputBlocker.class.getName());
    private JDialog modalDialog = null;
    private boolean showWaitCursor = false;


    ScreenInputBlocker(Task task, Task.BlockingScope scope, Object target, ApplicationAction action) {
        super(task, scope, target, action);
    }

    private void setActionTargetBlocked(boolean f) {
        javax.swing.Action action = (javax.swing.Action) getTarget();
        action.setEnabled(!f);
    }

    private void setComponentTargetBlocked(boolean f) {
        Component c = (Component) getTarget();
        c.setEnabled(!f);
        // Note: can't set the cursor on a disabled component
    }

    private void setScreenBlocked(final boolean enabled) {
        final JXFrame frame = (JXFrame) ((SingleFrameApplication) Application.getInstance()).getMainFrame();
        frame.setWaiting(enabled);
    }


    /* Accumulates a list of all of the descendants of root whose name
    * begins with "BlockingDialog"
    */
    private void blockingDialogComponents(Component root, java.util.List<Component> rv) {
        String rootName = root.getName();
        if ((rootName != null) && rootName.startsWith("BlockingDialog")) {
            rv.add(root);
        }
        if (root instanceof Container) {
            for (Component child : ((Container) root).getComponents()) {
                blockingDialogComponents(child, rv);
            }
        }
    }

    private java.util.List<Component> blockingDialogComponents(Component root) {
        java.util.List<Component> rv = new ArrayList<Component>();
        blockingDialogComponents(root, rv);
        return rv;
    }

    /* Inject resources from both the Task's ResourceMap and the
     * ApplicationAction's ResourceMap.  We add the action's name
     * prefix to all of the components before the second step.
     */
    private void injectBlockingDialogComponents(Component root) {
        final ResourceMap taskResourceMap = ((CoreTask) getTask()).getTaskResourceMap();
        if (taskResourceMap != null) {
            taskResourceMap.injectComponents(root);
        }
        ApplicationAction action = getAction();
        if (action != null) {
            ResourceMap actionResourceMap = action.getResourceMap();
            String actionName = action.getName();
            for (Component c : blockingDialogComponents(root)) {
                c.setName(actionName + "." + c.getName());
            }
            actionResourceMap.injectComponents(root);
        }
    }

    /* Creates a dialog whose visuals are initialized from the
     * following Task resources:
     * BlockingDialog.title
     * BlockingDialog.optionPane.icon
     * BlockingDialog.optionPane.message
     * BlockingDialog.cancelButton.text
     * BlockingDialog.cancelButton.icon
     * BlockingDialog.progressBar.stringPainted
     *
     * If the Task has an Action then use the actionName as a prefix
     * and look up the resources again, in the action's ResourceMap
     * (that's the @Action's ApplicationActionMap ResourceMap really):
     * actionName.BlockingDialog.title
     * actionName.BlockingDialog.optionPane.icon
     * actionName.BlockingDialog.optionPane.message
     * actionName.BlockingDialog.cancelButton.text
     * actionName.BlockingDialog.cancelButton.icon
     * actionName.BlockingDialog.progressBar.stringPainted
     */
    private JDialog createBlockingDialog() {
        JOptionPane optionPane = new JOptionPane();
        /* If the task can be canceled, then add the cancel
         * button.  Otherwise clear the default OK button.
         */
        if (getTask().getUserCanCancel()) {
            JButton cancelButton = new JButton();
            cancelButton.setName("BlockingDialog.cancelButton");
            ActionListener doCancelTask = new ActionListener() {
                public void actionPerformed(ActionEvent ignore) {
                    getTask().cancel(true);
                }
            };
            cancelButton.addActionListener(doCancelTask);
            optionPane.setOptions(new Object[]{cancelButton});
        } else {
            optionPane.setOptions(new Object[]{}); // no OK button
        }
        /* Create the JDialog.  If the task can be canceled, then
         * map closing the dialog window to canceling the task.
         */
        if (showWaitCursor)
            optionPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        Component dialogOwner = (Component) getTarget();
        String taskTitle = getTask().getTitle();
        String dialogTitle = (taskTitle == null) ? "BlockingDialog" : taskTitle;
        final JDialog dialog = optionPane.createDialog(dialogOwner, dialogTitle);
        dialog.setModal(true);
        dialog.setName("BlockingDialog");
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        WindowListener dialogCloseListener = new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (getTask().getUserCanCancel()) {
                    getTask().cancel(true);
                    dialog.setVisible(false);
                }
            }
        };
        dialog.addWindowListener(dialogCloseListener);
        optionPane.setName("BlockingDialog.optionPane");
        injectBlockingDialogComponents(dialog);
        /* Reset the JOptionPane's message property after injecting
         * an initial value for the message string.
         */
        recreateOptionPaneMessage(optionPane);
        dialog.pack();
        dialog.setMinimumSize(new Dimension(150, 130));
        return dialog;
    }

    /* Replace the default message panel with one that where the
     * message text can be selected and that includes a status bar for
     * task progress.  We inject resources here because the
     * JOptionPane#setMessage() doesn't add the panel to the JOptionPane
     * immediately.
     */
    private void recreateOptionPaneMessage(JOptionPane optionPane) {
        Object message = optionPane.getMessage();
        if (message instanceof String) {
            final JLabel label = new JLabel((String) message);
            label.setText("                                                                                                  \n\n\n\n\n");
            JPanel panel = new JPanel(new BorderLayout());
            panel.add(label, BorderLayout.CENTER);
            final JProgressBar progressBar = new JProgressBar();
            progressBar.setName("BlockingDialog.progressBar");
            progressBar.setIndeterminate(true);
            PropertyChangeListener taskPCL = new PropertyChangeListener() {
                public void propertyChange(PropertyChangeEvent e) {
                    if ("progress".equals(e.getPropertyName())) {
                        progressBar.setIndeterminate(false);
                        progressBar.setValue((Integer) e.getNewValue());
                        updateStatusBarString(progressBar);
                    } else if ("message".equals(e.getPropertyName())) {
                        label.setText((String) e.getNewValue());
                    }
                }
            };
            getTask().addPropertyChangeListener(taskPCL);
            panel.add(progressBar, BorderLayout.SOUTH);
            injectBlockingDialogComponents(panel);
            optionPane.setMessage(panel);
        }
    }

    private void updateStatusBarString(JProgressBar progressBar) {
        if (!progressBar.isStringPainted()) {
            return;
        }
        /* The initial value of the progressBar string is the format.
         * We save the format string in a client property.  The format
         * String will be applied four values (see below).  The default
         * format String is in resources/Application.properties, it's:
         * "%02d:%02d, %02d:%02d remaining"
         */
        String key = "progressBarStringFormat";
        if (progressBar.getClientProperty(key) == null) {
            progressBar.putClientProperty(key, progressBar.getString());
        }
        String fmt = (String) progressBar.getClientProperty(key);
        if (progressBar.getValue() <= 0) {
            progressBar.setString("");
        } else if (fmt == null) {
            progressBar.setString(null);
        } else {
            double pctComplete = progressBar.getValue() / 100.0;
            long durSeconds = getTask().getExecutionDuration(TimeUnit.SECONDS);
            long durMinutes = durSeconds / 60;
            long remSeconds = (long) (0.5 + ((double) durSeconds / pctComplete)) - durSeconds;
            long remMinutes = remSeconds / 60;
            String s = String.format(fmt, durMinutes, durSeconds - (durMinutes * 60),
                    remMinutes, remSeconds - (remMinutes * 60));
            progressBar.setString(s);
        }

    }

    private void showBusyGlassPane(boolean f) {
        RootPaneContainer rpc = null;
        Component root = (Component) getTarget();
        while (root != null) {
            if (root instanceof RootPaneContainer) {
                rpc = (RootPaneContainer) root;
                break;
            }
            root = root.getParent();
        }
        if (rpc != null) {
            String oldGPKey = "OldGlassPane";
            if (f) {
                JComponent glassPane = new BusyGlassPane();
                InputVerifier retainFocusWhileVisible = new InputVerifier() {
                    public boolean verify(JComponent c) {
                        return !c.isVisible();
                    }
                };
                glassPane.setInputVerifier(retainFocusWhileVisible);
                Component oldGlassPane = rpc.getGlassPane();
                rpc.getRootPane().putClientProperty(oldGPKey, oldGlassPane);
                rpc.setGlassPane(glassPane);
                glassPane.setVisible(true);
                glassPane.revalidate();
            } else {
                Component oldGlassPane = (Component) rpc.getRootPane().getClientProperty(oldGPKey);
                if (!oldGlassPane.isVisible()) {
                    rpc.getGlassPane().setVisible(false);
                }
                rpc.setGlassPane(oldGlassPane); // sets oldGlassPane.visible
            }
        }
    }

    /* Note: unfortunately, the busy cursor is reset when the modal
     * dialog is shown.
     */
    private static class BusyGlassPane extends JPanel {
        BusyGlassPane() {
            super(null, false);
            setVisible(false);
            setOpaque(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            MouseInputListener blockMouseEvents = new MouseInputAdapter() {
            };
            addMouseMotionListener(blockMouseEvents);
            addMouseListener(blockMouseEvents);
        }
    }

    /* If an action was specified then return the value of the
     * actionName.BlockingDialogTimer.delay resource from the action's
     * resourceMap.  Otherwise return the value of the
     * BlockingDialogTimer.delay resource from the Task's ResourceMap.
     * The latter's default in defined in resources/Application.properties.
     */
    private int blockingDialogDelay() {
        Integer delay = null;
        String key = "BlockingDialogTimer.delay";
        ApplicationAction action = getAction();
        if (action != null) {
            ResourceMap actionResourceMap = action.getResourceMap();
            String actionName = action.getName();
            delay = actionResourceMap.getInteger(actionName + "." + key);
        }
        ResourceMap taskResourceMap = ((CoreTask) getTask()).getTaskResourceMap();
        if ((delay == null) && (taskResourceMap != null)) {
            delay = taskResourceMap.getInteger(key);
        }
        return (delay == null) ? 0 : delay;
    }

    private void showBlockingDialog(boolean f) {
        if (f) {
            if (modalDialog != null) {
                String msg = String.format("unexpected InputBlocker state [%s] %s", f, this);
                logger.warning(msg);
                modalDialog.dispose();
            }
            modalDialog = createBlockingDialog();
            ActionListener showModalDialog = new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    if (modalDialog != null) { // already dismissed
                        modalDialog.setVisible(true);
                    }
                }
            };
            Timer showModalDialogTimer = new Timer(blockingDialogDelay(), showModalDialog);
            showModalDialogTimer.setRepeats(false);
            showModalDialogTimer.start();
        } else {
            if (modalDialog != null) {
                modalDialog.dispose();
                modalDialog = null;
            } else {
                String msg = String.format("unexpected InputBlocker state [%s] %s", f, this);
                logger.warning(msg);
            }
        }
    }

    @Override
    protected void block() {
        switch (getScope()) {
            case ACTION:
                setActionTargetBlocked(true);
                break;
            case COMPONENT:
                setComponentTargetBlocked(true);
                break;
            case WINDOW:
            case APPLICATION:
                showBusyGlassPane(true);
                showBlockingDialog(true);
                break;
        }
    }

    @Override
    public void unblock() {
        switch (getScope()) {
            case ACTION:
                setActionTargetBlocked(false);
                break;
            case COMPONENT:
                setComponentTargetBlocked(false);
                break;
            case WINDOW:
            case APPLICATION:
                showBusyGlassPane(false);
                showBlockingDialog(false);
                break;
        }
    }

    public void setShowWaitCursor(boolean showWaitCursor) {
        this.showWaitCursor = showWaitCursor;
    }
}
