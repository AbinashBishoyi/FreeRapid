package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
final class SpeedCellRenderer extends DefaultTableCellRenderer {
    private ImageIcon iconImage;
    private ResourceMap map;

    SpeedCellRenderer(ApplicationContext context) {
        map = context.getResourceMap();
        iconImage = Swinger.getIconImage("speedLimitImage");
    }

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;
        this.setIcon(null);
        String tooltip = null;
        if (downloadFile.getState() == DownloadState.DOWNLOADING) {
            if (downloadFile.getSpeed() >= 0) {
                value = ContentPanel.bytesToAnother(downloadFile.getSpeed()) + "/s";
                if (downloadFile.hasSpeedLimit()) {
                    this.setIcon(iconImage);
                    tooltip = map.getString("speedLimitIsEnabled", ContentPanel.bytesToAnother((long) downloadFile.getSpeedLimit() * 1024));
                }
            } else value = "0 B/s";
            //this.setToolTipText("Average speed " + bytesToAnother((long) downloadFile.getAverageSpeed()) + "/s");
        } else value = "";
        this.setToolTipText(tooltip);
        getAccessibleContext().setAccessibleName(table.getColumnName(column) + " " + value.toString());
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
