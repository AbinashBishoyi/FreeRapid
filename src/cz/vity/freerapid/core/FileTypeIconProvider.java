package cz.vity.freerapid.core;

import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class FileTypeIconProvider {
    private ResourceMap map;
    private final static Logger logger = Logger.getLogger(FileTypeIconProvider.class.getName());
    private static Pattern pattern;
    private static Pattern fileNamePattern = Pattern.compile("/([^/]*?\\.(zip|rar|avi|wmv|mp3))\\.html?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private static Pattern encoded = Pattern.compile("%[A-Z0-9]{2}%");
    //    private static final String DEFAULT_EXTENSION = "unknown";
    private final Map<String, Icon> systemLargeIcons = new Hashtable<String, Icon>();
    private final Map<String, Icon> systemSmallIcons = new Hashtable<String, Icon>();


    public FileTypeIconProvider(ApplicationContext context) {
        map = context.getResourceMap();
        final String[] strings = (String[]) map.getObject("fileTypes", String[].class);
        StringBuilder builder = new StringBuilder();
        builder.append("(\\.|_)(");
        for (int i = 0; i < strings.length; i++) {
            String item = strings[i];
            builder.append(item);
            if (i != strings.length - 1) {
                builder.append("|");
            }
        }
        builder.append(")(\\.?|_?|$)");
        final String regexp = builder.toString();
        logger.info("Regexp " + regexp);
        pattern = Pattern.compile(regexp);
    }

    public static String identifyFileType(String fileName) {
        if (pattern == null)
            throw new IllegalStateException("Not initialized yet");
        if (fileName == null)
            return "";
        fileName = fileName.toLowerCase();

        final Matcher matcher = pattern.matcher(fileName);
        final String fileType;
        if (matcher.find()) {
            fileType = matcher.group(2);
        } else {
            fileType = "";
        }

        return fileType;
    }

    public static String identifyFileName(final String url) {
        final Matcher matcher = fileNamePattern.matcher(url);
        if (matcher.find()) {
            return checkEncodedFileName(matcher.group(1));
        }
        final String[] strings = url.split("/");
        for (int i = strings.length - 1; i >= 0; i--) {
            final String s = strings[i].trim();
            if (!s.isEmpty())
                return s;
        }
        String s = url.replaceAll("\\:", "_").trim();
        if (s.startsWith("?"))
            s = s.substring(1);
        if (s.isEmpty()) {
            return "?";
        }
        return checkEncodedFileName(s);
    }

    public Icon getIconImageByFileType(String fileType, boolean bigImage) {
        if (fileType == null)
            return null;
        if (AppPrefs.getProperty(UserProp.USE_SYSTEM_ICONS, true)) {
            fileType = fileType.toLowerCase();

            if (bigImage)
                return getBigSystemIcon(fileType);
            else
                return getSmallSystemIcon(fileType);

        } else {


            fileType = fileType.toUpperCase(Locale.ENGLISH);


            final String base;
            if (bigImage) {
                base = "iconFileTypeBig_" + fileType;
            } else {
                base = "iconFileTypeSmall_" + fileType;
            }
            if (map.containsKey(base)) {
                return map.getImageIcon(base);
            } else {
                if (bigImage)
                    return map.getImageIcon("iconFileTypeBig_ISO");
                else
                    return map.getImageIcon("iconFileTypeSmall_ISO");
            }
        }
    }

    private Icon getSmallSystemIcon(String extension) {
//        if ("".equals(extension))
//            return null;
        if (this.systemSmallIcons.containsKey(extension))
            return systemSmallIcons.get(extension);
        File file = null;
        try {
//Create a temporary file with the specified extension
            file = File.createTempFile("icon", "." + extension);
            if (file == null)
                throw new IOException("Creation TMP file failed");
            FileSystemView view = FileSystemView.getFileSystemView();
            Icon icon = view.getSystemIcon(file);
            if (icon == null) {
                final ImageIcon ico = map.getImageIcon("iconFileTypeSmall_UNKNOWN");
                systemSmallIcons.put(extension, ico);
                return ico;
            }
            //Delete the temporary file
            systemSmallIcons.put(extension, icon);
            return icon;
        } catch (IOException e) {
            return map.getImageIcon("iconFileTypeSmall_UNKNOWN");
        } catch (NullPointerException e) {//JDK BUG
            return map.getImageIcon("iconFileTypeSmall_UNKNOWN");
        } catch (ClassFormatError e) {
            return map.getImageIcon("iconFileTypeSmall_UNKNOWN");
        } finally {
            if (file != null)
                file.delete();
        }
    }

    private Icon getBigSystemIcon(String extension) {
        if (this.systemLargeIcons.containsKey(extension))
            return systemLargeIcons.get(extension);
        File file = null;
        try {
            file = File.createTempFile("icon", "." + extension);

            Image ico = getShellFolderIcon(file);
            if (ico == null) {
                Icon icon = map.getImageIcon("iconFileTypeBig_UNKNOWN");
                systemLargeIcons.put(extension, icon);
                return icon;
            }
            Icon icon = new ImageIcon(ico);
            //Delete the temporary file
            systemLargeIcons.put(extension, icon);
            return icon;
        } catch (IOException e) {
            return map.getImageIcon("iconFileTypeBig_UNKNOWN");
        } finally {
            if (file != null)
                file.delete();
        }
    }

    private static Image getShellFolderIcon(final File file) {
//        ShellFolder shellFolder = ShellFolder.getShellFolder(file);
//        Image ico = shellFolder.getIcon(true);

        try {
            final Method method = Class.forName("sun.awt.shell.ShellFolder").getMethod("getShellFolder", File.class);
            final Object sf = method.invoke(null, file);
            final Method m = sf.getClass().getMethod("getIcon", Boolean.TYPE);
            m.setAccessible(true);
            final Object result = m.invoke(sf, true);
            return (Image) result;
        } catch (Exception e) {
            return null;
        }
    }

    private static String checkEncodedFileName(String name) {
        if (encoded.matcher(name).find()) {
            return Utils.urlDecode(name);
        }
        return name;
    }

    //    public static void main(String[] args) {
//        assert identifyFileName("http://netload.in/dateiMTc4MzUxMT/heroes.311.hdtv-lol.avi.htm").equals("heroes.311.hdtv-lol.avi");
//    }

}
