package cz.vity.freerapid.plugins.webclient;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ladislav Vitasek
 */
public final class PlugUtils {

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
        }
        if (index > 0) {
            value = value.substring(0, index);
        }
        value = value.trim().replaceAll(" ", "").replaceAll(",", ".");
        if (value.indexOf('.') > 0)
            return new BigDecimal(value).multiply(new BigDecimal(constant)).setScale(0).longValue();
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
}
