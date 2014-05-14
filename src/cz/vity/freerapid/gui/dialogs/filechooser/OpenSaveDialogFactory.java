package cz.vity.freerapid.gui.dialogs.filechooser;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.gui.FRDUtils;
import cz.vity.freerapid.swing.Swinger;
import cz.vity.freerapid.utilities.Utils;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author Vity
 */
public class OpenSaveDialogFactory {
    private static OpenSaveDialogFactory instance = null;
    private ApplicationContext context;

    private OpenSaveDialogFactory(ApplicationContext context) {
        this.context = context;
    }

    public static synchronized OpenSaveDialogFactory getInstance(ApplicationContext context) {
        if (instance == null)
            return new OpenSaveDialogFactory(context);
        else return instance;
    }

    public File getSaveResultsDialog() {
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(3);
        filters.add(new EnhancedFileFilter(new String[]{"txt"}, "filterTxt"));
        filters.add(new EnhancedFileFilter(new String[]{"csv"}, "filterCsv"));
        filters.add(new EnhancedFileFilter(new String[]{"dxf"}, "filterDxf"));
        final String path = AppPrefs.getProperty(UserProp.LAST_USED_FOLDER_EXPORT, "export");

        final String defaultName = AppPrefs.getProperty(UserProp.LAST_EXPORT_FILENAME, new File(path).getName());
        final File result = getSaveFileDialog(filters, UserProp.LAST_EXPORT_FILTER, UserProp.LAST_USED_FOLDER_EXPORT, defaultName);
        if (result != null) {
            AppPrefs.storeProperty(UserProp.LAST_EXPORT_FILENAME, result.getName());
        }
        return result;
    }

    public File getDirChooser(File currentDirectory, String directoryChooserTitle) {
        final OpenFileChooser fileDialog = new OpenFileChooser(currentDirectory);

        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.setDialogTitle(directoryChooserTitle);
        fileDialog.setMultiSelectionEnabled(false);
        fileDialog.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        final int result = fileDialog.showOpenDialog(Frame.getFrames()[0]);
        if (result != JFileChooser.APPROVE_OPTION) {
            return null;
        } else {
            return fileDialog.getSelectedFile();
        }
    }

    public File[] getChooseImageFileDialog() {
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(3);
        filters.add(new EnhancedFileFilter(new String[]{"tif", "tiff"}, "filterTIF"));
        return getOpenFileDialog(filters, UserProp.LAST_IMPORT_FILTER, UserProp.IMPORT_LAST_USED_FOLDER, false);
    }

    @SuppressWarnings({"SuspiciousMethodCalls"})
    private File[] getOpenFileDialog(final List<EnhancedFileFilter> fileFilters, final String lastUsedFilterKey, final String folderPathKey, final boolean multiSelection) {
        final OpenFileChooser fileDialog = new OpenFileChooser(new File(AppPrefs.getProperty(folderPathKey, "")));
        fileDialog.updateFileFilters(fileFilters, lastUsedFilterKey);
        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.setMultiSelectionEnabled(multiSelection);
        final int result = fileDialog.showOpenDialog(Frame.getFrames()[0]);
        if (result != JFileChooser.APPROVE_OPTION) {
            return new File[0];
        } else {
            AppPrefs.storeProperty(lastUsedFilterKey, fileFilters.indexOf(fileDialog.getFileFilter()));
            AppPrefs.storeProperty(folderPathKey, FRDUtils.getAbsRelPath(fileDialog.getSelectedFile()).getPath());
            return multiSelection
                    ? fileDialog.getSelectedFiles()
                    : new File[]{fileDialog.getSelectedFile()};
        }
    }

    @SuppressWarnings({"SuspiciousMethodCalls"})
    private File getSaveFileDialog(final List<EnhancedFileFilter> fileFilters, final String lastUsedFilterKey, final String folderPathKey, final String fileName) {
        final SaveFileChooser fileDialog = new SaveFileChooser(new File(AppPrefs.getProperty(folderPathKey, "")));
        fileDialog.setMultiSelectionEnabled(false);
        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.setFileName(fileName);
        fileDialog.updateFileFilters(fileFilters, lastUsedFilterKey);
        final int result = fileDialog.showSaveDialog(Frame.getFrames()[0]);
        if (result != JFileChooser.APPROVE_OPTION)
            return null;
        else {
            File f = fileDialog.getSelectedFile();
            if (f == null)
                return null;
            final FileFilter usedFilter = fileDialog.getFileFilter();
            if (usedFilter instanceof IFileType && (Utils.getExtension(f)) == null) {
                final String extension = ((IFileType) usedFilter).getExtension();
                f = new File(f.getPath().concat(".").concat(extension));
            }
            if (f.isFile() && f.exists()) {
                final ResourceMap map = Swinger.getResourceMap(JAppFileChooser.class);
                final int choice = Swinger.getChoiceYesNoCancel(map.getString("message.confirm.overwrite"));
                if (choice == Swinger.RESULT_NO)
                    return null;
            }
            AppPrefs.storeProperty(lastUsedFilterKey, fileFilters.indexOf(usedFilter));
            AppPrefs.storeProperty(folderPathKey, f.getPath());
            return f;
        }
    }

    public File[] getChooseProxyList() {
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(1);
        filters.add(new EnhancedFileFilter(new String[]{"txt", "list"}, "filterTxt"));
        return getOpenFileDialog(filters, UserProp.LAST_IMPORT_FILTER, UserProp.IMPORT_LAST_USED_FOLDER, false);
    }

    public File[] getImportLinks(final List<String[]> supportedTypes) {
        return getOpenFileDialog(getFilters(supportedTypes, false), UserProp.LAST_IMPORT_FILTER, UserProp.IMPORT_LAST_USED_FOLDER, true);
    }

    public File getExportLinks(final List<String[]> supportedTypes) {
        return getSaveFileDialog(getFilters(supportedTypes, true), UserProp.LAST_EXPORT_FILTER, UserProp.LAST_USED_FOLDER_EXPORT, "");
    }

    private static List<EnhancedFileFilter> getFilters(final List<String[]> supportedTypes, final boolean export) {
        final String[] allSupported = getAllSupported(supportedTypes);
        final List<EnhancedFileFilter> filters = new ArrayList<EnhancedFileFilter>(allSupported.length + 1);
        if (!export) {
            filters.add(new EnhancedFileFilter(allSupported, "allSupportedFiles"));
        }
        for (final String[] strings : supportedTypes) {
            if (strings[0].equals("txt")) {
                filters.add(new EnhancedFileFilter(strings, "filterTxt"));
            } else if (strings[0].equals("csv")) {
                filters.add(new EnhancedFileFilter(strings, "filterCsv"));
            } else {
                filters.add(new EnhancedFileFilter(strings, "filterContainer", strings[0].toUpperCase(Locale.ENGLISH)));
            }
        }
        return filters;
    }

    private static String[] getAllSupported(final List<String[]> supportedTypes) {
        int len = 0;
        for (final String[] strings : supportedTypes) {
            len += strings.length;
        }
        final String[] allSupported = new String[len];
        len = 0;
        for (final String[] strings : supportedTypes) {
            System.arraycopy(strings, 0, allSupported, len, strings.length);
            len += strings.length;
        }
        return allSupported;
    }

}
