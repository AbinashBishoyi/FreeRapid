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
import cz.vity.freerapid.utilities.Browser;
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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
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
    private static final String PARAM_LANGUAGE = "lang";
    private static final String PARAM_VERSION_ONLY = "versiononly";
    private static final String PARAM_COUNTRY = "country";

    private static int failed = 0;
    private String newVersionURL;
    private ConnectResult result;


    public CheckPluginUpdateTask(ManagerDirector director, ApplicationContext context, boolean quiet) {
        super(context.getApplication());
        this.director = director;
        quietMode = quiet;
        result = ConnectResult.SAME_VERSION;
        logger.info("Starting to check for a new plugins version");
        setTaskToForeground();
        if (!quiet)
            setInputBlocker(new ScreenInputBlocker(this, BlockingScope.APPLICATION, Swinger.getActiveFrame(), null));
        else
            setInputBlocker(null);
    }


    protected List<Plugin> doInBackground() throws Exception {
        final List<Plugin> newPlugins = new ArrayList<Plugin>();
        if (quietMode && failed > 6)
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
            postMethod.addParameter(PARAM_VERSION_ONLY, Consts.VERSION);
            final Locale locale = Locale.getDefault();
            postMethod.addParameter(PARAM_LANGUAGE, locale.getLanguage());
            postMethod.addParameter(PARAM_COUNTRY, locale.getCountry());

            method = postMethod;
        }
        message("message.connecting");
        if (client.makeRequest(method, true) != HttpStatus.SC_OK)
            throw new ConnectException(getResourceMap().getString("Connection_failed"));
        message("message.checkingData");
        if (isCancelled())
            throw new InterruptedException();
        final String line = client.getContentAsString();

        if ((line != null && line.contains("thisisanupdate"))) {
            //   return CONNECT_SAME_VERSION;
            final String lineL = line.toLowerCase();
            if (lineL.contains("required")) {
                final int i = lineL.indexOf("http://");
                if (i != -1) {
                    newVersionURL = line.substring(i).trim();
                } else {
                    newVersionURL = Consts.WEBURL;
                }
                result = ConnectResult.NEW_VERSION_REQUIRED;
            } else {
                if (lineL.contains("yes"))
                    //   return CONNECT_SAME_VERSION;
                    result = ConnectResult.CONNECT_NEW_VERSION;
                else
                    result = ConnectResult.SAME_VERSION;
            }
        } else
            result = ConnectResult.SAME_VERSION;

        if (result == ConnectResult.SAME_VERSION) {
            final Plugins rootPlugins = new XMLBind().loadPluginList(line);
            return rootPlugins.getPlugin();
        } else return Collections.emptyList();
        //return newPlugins;
    }

    @Override
    protected void failed(Throwable cause) {
        LogUtils.processException(logger, cause);
//        if (handleRuntimeException(cause))
//            return;

        if (quietMode) {
            ++failed;
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
    protected void succeeded(List<Plugin> plugins) {
        switch (this.result) {
            case NEW_VERSION_REQUIRED:
                Swinger.showInformationDialog(getResourceMap().getString("message.connect.newVersionRequired"));
                Browser.openBrowser(newVersionURL);
                getApplication().exit();
                return;
            default:
                assert false;
        }

        for (Plugin plugin : plugins) {
            logger.info("plugin update from server: " + plugin.getId());
        }
    }

}
