package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.swing.Swinger;

import javax.swing.*;

/**
 * Provides mapping between DownloadFile and filename to allow edit the column
 * @author Vity
 */
public class RenameFileNameEditor extends DefaultCellEditor {


    public RenameFileNameEditor() {
        super(new JTextField());
        final JTextField field = (JTextField) editorComponent;
        field.removeActionListener(delegate);

        this.setClickCountToStart(1000); // to avoid editing using mouse
        delegate = new EditorDelegate() {

            private DownloadFile downloadFile;

            public void setValue(Object value) {
                downloadFile = (DownloadFile) value;
                final String fileName = downloadFile.getFileName();
                field.setText((fileName != null) ? fileName : "");
                Swinger.inputFocus(field);
                if (AppPrefs.getProperty(UserProp.RENAME_FILE_ACTION_SELECT_WITHOUT_EXTENSION, UserProp.RENAME_FILE_ACTION_SELECT_WITHOUT_EXTENSION_DEFAULT)) {
                    final int extIndex = field.getText().lastIndexOf('.');
                    if (extIndex > -1) {
                        field.setCaretPosition(0);
                        field.moveCaretPosition(extIndex);
                    } else field.selectAll();
                } else {
                    field.selectAll();
                }
            }


            public Object getCellEditorValue() {
                String text = field.getText();
                if (text.isEmpty()) {
                    return downloadFile;
                }
                text = HttpUtils.replaceInvalidCharsForFileSystem(text, "_");//we have to remove invalid characters
                downloadFile.setFileName(text);
                return downloadFile;
            }
        };
        field.addActionListener(delegate);
    }

}
