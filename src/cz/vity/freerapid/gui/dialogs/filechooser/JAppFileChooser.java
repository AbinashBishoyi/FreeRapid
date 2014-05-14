package cz.vity.freerapid.gui.dialogs.filechooser;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.appframework.swingx.SingleXFrameApplication;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.awt.*;
import java.io.File;

/**
 * @author Kleopatra
 * @author Vity
 */
abstract class JAppFileChooser extends JFileChooser {

    public JAppFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.setName(getDialogName());
        Swinger.getResourceMap(JAppFileChooser.class).injectComponents(this);
    }

    @Override
    public int showSaveDialog(Component parent) throws HeadlessException {
        return super.showSaveDialog(parent);
    }

    @Override
    protected JDialog createDialog(Component parent) throws HeadlessException {
        JDialog dialog = super.createDialog(parent);
        Application application = Application.getInstance(Application.class);
        ApplicationContext context = application.getContext();
        if (context.getApplication() instanceof SingleXFrameApplication) {
            dialog.setName(getDialogName());

            ((SingleXFrameApplication) context.getApplication()).prepareDialog(dialog, false);
        }
        return dialog;
    }


    public void setFileName(String fileName) {
        final FileChooserUI chooserUI = this.getUI();
        if (chooserUI instanceof BasicFileChooserUI)
            ((BasicFileChooserUI) chooserUI).setFileName(fileName);
    }

    protected abstract String getDialogName();

    public void updateFileFilters(final java.util.List<EnhancedFileFilter> fileFilters, final String lastUsedKey) {
        if (lastUsedKey == null) {
            return;
        }
        for (EnhancedFileFilter fileFilter : fileFilters)
            this.addChoosableFileFilter(fileFilter);
        final int filterIndex = AppPrefs.getProperty(lastUsedKey, fileFilters.size() - 1);
        if (filterIndex >= 0 && filterIndex < fileFilters.size())
            this.setFileFilter(fileFilters.get(filterIndex));
    }


}