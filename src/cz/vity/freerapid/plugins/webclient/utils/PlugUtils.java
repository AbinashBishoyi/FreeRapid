package cz.vity.freerapid.plugins.webclient.utils;

import cz.vity.freerapid.plugins.exceptions.PluginImplementationException;
import cz.vity.freerapid.plugins.webclient.interfaces.HttpFile;
import cz.vity.freerapid.utilities.Utils;
import org.apache.commons.httpclient.methods.PostMethod;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 * @author ntoskrnl
 */
public final class PlugUtils {
    /**
     * instance of logger
     */
    private final static Logger logger = Logger.getLogger(PlugUtils.class.getName());
    /**
     * regexp pattern for form parameter matching - cached because of speed optimization
     */
    private static Pattern parameterInputPattern;
    private static Pattern parameterNamePattern;
    private static Pattern parameterValuePattern;

    /**
     * <p>Parses a file size string and converts it into bytes.</p>
     * <p>Supports the following suffixes: KB, MB, GB, TB, BYTE, B, BB</p>
     * <p>Certain Cyrillic characters are automatically converted to their ASCII equivalents.
     * Any whitespace characters as well as the "&amp;nbsp;" literal are ignored.
     * Everything this function does is case insensitive.
     * Commas and points as decimal and thousand separators are automatically handled appropriately.</p>
     *
     * @param string file size to parse
     * @return file size in bytes
     * @throws PluginImplementationException if an error occurs when parsing
     * @see HttpFile#setFileSize(long)
     */
    public static long getFileSizeFromString(final String string) throws PluginImplementationException {
        String value = string.replace('\u0431', 'B').replace('\u043A', 'K').replace('\u041A', 'K').replace('\u041C', 'M').replace('\u0433', 'G').replace('\u0413', 'G');
        value = value.toUpperCase(Locale.ENGLISH).replaceAll("(\\s|\u00A0|&NBSP;)+", "");
        long constant = 1;
        int index = value.lastIndexOf("KB");
        if (index >= 0) {
            constant = 1024;
        } else if ((index = value.lastIndexOf("MB")) >= 0) {
            constant = 1024 * 1024;
        } else if ((index = value.lastIndexOf("GB")) >= 0) {
            constant = 1024 * 1024 * 1024;
        } else if ((index = value.lastIndexOf("TB")) >= 0) {
            constant = 1024L * 1024 * 1024 * 1024;
        } else {
            index = value.lastIndexOf("BYTE");
            if (index < 0)
                index = value.lastIndexOf("BB");
            if (index < 0)
                index = value.lastIndexOf("B");
        }
        if (index > 0) {
            value = value.substring(0, index);
        }
        try {
            value = handlePointAndComma(value);
        } catch (final IllegalArgumentException e) {
            throw new PluginImplementationException("Error parsing file size: " + string);
        }
        try {
            if (value.indexOf('.') > 0) {
                return new BigDecimal(value).multiply(BigDecimal.valueOf(constant)).setScale(0, RoundingMode.UP).longValue();
            } else {
                return Long.parseLong(value) * constant;
            }
        } catch (final NumberFormatException e) {
            throw new PluginImplementationException("Error parsing file size: " + string);
        }
    }

    private static String handlePointAndComma(final String string) {
        final int firstPointIndex = string.indexOf('.');
        final int lastPointIndex = string.lastIndexOf('.');
        final int firstCommaIndex = string.indexOf(',');
        final int lastCommaIndex = string.lastIndexOf(',');

        final boolean noPoints = firstPointIndex == -1 && lastPointIndex == -1;
        final boolean noCommas = firstCommaIndex == -1 && lastCommaIndex == -1;
        final boolean multiplePoints = firstPointIndex != lastPointIndex;
        final boolean multipleCommas = firstCommaIndex != lastCommaIndex;

        if (noPoints && noCommas) {
            return string;
        }
        if (multiplePoints && multipleCommas) {
            throw new IllegalArgumentException();
        }
        if (noPoints && multipleCommas) {
            return string.replace(",", "");
        }
        if (noCommas && multiplePoints) {
            return string.replace(".", "");
        }
        if (multiplePoints && firstCommaIndex < lastPointIndex) {
            throw new IllegalArgumentException();
        }
        if (multipleCommas && firstPointIndex < lastCommaIndex) {
            throw new IllegalArgumentException();
        }
        if (lastPointIndex < lastCommaIndex) {
            return string.replace(".", "").replace(',', '.');
        } else {
            return string.replace(",", "");
        }
    }

