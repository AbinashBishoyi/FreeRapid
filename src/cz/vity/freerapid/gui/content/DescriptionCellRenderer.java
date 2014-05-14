package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Iman Reihanian
 */
public class DescriptionCellRenderer extends DefaultTableCellRenderer {

    private String tooltip;

    public DescriptionCellRenderer(ApplicationContext context) {
        tooltip = context.getResourceMap().getString("tooltip");
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        DownloadFile downloadFile = (DownloadFile) value;
        String description = downloadFile.getDescription();
        if (description == null) {
            setToolTipText(null);
            getAccessibleContext().setAccessibleName(table.getColumnName(column));
        } else {
            setToolTipText(String.format(tooltip, description));
            getAccessibleContext().setAccessibleName(table.getColumnName(column) + " " + description);
        }

        return super.getTableCellRendererComponent(table, description, isSelected, hasFocus, row, column);
    }

}
