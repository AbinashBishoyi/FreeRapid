package cz.vity.freerapid.plugins.webclient.interfaces;

import org.apache.commons.httpclient.HttpMethod;

/**
 * Identifies whether content type for method indicates file stream or not
 *
 * @author Vity
 * @author ntoskrnl
 * @since 0.85
 */
public interface FileStreamRecognizer {

    /**
     * Indicates that the Content-Type header is empty or not present.
     *
     * @since 0.87
     */
    String EMPTY = "<empty>";

    /**
     * Identifies whether content type for http method indicates file stream or not
     *
     * @param method       method with response header
     * @param showWarnings indicates if method should show warnings into log file
     */
    boolean isStream(final HttpMethod method, final boolean showWarnings);

}
