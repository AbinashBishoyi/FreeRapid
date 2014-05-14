package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
final class CheckedCellRenderer extends DefaultTableCellRenderer {
    private final Icon notFound;
    private final Icon checked;
    private final Icon unknown;
    private final ApplicationContext context;


    CheckedCellRenderer(ApplicationContext context) {
        this.context = context;
        final ResourceMap map = context.getResourceMap();
        this.notFound = map.getIcon("notFoundIcon");
        this.checked = map.getIcon("checkedIcon");
        this.unknown = map.getIcon("unknownIcon");
    }

    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;

        assert downloadFile != null;
        this.setIconTextGap(2);
        this.setHorizontalAlignment(CENTER);
        final Component rendererComponent = super.getTableCellRendererComponent(table, null, isSelected, hasFocus, row, column);
        switch (downloadFile.getFileState()) {
            case FILE_NOT_FOUND:
                this.setIcon(notFound);
                this.setToolTipText(context.getResourceMap().getString("checked_fileNotFound"));
                break;
            case CHECKED_AND_EXISTING:
                this.setIcon(checked);
                this.setToolTipText(context.getResourceMap().getString("checked_success"));
                break;
            default:
                this.setToolTipText(context.getResourceMap().getString("checked_unknown"));
                this.setIcon(unknown);
                break;
        }
        this.getAccessibleContext().setAccessibleName(table.getColumnName(column) + "   " + this.getToolTipText());
        return rendererComponent;
    }
}
