package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
class ProgressCellRenderer extends DefaultTableCellRenderer {

    ProgressCellRenderer() {
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final DownloadFile downloadFile = (DownloadFile) value;
        this.setHorizontalAlignment(CENTER);
        final int progress = ContentPanel.getProgress(downloadFile);
        value = progress + "%";
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
