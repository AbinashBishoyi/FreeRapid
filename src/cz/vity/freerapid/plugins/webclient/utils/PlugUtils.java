package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import org.apache.commons.httpclient.methods.PostMethod;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
public final class PlugUtils {
    /**
     * instance of logger
     */
    private final static Logger logger = Logger.getLogger(PlugUtils.class.getName());


    /**
     * Parses input string and converts it into bytes.<br />
     * Acceptable input:<br />
     * <code>1.35 Gb, 0.5 Mb 5 465kB, 45654 6544 bytes, 54654654</code> - default value is kB<br />
     * Function is not case sensitive. Spaces among numbers are not important (they are removed).<br />
     * All ',' are converted to '.'<br />
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

    /**
     * Help method to get Matcher for given regular expression and string.<br />
     * Pattern is case sensitive.<br />
     *
     * @param regexp        regular expression
     * @param contentString string that is searched for regular expression pattern
     * @return
     * @see java.util.regex.Pattern
     */
    public static Matcher matcher(final String regexp, final String contentString) {
        if (contentString == null)
            throw new NullPointerException("Input value cannot be null");
        return Pattern.compile(regexp, Pattern.MULTILINE).matcher(contentString);
    }

    /**
     * Help method to test whether given regular expression matches given content string.<br />
     * Pattern is case sensitive.
     *
     * @param regexp        regular expression
     * @param contentString string that is searched for regular expression pattern
     * @return true if the given pattern was found in given string, false otherwise
     * @see java.util.regex.Pattern
     */
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
     * @param name    name of parameter
     * @param content <code>String</code> to search in
     * @return <code>String</code> value of parameter
     * @throws cz.vity.freerapid.plugins.exceptions.PluginImplementationException
     *          given name not found in given content
     */

    public static String getParameter(String name, String content) throws PluginImplementationException {
        final Matcher matcher = Pattern.compile("name=\"" + name + "\"[^>]*value=\"([^\"]*)\"", Pattern.MULTILINE).matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        } else
            throw new PluginImplementationException("Parameter " + name + " was not found");
    }

    /**
     * Parses content and search for parameter's value in tag form.<br />
     * Html <code>&lt;form&gt;</code> should have structure <code>name="paramName" .... value="paramValue"</code></br>
     * This parameter and its value are added to POST method.
     *
     * @param postMethod method to add found parameters
     * @param content    <code>String</code> to search in
     * @param parameters form parameter names
     * @throws PluginImplementationException any of the parameter were not found in given content
     * @see cz.vity.freerapid.plugins.webclient.utils.PlugUtils#getParameter(String, String)
     */
    public static void addParameters(final PostMethod postMethod, final String content, final String[] parameters) throws PluginImplementationException {
        if (parameters.length == 0)
            throw new IllegalArgumentException("You have to provide some parameter names");
        final Set<String> set = new HashSet<String>(parameters.length);
        set.addAll(Arrays.asList(parameters));
        final Matcher matcher = Pattern.compile("name=\"(.*?)\"[^>]*value=\"([^\"]*)\"", Pattern.MULTILINE).matcher(content);
        int start = 0;
        String param;
        while (matcher.find(start)) {
            param = matcher.group(1);
            if (set.contains(param)) {
                set.remove(param);
                postMethod.addParameter(param, matcher.group(2));
            }
            if (set.isEmpty())
                break;
            start = matcher.end();
        }
        if (!set.isEmpty()) {
            throw new PluginImplementationException("Following parameters: " + Arrays.toString(set.toArray()) + " were not found");
        }
    }

    /**
     * <p>Replace entity \&amp; with character &.</p>
     * <p>Used in partly decode given URL, where unescapeHtml is not suitable <p/>
     *
     * @param s <code>String</code> where replace
     * @return <code>String</code> with replacement
     */
    public static String replaceEntities(String s) {
        return s.replaceAll("\\&amp;", "&");
    }

    /**
     * Method calls GOCR implementation to recognize text from image.
     *
     * @param image              an image from which text should be recognized
     * @param commandLineOptions additional command line options for GOCR application
     * @return text that was recognized, or null if there was an error during calling of GOCR application
     */
    public static String recognize(final BufferedImage image, final String commandLineOptions) {
        final GOCR gocr = new GOCR(image, commandLineOptions);
        try {
            return gocr.recognize();
        } catch (IOException e) {
            return null;
        }
    }

}
