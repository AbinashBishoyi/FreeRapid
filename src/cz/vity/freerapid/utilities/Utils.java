package cz.vity.freerapid.utilities;

import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.util.URIUtil;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Pomocne utility pro spravu aplikace Test na system.
 *
 * @author Vity
 */
public final class Utils {
    private final static Logger logger = Logger.getLogger(Utils.class.getName());

    private final static int WINDOWS_OS = 0;
    private static int operatingSystem = -1;
    /**
     * pomocna promenna pro ulozeni verze JVM na ktere bezime
     */
    private static String actualJavaVersion = null;
    private static final int XOR_VALUE = 35132;
    public static volatile String appPath = null;

    static {
        removeCryptographyRestrictions();
    }

    private Utils() {
    }

    /**
     * Vygeneruje/pseudokodovany retezec pomoci funkce XOR - je obousmerna
     *
     * @param text text k zakodovani/dekodovani
     * @return zakodovany/dekodovany retezec
     */
    public static String generateXorString(final String text) {
        if (text == null)
            return null;
        final char[] textArray = text.toCharArray();
        final int length = textArray.length;
        if (length > 0) {
            final StringBuilder buffer = new StringBuilder(length);
            for (int i = 0; i < length; ++i)
                buffer.append((char) (textArray[i] ^ XOR_VALUE));
            return buffer.toString();
        } else return "";
    }

    /**
     * Provede test na verzi JVM na ktere aplikaci bezi
     *
     * @param requiredVersion pozadovana verze
     * @return vraci true, pokud aplikace bezi na pozadovane verzi, pokud ne, vraci false
     */
    public static boolean isJVMVersion(final double requiredVersion) {
        if (actualJavaVersion == null) {
            final String javaVersion = System.getProperty("java.version");
            if (javaVersion == null) {
                return false;
            }
            actualJavaVersion = javaVersion.substring(0, 3);
        }
        final Double actualVersion = new Double(actualJavaVersion);
        return actualVersion.compareTo(requiredVersion) >= 0;
    }

    /*
     * Get the extension of a file.
     */

    public static String getExtension(final File f) {
        String ext = null;
        final String s = f.getName();
        final int i = s.lastIndexOf('.');

        if (i > 0 && i < s.length() - 1) {
            ext = s.substring(i + 1).toLowerCase();
        }
        return ext;
    }

    public static String getPureFilename(final File f) {
        final String[] fileName = f.getName().split("\\.", 2);
        return fileName[0];
    }

    public static String getPureFilenameWithDots(final File f) {
        final String s = f.getName();
        int index = s.lastIndexOf('.');
        if (index > 0)
            return s.substring(0, index);
        else
            return s;
    }

    /**
     * Prida na danou cestu oddelovac, pokud jiz oddelovac na konci ma, nic se nepridava
     *
     * @param filePath cesta
     * @return cesta s oddelovacem
     */
    public static String addFileSeparator(final String filePath) {
        return filePath.endsWith(File.separator) ? filePath : filePath + File.separator;
    }

    /**
     * Makes file name max 60 characters long
     *
     * @param file file
     * @return shortened file path
     */
    public static String shortenFileName(final File file) {
        return shortenFileName(file.getAbsolutePath());
    }

    /**
     * Makes file name shorter
     *
     * @param file        file
     * @param lengthLimit max length limit
     * @return shortened file path
     */
    public static String shortenFileName(final File file, final int lengthLimit) {
        return shortenFileName(file.getAbsolutePath(), lengthLimit);
    }

    /**
     * Makes file name max 60 characters long
     *
     * @param text filePath as string
     * @return shortened file path
     */
    public static String shortenFileName(final String text) {
        return shortenFileName(text, 60);
    }

    /**
     * Makes file name shorter
     *
     * @param text        file path as string
     * @param lengthLimit max length limit
     * @return shortened file path
     */
    public static String shortenFileName(final String text, final int lengthLimit) {
        final int textLength = text.length();
        if (textLength < lengthLimit)
            return text;
        final String fileSeparator = File.separator;
        final String[] separated = text.split((fileSeparator.equals("\\") ? "\\\\" : fileSeparator));
        final int separatedCount = separated.length;
        if (separatedCount > 3) {
            //int charsCount = separated[0].length() + separated[1].length() + separated[separatedCount -2].length() + separated[separatedCount -1].length() + 4;
            int extractIndex = 2, wouldDelete = 0;
            for (; extractIndex < (separatedCount - 2); ++extractIndex) {
                wouldDelete += separated[extractIndex].length();
                if ((textLength - wouldDelete + 3) < lengthLimit)
                    break;
            }
            final StringBuilder result = new StringBuilder(textLength - wouldDelete + 3);
            result.append(separated[0]).append(fileSeparator).append(separated[1]).append(fileSeparator).append("...");
            for (int i = extractIndex + 1; i < separatedCount; ++i) {
                result.append(fileSeparator);
                result.append(separated[i]);
            }
            return result.toString();
        } else
            return text;
    }

