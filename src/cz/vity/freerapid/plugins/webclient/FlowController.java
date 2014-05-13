package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.webclient.interfaces.ShareDownloadService;

/**
 * @author Ladislav Vitasek
 */
public class FlowController {
    private final ShareDownloadService service;


    public FlowController(ShareDownloadService service) {
        this.service = service;
    }

//    public void testForNotFound(String content, String... values) throws ErrorDuringDownloadingException {
//        if (contains(content, values))
//            throw new ServiceConnectionProblemException(errorMessage);
//    }
//
//    public void testForServiceConnectionProblem(String content, String errorMessage, String... values) throws ErrorDuringDownloadingException {
//        if (contains(content, values))
//            throw new ServiceConnectionProblemException(errorMessage);
//    }
//
//    public void throwInvalidURLOrServiceProblem(String message) throws ErrorDuringDownloadingException  {
//        throw new InvalidURLOrServiceProblemException(message);
//    }
//
//    public void youHaveToWait(String errorMessage, int seconds) {
//        final YouHaveToWaitException youHaveToWaitException = new YouHaveToWaitException(message, seconds);
//
//        throw youHaveToWaitException;
//    }
//
//    private boolean contains(String content, String[] values) {
//        for (String s : values) {
//            if (content.contains(s))
//                return true;
//        }
//    }
}
