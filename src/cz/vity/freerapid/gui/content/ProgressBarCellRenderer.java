package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
 * @author Ladislav Vitasek
 */
class ProgressBarCellRenderer extends JProgressBar implements TableCellRenderer {
    private static final Color BG_RED = new Color(0xFFD0D0);
    private static final Color BG_ORANGE = new Color(0xFFEDD0);
    private static final Color BG_GREEN = new Color(0xD0FFE9);
    private static final Color BG_BLUE = new Color(0xb6e9ff);
    private String autoReconnectIn;
    private String attemptForDownloading;

    public ProgressBarCellRenderer(ApplicationContext context) {
        super(0, 100);
        final ResourceMap map = context.getResourceMap();
        autoReconnectIn = map.getString("autoreconnectIn");
        attemptForDownloading = map.getString("attemptForDownloading");
        final int h = this.getPreferredSize().height;
        this.setPreferredSize(new Dimension(70, h));
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final DownloadFile downloadFile = (DownloadFile) value;
        final DownloadState state = downloadFile.getState();
        if (state == DownloadState.DOWNLOADING || state == DownloadState.GETTING || state == DownloadState.WAITING) {
            this.setBackground(BG_GREEN);
        } else if (state == DownloadState.CANCELLED || state == DownloadState.ERROR || state == DownloadState.DELETED) {
            this.setBackground(BG_RED);
        } else if (state == DownloadState.PAUSED || state == DownloadState.DISABLED) {
            this.setBackground(Color.BLACK);
        } else if (state == DownloadState.QUEUED) {
            this.setBackground(BG_ORANGE);
        } else if (state == DownloadState.SLEEPING) {
            this.setBackground(BG_BLUE);
        } else if (state == DownloadState.COMPLETED) {
            this.setBackground(null);
            // this.setBackground(Color.GREEN);
        } else
            this.setBackground(Color.BLACK);

        final int toQueued = downloadFile.getTimeToQueued();
        if ((state == DownloadState.ERROR || state == DownloadState.SLEEPING) && toQueued >= 0) {
            final int max = downloadFile.getTimeToQueuedMax();
            this.setStringPainted(true);
            this.setString(ContentPanel.secondsToHMin(toQueued));
            this.setValue(ContentPanel.getProgress(max, toQueued));
            this.setToolTipText(String.format(autoReconnectIn, toQueued));
        } else {
            final int sleep = downloadFile.getSleep();
            if (state == DownloadState.WAITING && sleep >= 0) {
                final int max = downloadFile.getTimeToQueuedMax();
                this.setStringPainted(true);
                this.setString(ContentPanel.secondsToHMin(sleep));
                this.setValue(ContentPanel.getProgress(max, sleep));
                this.setToolTipText(String.format(attemptForDownloading, sleep));
            } else {
                this.setToolTipText(null);
                final int progress = ContentPanel.getProgress(downloadFile);

                if (AppPrefs.getProperty(UserProp.SHOW_PROGRESS_IN_PROGRESSBAR, UserProp.SHOW_PROGRESS_IN_PROGRESSBAR_DEFAULT)) {
                    this.setStringPainted(true);
                    this.setString(progress + "%");
                } else {
                    this.setStringPainted(false);
                }
                this.setValue(progress);
            }
        }
        return this;
    }

}
