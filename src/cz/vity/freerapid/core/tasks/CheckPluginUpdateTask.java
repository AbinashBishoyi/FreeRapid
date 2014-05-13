package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.exceptions.NoAvailableConnection;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
import cz.vity.freerapid.gui.managers.PluginsManager;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.xmlimport.XMLBind;
import cz.vity.freerapid.xmlimport.ver1.Plugin;
import cz.vity.freerapid.xmlimport.ver1.Plugins;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.java.plugin.registry.Version;
import org.jdesktop.application.ApplicationContext;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class CheckPluginUpdateTask extends CoreTask<ConnectResult, Void> {
    private final static Logger logger = Logger.getLogger(CheckPluginUpdateTask.class.getName());

    private final ManagerDirector director;
    private final boolean quietMode;
    private List<Plugin> newPlugins;
    private static final String VERSION__PARAM = "version";
    private static final String PRODUCT_PARAM = "product";


    public CheckPluginUpdateTask(ManagerDirector director, ApplicationContext context, boolean quiet) {
        super(context.getApplication());
        this.director = director;
        quietMode = quiet;
        logger.info("Starting to check for a new plugins version");
        this.newPlugins = new ArrayList<Plugin>();
        setTaskToForeground();
    }


    protected ConnectResult doInBackground() throws Exception {
        AppPrefs.storeProperty(UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK, System.currentTimeMillis());
        message("updatesPluginCheck");
        final ClientManager clientManager = director.getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getEnabledConnections();
        if (connectionSettingses.isEmpty())
            throw new NoAvailableConnection(getResourceMap().getString("noAvailableConnection"));
        final DownloadClient client = new DownloadClient();
        client.initClient(connectionSettingses.get(0));
        HttpMethod method;
        final String url = AppPrefs.getProperty(UserProp.PLUGIN_CHECK_URL_SELECTED, Consts.PLUGIN_CHECK_UPDATE_URL);
        if (url.toLowerCase().endsWith(".xml")) { //for testing purposes
            method = client.getGetMethod(url);
        } else {
            PostMethod postMethod = client.getPostMethod(url);
            postMethod.addParameter(PRODUCT_PARAM, Consts.PRODUCT);
            postMethod.addParameter(VERSION__PARAM, Consts.VERSION);
            method = postMethod;
        }
        message("message.connecting");
        if (client.makeRequest(method) != HttpStatus.SC_OK)
            throw new IOException("Connection failed");
        message("message.checkingData");
        final Plugins rootPlugins = new XMLBind().loadSchema(client.getContentAsString());
        if (isCancelled())
            throw new InterruptedException();
        final List<Plugin> plugins = rootPlugins.getPlugin();
        newPlugins = new ArrayList<Plugin>(plugins.size());
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
        if (newPlugins.isEmpty())
            return ConnectResult.CONNECT_NEW_VERSION;
        else
            return ConnectResult.SAME_VERSION;
    }

    @Override
    protected void failed(Throwable cause) {
        LogUtils.processException(logger, cause);
        if (handleRuntimeException(cause))
            return;
        if (quietMode)
            return;
        if (cause instanceof UnknownHostException) {
            Swinger.showErrorMessage(getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(getResourceMap(), "errormessage_submit_failed", cause.getMessage());
        }

    }

    @Override
    protected void succeeded(ConnectResult result) {
        for (Plugin plugin : newPlugins) {
            logger.info("plugin update from server: " + plugin.getId());
        }
    }

    public List<Plugin> getPluginList() {
        return newPlugins;
    }
}
