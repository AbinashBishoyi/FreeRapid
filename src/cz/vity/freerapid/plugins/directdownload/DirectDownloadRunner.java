package cz.vity.freerapid.plugins.directdownload;

import cz.vity.freerapid.plugins.exceptions.ServiceConnectionProblemException;
import cz.vity.freerapid.plugins.exceptions.URLNotAvailableAnymoreException;
import cz.vity.freerapid.plugins.webclient.AbstractRunner;
import cz.vity.freerapid.plugins.webclient.DownloadClientConsts;
import cz.vity.freerapid.plugins.webclient.interfaces.FileStreamRecognizer;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;

/**
 * @author ntoskrnl
 */
class DirectDownloadRunner extends AbstractRunner implements FileStreamRecognizer {

    @Override
    public void run() throws Exception {
        super.run();
        setClientParameter(DownloadClientConsts.FILE_STREAM_RECOGNIZER, this);
        final HttpMethod method = getGetMethod(fileURL);
        if (!tryDownloadAndSaveFile(method)) {
            if (method.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new URLNotAvailableAnymoreException("File not found");
            }
            throw new ServiceConnectionProblemException("Error starting download");
        }
    }

    @Override
    public boolean isStream(HttpMethod method, boolean showWarnings) {
        return true;
    }

}
