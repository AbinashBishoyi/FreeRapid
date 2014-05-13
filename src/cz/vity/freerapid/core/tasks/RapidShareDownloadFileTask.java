package cz.vity.freerapid.core.tasks;

import cz.vity.freerapid.core.tasks.exceptions.InvalidURLOrServiceProblemException;
import cz.vity.freerapid.core.tasks.exceptions.PluginImplementationException;
import cz.vity.freerapid.core.tasks.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.model.DownloadState;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.jdesktop.application.Application;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
public class RapidShareDownloadFileTask extends DownloadTask {
    private final static Logger logger = Logger.getLogger(RapidShareDownloadFileTask.class.getName());


    public RapidShareDownloadFileTask(Application application, DownloadClient client, DownloadFile downloadFile) {
        super(application, client, downloadFile);
        downloadFile.setState(DownloadState.GETTING);
    }

    @Override
    protected Void doInBackground() throws Exception {
        initBackground();
        final GetMethod getMethod = client.getGetMethod(downloadFile.getFileUrl().toString());
        if (client.makeRequest(getMethod) == HttpStatus.SC_OK) {
            Matcher matcher = Pattern.compile("form id=\"ff\" action=\"([^\"]*)\"", Pattern.MULTILINE).matcher(client.asString);
            if (!matcher.find()) {
                matcher = Pattern.compile("class=\"klappbox\">((\\s|.)*?)</div>", Pattern.MULTILINE).matcher(client.asString);
                if (matcher.find()) {
                    throw new InvalidURLOrServiceProblemException("<b>RapidShare error:</b><br>" + matcher.group(1));
                }
                throw new InvalidURLOrServiceProblemException("Invalid URL or unindentified service");
            }
            String s = matcher.group(1);
            //| 5277 KB</font>
            matcher = Pattern.compile("\\| (.*?) KB</font>", Pattern.MULTILINE).matcher(client.asString);
            if (matcher.find())
                downloadFile.setFileSize(new Integer(matcher.group(1).replaceAll(" ", "")) * 1024);

            logger.info("Found File URL - " + s);
            client.setReferer(downloadFile.getFileUrl().toString());
            final PostMethod postMethod = client.getPostMethod(s);
            postMethod.addParameter("dl.start", "Free");
            if (client.makeRequest(postMethod) == HttpStatus.SC_OK) {
                matcher = Pattern.compile("var c=([0-9]*);", Pattern.MULTILINE).matcher(client.asString);
                if (!matcher.find())
                    throw new ServiceConnectionProblemException("Problem with a connection to service.\nCannot find requested page content");
                s = matcher.group(1);
                int seconds = new Integer(s);
                matcher = Pattern.compile("form name=\"dlf\" action=\"([^\"]*)\"", Pattern.MULTILINE).matcher(client.asString);
                if (matcher.find()) {
                    s = matcher.group(1);
                    logger.info("Download URL: " + s);
                    sleep(seconds + 1);
                    if (isCancelled())
                        throw new InterruptedException();
                    downloadFile.setState(DownloadState.GETTING);
                    final PostMethod method = client.getPostMethod(s);
                    method.addParameter("mirror", "on");
                    try {
                        final InputStream inputStream = client.makeFinalRequestForFile(method, downloadFile);
                        if (inputStream != null) {
                            saveToFile(inputStream);
                        } else
                            throw new IOException("File input stream is empty.");
                    } finally {
                        method.releaseConnection();
                    }
                } else {
                    //Your IP address 195.70.135.222 is already downloading a file.  Please wait until the download is completed.
                    matcher = Pattern.compile("Your IP address (.*?) is already", Pattern.MULTILINE).matcher(client.asString);
                    if (matcher.find()) {
                        final String ip = matcher.group(1);
                        throw new ServiceConnectionProblemException(String.format("<b>RapidShare error:</b><br>Your IP address %s is already downloading a file. <br>Please wait until the download is completed.", ip));
                    }

                    throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");
                }
            }
        } else
            throw new PluginImplementationException("Problem with a connection to service.\nCannot find requested page content");
        return null;
    }

}