    /**
     * Help method to get Matcher for given regular expression and string.<br />
     * Pattern is case sensitive.<br />
     *
     * @param regexp        regular expression
     * @param contentString string that is searched for regular expression pattern
     * @return Matcher for given regular expression and string
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
            return writer.toString().replaceAll("\u00A0", " ");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Cannot unescape HTML", e);
            return null;
        }
    }

    /**
     * Converts a string in this form:<br>
     * <code>\u0025D9\u002583\u0025D8\u0025B4\u0025D9\u002581+\u0025D8\u0025A7\u0025D9\u002584\u0025D8\u0025AD\u0025D9\u002582\u0025D9\u00258A\u0025D9\u002582\u0025D8\u0025A9+32++\u0025D8\u0025A7\u0025D9\u002584\u0025D8\u0025AA\u0025D9\u002588\u0025D8\u0025B6\u0025D9\u00258A\u0025D8\u0025AD</code>
     * <br> into <code>Slečna si to opravdu užívá... _))))))))))))))))))</code><br />
     *
     * @param str a string to convert
     * @return decoded string
     * @throws PluginImplementationException if malformed \\u encoding
     * @since 0.86
     */
    public static String unescapeUnicode(final String str) throws PluginImplementationException {
        final StringBuilder buf = new StringBuilder();
        for (int i = 0, len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            label0:
            switch (c) {
                case '\\':
                    if (i == str.length() - 1) {
                        buf.append('\\');
                        break;
                    }
                    c = str.charAt(++i);
                    switch (c) {
                        case 'n':
                            buf.append('\n');
                            break label0;
                        case 't':
                            buf.append('\t');
                            break label0;
                        case 'r':
                            buf.append('\r');
                            break label0;
                        case 'u':
                            int value = 0;
                            for (int j = 0; j < 4; j++) {
                                c = str.charAt(++i);
                                switch (c) {
                                    case '0':
                                    case '1':
                                    case '2':
                                    case '3':
                                    case '4':
                                    case '5':
                                    case '6':
                                    case '7':
                                    case '8':
                                    case '9':
                                        value = ((value << 4) + c) - 48;
                                        break;
                                    case 'a':
                                    case 'b':
                                    case 'c':
                                    case 'd':
                                    case 'e':
                                    case 'f':
                                        value = ((value << 4) + 10 + c) - 97;
                                        break;
                                    case 'A':
                                    case 'B':
                                    case 'C':
                                    case 'D':
                                    case 'E':
                                    case 'F':
                                        value = ((value << 4) + 10 + c) - 65;
                                        break;
                                    default:
                                        throw new PluginImplementationException("Malformed \\uxxxx encoding: " + str);
                                }
                            }
                            buf.append((char) value);
                            break;
                        default:
                            buf.append(c);
                            break;
                    }
                    break;
                default:
                    buf.append(c);
                    break;
            }
        }
        return buf.toString();
    }

    /**
     * <p>Find and return value of given parameter in html tag (eg. input).</p>
     * <p/>
     * <p>For example, the for content <code>"input type="hidden" name="par" value="val>"</code>
     * and parameter "par" returns "val"</p>
     * <p/>
     *
     * @param name    name of parameter
     * @param content <code>String</code> to search in
     * @return <code>String</code> value of parameter
     * @throws cz.vity.freerapid.plugins.exceptions.PluginImplementationException given name not found in given content
     */
    public static String getParameter(String name, String content) throws PluginImplementationException {
        //(?: means no capturing group
        initParameterPatterns();

        int start = 0;
        Matcher matcher = parameterInputPattern.matcher(content);
        while (matcher.find(start)) {
            final String input = matcher.group(1);
            final Matcher matchName = parameterNamePattern.matcher(input);
            if (matchName.find()) {
                String paramName = getCorrectGroup(matchName);
                if (name.toLowerCase().equals(paramName.toLowerCase())) {
                    final Matcher matchValue = parameterValuePattern.matcher(input);

                    if (matchValue.find()) {
                        return getCorrectGroup(matchValue);
                    } else {
                        return "";
                    }

                }
            }
            start = matcher.end();
        }

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
        initParameterPatterns();
        int start = 0;
        Matcher matcher = parameterInputPattern.matcher(content);
        while (matcher.find(start)) {
            final String input = matcher.group(1);
            final Matcher matchName = parameterNamePattern.matcher(input);
            if (matchName.find()) {
                String paramName = getCorrectGroup(matchName);
                if (set.contains(paramName)) {
                    final Matcher matchValue = parameterValuePattern.matcher(input);
                    String paramValue = "";
                    if (matchValue.find()) {
                        paramValue = getCorrectGroup(matchValue);
                    }
                    set.remove(paramName);
                    postMethod.addParameter(paramName, paramValue);
                    if (set.isEmpty())
                        break;
                }
            }
            start = matcher.end();
        }
        if (!set.isEmpty()) {
            throw new PluginImplementationException("The parameters " + Arrays.toString(set.toArray()) + " were not found");
        }
    }

