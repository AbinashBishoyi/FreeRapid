package cz.vity.freerapid.swing;

import cz.vity.freerapid.core.application.SubmitErrorReporter;
import cz.vity.freerapid.gui.dialogs.ErrorDialog;
import org.jdesktop.application.*;
import org.jdesktop.swingx.JXErrorPane;
import org.jdesktop.swingx.error.ErrorInfo;

import javax.swing.Action;
import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Pomocna trida pro caste volani nekterych metod. Nastavuje vychozi Look&Feel.
 *
 * @author Vity
 */
public class Swinger {
    private static final Logger logger = Logger.getLogger(Swinger.class.getName());

    private static final String MESSAGE_ERROR_TITLE_CODE = "errorMessage";
    private static final String MESSAGE_CONFIRM_TITLE_CODE = "confirmMessage";
    private static final String MESSAGE_INFORMATION_TITLE_CODE = "informationMessage";
    private static final String MESSAGE_BTN_YES_CODE = "message.button.yes";
    private static final String MESSAGE_BTN_NO_CODE = "message.button.no";
    private static final String MESSAGE_BTN_OK_CODE = "message.button.ok";
    public static final String MESSAGE_BTN_CANCEL_CODE = "message.button.cancel";

    public static final int RESULT_NO = 1;
    public static final int RESULT_YES = 0;
    public static final int RESULT_CANCEL = 1;
    public static final int RESULT_OK = 0;

    private Swinger() {
    }

    /**
     * Inicializuje akce v dane tride
     *
     * @param actionsObject trida s akcemi
     * @param context       context aplikace
     */
    public static void initActions(Object actionsObject, ApplicationContext context) {
        final ApplicationActionMap globalMap = context.getActionMap();
        final ApplicationActionMap actionMap = context.getActionMap(actionsObject);
        for (Object key : actionMap.keys()) {
            globalMap.put(key, actionMap.get(key));
        }
    }

    public static void showInformationDialog(final String message) {
        JOptionPane.showMessageDialog(getActiveWindowComponent(), message, getResourceMap().getString(MESSAGE_INFORMATION_TITLE_CODE), JOptionPane.INFORMATION_MESSAGE);
    }

    public static int getChoiceYesNoCancel(final String message) {
        final ResourceMap map = getResourceMap();
        return JOptionPane.showOptionDialog(getActiveWindowComponent(), message, map.getString(MESSAGE_CONFIRM_TITLE_CODE),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{map.getString(MESSAGE_BTN_YES_CODE), map.getString(MESSAGE_BTN_NO_CODE),
                map.getString(MESSAGE_BTN_CANCEL_CODE)},
                map.getString(MESSAGE_BTN_YES_CODE));
    }

    public static int getChoiceYesNoCancel(final String messageCode, Object... args) {
        final ResourceMap map = getResourceMap();
        return getChoiceYesNoCancel(map.getString(messageCode, args));
    }

    public static int getChoiceOKCancel(final String messageCode, Object... args) {
        final ResourceMap map = getResourceMap();
        return JOptionPane.showOptionDialog(getActiveWindowComponent(), map.getString(messageCode, args), map.getString(MESSAGE_CONFIRM_TITLE_CODE),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{map.getString(MESSAGE_BTN_OK_CODE), map.getString(MESSAGE_BTN_CANCEL_CODE)},
                map.getString(MESSAGE_BTN_OK_CODE));
    }

    /**
     * Vrati obrazek podle key property v resourcu
     * Nenajde-li se obrazek pod danym kodem, vypise WARNING pokud neni obrazek nalezen
     *
     * @param imagePropertyCode kod obrazku
     * @return obrazek
     */
    public static ImageIcon getIconImage(final String imagePropertyCode) {
        final ResourceMap map = getResourceMap();
        return getIconImage(map, imagePropertyCode);
    }

    /**
     * Vrati obrazek podle key property v resourcu
     * Nenajde-li se obrazek pod danym kodem, vypise WARNING pokud neni obrazek nalezen
     *
     * @param map               resourcemapa
     * @param imagePropertyCode kod obrazku
     * @return obrazek
     */
    public static ImageIcon getIconImage(ResourceMap map, final String imagePropertyCode) {
        final ImageIcon imageIcon = map.getImageIcon(imagePropertyCode);
        if (imageIcon == null)
            logger.warning("Invalid image property code:" + imagePropertyCode);
        return imageIcon;
    }

    public static ResourceMap getResourceMap() {
        return getContext().getResourceManager().getResourceMap();
    }

    public static ResourceMap getResourceMap(final Class className) {
        final ResourceManager rm = getContext().getResourceManager();
        return rm.getResourceMap(className);
    }

    public static ResourceMap getResourceMap(final Class className, final Class stopClass) {
        final ResourceManager rm = getContext().getResourceManager();
        return rm.getResourceMap(className, stopClass);
    }

    public static Action getAction(Object actionName) {
        final Action action = getContext().getActionMap().get(actionName);
        if (action == null) {
            throw new IllegalStateException("Action with a name \"" + actionName + "\" does not exist.");
        }
        return action;
    }

