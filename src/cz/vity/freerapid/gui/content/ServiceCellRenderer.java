package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.gui.managers.exceptions.NotSupportedDownloadServiceException;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ladislav Vitasek
 */
class ServiceCellRenderer extends DefaultTableCellRenderer {
    private final PluginsManager manager;
    private final Map<String, Icon> iconCache = new HashMap<String, Icon>();

    ServiceCellRenderer(ManagerDirector director) {
        this.manager = director.getPluginsManager();
        final Icon icon = director.getContext().getResourceMap().getIcon("serviceWithNoIcon");
        iconCache.put("default", icon);
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (value == null) {
            value = table.getValueAt(row, column);
        }
        final DownloadFile downloadFile = (DownloadFile) value;
        final String shareDownloadServiceID = downloadFile.getPluginID();
        assert shareDownloadServiceID != null;
        final String serviceName = downloadFile.getServiceName();
        Icon faviconImage;
        super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        faviconImage = iconCache.get(shareDownloadServiceID);
        if (faviconImage == null) {
            try {
                if (manager.hasPlugin(shareDownloadServiceID) && manager.getPluginMetadata(shareDownloadServiceID).hasFavicon()) {
                    final ShareDownloadService service = manager.getPluginInstance(shareDownloadServiceID);
                    faviconImage = service.getFaviconImage();
                    if (faviconImage != null) {
                        iconCache.put(shareDownloadServiceID, faviconImage);
                    }
                }
            } catch (NotSupportedDownloadServiceException e) {
                //do nothing
            }
            if (faviconImage == null)
                faviconImage = iconCache.get("default");
        }
        if (faviconImage != null) {
            this.setIcon(faviconImage);
        }
        if (!AppPrefs.getProperty(UserProp.SHOW_SERVICES_ICONS, UserProp.SHOW_SERVICES_ICONS_DEFAULT)) {
            this.setHorizontalAlignment(LEFT);
            this.setText(serviceName);
            this.setToolTipText(null);
        } else {
            this.setToolTipText(serviceName);
            this.setText(null);
            this.setHorizontalAlignment(CENTER);
        }
        getAccessibleContext().setAccessibleName(table.getColumnName(column) + " " + serviceName);
        return this;
    }
}
