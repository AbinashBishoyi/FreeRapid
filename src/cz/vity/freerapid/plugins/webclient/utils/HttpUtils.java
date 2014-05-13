package cz.vity.freerapid.plugins.webclient.utils;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
final public class HttpUtils {
    private final static Logger logger = Logger.getLogger(HttpUtils.class.getName());

    /**
     * Do not instantiate HttpUtils.
     */
    private HttpUtils() {
    }


    public static String getFileName(HttpMethod method) {

        final Header disposition = method.getResponseHeader("Content-Disposition");
        if (disposition != null && disposition.getValue().toLowerCase().contains("attachment")) {
            final String value = disposition.getValue();
            String str = "filename=";
            final String lowercased = value.toLowerCase();
            int index = lowercased.lastIndexOf(str);
            if (index >= 0) {
                String s = value.substring(index + str.length());
                if (s.startsWith("\"") && s.endsWith("\";"))
                    s = s.substring(1, s.length() - 2);
                if (s.startsWith("\"") && s.endsWith("\""))
                    s = s.substring(1, s.length() - 1);
                // napr. pro xtraupload je jeste treba dekodovat
                if (s.matches(".*%[0-9A-Fa-f]+.*"))
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.warning("Unsupported encoding");
                    }
                return s;
            } else {
                //test na buggove Content-Disposition
                str = "filename\\*=UTF-8''";
                index = lowercased.lastIndexOf(str);
                if (index >= 0) {
                    final String s = value.substring(index + str.length());
                    if (!s.isEmpty())
                        try {
                            return URLDecoder.decode(s, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.warning("Unsupported encoding");
                        }
                } else {
                    logger.warning("File name was not found in:" + value);
                }
            }
        }
        return null;
    }
}
