package cz.vity.freerapid.core;

import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Vity
 */
public class FileTypeIconProvider {
    private ResourceMap map;
    private HashSet<String> supportedTypes;
    private final static Logger logger = Logger.getLogger(FileTypeIconProvider.class.getName());
    private static Pattern pattern;
    private static final String DEFAULT_EXTENSION = "iso";


    public FileTypeIconProvider(ApplicationContext context) {
        map = context.getResourceMap();
        final String[] strings = (String[]) map.getObject("fileTypes", String[].class);
        supportedTypes = new HashSet<String>(Arrays.asList(strings));
        StringBuilder builder = new StringBuilder();
        builder.append("\\.(");
        for (int i = 0; i < strings.length; i++) {
            String item = strings[i];
            builder.append(item);
            if (i != strings.length - 1) {
                builder.append("|");
            }
        }
        builder.append(")(\\.?|$)");
        final String regexp = builder.toString();
        logger.info("Regexp " + regexp);
        pattern = Pattern.compile(regexp);
    }

    public static String identifyFileType(String fileName) {
        if (pattern == null)
            throw new IllegalStateException("Not initialized yet");
        if (fileName == null)
            return DEFAULT_EXTENSION;
        fileName = fileName.toLowerCase();

        final Matcher matcher = pattern.matcher(fileName);
        final String fileType;
        if (matcher.find()) {
            fileType = matcher.group(1);
        } else {
            fileType = DEFAULT_EXTENSION;
        }
        logger.info("Found file type for file " + fileName + " (" + fileType + ")");
        return fileType;
    }

    public static String identifyFileName(String url) {
        int indexFrom = url.length();
        if (url.endsWith("/"))
            --indexFrom;
        final int foundIndex = url.lastIndexOf("/", indexFrom);
        if (foundIndex >= 0) {
            return url.substring(foundIndex + 1, indexFrom);
        } else return "";
    }

    public ImageIcon getIconImageByFileType(String fileType, boolean bigImage) {
        fileType = fileType.toUpperCase();
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
