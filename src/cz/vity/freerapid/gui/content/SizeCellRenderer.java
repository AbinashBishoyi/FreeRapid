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
final class SizeCellRenderer extends DefaultTableCellRenderer {
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
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;
        final long fs = downloadFile.getFileSize();
        final long dl = downloadFile.getDownloaded();
        if (fs >= 0) {
            if (dl != fs) {
                value = String.format(sizeRendererProgress, ContentPanel.bytesToAnother(dl), ContentPanel.bytesToAnother(fs));
            } else {
                value = ContentPanel.bytesToAnother(fs);
            }
            setToolTipText(String.format(sizeRendererInBytes, numberFormatter.format(fs)));
        } else {
            if (dl > 0) {
                value = ContentPanel.bytesToAnother(dl);
                setToolTipText(String.format(sizeRendererInBytes, numberFormatter.format(dl)));
            } else {
                value = sizeRendererUnknown;
                setToolTipText(null);
            }
        }
        getAccessibleContext().setAccessibleName(table.getColumnName(column) + " " + value);
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