    /**
     * Prida dalsi parametr s jeho hodnotou pro odeslani v URL konexi
     *
     * @param params     skladane parametry pro odeslani
     * @param paramName  jmeno parametru
     * @param paramValue hodnota parametru
     */
    public static void addParam(final StringBuilder params, final String paramName, String paramValue) {
        if (paramValue == null)
            paramValue = "";
        String encoded;
        try {
            encoded = java.net.URLEncoder.encode(paramValue, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            encoded = "";
            LogUtils.processException(logger, e);
        }
        if (params.length() > 0)
            params.append('&');
        params.append(paramName).append('=').append(encoded);
    }

    /**
     * Otestuje zda aplikace bezi na Windows
     *
     * @return Vraci test, zda je OS windows
     */
    public static boolean isWindows() {
        if (operatingSystem == -1) {
            final String osName = System.getProperty("os.name");
            if (osName == null || osName.startsWith("Windows")) {
                operatingSystem = WINDOWS_OS;
            } else operatingSystem = 1;
        }
        return operatingSystem == WINDOWS_OS;
    }

    public static void setFileAsHidden(File file) {
        if (!file.exists() || !file.isFile())
            return;
        if (isWindows()) {
            try {
                Runtime.getRuntime().exec("attrib.exe +H " + file.getAbsolutePath());
            } catch (IOException e) {
                logger.warning("Setting file " + file.getAbsolutePath() + " as hidden failed.");
                LogUtils.processException(logger, e);
            }
        } else {
            final String s = file.getName();
            if (!s.startsWith(".")) {
                final File newFile = new File(file.getParentFile(), "." + s);
                if (!file.renameTo(newFile)) {
                    logger.warning("Setting file " + file.getAbsolutePath() + " as hidden failed.");
                }
            }
        }
    }

    public static Properties loadProperties(final String propertiesFile, final boolean isResource) {
        final Properties props = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = (!isResource) ? new FileInputStream(propertiesFile) : Utils.class.getClassLoader().getResourceAsStream(propertiesFile);
            if (inputStream == null)
                throw new IOException("Couldn't read Properties file");
            props.load(inputStream);
            inputStream.close();
            return props;
        } catch (IOException e) {
            try {
                if (inputStream != null)
                    inputStream.close();
            } catch (IOException ex) {
                LogUtils.processException(logger, ex);
            }
            logger.warning("Couldn't load properties:" + propertiesFile);
            return props;
        }
    }

    public static String addParam(final String params, final String paramName, final String paramValue) {
        final String paramWithValue;
        String encoded;
        try {
            encoded = java.net.URLEncoder.encode(paramValue, "ISO-8859-2");
        } catch (UnsupportedEncodingException e) {
            encoded = "";
            LogUtils.processException(logger, e);
        }
        paramWithValue = paramName + "=" + encoded;
        return params.length() > 0 ? params + "&" + paramWithValue : paramWithValue;
    }

    public static boolean hasValue(final String string) {
        return string != null && !string.isEmpty();
    }


    public static String getExceptionMessage(Throwable cause) {
        String message = cause.getLocalizedMessage();
        if (message == null) {
            if (cause.getMessage() != null) {
                message = cause.getMessage();
            } else message = cause.toString();
        }
        return message;
    }

    public static String loadFile(final String fileName) {
        return loadFile(new File(fileName), null);
    }

    public static String loadFile(File file) {
        return loadFile(file, null);
    }

    public static String loadFile(File file, String encoding) {
        final StringBuilder buffer = new StringBuilder(2000);
        BufferedReader stream = null;
        try {
            final InputStreamReader inputStreamReader = (encoding == null) ? new InputStreamReader(new FileInputStream(file)) : new InputStreamReader(new FileInputStream(file), encoding);
            stream = new BufferedReader(inputStreamReader);
            final char[] lines = new char[2000];
            int read;
            while ((read = stream.read(lines)) != -1)
                buffer.append(lines, 0, read);

        } catch (IOException e) {
            LogUtils.processException(logger, e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
            }
        }
        return buffer.toString();
    }

