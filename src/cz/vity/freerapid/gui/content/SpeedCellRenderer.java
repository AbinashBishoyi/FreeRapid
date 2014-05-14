package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
final class SpeedCellRenderer extends DefaultTableCellRenderer {

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;
        if (downloadFile.getState() == DownloadState.DOWNLOADING) {
            if (downloadFile.getSpeed() >= 0) {
                value = ContentPanel.bytesToAnother(downloadFile.getSpeed()) + "/s";
            } else value = "0 B/s";
            //this.setToolTipText("Average speed " + bytesToAnother((long) downloadFile.getAverageSpeed()) + "/s");
        } else value = "";
        getAccessibleContext().setAccessibleName(table.getColumnName(column) + " " + value.toString());
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
