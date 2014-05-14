package cz.vity.freerapid.plugins.webclient.interfaces;

import org.apache.commons.httpclient.HttpMethod;

/**
 * Identifies whether content type for method indicates file stream or not
 * @since 0.85
 * @author Vity
 */
public interface FileStreamRecognizer {
    /**
     * Identifies whether content type for http method indicates file stream or not
     * @param method method with response header - content type value CAN BE NULL
     * @param showWarnings indicates if method should show warnings into log file
     */
    boolean isStream(HttpMethod method, boolean showWarnings);
}
