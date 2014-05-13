package cz.vity.freerapid.gui.dialogs.filechooser;

import java.io.File;

/**
 * @author Vity
 */
class OpenFileChooser extends JAppFileChooser {

    public OpenFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.setDialogType(OPEN_DIALOG);
    }

    protected String getDialogName() {
        return "OpenFileChooser";
    }
}
