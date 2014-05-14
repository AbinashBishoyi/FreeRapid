package cz.vity.freerapid.plugins.webclient;

/**
 * Constants for {@link AbstractRunner#setClientParameter(String, Object)}
 *
 * @author ntoskrnl
 * @see AbstractRunner#setClientParameter(String, Object)
 * @see AbstractRunner#setClientParameter(String, int)
 * @see AbstractRunner#setClientParameter(String, boolean)
 * @since 0.85
 */
public class DownloadClientConsts {

    /**
     * Value is a Content-Type considered as stream
     */
    public final static String CONSIDER_AS_STREAM = "considerAsStream";
    /**
     * If <code>true</code>, Content-Type header is ignored
     */
    public final static String NO_CONTENT_TYPE_IN_HEADER = "noContentTypeInHeader";
    /**
     * If <code>true</code>, Content-Disposition header is ignored
     */
    public final static String DONT_USE_HEADER_FILENAME = "dontUseHeaderFilename";
    /**
     * If <code>true</code>, Referer will be used in redirects
     */
    public final static String USE_REFERER_WHEN_REDIRECT = "useRefererWhenRedirect";
    /**
     * If <code>true</code>, Accept-Ranges header is ignored
     */
    public final static String IGNORE_ACCEPT_RANGES = "ignoreAcceptRanges";
    /**
     * If <code>true</code>, download with no file size is allowed
     */
    public final static String NO_CONTENT_LENGTH_AVAILABLE = "noContentLengthAvailable";
    /**
     * Value is a charset used for page content
     */
    public final static String PAGE_CHARSET = "pageCharset";

    /**
     * Do not instantiate.
     */
    private DownloadClientConsts() {
    }
}
