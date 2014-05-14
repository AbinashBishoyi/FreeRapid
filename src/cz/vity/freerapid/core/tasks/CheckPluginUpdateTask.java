package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.core.tasks.exceptions.NoAvailableConnection;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.gui.managers.ManagerDirector;
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
import org.jdesktop.application.ApplicationContext;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class CheckPluginUpdateTask extends CoreTask<List<Plugin>, Void> {
    private final static Logger logger = Logger.getLogger(CheckPluginUpdateTask.class.getName());

    private final ManagerDirector director;
    private final boolean quietMode;
    private static final String VERSION__PARAM = "version";
    private static final String PRODUCT_PARAM = "product";
    private static final String APIVERSION_PARAM = "apiversion";
    private static int failed = 0;


    public CheckPluginUpdateTask(ManagerDirector director, ApplicationContext context, boolean quiet) {
        super(context.getApplication());
        this.director = director;
        quietMode = quiet;
        logger.info("Starting to check for a new plugins version");
        setTaskToForeground();
        if (!quiet)
            setInputBlocker(new ScreenInputBlocker(this, BlockingScope.APPLICATION, Swinger.getActiveFrame(), null));
        else
            setInputBlocker(null);
    }


    protected List<Plugin> doInBackground() throws Exception {
        final List<Plugin> newPlugins = new ArrayList<Plugin>();
        if (failed > 6)
            return newPlugins;
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
            postMethod.addParameter(APIVERSION_PARAM, Consts.APIVERSION);
            method = postMethod;
        }
        message("message.connecting");
        if (client.makeRequest(method, true) != HttpStatus.SC_OK)
            throw new ConnectException(getResourceMap().getString("Connection_failed"));
        message("message.checkingData");
        if (isCancelled())
            throw new InterruptedException();
        final Plugins rootPlugins = new XMLBind().loadPluginList(client.getContentAsString());
        return rootPlugins.getPlugin();
        //return newPlugins;
    }

    @Override
    protected void failed(Throwable cause) {
        LogUtils.processException(logger, cause);
//        if (handleRuntimeException(cause))
//            return;
        ++failed;
        if (quietMode) {
            AppPrefs.storeProperty(UserProp.PLUGIN_LAST_UPDATE_TIMESTAMP_CHECK, -1);
            return;
        }
        if (cause instanceof UnknownHostException) {
            Swinger.showErrorMessage(getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(getResourceMap(), "errormessage_submit_failed", cause.getMessage());
        }

    }

    @Override
    protected void succeeded(List<Plugin> result) {
        for (Plugin plugin : result) {
            logger.info("plugin update from server: " + plugin.getId());
        }
    }

}
