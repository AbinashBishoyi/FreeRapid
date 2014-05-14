package cz.vity.freerapid.swing.renderers;

import javax.swing.*;
import java.awt.*;

/**
 * Jednoduchy combobox renderer podporujici '-' separator ve vyberu
 *
 * @author Vity
 */
public class ComboBoxRenderer extends JLabel implements ListCellRenderer {
    static final String SEPARATOR = "-";
    JSeparator separator;

    public ComboBoxRenderer() {
        setOpaque(true);
        setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
        separator = new JSeparator(JSeparator.HORIZONTAL);
    }

    public Component getListCellRendererComponent(JList list,
                                                  Object value, int index, boolean isSelected, boolean cellHasFocus) {
        if (value == null)
            value = list.getModel().getElementAt(index);
        if (SEPARATOR.equals(value)) {
            return separator;
        }
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        String str = (value == null) ? "" : value.toString();
        setFont(list.getFont());
        setText(str);
        return this;
    }
}