    private static void initParameterPatterns() {
        if (parameterInputPattern == null)
            parameterInputPattern = Pattern.compile("<input (.+?)>", Pattern.DOTALL | Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (parameterNamePattern == null)
            parameterNamePattern = Pattern.compile("(?:name\\s?=\\s?)(?:([\"]([^\"]+)[\">$])|([']([^']+)['>$])|(([^'\">\\s]+)[/\\s>$]?))", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        if (parameterValuePattern == null)
            parameterValuePattern = Pattern.compile("(?:value\\s?=\\s?)(?:([\"]([^\"]+)[\">$])|([']([^']+)['>$])|(([^'\">\\s]+)[/\\s>$]?))", Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
    }

    private static String getCorrectGroup(Matcher matcher) {
        for (int i = matcher.groupCount(); i > 0; i--) {
            final String group = matcher.group(i);
            if (group != null) {
                return group;
            }
        }
        throw new IllegalStateException("Group cannot be empty");
    }

    /**
     * Replace literal "&amp;amp;" with character '&'. Light version of {@link #unescapeHtml(String)}.
     *
     * @param s <code>String</code> where replace
     * @return <code>String</code> with replacement
     */
    public static String replaceEntities(String s) {
        return s.replaceAll("&amp;", "&");
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

    /**
     * Extracts file name from the site. White space around the file name is trimmed.
     *
     * @param file           file to apply found file name
     * @param content        content to search
     * @param fileNameBefore string before file name, character '\n' is replaced with regexp \\s*
     * @param fileNameAfter  string after file name, character '\n' is replaced with regexp \\s*
     * @throws PluginImplementationException file name was not found
     * @since 0.82
     */
    public static void checkName(HttpFile file, String content, String fileNameBefore, String fileNameAfter) throws PluginImplementationException {
        final Matcher matcher = prepareMatcher(fileNameBefore, "(.+?)", fileNameAfter, content);
        if (matcher.find()) {
            String fileName = matcher.group(1);
            logger.info("File name " + fileName);
            file.setFileName(fileName);
        } else {
            throw new PluginImplementationException("File name not found");
        }
    }

    /**
     * Extracts file name from the site. White space around the file size is trimmed.
     *
     * @param file           file to apply found file name
     * @param content        content to search
     * @param fileSizeBefore string before file name, character '\n' is replaced with regexp \\s*
     * @param fileSizeAfter  string after file name, character '\n' is replaced with regexp \\s*
     * @throws PluginImplementationException file size string was not found
     * @since 0.82
     */
    public static void checkFileSize(HttpFile file, String content, String fileSizeBefore, String fileSizeAfter) throws PluginImplementationException {
        final Matcher matcher = prepareMatcher(fileSizeBefore, "(.+?)", fileSizeAfter, content);
        if (matcher.find()) {
            final String fileSize = matcher.group(1);
            logger.info("File size " + fileSize);
            final long size = getFileSizeFromString(matcher.group(1));
            file.setFileSize(size);
        } else {
            throw new PluginImplementationException("File size not found");
        }
    }

    /**
     * Returns the string between two other strings. White space around the string is trimmed.
     *
     * @param content      searched content
     * @param stringBefore string before searched string, character '\n' is replaced with regexp \\s*
     * @param stringAfter  string after searched string, character '\n' is replaced with regexp \\s*
     * @return found string - result is trimmed
     * @throws PluginImplementationException No string between stringBefore and stringAfter
     */
    public static String getStringBetween(String content, String stringBefore, String stringAfter) throws PluginImplementationException {
        return getStringBetween(content, stringBefore, stringAfter, 1);
    }

    /**
     * Returns string between two other strings.
     * With parameter count you can specify count of sucessful result, the final result is returned <br />
     * example:<br/> <code>blablabla(<b>xxx</b>); blablabla(<b>yyyy</b>)</code>, with parameters <code>'blablabla(', ';', count=2</code>   <b>yyyy</b> will be returned
     *
     * @param content      searched content
     * @param stringBefore string before searched string, character '\n' is replaced with regexp \\s*
     * @param stringAfter  string after searched string, character '\n' is replaced with regexp \\s*
     * @param count        what item in row is the right result
     * @return found string - result is trimmed
     * @throws PluginImplementationException No string between stringBefore and stringAfter
     * @since 0.84
     */
    public static String getStringBetween(final String content, final String stringBefore, final String stringAfter, final int count) throws PluginImplementationException {
        if (count < 1) {
            throw new IllegalArgumentException("Finding count is less than 1");
        }
        final Matcher matcher = prepareMatcher(stringBefore, "(.+?)", stringAfter, content);
        for (int i = 1; i <= count; ++i) {
            if (matcher.find()) {
                if (i == count) {
                    return matcher.group(1);
                }
            } else {
                throw new PluginImplementationException(String.format("No string between '%s' and '%s' was found", stringBefore, stringAfter));
            }
        }
        throw new PluginImplementationException();
    }

    /**
     * Returns number between two other strings. White space around the number is trimmed.
     *
     * @param content      searched content
     * @param stringBefore string before searched string, character '\n' is replaced with regexp \\s*
     * @param stringAfter  string after searched string, character '\n' is replaced with regexp \\s*
     * @return found number
     * @throws PluginImplementationException No number between stringBefore and stringAfter
     */
    public static int getNumberBetween(String content, String stringBefore, String stringAfter) throws PluginImplementationException {
        final Matcher matcher = prepareMatcher(stringBefore, "(\\d+?)", stringAfter, content);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new PluginImplementationException(String.format("No number between '%s' and '%s' was found", stringBefore, stringAfter));
        }
    }

    /**
     * Returns time value in seconds between two other strings. White space around the number is trimmed.
     *
     * @param content      searched content
     * @param stringBefore string before searched string, character '\n' is replaced with regexp \\s*
     * @param stringAfter  string after searched string, character '\n' is replaced with regexp \\s*
     * @param srcTimeUnit  source time unit
     * @return time value in seconds
     * @throws PluginImplementationException No wait time value between stringBefore and stringAfter
     */
    public static int getWaitTimeBetween(String content, String stringBefore, String stringAfter, TimeUnit srcTimeUnit) throws PluginImplementationException {
        final int i = getNumberBetween(content, stringBefore, stringAfter);
        return (int) srcTimeUnit.toSeconds(i);
    }

    private static Matcher prepareMatcher(String before, String middle, String after, String content) {
        before = Pattern.quote(Utils.rtrim(before)).replaceAll("\n", "\\\\E\\\\s*\\\\Q");
        after = Pattern.quote(Utils.ltrim(after)).replaceAll("\n", "\\\\E\\\\s*\\\\Q");
        return matcher(before + "\\s*" + middle + "\\s*" + after, content);
    }

    /**
     * Returns file name suggestion from URL
     *
     * @param stringURL URL in string form
     * @return file name suggestion
     * @throws PluginImplementationException Error suggesting file name
     * @since 0.855
     */
    public static String suggestFilename(String stringURL) throws PluginImplementationException {
        if (stringURL == null || stringURL.isEmpty()) {
            throw new PluginImplementationException("Error suggesting file name");
        }

        try {
            String path = new URL(stringURL).getPath();
            final int i = path.lastIndexOf("/");
            if (i < 0) {
                throw new PluginImplementationException("Error suggesting file name");
            }
            return URLDecoder.decode(path.substring(i + 1), "UTF-8");
        } catch (Exception e) {
            throw new PluginImplementationException("Error suggesting file name");
        }
    }

}