    /**
     * Vraci aktualni cestu k adresari programu ve kterem je jar spusten
     *
     * @return cesta do adresare
     */
    public static String getAppPath() {
        if (appPath != null)
            return appPath;
        try {
            final URI uri = LogUtils.class.getProtectionDomain().getCodeSource().getLocation().toURI();
            if (!"file".equalsIgnoreCase(uri.getScheme())) {
                logger.info("Running Webstart application");
                return appPath = "";
            }
            appPath = new File(uri).getParent();
        } catch (URISyntaxException e) {
            LogUtils.processException(logger, e);
            return appPath = "";
        }
        logger.config("App Path is " + appPath);
        return appPath;
    }

    public static String getSystemLineSeparator() {
        return System.getProperty("line.separator", "\n");
    }

    public static String getApplicationArguments() {
        final Properties properties = System.getProperties();
        if (properties.containsKey("arguments")) {
            final String[] strings = (String[]) properties.get("arguments");
            StringBuilder builder = new StringBuilder();
            for (String s : strings) {
                builder.append(s).append(' ');
            }
            return builder.toString().trim();
        }
        return "";
    }


    /* remove leading whitespace */

    public static String ltrim(String source) {
        return source.replaceAll("^\\s+", "");
    }

    /* remove trailing whitespace */

    public static String rtrim(String source) {
        return source.replaceAll("\\s+$", "");
    }

    public static String urlDecode(String s) {
        try {
            String decoded = URLDecoder.decode(s, "UTF-8");
            if (decoded.contains("\uFFFD")) {
                decoded = URLDecoder.decode(s, "Windows-1250");
            }
            if (decoded.contains("\uFFFD")) {
                return s;
            }
            return decoded;
        } catch (UnsupportedEncodingException e) {
            //ignore
        }
        return s;
    }

    public static URI convertToURI(String url) throws URISyntaxException, URIException {
        URI uri;
        try {
            uri = new URI(url);
        } catch (URISyntaxException e) {
            uri = new URI(URIUtil.encodePathQuery(url));
        }
        return uri;
    }


    public static String reverseString(String string) {
        return new StringBuilder(string).reverse().toString();
    }

    public static String getThrowableDescription(final Throwable t) {
        final String s = t.getClass().getSimpleName();
        final String l = t.getLocalizedMessage();
        if (l == null) {
            return s;
        } else {
            return s + ": " + l;
        }
    }

    public static String dumpStackTraces() {
        final Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
        final Throwable throwable = new Throwable() {
            @Override
            public String toString() {
                return "";
            }
        };
        final StringWriter writer = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(writer);
        for (Map.Entry<Thread, StackTraceElement[]> entry : map.entrySet()) {
            final StackTraceElement[] stackTraceElements = entry.getValue();
            throwable.setStackTrace(stackTraceElements);
            final Thread thread = entry.getKey();
            printWriter.append(String.valueOf(thread)).append(" [").append(String.valueOf(thread.getState())).append(']').append(getSystemLineSeparator());
            throwable.printStackTrace(printWriter);
            printWriter.append(getSystemLineSeparator());
        }
        return writer.toString();
    }

    private static void removeCryptographyRestrictions() {
        if (!isRestrictedCryptography()) {
            return;
        }
        try {
            java.lang.reflect.Field isRestricted;
            try {
                final Class<?> c = Class.forName("javax.crypto.JceSecurity");
                isRestricted = c.getDeclaredField("isRestricted");
            } catch (final ClassNotFoundException e) {
                try {
                    // Java 6 has obfuscated JCE classes
                    final Class<?> c = Class.forName("javax.crypto.SunJCE_b");
                    isRestricted = c.getDeclaredField("g");
                } catch (final ClassNotFoundException e2) {
                    throw e;
                }
            }
            isRestricted.setAccessible(true);
            isRestricted.set(null, false);
        } catch (final Throwable e) {
            logger.log(Level.WARNING, "Failed to remove cryptography restrictions", e);
        }
    }

    private static boolean isRestrictedCryptography() {
        return "Java(TM) SE Runtime Environment".equals(System.getProperty("java.runtime.name"));
    }

}
