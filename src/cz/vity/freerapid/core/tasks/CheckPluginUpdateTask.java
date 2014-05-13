package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.xmlimport.XMLBind;
import cz.vity.freerapid.xmlimport.ver1.Plugin;
import cz.vity.freerapid.xmlimport.ver1.Plugins;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.java.plugin.registry.Version;
import org.jdesktop.application.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ladislav Vitasek
 */
public class CheckPluginUpdateTask extends CoreTask<List<Plugin>, Void> {
    private final ManagerDirector director;
    private List<Plugin> pluginList;
    private static final String VERSION__PARAM = "version";
    private static final String PRODUCT_PARAM = "product";


    public CheckPluginUpdateTask(ManagerDirector director, ApplicationContext context) {
        super(context.getApplication());
        this.director = director;
    }


    protected List<Plugin> doInBackground() throws Exception {
        AppPrefs.storeProperty(UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK, System.currentTimeMillis());
        message("updatesPluginCheck");
        final ClientManager clientManager = director.getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getEnabledConnections();
        if (connectionSettingses.isEmpty())
            throw new IllegalStateException("No available connection");
        final DownloadClient client = new DownloadClient();
        client.initClient(connectionSettingses.get(0));
        final PostMethod postMethod = client.getPostMethod(AppPrefs.getProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, Consts.PLUGIN_CHECK_UPDATE_URL));
        postMethod.addParameter(PRODUCT_PARAM, Consts.PRODUCT);
        postMethod.addParameter(VERSION__PARAM, Consts.VERSION);

        if (client.makeRequest(postMethod) != HttpStatus.SC_OK)
            throw new IllegalStateException("Connection failed");

        final Plugins rootPlugins = new XMLBind().loadSchema(client.getContentAsString());
        final List<Plugin> plugins = rootPlugins.getPlugin();
        final List<Plugin> newPlugins = new ArrayList<Plugin>(plugins.size());
        final PluginsManager pluginsManager = director.getPluginsManager();
        for (Plugin plugin : plugins) {
            final String id = plugin.getId();
            if (pluginsManager.hasPlugin(id)) {
                final Version newVersion = Version.parse(plugin.getVersion());
                final Version oldVersion = Version.parse(pluginsManager.getPluginMetadata(id).getVersion());
                if (newVersion.isGreaterThan(oldVersion))
                    newPlugins.add(plugin);
            } else newPlugins.add(plugin);
        }
        return newPlugins;
    }

    @Override
    protected void failed(Throwable cause) {
        super.failed(cause);
    }

    @Override
    protected void succeeded(List<Plugin> result) {
        this.pluginList = result;
        for (Plugin plugin : result) {
            System.out.println("id: " + plugin.getId());
        }
    }

    public List<Plugin> getPluginList() {
        return pluginList;
    }
}
