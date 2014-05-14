package cz.vity.freerapid.core.application;


import cz.vity.freerapid.utilities.LogUtils;
import org.jdesktop.swingx.error.ErrorInfo;
import org.jdesktop.swingx.error.ErrorReporter;

import java.awt.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Odesle pomoci vestaveneho klienta vyjimku
 *
 * @author Vity
 */
class EmailErrorReporter implements ErrorReporter {
    private final static Logger logger = Logger.getLogger(EmailErrorReporter.class.getName());
    private final static String MAIL_FOR_ERRORS = "info@wordrider.net";

    public void reportError(ErrorInfo info) throws NullPointerException {
        if (!Desktop.isDesktopSupported() || !Desktop.getDesktop().isSupported(Desktop.Action.MAIL))
            return;

        final Desktop desktop = Desktop.getDesktop();
        String mailbody = String.format("Basic Error message:%s%nCategory:%sState:%s%nException:%s%n", info.getBasicErrorMessage(), info.getCategory(), mapToString(info.getState()), info.getErrorException().toString());
        mailbody = encode(mailbody);
        String mail = String.format("mailto:%s?subject=%s&body=%s", MAIL_FOR_ERRORS, encode("ERModeller Error report"), mailbody);
        try {
            desktop.mail(new URI(mail));
        } catch (IOException e) {
            LogUtils.processException(logger, e);
        }
        catch (URISyntaxException e) {
            LogUtils.processException(logger, e);
        }
    }

    private static String encode(final String text) {
        try {
            return URLEncoder.encode(text, "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            LogUtils.processException(logger, e);
            return "";
        }
    }

    private static String mapToString(Map<String, String> state) {
        final StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> entry : state.entrySet()) {
            builder.append(entry.getKey());
            builder.append('=');
            builder.append(entry.getValue());
            builder.append('\n');
        }
        return builder.toString();
    }
}
