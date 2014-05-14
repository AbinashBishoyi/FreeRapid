package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.tasks.exceptions.NoAvailableConnection;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class CheckForNewVersionTask extends CoreTask<ConnectResult, Void> {
    private final static Logger logger = Logger.getLogger(CheckForNewVersionTask.class.getName());

    private static final String PARAM_VERSION = "version";
    private static final String PARAM_LANGUAGE = "lang";
    private static final String PARAM_VERSION_ONLY = "versiononly";
    private static final String PARAM_COUNTRY = "country";

    private final boolean showInfoMessages;
    private static int counter = 0;
    private String newVersionURL;

    public CheckForNewVersionTask(final boolean showInfoMessages) {
        super(Application.getInstance());
        logger.info("Starting to check for a new version");
        this.showInfoMessages = showInfoMessages;
        this.setUserCanCancel(false);
//        ProxyHelper.initProxy();//init proxy settings
        this.setDescription("");
        this.setMessage("");

        setTaskToForeground();
    }


    @Override
    protected ConnectResult doInBackground() throws Exception {
        if (counter++ > 4)
            return ConnectResult.SAME_VERSION; //security
        message("message.connecting");

        final String url = AppPrefs.getProperty(FWProp.CHECK_FOR_NEW_VERSION_URL, Consts.WEBURL_CHECKNEWVERSION);
        final ClientManager clientManager = ((MainApp) getApplication()).getManagerDirector().getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getEnabledConnections();
        final ResourceMap map = getApplication().getContext().getResourceMap(CheckPluginUpdateTask.class);
        if (connectionSettingses.isEmpty())
            throw new NoAvailableConnection(map.getString("noAvailableConnection"));
        final DownloadClient client = new DownloadClient();
        client.initClient(connectionSettingses.get(0));
        PostMethod postMethod = client.getPostMethod(url);
        postMethod.addParameter(PARAM_VERSION, Consts.APPVERSION);
        postMethod.addParameter(PARAM_VERSION_ONLY, Consts.VERSION);
        final Locale locale = Locale.getDefault();
        postMethod.addParameter(PARAM_LANGUAGE, locale.getLanguage());
        postMethod.addParameter(PARAM_COUNTRY, locale.getCountry());
        logger.info("Connected to the web, Writing params");
        message("message.connect.status.checking");
        if (client.makeRequest(postMethod, true) != HttpStatus.SC_OK)
            throw new ConnectException(map.getString("Connection_failed"));
        message("message.checkingData");
        if (isCancelled())
            throw new InterruptedException();

        logger.info("disconnecting");
        message("message.connect.status.disconnect");
        final String line = client.getContentAsString();
        if ((line != null)) {
            //   return CONNECT_SAME_VERSION;
            final String lineL = line.toLowerCase();
            if (lineL.contains("required")) {
                final int i = lineL.indexOf("http://");
                if (i != -1) {
                    newVersionURL = line.substring(i).trim();
                } else {
                    newVersionURL = Consts.WEBURL;
                }
                return ConnectResult.NEW_VERSION_REQUIRED;
            }
            if (lineL.contains("yes"))
                //   return CONNECT_SAME_VERSION;
                return ConnectResult.CONNECT_NEW_VERSION;
            else
                return ConnectResult.SAME_VERSION;
        } else
            return ConnectResult.SAME_VERSION;
    }

    @Override
    protected void succeeded(ConnectResult result) {
        super.succeeded(result);
        switch (result) {
            case SAME_VERSION:
                if (showInfoMessages)
                    Swinger.showInformationDialog(getResourceMap().getString("message.connect.sameVersion"));
                break;
            case NEW_VERSION_REQUIRED:
                Swinger.showInformationDialog(getResourceMap().getString("message.connect.newVersionRequired"));
                Browser.openBrowser(newVersionURL);
                getApplication().exit();
                return;
            case CONNECT_NEW_VERSION:
                int res = Swinger.getChoiceYesNo(this.getResourceMap().getString("message.connect.newVersion"));
                if (res == Swinger.RESULT_YES)
                    Browser.showHomepage();
                break;
            default:
                assert false;
        }
    }

    @Override
    protected void failed(Throwable cause) {
        LogUtils.processException(logger, cause);
        if (handleRuntimeException(cause)) {
            return;
        }
        if (!showInfoMessages)
            return;
        if (cause instanceof UnknownHostException) {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_submit_failed", cause.getMessage());
        }
    }
}