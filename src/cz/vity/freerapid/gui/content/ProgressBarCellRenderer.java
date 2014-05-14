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
final class ProgressBarCellRenderer extends JProgressBar implements TableCellRenderer {
    private static final Color BG_RED = new Color(0xFFD0D0);
    private static final Color BG_ORANGE = new Color(0xFFEDD0);
    private static final Color BG_GREEN = new Color(0xD0FFE9);
    private static final Color BG_BLUE = new Color(0xB6E9FF);
    private static final Color BG_BLACK = new Color(0xFFCE9B);
    private static final Color BG_PINK = Color.PINK;
    private static final Color BG_GREY = new Color(0xAAAAAA);
    private final Color defaultColor;

    private String autoReconnectIn;
    private String attemptForDownloading;

    public ProgressBarCellRenderer(ApplicationContext context) {
        super(0, 100);
        final ResourceMap map = context.getResourceMap();
        autoReconnectIn = map.getString("autoreconnectIn");
        attemptForDownloading = map.getString("attemptForDownloading");
        final int h = this.getPreferredSize().height;
        this.setPreferredSize(new Dimension(70, h));
        defaultColor = this.getBackground();
    }

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;

        final DownloadState state = downloadFile.getState();
        switch (state) {
            case DOWNLOADING:
            case GETTING:
            case WAITING:
                this.setBackground(BG_GREEN);
                break;
            case CANCELLED:
            case ERROR:
            case DELETED:
                this.setBackground(BG_RED);
                break;
            case PAUSED:
            case DISABLED:
                this.setBackground(BG_PINK);
                break;
            case QUEUED:
                this.setBackground(BG_ORANGE);
                break;
            case SLEEPING:
            case HOLD_ON:
                this.setBackground(BG_BLUE);
                break;
            case SKIPPED:
                this.setBackground(BG_GREY);
                break;
            case COMPLETED:
                this.setBackground(defaultColor);
                break;
            default:
                assert false;
                //this.setBackground(BG_BLACK);
                break;
        }

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
        getAccessibleContext().setAccessibleName(this.getString());
        return this;
    }

}
