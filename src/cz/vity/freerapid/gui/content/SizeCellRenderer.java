package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.model.DownloadFile;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.text.NumberFormat;

/**
 * @author Ladislav Vitasek
 */
class SizeCellRenderer extends DefaultTableCellRenderer {
    private final String sizeRendererProgress;
    private final String sizeRendererUnknown;
    private final String sizeRendererInBytes;
    private NumberFormat numberFormatter;

    SizeCellRenderer(ApplicationContext context) {
        final ResourceMap map = context.getResourceMap();
        sizeRendererProgress = map.getString("sizeRendererProgress");
        sizeRendererUnknown = map.getString("sizeRendererUnknown");
        sizeRendererInBytes = map.getString("sizeRendererInBytes");
        numberFormatter = NumberFormat.getIntegerInstance();
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        final DownloadFile downloadFile = (DownloadFile) value;
        final long fs = downloadFile.getFileSize();
        if (fs >= 0) {
            if (downloadFile.getDownloaded() != fs)
                value = String.format(sizeRendererProgress, ContentPanel.bytesToAnother(downloadFile.getDownloaded()), ContentPanel.bytesToAnother(fs));
            else
                value = ContentPanel.bytesToAnother(fs);

            this.setToolTipText(String.format(sizeRendererInBytes, numberFormatter.format(fs)));
        } else {
            value = sizeRendererUnknown;
            this.setToolTipText(null);
        }
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
