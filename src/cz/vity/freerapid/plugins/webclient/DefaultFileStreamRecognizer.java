package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.webclient.interfaces.FileStreamRecognizer;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.util.Arrays;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Default implementation of FileStreamRecognizer - it identifies file stream.
 * Eg. for text/plain as file stream use this code:
 * <code>
 *      new DefaultFileStreamRecognizer(new String[] {"text/plain"}, false);
 * </code>
 * @author Vity
 * @since 0.85
 * @see cz.vity.freerapid.plugins.webclient.AbstractHttpRunner#setFileStreamContentTypes(String[])
 */
public class DefaultFileStreamRecognizer implements FileStreamRecognizer {
    private final static Logger logger = Logger.getLogger(DefaultFileStreamRecognizer.class.getName());
    private final String[] allowedValues;
    private final boolean exactMatch;

    public DefaultFileStreamRecognizer() {
        allowedValues = new String[0];
        exactMatch = false;
    }

    /**
     * Constructor
     * @param allowedValues allowed values for content type identifying filestream. One of allowed value can be NULL.
     * @param exactMatch whether only <code>allowedValues</code> should be considered as file stream
     */
    public DefaultFileStreamRecognizer(String[] allowedValues, boolean exactMatch) {
        this.allowedValues = allowedValues;
        this.exactMatch = exactMatch;
    }

    public boolean isStream(HttpMethod method, boolean showWarnings) {
        final Header contentType = getContentType(method);
        String value = null;
        if (contentType != null) {
            value = contentType.getValue().toLowerCase(Locale.ENGLISH);
        } else {
            if (showWarnings) {
                logger.warning("No Content-Type!");
            }
        }
        boolean found = false;
        for (String s : allowedValues) {
            if (s == null) {
                if (value == null) {
                    found = true;
                    break;
                }
            } else {
                if (value != null) {
                    s = s.toLowerCase(Locale.ENGLISH);
                    if (value.startsWith(s)) {
                        found = true;
                        break;
                    }
                }
            }
        }
        if (found) {
            return true;
        } else if (exactMatch) {
            if (showWarnings && allowedValues.length > 0) {
                logger.warning("Content type " + value + " is not on supported list " + Arrays.toString(allowedValues));
            }
            return false;
        }
        if (contentType == null || value == null) {
            return false;
        } else {
            boolean stream = true;
            final boolean isImage = value.startsWith("image/");
            final boolean isAudioVideo = value.startsWith("audio/") || value.startsWith("video/");
            if (!value.startsWith("application/") && !isImage && !isAudioVideo) {
                stream = false;
                if (showWarnings) {
                    logger.warning("Suspicious Content-Type: " + contentType.getValue());
                }
            }
            return stream;
        }
    }

    protected Header getContentType(HttpMethod method) {
        return method.getResponseHeader("Content-Type");
    }
}
