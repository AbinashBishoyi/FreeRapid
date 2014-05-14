package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.tasks.DownloadTask;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadState;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final class ConnectionCellRenderer extends DefaultTableCellRenderer {
    private final String defaultConnection;

    private final static Logger logger = Logger.getLogger(ConnectionCellRenderer.class.getName());

    ConnectionCellRenderer(ApplicationContext context) {
        final ResourceMap map = context.getResourceMap();
        defaultConnection = map.getString("defaultConnection");
    }

    @Override
    public final Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;

        final DownloadTask task = downloadFile.getTask();

        ConnectionSettings con = null;
        if (downloadFile.getState() == DownloadState.SLEEPING || downloadFile.getState() == DownloadState.ERROR) {
            con = downloadFile.getConnectionSettings();
        }

        if (con == null && task != null) {
            final HttpDownloadClient client = task.getClient();
            if (client != null)
                con = client.getSettings();
        }

        if (con != null) {
            if (con.isProxySet()) {
                value = String.format("%s:%s", con.getProxyURL(), con.getProxyPort());
                if (con.getUserName() != null) {
                    value = con.getUserName() + "@" + value;
                }
            } else value = defaultConnection;
        } else value = "";
        getAccessibleContext().setAccessibleDescription(table.getColumnName(column) + " " + value.toString());
        return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
    }
}
