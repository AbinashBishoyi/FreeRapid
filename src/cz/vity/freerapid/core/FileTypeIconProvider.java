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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class FileTypeIconProvider {
    private final static Logger logger = Logger.getLogger(FileTypeIconProvider.class.getName());

    private final static Pattern fileExtensionPattern = Pattern.compile("[\\.\\-_]([a-z\\d]+?)$");
    private final static Pattern fileNamePattern = Pattern.compile("/([^/]*?(\\.|-)(zip|rar|avi|wmv|mp\\d?|srt|sub|apk))\\.html?", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
    private final static Pattern encodedPattern = Pattern.compile("%[A-Z0-9]{2}%");

    private final Map<String, Icon> systemIcons = new ConcurrentHashMap<String, Icon>();
    private final ResourceMap resourceMap;

    public FileTypeIconProvider(ApplicationContext context) {
        resourceMap = context.getResourceMap();
    }

    public static String identifyFileType(String fileName) {
        if (fileName == null) {
            return "";
        }
        fileName = fileName.toLowerCase(Locale.ENGLISH);
        final Matcher matcher = fileExtensionPattern.matcher(fileName);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
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
        String s = url.replace(":", "_").trim();
        if (s.startsWith("?"))
            s = s.substring(1);
        if (s.isEmpty()) {
            return "?";
        }
        return checkEncodedFileName(s);
    }

    private static String checkEncodedFileName(String name) {
        if (encodedPattern.matcher(name).find()) {
            return Utils.urlDecode(name);
        }
        return name;
    }

    public Icon getIconImageByFileType(String fileType, boolean bigImage) {
        if (fileType == null) {
            return null;
        }
        if (AppPrefs.getProperty(UserProp.USE_SYSTEM_ICONS, true)) {
            fileType = fileType.toLowerCase(Locale.ENGLISH);
            return getSystemIcon(fileType, bigImage);
        } else {
            fileType = fileType.toUpperCase(Locale.ENGLISH);
            final String base;
            if (bigImage) {
                base = "iconFileTypeBig_" + fileType;
            } else {
                base = "iconFileTypeSmall_" + fileType;
            }
            if (resourceMap.containsKey(base)) {
                return resourceMap.getImageIcon(base);
            } else {
                if (bigImage) {
                    return resourceMap.getImageIcon("iconFileTypeBig_ISO");
                } else {
                    return resourceMap.getImageIcon("iconFileTypeSmall_ISO");
                }
            }
        }
    }

    private Icon getSystemIcon(String extension, boolean bigImage) {
        Icon icon = systemIcons.get(extension + bigImage);
        if (icon != null) {
            return icon;
        }
        File file = null;
        try {
            file = File.createTempFile("icon", "." + extension);
            if (bigImage) {
                icon = getShellFolderIcon(file);
            } else {
                icon = FileSystemView.getFileSystemView().getSystemIcon(file);
            }
        } catch (IOException e) {
            //ignore
        } catch (NullPointerException e) {//JDK BUG
            //ignore
        } catch (ClassFormatError e) {
            //ignore
        } finally {
            if (file != null) {
                if (!file.delete()) {
                    logger.warning("Failed to delete temporary file " + file);
                }
            }
        }
        if (icon == null) {
            if (bigImage) {
                icon = resourceMap.getImageIcon("iconFileTypeBig_UNKNOWN");
            } else {
                icon = resourceMap.getImageIcon("iconFileTypeSmall_UNKNOWN");
            }
        }
        systemIcons.put(extension + bigImage, icon);
        return icon;
    }

    private static Icon getShellFolderIcon(final File file) {
        //This method takes several milliseconds to run, but it's invoked rarely.
        try {
            final Method method = Class.forName("sun.awt.shell.ShellFolder").getMethod("getShellFolder", File.class);
            final Object sf = method.invoke(null, file);
            final Method m = sf.getClass().getMethod("getIcon", boolean.class);
            m.setAccessible(true);
            final Object result = m.invoke(sf, true);
            if (result != null) {
                return new ImageIcon((Image) result);
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

}
