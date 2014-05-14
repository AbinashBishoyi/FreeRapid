package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.BCodec;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * Helpful utilities for parsing http headers
 *
 * @author Ladislav Vitasek
 */
final public class HttpUtils {
    private final static Logger logger = Logger.getLogger(HttpUtils.class.getName());

    /**
     * Do not instantiate HttpUtils.
     */
    private HttpUtils() {
    }


    /**
     * Extracts file name from response header Content-Disposition
     * Eg for <code>Content-Disposition: =?UTF-8?attachment;filename="Two Peaks Personal Vehicle Manager 2005 3.2.zip";?=</code>
     * it returns <code>Two Peaks Personal Vehicle Manager 2005 3.2.zip</code>
     *
     * @param method executed HttpMethod with Content-Disposition header
     * @return null if there was now header Content-Disposition or parsed file name
     */
    public static String getFileName(final HttpMethod method) {

        final Header disposition = method.getResponseHeader("Content-Disposition");
        if (disposition != null) {
            final String value = disposition.getValue();
            final String lowercasedValue = value.toLowerCase(Locale.ENGLISH);
            if (!(lowercasedValue.contains("attachment") || lowercasedValue.contains("inline")))
                return null;
            String str = "filename==?";
            int index = lowercasedValue.indexOf(str);
            if (index >= 0) {
                final String s = value.substring(index + str.length() - 2);
                if (!s.isEmpty())
                    try {
                        return new BCodec().decode(s);
                    } catch (DecoderException e) {
                        logger.warning("BCodec - Unsupported encoding or decoder failed");
                    }
            }
            str = "filename=";
            final String lowercased = value.toLowerCase();
            index = lowercased.lastIndexOf(str);
            if (index >= 0) {
                String s = value.substring(index + str.length());
                final int secondQuoteIndex = s.lastIndexOf('\"');
                if (s.startsWith("\"") && secondQuoteIndex > 0)
                    s = s.substring(1, secondQuoteIndex);
                // napr. pro xtraupload je jeste treba dekodovat
                if (s.matches(".*%[0-9A-Fa-f]+.*"))
                    try {
                        s = URLDecoder.decode(s, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        logger.warning("Unsupported encoding");
                    } catch (IllegalArgumentException e) {
                        logger.warning("Invalid file name header: " + e.getMessage());
                        return s;
                    }
                return s;
            } else {
                //test na buggove Content-Disposition
                str = "filename\\*=utf-8''";
                index = lowercased.lastIndexOf(str);
                if (index == -1) {
                    str = "filename*=utf-8''";
                    index = lowercased.lastIndexOf(str);
                }

                if (index >= 0) {
                    final String s = value.substring(index + str.length());
                    if (!s.isEmpty())
                        try {
                            return URLDecoder.decode(s, "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            logger.warning("Unsupported encoding");
                        } catch (IllegalArgumentException e) {
                            logger.warning("Invalid file name header: " + e.getMessage());
                            return s;
                        }

                } else
                    logger.warning("File name was not found in " + value);
            }
        }
        return null;
    }

    /**
     * Replace invalid characters for file name on current file system with given one. <br/>
     * Usual use: <br >
     * <code>replaceInvalidCharsForFileSystem("diskFileName:", "_")</code> returns <code>diskFileName_</code> on Windows file system <br />
     *
     * @param fileName      given file name
     * @param replaceString usually a character that should be used for invalid characters
     * @return string with replaced invalid characters
     */
    public static String replaceInvalidCharsForFileSystem(final String fileName, final String replaceString) {
        String result;
        if (Utils.isWindows()) {
            // http://msdn.microsoft.com/en-us/library/aa365247(VS.85)
            result = fileName.replaceAll("[<>:\"/\\\\\\|\\?\\*\\uFFFD[\\u0000-\\u001F]]", replaceString);
            result = result.replaceAll("\\.+$", "");
            result = result.replaceFirst("(?i)^(CON|PRN|AUX|NUL|COM\\d|LPT\\d)(\\..*)?$", "$1" + replaceString + "$2");
        } else {
            result = fileName.replaceAll("[/\\uFFFD]", replaceString);
            if (result.matches("\\.+")) {
                result = "";
            }
        }
        if (result.isEmpty()) {
            return replaceString;
        }
        return result;
    }
}