    public static ActionMap getActionMap(Class aClass, Object actionsObject) {
        return getContext().getActionMap(aClass, actionsObject);
    }


    public static String[] getList(ResourceMap resourceMap, final String key, final int valueCount) {
        final String[] list = new String[valueCount];
        for (int i = 0; i < valueCount; i++) {
            list[i] = resourceMap.getString(key + "_" + i);
        }
        return list;
    }

    private static ApplicationContext getContext() {
        return Application.getInstance().getContext();
    }

    public static ActionMap getActionMap(Object actionsObject) {
        return getContext().getActionMap(actionsObject);
    }

    public static void showErrorMessage(ResourceMap map, final String message, final Object... args) {
        JOptionPane.showMessageDialog(getActiveWindowComponent(), (map.containsKey(message)) ? map.getString(message, args) : message, getResourceMap().getString("errorMessage"), JOptionPane.ERROR_MESSAGE);
    }

    public static void showErrorMessage(ResourceMap map, final Throwable cause) {
        JOptionPane.showMessageDialog(getActiveWindowComponent(), getMessageFromException(map, cause), getResourceMap().getString("errorMessage"), JOptionPane.ERROR_MESSAGE);
    }

    public static int showOptionDialog(ResourceMap map, final int messageType, final String titleCode, final String messageCode, final String[] buttons, final Object... args) {
        return showOptionDialog(map, true, messageType, titleCode, messageCode, buttons, args);
    }

    public static int showOptionDialog(ResourceMap map, final boolean bringToFront, final int messageType, final String titleCode, final String messageCode, final String[] buttons, final Object... args) {
        final ResourceMap mainMap = getResourceMap();
        final Object[] objects = new Object[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final String s = map.getString(buttons[i]);
            assert s != null;
            objects[i] = s;
        }
        final Frame frame = getActiveFrame();
        if (bringToFront) {
            bringToFront(frame, true);
        }
        return JOptionPane.showOptionDialog(getActiveWindowComponent(), map.getString(messageCode, args), mainMap.getString(titleCode), JOptionPane.NO_OPTION, messageType, null, objects, objects[0]);
    }

