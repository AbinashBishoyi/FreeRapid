package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
class SpeedCellRenderer extends DefaultTableCellRenderer {

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final DownloadFile downloadFile = (DownloadFile) value;
        if (downloadFile.getState() == DownloadState.DOWNLOADING) {
            if (downloadFile.getSpeed() >= 0) {
                value = ContentPanel.bytesToAnother(downloadFile.getSpeed()) + "/s";
            } else value = "0 B/s";
            //this.setToolTipText("Average speed " + bytesToAnother((long) downloadFile.getAverageSpeed()) + "/s");
        } else value = "";
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
