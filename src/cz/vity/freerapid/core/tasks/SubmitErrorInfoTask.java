package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.MainApp;
import cz.vity.freerapid.core.application.SubmitErrorInfo;
import cz.vity.freerapid.core.tasks.exceptions.NoAvailableConnection;
import cz.vity.freerapid.gui.managers.ClientManager;
import cz.vity.freerapid.plugins.webclient.ConnectionSettings;
import cz.vity.freerapid.plugins.webclient.DownloadClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdesktop.application.Application;
import org.jdesktop.application.ResourceMap;

import java.net.ConnectException;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Vity
 */
public class SubmitErrorInfoTask extends CoreTask<Void, Void> {
    private final static Logger logger = Logger.getLogger(SubmitErrorInfoTask.class.getName());
    private final SubmitErrorInfo errorInfo;

    public SubmitErrorInfoTask(SubmitErrorInfo errorInfo) {
        super(Application.getInstance());
        this.errorInfo = errorInfo;
        this.setUserCanCancel(false);
//        ProxyHelper.initProxy();//init proxy settings
    }


    @Override
    protected Void doInBackground() throws Exception {
        message("message.connecting");

        final String url = AppPrefs.getProperty(FWProp.WEBURL_SUBMIT_ERROR, Consts.WEBURL_SUBMIT_ERROR);

        final ClientManager clientManager = ((MainApp) getApplication()).getManagerDirector().getClientManager();
        final List<ConnectionSettings> connectionSettingses = clientManager.getEnabledConnections();
        final ResourceMap map = getApplication().getContext().getResourceMap(CheckPluginUpdateTask.class);
        if (connectionSettingses.isEmpty())
            throw new NoAvailableConnection(map.getString("noAvailableConnection"));
        final DownloadClient client = new DownloadClient();
        client.initClient(connectionSettingses.get(0));
        PostMethod postMethod = client.getPostMethod(url);
        errorInfo.toURLPostData(postMethod);

        logger.info("Connected to the web, Writing params");

        message("message.sending");
        if (client.makeRequest(postMethod, true) != HttpStatus.SC_OK)
            throw new ConnectException(map.getString("Connection_failed"));
        logger.info("disconnecting");

        message("message.disconnect");

        return null;
    }
}
