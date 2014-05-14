package cz.vity.freerapid.plugins.webclient.interfaces;

import java.net.URI;
import java.util.List;

/**
 * @author Vity
 */
public interface MaintainQueueSupport {

    boolean addLinksToQueue(HttpFile parentFile, List<URI> uriList);
}
