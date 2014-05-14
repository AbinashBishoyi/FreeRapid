package cz.vity.freerapid.swing.renderers;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

/**
 * Renderer pro hlavicku sloupce obsahujici misto textu ikonu
 *
 * @author Vity
 */
public class HeaderIconRenderer extends DefaultTableCellRenderer {
    private final Icon icon;

    public HeaderIconRenderer(Icon icon) {
        this.icon = icon;
    }


    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        // Inherit the colors and font from the header component
        if (table != null) {
            if (value == null)
                value = table.getValueAt(row, column);
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }
        }

        if (icon != null) {
            // Value is an Icon
            setIcon(icon);
            setText("");
        } else {
            // Value is text
            setText((value == null) ? "" : value.toString());
            setIcon(null);
        }
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        setHorizontalAlignment(JLabel.CENTER);
        return this;
    }

}