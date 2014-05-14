package cz.vity.freerapid.utilities;

import org.apache.commons.codec.binary.Base64;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ntoskrnl
 */
public class DataURI {
    private final static Logger logger = Logger.getLogger(DataURI.class.getName());

    private final byte[] bytes;
    private final boolean base64;
    private final String contentType;
    private final String charset;

    private DataURI(byte[] bytes, boolean base64, String contentType, String charset) {
        this.bytes = bytes;
        this.base64 = base64;
        this.contentType = contentType;
        this.charset = charset;
    }

    public boolean isBase64() {
        return base64;
    }

    public String getContentType() {
        return contentType;
    }

    public String getCharset() {
        return charset;
    }

    public byte[] toBytes() {
        return Arrays.copyOf(bytes, bytes.length);
    }

    @Override
    public String toString() {
        final String s;
        try {
            s = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LogUtils.processException(logger, e);
            return null;
        }
        return s;
    }

    public BufferedImage toImage() {
        final BufferedImage i;
        try {
            i = ImageIO.read(new ByteArrayInputStream(bytes));
        } catch (IOException e) {
            LogUtils.processException(logger, e);
            return null;
        } catch (IllegalArgumentException e) {
            //JDK 6 bug ICO vs WBMP
            LogUtils.processException(logger, e);
            return null;
        }

        return i;
    }

    public static DataURI parse(final String spec) {
        if (!spec.startsWith("data:")) {
            logger.warning("Data URI doesn't start with 'data:'");
            return null;
        }
        if (!spec.contains(",")) {
            logger.warning("Data URI doesn't contain a comma");
            return null;
        }
        final Matcher matcher = Pattern.compile("data:([^;,]+?)?(;base64)?(?:;charset=([^;,]+?))?(;base64)?,(.*)", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE).matcher(spec);
        if (!matcher.find()) {
            logger.warning("Invalid data URI " + spec);
            return null;
        }
        boolean base64 = false;
        String contentType = "text/plain";
        String charset = "US-ASCII";
        if (matcher.group(2) != null || matcher.group(4) != null) {
            base64 = true;
        }
        String s = matcher.group(1);
        if (s != null) {
            contentType = s;
        }
        s = matcher.group(3);
        if (s != null) {
            charset = s;
        }
        String strData = matcher.group(5);
        if (base64) {
            strData = strData.replaceAll("\\s+", "");
        } else {
            try {
                strData = URLDecoder.decode(strData, charset);
            } catch (UnsupportedEncodingException e) {
                LogUtils.processException(logger, e);
                return null;
            } catch (IllegalArgumentException e) {
                LogUtils.processException(logger, e);
                return null;
            }
        }
        byte[] bytes;
        try {
            bytes = strData.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            LogUtils.processException(logger, e);
            return null;
        }
        if (base64) {
            bytes = Base64.decodeBase64(bytes);
        }
        return new DataURI(bytes, base64, contentType, charset);
    }

}
