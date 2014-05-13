package cz.vity.freerapid.plugins.webclient;

import cz.vity.freerapid.plugins.webclient.utils.Entities;
import cz.vity.freerapid.plugins.webclient.utils.GOCR;
import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
public final class PlugUtils {
    private final static Logger logger = Logger.getLogger(PlugUtils.class.getName());


    /**
     * Parse input string and converts it into bytes
     * Acceptable input:
     * 1.35 Gb, 0.5 Mb 5 465kB, default value is kB
     * Function is not case sensitive.
     * All ',' are converted to '.'
     *
     * @param value input string parsed from page
     * @return filesize in bytes
     */
    public static long getFileSizeFromString(String value) {
        if (value == null)
            throw new NullPointerException("Input value cannot be null");

        value = value.toUpperCase();
        int constant = 1;
        int index = value.lastIndexOf("KB");
        if (index >= 0) {
            constant = 1024;
        } else if ((index = value.lastIndexOf("MB")) >= 0) {
            constant = 1024 * 1024;
        } else if ((index = value.lastIndexOf("GB")) >= 0) {
            constant = 1024 * 1024 * 1024;
        } else {
            index = value.lastIndexOf("BYTES");
        }
        if (index > 0) {
            value = value.substring(0, index);
        }
        value = value.trim().replaceAll(" ", "").replaceAll(",", ".");
        if (value.indexOf('.') > 0)
            return new BigDecimal(value).multiply(BigDecimal.valueOf(constant)).setScale(0, RoundingMode.UP).longValue();
        else
            return Long.parseLong(value) * constant;
    }

    public static Matcher matcher(final String regexp, final String contentString) {
        if (contentString == null)
            throw new NullPointerException("Input value cannot be null");
        return Pattern.compile(regexp, Pattern.MULTILINE).matcher(contentString);
    }

    public static boolean find(final String regexp, final String contentString) {
        return matcher(regexp, contentString).find();
    }


    /**
     * <p>Unescapes a string containing entity escapes to a string
     * containing the actual Unicode characters corresponding to the
     * escapes. Supports HTML 4.0 entities.</p>
     * <p/>
     * <p>For example, the string "&amp;lt;Fran&amp;ccedil;ais&amp;gt;"
     * will become "&lt;Fran&ccedil;ais&gt;"</p>
     * <p/>
     * <p>If an entity is unrecognized, it is left alone, and inserted
     * verbatim into the result string. e.g. "&amp;gt;&amp;zzzz;x" will
     * become "&gt;&amp;zzzz;x".</p>
     *
     * @param str the <code>String</code> to unescape, may be null
     * @return a new unescaped <code>String</code>, <code>null</code> if null string input
     */
    public static String unescapeHtml(String str) {
        if (str == null) {
            return null;
        }
        try {
            StringWriter writer = new StringWriter((int) (str.length() * 1.5));
            Entities.HTML40.unescape(writer, str);
            return writer.toString();
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot unescape HTML", e);
            return null;
        }
    }
        /**
     * <p>Find and return value of given parameter in html tag (eg. input).</p>
     * <p/>
     * <p>For example, the for content "input type="hidden" name="par" value="val>"
     * and parameter "par" returns "val"</p>
     * <p/>
     * 
     * @param name name of parameter
     * @param content  <code>String</code> to search in
     * @throws cz.vity.freerapid.plugins.exceptions.PluginImplementationException given name not found in given content
     * @return <code>String</code> value of parameter
     */

    public static String getParameter(String name, String content) throws PluginImplementationException {
        Matcher matcher = PlugUtils.matcher("name=\"" + name + "\"[^v>]*value=\"([^\"]*)\"", content);
        if (matcher.find()) {
            return matcher.group(1);
        } else
            throw new PluginImplementationException("Parameter " + name + " was not found");
    }


    public static String recognize(final BufferedImage image, final String commandLineOptions) {
        final GOCR gocr = new GOCR(image, commandLineOptions);
        try {
            return gocr.recognize();
        } catch (IOException e) {
            return null;
        }
    }

}
