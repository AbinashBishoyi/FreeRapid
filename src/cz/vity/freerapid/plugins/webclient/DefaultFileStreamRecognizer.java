package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.webclient.interfaces.FileStreamRecognizer;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.util.Locale;
import java.util.logging.Logger;

/**
 * Default implementation of FileStreamRecognizer - it identifies file stream.
 * Eg. for text/plain as file stream use this code:
 * <code>
 * new DefaultFileStreamRecognizer(new String[]{"text/plain"}, false);
 * </code>
 *
 * @author Vity
 * @author ntoskrnl
 * @see AbstractRunner#setFileStreamContentTypes(String[])
 * @since 0.85
 */
public class DefaultFileStreamRecognizer implements FileStreamRecognizer {
    private final static Logger logger = Logger.getLogger(DefaultFileStreamRecognizer.class.getName());

    protected final static String[] DEFAULT_STREAM_VALUES = {"application/", "image/", "audio/", "video/", EMPTY};
    protected final static String[] DEFAULT_TEXT_VALUES = {"text/", "xml", "javascript", "json", "smil"};

    protected final String[] streamValues;
    protected final String[] textValues;
    protected final boolean exactMatch;

    public DefaultFileStreamRecognizer() {
        streamValues = new String[0];
        textValues = new String[0];
        exactMatch = false;
    }

    /**
     * @param streamValues consider these content types as streams
     * @param textValues   consider these content types as text
     * @param exactMatch   if true, don't check against default values
     */
    public DefaultFileStreamRecognizer(final String[] streamValues, final String[] textValues, final boolean exactMatch) {
        this.streamValues = streamValues;
        this.textValues = textValues;
        this.exactMatch = exactMatch;
    }

    @Override
    public boolean isStream(HttpMethod method, boolean showWarnings) {
        final String contentType = getContentType(method);
        if (isOnList(contentType, textValues)) {
            return false;
        }
        if (isOnList(contentType, streamValues)) {
            return true;
        }
        if (!exactMatch) {
            if (isOnList(contentType, DEFAULT_TEXT_VALUES)) {
                return false;
            }
            if (isOnList(contentType, DEFAULT_STREAM_VALUES)) {
                return true;
            }
        }
        if (showWarnings) {
            logger.warning("Unknown content type: " + contentType);
        }
        return true;
    }

    protected boolean isOnList(final String contentType, final String[] list) {
        for (final String s : list) {
            if (contentType.contains(s)) {
                return true;
            }
        }
        return false;
    }

    protected String getContentType(final HttpMethod method) {
        final Header header = method.getResponseHeader("Content-Type");
        if (header != null) {
            final String contentType = header.getValue();
            if (contentType != null && !contentType.isEmpty()) {
                return contentType.toLowerCase(Locale.ENGLISH);
            }
        }
        return EMPTY;
    }

}
