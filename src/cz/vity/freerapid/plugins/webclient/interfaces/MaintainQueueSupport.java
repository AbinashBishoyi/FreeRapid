package cz.vity.freerapid.plugins.webclient.interfaces;

import cz.vity.freerapid.plugins.container.FileInfo;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * @author Vity
 * @author ntoskrnl
 */
public interface MaintainQueueSupport {

    /**
     * Adds links to the queue.
     *
     * @param parentFile parent file where description is copied from
     * @param uriList    list of links which should be added to the queue
     * @return true on success, false otherwise
     */
    public boolean addLinksToQueue(HttpFile parentFile, List<URI> uriList);

    /**
     * Parses a String for supported links and adds them to the queue.
     *
     * @param parentFile parent file where description is copied from
     * @param data       data to parse for links to be added to the queue
     * @return true on success, false otherwise
     * @since 0.85
     */
    public boolean addLinksToQueue(HttpFile parentFile, String data);

    /**
     * Adds links to the queue.
     *
     * @param parentFile parent file where description is copied from
     * @param infoList   list of links which should be added to the queue
     * @return true on success, false otherwise
     * @since 0.85
     */
    public boolean addLinksToQueueFromContainer(HttpFile parentFile, List<FileInfo> infoList);

    /**
     * Adds one of the links to the queue (depending on user settings of plugin priorities).
     *
     * @param parentFile parent file where description is copied from
     * @param urlList    list of links of which one is chosen to be added to the queue
     * @return true on success, false otherwise
     * @throws Exception if something goes wrong
     * @since 0.85
     */
    public boolean addLinkToQueueUsingPriority(HttpFile parentFile, List<URL> urlList) throws Exception;

    /**
     * Parses a String for supported links and adds one of the links to the queue
     * (depending on user settings of plugin priorities).
     *
     * @param parentFile parent file where description is copied from
     * @param data       data to parse for links of which one is chosen to be added to the queue
     * @return true on success, false otherwise
     * @throws Exception if something goes wrong
     * @since 0.85
     */
    public boolean addLinkToQueueUsingPriority(HttpFile parentFile, String data) throws Exception;

    /**
     * Adds one of the links to the queue (depending on user settings of plugin priorities).
     *
     * @param parentFile parent file where description is copied from
     * @param infoList   list of links of which one is chosen to be added to the queue
     * @return true on success, false otherwise
     * @throws Exception if something goes wrong
     * @since 0.85
     */
    public boolean addLinkToQueueFromContainerUsingPriority(HttpFile parentFile, List<FileInfo> infoList) throws Exception;

}