    public static int showInputDialog(final String title, final Object inputObject, boolean cancelButton) {
        final ResourceMap mainMap = getResourceMap();
        final String[] buttons;
        if (cancelButton)
            buttons = new String[]{MESSAGE_BTN_OK_CODE, MESSAGE_BTN_CANCEL_CODE};
        else
            buttons = new String[]{MESSAGE_BTN_OK_CODE};
        final Object[] objects = new Object[buttons.length];
        for (int i = 0; i < buttons.length; i++) {
            final String s = mainMap.getString(buttons[i]);
            assert s != null;
            objects[i] = s;
        }
        final Frame frame = getActiveFrame();
        bringToFront(frame, true);
        if (inputObject instanceof JComponent) {
            final Timer timer = new Timer(500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Swinger.inputFocus((JComponent) inputObject);
                }
            });
            timer.setRepeats(false);
            timer.start();
        }
        return JOptionPane.showOptionDialog(frame, inputObject, title, JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, objects, objects[0]);
    }

    public static void bringToFront(Frame frame, boolean activate) {
        if (frame != null) {
            int state = frame.getExtendedState();
            if ((state & JFrame.ICONIFIED) == 1)
                frame.setExtendedState(state &= ~Frame.ICONIFIED);
            frame.setVisible(true);
            if (activate)
                frame.toFront();
        }
    }

    public static void showMessage(ResourceMap map, final String message, final Object... args) {
        JOptionPane.showMessageDialog(getActiveFrame(), map.getString(message, args), getResourceMap().getString("errorMessage", args), JOptionPane.ERROR_MESSAGE);
    }

    public static void inputFocus(final JComboBox combo) {
        inputFocus((JComponent) combo.getEditor().getEditorComponent());
    }

    public static void inputFocus(final JComponent field) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                field.grabFocus();
                field.requestFocus();
            }
        });
    }

    public static TableColumn updateColumn(JTable table, String name, final int columnId, final int minWidth, final int width, TableCellRenderer renderer) {
        final TableColumnModel columnModel = table.getColumnModel();
        TableColumn column = columnModel.getColumn(columnId);
        column.setIdentifier(name);
        if (renderer != null)
            column.setCellRenderer(renderer);
        if (width != -1) {
            column.setPreferredWidth(width);
        }
        if (minWidth != -1)
            column.setMinWidth(width);
        return column;
    }

    public static TableColumn updateColumn(JTable table, String name, final int columnId, final int width) {
        return updateColumn(table, name, columnId, width, width, null);
    }

    public static void showErrorDialog(Class clazz, final String messageResource, final Throwable e, final boolean showErrorReporter) {
        showErrorDialog(Swinger.getResourceMap(clazz), messageResource, e, showErrorReporter);
    }

    public static void showErrorDialog(Class clazz, final String messageResource, final Throwable e) {
        showErrorDialog(Swinger.getResourceMap(clazz), messageResource, e, false);
    }

    public static void showErrorDialog(final String messageResource, final Throwable e, final boolean showErrorReporter) {
        showErrorDialog(ErrorDialog.class, messageResource, e, showErrorReporter);
    }

    public static void showErrorDialog(final String messageResource, final Throwable e) {
        showErrorDialog(ErrorDialog.class, messageResource, e, false);
    }

    public static void showErrorDialog(ResourceMap map, final String messageResource, final Throwable e) {
        showErrorDialog(map, messageResource, e, false);
    }

    private static void showErrorDialog(ResourceMap map, final String messageResource, final Throwable e, boolean showErrorReporter) {
        if (map == null)
            throw new IllegalArgumentException("Map cannot be null");
        String localizedMessage = e.getLocalizedMessage();
        if (localizedMessage == null)
            localizedMessage = "";
        final String text = map.getString(messageResource, localizedMessage);
        showErrorDialog(text, false, e, showErrorReporter);
    }

    private static void showErrorDialog(final String message, final boolean isMesssageResourceKey, final Throwable e, boolean showErrorReporter) {
        final ResourceMap map = getResourceMap();
        final ErrorInfo errorInfo = new ErrorInfo(map.getString(MESSAGE_ERROR_TITLE_CODE), (isMesssageResourceKey) ? map.getString(message) : message, null, "EDT Thread", e, Level.SEVERE, null);
        final JXErrorPane pane = new JXErrorPane();
        if (showErrorReporter)
            pane.setErrorReporter(new SubmitErrorReporter());

        pane.setErrorInfo(errorInfo);
        JXErrorPane.showDialog(getActiveFrame(), pane);
    }

    public static Component getActiveWindowComponent() {
        final Window window = getActiveDialog();
        if (window == null)
            return getActiveFrame();
        return window;
    }

    public static JComponent getTitleComponent(final String title) {
        return getTitleComponent(new JLabel(title));
    }

    public static JComponent getTitleComponent2(final String titleCode) {
        final JLabel label = new JLabel();
        label.setName(titleCode);

        label.setFont(label.getFont().deriveFont(Font.BOLD, 16));
        return getTitleComponent(label);
    }

    private static JComponent getTitleComponent(JLabel label) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new GridBagLayout());
        toolBar.setFloatable(false);
        toolBar.setBorderPainted(false);
        toolBar.setOpaque(false);
        toolBar.add(label, new GridBagConstraints(0, 0, 1, 2, 0, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 4, 0, 2), 0, 0));
        toolBar.add(new JSeparator(SwingConstants.HORIZONTAL), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(4, 0, 0, 0), 0, 0));
        return toolBar;
    }

    public static int getChoiceYesNo(final String message) {
        final ResourceMap map = getResourceMap();
        return JOptionPane.showOptionDialog(getActiveWindowComponent(), message, map.getString(MESSAGE_CONFIRM_TITLE_CODE),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null, new Object[]{map.getString(MESSAGE_BTN_YES_CODE), map.getString(MESSAGE_BTN_NO_CODE)},
                null);
    }

    public static void minimize(JFrame frame) {
        int state = frame.getExtendedState();
        // Set the iconified bit
        state |= Frame.ICONIFIED;
        // Iconify the frame
        frame.setExtendedState(state);
    }

    public static Frame getActiveFrame() {
        final Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame.isActive())
                return frame;
        }
        return frames.length > 0 ? frames[0] : null;
    }

    public static Window getActiveDialog() {
        final Window[] frames = Dialog.getWindows();
        for (Window frame : frames) {
            if (frame.isActive())
                return frame;
        }
        return frames.length > 0 ? frames[0] : null;
    }

    public static int[] getSelectedRows(final JTable table) {
        final int[] ints = table.getSelectedRows();

        final int length = ints.length;

        int count = 0;
        for (int i = 0; i < length; i++) {
            final int rowIndex = ints[i];
            if (rowIndex < table.getModel().getRowCount() && rowIndex < table.getRowSorter().getViewRowCount())
                ints[count++] = table.convertRowIndexToModel(rowIndex);
        }

        if (count > 0) {
            return Arrays.copyOfRange(ints, 0, count);
        } else return new int[0];

    }

    public static String getMessageFromException(ResourceMap map, Throwable cause) {
        final String localizedMessage = cause.getLocalizedMessage();
        final String msg;
        if (localizedMessage != null) {
            if (map.containsKey(localizedMessage))
                msg = map.getString(localizedMessage);
            else
                msg = localizedMessage;
        } else {
            final String errorMessage = cause.getMessage();
            if (errorMessage != null && map.containsKey(errorMessage))
                msg = map.getString(errorMessage);
            else
                msg = errorMessage;
        }
        return msg;
    }

    public static int convertRowIndexToView(JTable table, int row) {
        if (row <= -1 || row >= table.getModel().getRowCount())
            return -1;
        else return table.convertRowIndexToView(row);
    }

}
