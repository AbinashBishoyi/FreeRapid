package cz.vity.freerapid.sandbox;

import cz.vity.freerapid.plugins.exceptions.*;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpDownloadClient;

import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
public class FlowController {

    private final HttpDownloadClient client;
    private final static Logger logger = Logger.getLogger(FlowController.class.getName());

    private boolean logContentBeforeException;

    public FlowController(HttpDownloadClient client) {
        this.client = client;
    }

    public void throwYouHaveToWait(String message, int seconds) throws ErrorDuringDownloadingException {
        throwEx(new YouHaveToWaitException(message, seconds));
    }

    public void throwFileIsNotAvailableAnymore(final String message) throws ErrorDuringDownloadingException {
        throwEx(new URLNotAvailableAnymoreException(message));
    }

    public void throwInvalidUsernameOrPassword(final String message) throws ErrorDuringDownloadingException {
        throwEx(new BadLoginException(message));
    }

    public void throwCAPTCHAEntryMismatch() throws ErrorDuringDownloadingException {
        throwEx(new CaptchaEntryInputMismatchException());
    }

    public void throwServiceConnectionProblem(final String message) throws ErrorDuringDownloadingException {
        throwEx(new ServiceConnectionProblemException(message));
    }

    public void throwInvalidURLOrServiceProblem(final String message) throws ErrorDuringDownloadingException {
        throwEx(new InvalidURLOrServiceProblemException(message));
    }

    public void throwInvalidURLOrServiceProblem() throws ErrorDuringDownloadingException {
        throwInvalidURLOrServiceProblem("Invalid URL or unindentified service");
    }

    public void throwPluginImplementationError() throws ErrorDuringDownloadingException {
        throwPluginImplementationError("Problem with a connection to service.\nCannot find requested page content");
    }

    public void throwPluginImplementationError(String message) throws ErrorDuringDownloadingException {
        final PluginImplementationException e = new PluginImplementationException(message);
        setLogContentBeforeException(true);
        throwEx(e);
    }

    public void throwFailedToLoadCAPTCHA(String message) throws ErrorDuringDownloadingException {
        throwEx(new FailedToLoadCaptchaPictureException(message));
    }

    private void throwEx(ErrorDuringDownloadingException e) throws ErrorDuringDownloadingException {
        throw e;
    }

    public void throwException(ErrorDuringDownloadingException e) throws ErrorDuringDownloadingException {
        if (logContentBeforeException) {
            final String separator = "\n=====================================================================\n";
            logger.severe(separator + client.getContentAsString() + separator);
        }
        throwEx(e);
    }


    /**
     * Getter for property 'logContentBeforeException'.
     *
     * @return Value for property 'logContentBeforeException'.
     */
    public boolean isLogContentBeforeException() {
        return logContentBeforeException;
    }

    /**
     * Setter for property 'logContentBeforeException'.
     *
     * @param logContentBeforeException Value to set for property 'logContentBeforeException'.
     */
    public void setLogContentBeforeException(boolean logContentBeforeException) {
        this.logContentBeforeException = logContentBeforeException;
    }
}
