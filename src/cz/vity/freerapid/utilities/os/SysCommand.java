package cz.vity.freerapid.utilities.os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public final class SysCommand {
    private final static Logger logger = Logger.getLogger(SysCommand.class.getName());


    private SysCommand() {
    }

    /**
     * Splits command string
     *
     * @param command
     * @return arguments for calling system command
     */
    public static String[] splitCommand(String command) {
        final Matcher matcher = Pattern.compile("(\\\".*?\\\")|([^\\s]+)").matcher(command);
        int start = 0;
        final List<String> list = new ArrayList<String>();
        while (matcher.find(start)) {
            list.add(matcher.group());
            start = matcher.end();
        }
        final String[] result = list.toArray(new String[list.size()]);
        if (logger.isLoggable(Level.INFO)) {
            logger.info("Command separate:" + Arrays.toString(result));
        }
        return result;
    }

}
