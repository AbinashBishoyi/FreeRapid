package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.model.DownloadFile;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final class NameURLCellRenderer extends DefaultTableCellRenderer {

    private final FileTypeIconProvider iconProvider;
    private final static Logger logger = Logger.getLogger(NameURLCellRenderer.class.getName());


    NameURLCellRenderer(FileTypeIconProvider iconProvider) {
        this.iconProvider = iconProvider;
    }

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;

        final String fn = downloadFile.getFileName();
        final String url = downloadFile.getFileUrl().toString();
        if (fn != null && !fn.isEmpty()) {
            value = fn;
        } else {
            value = url;
        }
        super.getTableCellRendererComponent(table, " " + value, isSelected, hasFocus, row, column);
        getAccessibleContext().setAccessibleName(table.getColumnName(column) + "   " + value);
//        getAccessibleContext().setAccessibleDescription((String) value);

        //this.setForeground(Color.BLUE);
        if (value != null) {
            this.setToolTipText(url);
            this.setIcon(iconProvider.getIconImageByFileType(downloadFile.getFileType(), false));
        }

        return this;
    }

}
