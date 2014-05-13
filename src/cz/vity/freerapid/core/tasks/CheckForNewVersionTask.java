package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.Consts;
import cz.vity.freerapid.core.FWProp;
import cz.vity.freerapid.core.application.ProxyHelper;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Browser;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.Application;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class CheckForNewVersionTask extends CoreTask<ConnectResult, Void> {
    private final static Logger logger = Logger.getLogger(CheckForNewVersionTask.class.getName());

    private static final String PARAM_VERSION = "version";

    private final boolean showInfoMessages;

    public CheckForNewVersionTask(final boolean showInfoMessages) {
        super(Application.getInstance());
        logger.info("Starting to check for a new version");
        this.showInfoMessages = showInfoMessages;
        this.setUserCanCancel(false);
        ProxyHelper.initProxy();//init proxy settings
    }


    @Override
    protected ConnectResult doInBackground() throws Exception {
        HttpURLConnection urlConn = null;
        message("message.connecting");
        try {
            final String url = AppPrefs.getProperty(FWProp.CHECK_FOR_NEW_VERSION_URL, Consts.WEBURL_SUBMIT_ERROR);
            urlConn = (HttpURLConnection) new URL(url).openConnection();
            urlConn.setDoOutput(true);
            urlConn.setDoInput(true);
            urlConn.setUseCaches(false);
            final DataOutputStream bufferOut = new DataOutputStream(urlConn.getOutputStream());
            logger.info("Connected to the web, Writing params");
            bufferOut.write(Utils.addParam("", PARAM_VERSION, Consts.APPVERSION).getBytes());
            message("message.connect.status.checking");
            bufferOut.close();
            final BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));
            final String line = br.readLine();
            br.close();

            logger.info("disconnecting");
            message("message.connect.status.disconnect");
            urlConn.disconnect();
            if (line != null && line.toLowerCase().contains("yes"))
                //   return CONNECT_SAME_VERSION;
                return ConnectResult.CONNECT_NEW_VERSION;

        } catch (UnknownHostException e) { //Inet not available
            if (urlConn != null) urlConn.disconnect();
            throw e;
        } catch (IOException e) {
            LogUtils.processException(logger, e);
            if (urlConn != null) urlConn.disconnect();
            throw e;
        }
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
            case CONNECT_NEW_VERSION:
                int res = Swinger.getChoiceYesNoCancel(this.getResourceMap().getString("message.connect.newVersion"));
                if (res == Swinger.RESULT_YES)
                    Browser.showHomepage();
                break;
            default:
                assert false;
        }
    }

    @Override
    protected void failed(Throwable cause) {
        if (handleRuntimeException(cause))
            return;

        if (!showInfoMessages)
            return;
        if (cause instanceof UnknownHostException) {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_check_inet_settings");
        } else {
            Swinger.showErrorMessage(this.getResourceMap(), "errormessage_submit_failed", cause.getMessage());
        }
    }
}