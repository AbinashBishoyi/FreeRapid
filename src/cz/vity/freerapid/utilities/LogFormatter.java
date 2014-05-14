package cz.vity.freerapid.utilities;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Vity
 */
public class LogFormatter extends Formatter {

    private Date dat = new Date();
    private final static String format = "{0,time}";
    private MessageFormat formatter;

    private Object args[] = new Object[1];

    final static String LINE_SEPARATOR = System.getProperty("line.separator", "\n");
    // Line separator string.  This is the value of the line.separator
    // property at the moment that the SimpleFormatter was created.

    /**
     * Format the given LogRecord.
     *
     * @param record the log record to be formatted.
     * @return a formatted log record
     */
    public synchronized String format(LogRecord record) {
        StringBuilder sb = new StringBuilder();
        // Minimize memory allocations here.
        dat.setTime(record.getMillis());
        args[0] = dat;
        StringBuffer text = new StringBuffer();
        if (formatter == null) {
            formatter = new MessageFormat(LogFormatter.format);
        }
        formatter.format(args, text, null);
        sb.append(text);
        sb.append(' ');
        String message = formatMessage(record);
        sb.append(record.getLevel().getName());
        sb.append(": ");
        sb.append(message);
        sb.append(LINE_SEPARATOR);
        if (record.getThrown() != null) {
            try {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                record.getThrown().printStackTrace(pw);
                pw.close();
                sb.append(sw.toString());
            } catch (Exception ex) {
                //
            }
        }
        return sb.toString();
    }
}