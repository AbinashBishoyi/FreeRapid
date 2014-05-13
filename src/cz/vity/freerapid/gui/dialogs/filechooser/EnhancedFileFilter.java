package cz.vity.freerapid.gui.dialogs.filechooser;

import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Utils;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
 * @author Vity
 */
final class EnhancedFileFilter extends FileFilter implements IFileType {
    private final Object[] extensions;
    private final String description;

    public static EnhancedFileFilter createAllFilesFilter() {
        return createFilter(new String[0], "allFiles");
    }

    public static EnhancedFileFilter createFilter(final String[] extensions, final String labelDescription) {
        return new EnhancedFileFilter(extensions, labelDescription);
    }

    public EnhancedFileFilter(final String[] extensions, final String labelDescription) {
        this.extensions = extensions;
        final StringBuilder buffer = new StringBuilder();
        final int length = extensions.length;
        for (int i = 0; i < length; ++i) {
            buffer.append("*.").append(extensions[i]);
            if (i + 1 != length)
                buffer.append(',');
        }
        if (extensions.length == 0)
            buffer.append("*.*");
        this.description = Swinger.getResourceMap(JAppFileChooser.class).getString(labelDescription, buffer.toString());
    }

    public final String getExtension() {
        return extensions[0].toString();
    }

    public final boolean accept(final File f) {
        if (f.isDirectory())
            return true;
        if (extensions.length == 0)//all files
            return true;
        final String extension = Utils.getExtension(f);
        if (extension != null)
            for (Object ext : extensions) {
                if (extension.equals(ext)) {
                    return true;
                }
            }
        return false;
    }

    //The description of this filter
    public final String getDescription() {
        return description;
    }
}