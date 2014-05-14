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
        return new EnhancedFileFilter(new String[0], "allFiles");
    }

    public EnhancedFileFilter(final String[] extensions, final String labelDescription, final Object... additionalParams) {
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
        final Object[] params = new Object[additionalParams.length + 1];
        params[0] = buffer.toString();
        System.arraycopy(additionalParams, 0, params, 1, additionalParams.length);
        this.description = Swinger.getResourceMap(JAppFileChooser.class).getString(labelDescription, params);
    }

    @Override
    public final String getExtension() {
        return extensions[0].toString();
    }

    @Override
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

    @Override
    public final String getDescription() {
        return description;
    }

}