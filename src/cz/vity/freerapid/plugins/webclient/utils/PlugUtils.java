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
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
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
     * regexp pattern for form parameter matching - cached because of speed optimization
     */
    private static Pattern parameterInputPattern;
    private static Pattern parameterNamePattern;
    private static Pattern parameterValuePattern;

    /**
     * Parses input string and converts it into bytes.<br />
     * Acceptable input:<br />
     * <code>1.35 Gb, 0.5 Mb 5 465kB, 45654 6544 bytes, 54654654, 280B, also buggy 280BB</code> - default value is B<br />
     * Function is not case sensitive. Spaces among numbers are not important (they are removed).<br />
     * All ',' are converted to '.'<br />
     * Since version 0.83 there is additional replacement for characters in Russian alphabet (azbuka).<br />
     * All <code>&nbsp;</code> are replaced to be a pure <code>' '</code>
     *
     * @param value input string parsed from page
     * @return filesize in bytes
     */
    public static long getFileSizeFromString(String value) {
        if (value == null)
            throw new NullPointerException("Input value cannot be null");
        value = value.replace('\u041C', 'M');//azbuka
        value = value.replace('\u0431', 'B');
        value = value.replace('\u043A', 'K');
        value = value.replace('\u041A', 'K');
        value = value.replace('\u0433', 'G');
        value = value.replace('\u0413', 'G');
        value = value.toUpperCase().replaceAll("&NBSP;", " ");
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
            if (index < 0)
                index = value.lastIndexOf("BB");
            if (index < 0)
                index = value.lastIndexOf("B");
        }
        if (index > 0) {
            value = value.substring(0, index);
        }
        value = value.replaceAll("(\\s|\u00A0)*", "").replace(',', '.');
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
     * Method converts a string in this form:<br>
     * <code>video_title", "\u0025D9\u002583\u0025D8\u0025B4\u0025D9\u002581+\u0025D8\u0025A7\u0025D9\u002584\u0025D8\u0025AD\u0025D9\u002582\u0025D9\u00258A\u0025D9\u002582\u0025D8\u0025A9+32++\u0025D8\u0025A7\u0025D9\u002584\u0025D8\u0025AA\u0025D9\u002588\u0025D8\u0025B6\u0025D9\u00258A\u0025D8\u0025AD</code>
     * <br> into <code>Slečna si to opravdu užívá... _))))))))))))))))))</code><br />
     * @param str a string to convert
     * @return decoded string
     * @since 0.86
     * @throws IllegalArgumentException if malformed \\u encoding
     */
    public static String unescapeUnicode(String str) {
        StringBuilder buf = new StringBuilder();
        int i = 0;
        for (int len = str.length(); i < len; i++) {
            char c = str.charAt(i);
            label0:
            switch (c) {
                case 92: // '\\'
                    if (i == str.length() - 1) {
                        buf.append('\\');
                        break;
                    }
                    c = str.charAt(++i);
                    switch (c) {
                        case 110: // 'n'
                            buf.append('\n');
                            break label0;

                        case 116: // 't'
                            buf.append('\t');
                            break label0;

                        case 114: // 'r'
                            buf.append('\r');
                            break label0;

                        case 117: // 'u'
                            int value = 0;
                            for (int j = 0; j < 4; j++) {
                                c = str.charAt(++i);
                                switch (c) {
                                    case 48: // '0'
                                    case 49: // '1'
                                    case 50: // '2'
                                    case 51: // '3'
                                    case 52: // '4'
                                    case 53: // '5'
                                    case 54: // '6'
                                    case 55: // '7'
                                    case 56: // '8'
                                    case 57: // '9'
                                        value = ((value << 4) + c) - 48;
                                        break;

                                    case 97: // 'a'
                                    case 98: // 'b'
                                    case 99: // 'c'
                                    case 100: // 'd'
                                    case 101: // 'e'
                                    case 102: // 'f'
                                        value = ((value << 4) + 10 + c) - 97;
                                        break;

                                    case 65: // 'A'
                                    case 66: // 'B'
                                    case 67: // 'C'
                                    case 68: // 'D'
                                    case 69: // 'E'
                                    case 70: // 'F'
                                        value = ((value << 4) + 10 + c) - 65;
                                        break;

                                    case 58: // ':'
                                    case 59: // ';'
                                    case 60: // '<'
                                    case 61: // '='
                                    case 62: // '>'
                                    case 63: // '?'
                                    case 64: // '@'
                                    case 71: // 'G'
                                    case 72: // 'H'
                                    case 73: // 'I'
                                    case 74: // 'J'
                                    case 75: // 'K'
                                    case 76: // 'L'
                                    case 77: // 'M'
                                    case 78: // 'N'
                                    case 79: // 'O'
                                    case 80: // 'P'
                                    case 81: // 'Q'
                                    case 82: // 'R'
                                    case 83: // 'S'
                                    case 84: // 'T'
                                    case 85: // 'U'
                                    case 86: // 'V'
                                    case 87: // 'W'
                                    case 88: // 'X'
                                    case 89: // 'Y'
                                    case 90: // 'Z'
                                    case 91: // '['
                                    case 92: // '\\'
                                    case 93: // ']'
                                    case 94: // '^'
                                    case 95: // '_'
                                    case 96: // '`'
                                    default:
                                        throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                                }
                            }

                            buf.append((char) value);
                            break;

                        case 111: // 'o'
                        case 112: // 'p'
                        case 113: // 'q'
                        case 115: // 's'
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
     * @throws cz.vity.freerapid.plugins.exceptions.PluginImplementationException
     *          given name not found in given content
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

    /**
     * Extracts file name from the site. White space around file name is trimmed.
     *
     * @param file           file to apply found file name
     * @param content        content to search
     * @param fileNameBefore string before file name, character '\n' is replaced as \\s*
     * @param fileNameAfter  string after file name, character '\n' is replaced as \\s*
     * @throws PluginImplementationException file name was not found
     * @since 0.82
     */
    public static void checkName(HttpFile file, String content, String fileNameBefore, String fileNameAfter) throws PluginImplementationException {
        final String before = Pattern.quote(Utils.rtrim(fileNameBefore)).replaceAll("\n", "\\\\E\\\\s*\\\\Q");
        final String after = Pattern.quote(Utils.ltrim(fileNameAfter)).replaceAll("\n", "\\\\E\\\\s*\\\\Q");
        final Matcher matcher = matcher(before + "\\s*(.+?)\\s*" + after, content);
        if (matcher.find()) {
            String fileName = matcher.group(1).trim();
            logger.info("File name " + fileName);
//            final String decoded = checkEncodedFileName(fileName);
//            if (!fileName.equals(decoded)) {
//                logger.info("File name decoded" + decoded);
//                fileName = decoded;
//            }
            file.setFileName(fileName);
        } else {
            throw new PluginImplementationException("File name not found");
        }
    }

    /**
     * Extracts file name from the site
     *
     * @param file           file to apply found file name
     * @param content        content to search
     * @param fileSizeBefore string before file name  - without white space characters on the RIGHT side, character '\n' is replaced as \\s*
     * @param fileSizeAfter  string after file name  - without white space characters on the LEFT side, character '\n' is replaced as \\s*
     * @throws PluginImplementationException file size string was not found
     * @since 0.82
     */
    public static void checkFileSize(HttpFile file, String content, String fileSizeBefore, String fileSizeAfter) throws PluginImplementationException {
        final String before = Pattern.quote(Utils.rtrim(fileSizeBefore)).replaceAll("\n", "\\\\E\\\\s*\\\\Q");
        final String after = Pattern.quote(Utils.ltrim(fileSizeAfter)).replaceAll("\n", "\\\\E\\\\s*\\\\Q");
        final Matcher matcher = matcher(before + "\\s*(.+?)\\s*" + after, content);
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
     * Returns string between 2 other strings.
     *
     * @param content      searched content
     * @param stringBefore string before searched string  - without white space characters on the RIGHT side
     * @param stringAfter  string after searched string  - without white space characters on the LEFT side
     * @return found string - result is trimmed
     * @throws PluginImplementationException No string between stringBefore and stringAfter
     */
    public static String getStringBetween(String content, String stringBefore, String stringAfter) throws PluginImplementationException {
        return getStringBetween(content, stringBefore, stringAfter, 1);
    }

    /**
     * Returns string between 2 other strings.
     * With parameter count you can specify count of sucessful result, the final result is returned <br />
     * example:<br/> <code>blablabla(<b>xxx</b>); blablabla(<b>yyyy</b>)</code>, with parameters <code>'blablabla(', ';', count=2</code>   <b>yyyy</b> will be returned
     *
     * @param content      searched content
     * @param stringBefore string before searched string  - without white space characters on the RIGHT side
     * @param stringAfter  string after searched string  - without white space characters on the LEFT side
     * @param count        what item in row is the right result
     * @return found string - result is trimmed
     * @throws cz.vity.freerapid.plugins.exceptions.PluginImplementationException
     *          No string between stringBefore and stringAfter
     * @since 0.84
     */
    public static String getStringBetween(final String content, final String stringBefore, final String stringAfter, final int count) throws PluginImplementationException {
        if (count < 1) {
            throw new IllegalArgumentException("Finding count is less than 1");
        }
        final String before = Pattern.quote(Utils.rtrim(stringBefore));
        final String after = Pattern.quote(Utils.ltrim(stringAfter));
        final Matcher matcher = PlugUtils.matcher(before + "\\s*(.+?)\\s*" + after, content);
        int start = 0;
        for (int i = 1; i <= count; ++i) {
            if (matcher.find(start)) {
                if (i == count) {
                    return matcher.group(1);
                } else
                    start = matcher.end();
            } else {
                throw new PluginImplementationException(String.format("No string between '%s' and '%s' was found - attempt %s", stringBefore, stringAfter, count));
            }
        }
        throw new PluginImplementationException();
    }

    /**
     * Returns number between 2 other strings.
     *
     * @param content      searched content
     * @param stringBefore string before searched string  - without white space characters on the RIGHT side
     * @param stringAfter  string after searched string  - without white space characters on the LEFT side
     * @return found number
     * @throws PluginImplementationException No number between stringBefore and stringAfter
     */
    public static int getNumberBetween(String content, String stringBefore, String stringAfter) throws PluginImplementationException {
        final String before = Pattern.quote(Utils.rtrim(stringBefore));
        final String after = Pattern.quote(Utils.ltrim(stringAfter));
        final Matcher matcher = matcher(before + "\\s*([0-9]+?)\\s*" + after, content);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        } else {
            throw new PluginImplementationException(String.format("No number between '%s' and '%s' was found", stringBefore, stringAfter));
        }
    }

    /**
     * Returns time value in seconds between 2 other strings.
     *
     * @param content      searched content
     * @param stringBefore string before searched string - without white space characters on the RIGHT side
     * @param stringAfter  string after searched string - without white space characters on the LEFT side
     * @param srcTimeUnit  source time unit - usually <code>TimeUnit.SECONDS</code> or <code>TimeUnit.MILLISECONDS</code>
     * @return time value in seconds
     * @throws PluginImplementationException No wait time value between stringBefore and stringAfter
     */
    public static int getWaitTimeBetween(String content, String stringBefore, String stringAfter, TimeUnit srcTimeUnit) throws PluginImplementationException {
        final String replace = "\\\\E\\\\s*\\\\Q";
        final String before = Pattern.quote(Utils.rtrim(stringBefore)).replaceAll("\n", replace);
        final String after = Pattern.quote(Utils.ltrim(stringAfter)).replaceAll("\n", replace);
        final Matcher matcher = matcher(before + "\\s*([0-9]+?)\\s*" + after, content);
        if (matcher.find()) {
            final long i = Long.parseLong(matcher.group(1));
            return new Long(srcTimeUnit.toSeconds(i)).intValue();
        } else
            throw new PluginImplementationException(String.format("No wait time value between '%s' and '%s' was found", stringBefore, stringAfter));
    }
    /*
        //(Or wait 5 minutes, 24 seconds)</font>
        public static int extractComplexWaitTime(String content, String pattern, TimeUnit greaterTimeUnit) {
            final Matcher matcher = PlugUtils.matcher(".*?([0-9]+?)?.+?([0-9]+?)", pattern);
            if (matcher.find()) {
                final int i1 = matcher.start(1);
                final int i2 = matcher.end(1);
                if (matcher.groupCount() == 1) {
                    try {
                        return getWaitTimeBetween(content, pattern.substring(0, i1), pattern.substring(i2), greaterTimeUnit);
                    } catch (PluginImplementationException e) {
                        return -1;
                    }
                } else {
                    final int i3 = matcher.start(2);
                    final int i4 = matcher.end(2);
                    final String middle = pattern.substring(i2, i3);
                    int timeBetween1;
                    try {
                        timeBetween1 = getWaitTimeBetween(content, pattern.substring(0, i1), middle, greaterTimeUnit);
                    } catch (PluginImplementationException e) {
                        timeBetween1 = -1;
                    }

                    int timeBetween2;
                    try {
                        final TimeUnit secondTimeUnit = (TimeUnit.HOURS == greaterTimeUnit) ? TimeUnit.MINUTES : TimeUnit.SECONDS;
                        timeBetween2 = getWaitTimeBetween(content, middle, pattern.substring(i4), secondTimeUnit);
                    } catch (PluginImplementationException e) {
                        timeBetween2 = -1;
                    }
                    if (timeBetween1 >= 0 && timeBetween2 >= 0) {
                        return timeBetween1 + timeBetween2;
                    } else if (timeBetween1 >= 0) {
                        return timeBetween1;
                    } else if (timeBetween2 >= 0) {
                        return timeBetween2;
                    } else return -1;
                }
            } else return -1;
        }
    */

}
