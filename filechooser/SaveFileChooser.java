package cz.vity.freerapid.gui.dialogs.filechooser;

import java.io.File;

/**
 * @author Vity
 */
class SaveFileChooser extends JAppFileChooser {

    public SaveFileChooser(File currentDirectory) {
        super(currentDirectory);
        this.setDialogType(SAVE_DIALOG);
    }

    protected String getDialogName() {
        return "SaveFileChooser";
    }

}