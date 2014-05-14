package cz.vity.freerapid.plugins.webclient.interfaces;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * @author Vity
 */
public interface MaintainQueueSupport {

    boolean addLinksToQueue(HttpFile parentFile, List<URI> uriList);

    public boolean addLinkToQueueUsingPriority(HttpFile parentFile, List<URL> urlList) throws Exception;

    boolean addLinkToQueueUsingPriority(HttpFile parentFile, String data) throws Exception;

}
