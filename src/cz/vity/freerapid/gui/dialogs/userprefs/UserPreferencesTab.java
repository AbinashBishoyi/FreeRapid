package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.adapter.SpinnerAdapterFactory;
import com.jgoodies.binding.list.SelectionInList;
import com.jgoodies.binding.value.ValueHolder;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.forms.layout.CellConstraints;
import cz.vity.freerapid.swing.SwingUtils;
import cz.vity.freerapid.swing.binding.MyPreferencesAdapter;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;

/**
 * @author ntoskrnl
 */
public abstract class UserPreferencesTab extends JPanel {

    protected final UserPreferencesDialog dialog;
    protected final ResourceMap resourceMap;
    protected final ActionMap actionMap;
    protected boolean initialized = false;


    protected UserPreferencesTab(final UserPreferencesDialog dialog) {
        this.dialog = dialog;
        this.resourceMap = dialog.getResourceMap();
        this.actionMap = dialog.getApp().getContext().getActionManager().getActionMap(this.getClass(), this, dialog.getResourceMap());
    }

    public abstract void build(final CellConstraints cc);

    public abstract void init();

    public boolean validated() {
        return true;
    }

    public void apply() {
    }

    public void cancel() {
    }

    protected Action setAction(AbstractButton button, String actionCode) {
        if (button == null) {
            throw new IllegalArgumentException("Button component cannot be null");
        }
        final Action action = actionMap.get(actionCode);
        if (action == null) {
            throw new IllegalArgumentException("Action with actionCode " + actionCode + " was not found");
        }
        button.setAction(action);
        final Object keystroke = action.getValue(Action.ACCELERATOR_KEY);
        if (keystroke != null) {
            dialog.registerKeyboardAction(action);
            final Object desc = action.getValue(Action.SHORT_DESCRIPTION);
            if (desc != null) {
                action.putValue(Action.SHORT_DESCRIPTION, desc.toString() + " (" + SwingUtils.keyStroke2String((KeyStroke) keystroke) + ")");
            }
        }
        return action;
    }

    protected void bindCombobox(final JComboBox combobox, final String key, final Object defaultValue, final String resourceKey, final int valueCount) {
        final String[] stringList = dialog.getList(resourceKey, valueCount);
        bindCombobox(combobox, key, defaultValue, stringList);
    }

    protected void bindCombobox(final JComboBox combobox, String key, final Object defaultValue, final String[] values) {
        if (values == null)
            throw new IllegalArgumentException("List of combobox values cannot be null!!");
        final MyPreferencesAdapter adapter = new MyPreferencesAdapter(key, defaultValue);
        final SelectionInList<String> inList = new SelectionInList<String>(values, new ValueHolder(values[(Integer) adapter.getValue()]), adapter);
        Bindings.bind(combobox, inList);
    }

    protected void bind(JSpinner spinner, String key, int defaultValue, int minValue, int maxValue, int step) {
        bind(spinner, defaultValue, minValue, maxValue, step, dialog.getModel().getBufferedPreferences(key, defaultValue));
    }

    protected void bind(JSpinner spinner, int defaultValue, int minValue, int maxValue, int step, final ValueModel valueModel) {
        spinner.setModel(SpinnerAdapterFactory.createNumberAdapter(
                valueModel,
                defaultValue,
                minValue,
                maxValue,
                step));
        final JComponent editor = spinner.getEditor();
        if (editor instanceof JFormattedTextField) {
            final JFormattedTextField field = (JFormattedTextField) editor;
            field.setFocusLostBehavior(JFormattedTextField.COMMIT);
        }
    }

    protected ValueModel bind(final JCheckBox checkBox, final String key, final Object defaultValue) {
        final ValueModel valueModel = dialog.getModel().getBufferedPreferences(key, defaultValue);
        return bind(checkBox, valueModel);
    }

    protected ValueModel bind(final JCheckBox checkBox, final ValueModel valueModel) {
        Bindings.bind(checkBox, valueModel);
        return valueModel;
    }

    protected void bind(final JTextField field, final String key, final Object defaultValue) {
        Bindings.bind(field, dialog.getModel().getBufferedPreferences(key, defaultValue), false);
    }

    protected void bind(final JComboBox combobox, final String key, final Object defaultValue, final String resourceKey, final int valueCount) {
        final String[] stringList = dialog.getList(resourceKey, valueCount);
        bind(combobox, key, defaultValue, stringList);
    }

    protected void bind(final JComboBox combobox, String key, final Object defaultValue, final String[] values) {
        if (values == null)
            throw new IllegalArgumentException("List of combobox values cannot be null!!");
        final MyPreferencesAdapter adapter = new MyPreferencesAdapter(key, defaultValue);
        final SelectionInList<String> inList = new SelectionInList<String>(values, new ValueHolder(values[(Integer) adapter.getValue()]), dialog.getModel().getBufferedModel(adapter));
        Bindings.bind(combobox, inList);
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
