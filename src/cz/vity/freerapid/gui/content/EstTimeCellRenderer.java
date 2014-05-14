package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.gui.actions.DownloadsActions;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.Task;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ladislav Vitasek
 */
final class EstTimeCellRenderer extends DefaultTableCellRenderer {
    private final String tooltip;
    private final String elapsedTime;

    EstTimeCellRenderer(ApplicationContext context) {
        final ResourceMap map = context.getResourceMap();
        tooltip = map.getString("tooltip");
        elapsedTime = map.getString("elapsedTime");
    }

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;

        final DownloadState state = downloadFile.getState();
        value = ContentPanel.stateToString(state);
        this.setHorizontalAlignment(CENTER);
        this.setToolTipText(null);
        if (state == DownloadState.DOWNLOADING) {
            long hasToBeDownloaded = downloadFile.getFileSize() - downloadFile.getDownloaded();
            final double avgSpeed = downloadFile.getShortTimeAvgSpeed();
            if (hasToBeDownloaded >= 0) {
                if (avgSpeed > 0) {
                    value = ContentPanel.secondsToHMin(Math.round((double) hasToBeDownloaded / avgSpeed));
                }
            }
        } else if (state == DownloadState.WAITING) {
//                if (downloadFile.getSleep() >= 0)
//                    value = String.format("%s (%s)", stateToString(state), secondsToHMin(downloadFile.getSleep()));
//                else value = "";
        }
        if (state == DownloadState.ERROR || state == DownloadState.SLEEPING || state == DownloadState.DISABLED || state == DownloadState.SKIPPED) {
            final String errorMessage = downloadFile.getErrorMessage();
            if (errorMessage != null) {
                if (state == DownloadState.ERROR || state == DownloadState.DISABLED)
                    value = value + " - " + errorMessage.replaceAll("<.*?>", "");
                this.setToolTipText(String.format(tooltip, errorMessage));
            }
        } else if (DownloadsActions.isProcessState(state)) {
            Task task = downloadFile.getTask();
            if (task != null)
                this.setToolTipText(String.format(elapsedTime, ContentPanel.secondsToHMin(task.getExecutionDuration(TimeUnit.SECONDS))));
        }
        final Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        getAccessibleContext().setAccessibleName(table.getColumnName(column) + " " + value);
        getAccessibleContext().setAccessibleDescription(table.getColumnName(column) + " " + value);
        return comp;
    }
}
