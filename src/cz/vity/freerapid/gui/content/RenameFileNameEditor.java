package cz.vity.freerapid.gui.content;

import cz.vity.freerapid.core.AppPrefs;
import cz.vity.freerapid.core.FileTypeIconProvider;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.model.DownloadFile;
import cz.vity.freerapid.plugins.webclient.utils.HttpUtils;
import cz.vity.freerapid.swing.Swinger;

import javax.swing.*;
import java.awt.*;

/**
 * Provides mapping between DownloadFile and filename to allow edit the column
 * @author Vity
 */
public class RenameFileNameEditor extends DefaultCellEditor {

    private JPanel component;
    private final JLabel iconLabel;

    public RenameFileNameEditor(final FileTypeIconProvider fileTypeIconProvider) {
        super(new JTextField());
        final JTextField field = (JTextField) editorComponent;
        component = new JPanel(new BorderLayout(0, 0));
        component.setBorder(null);
        iconLabel = new JLabel();
        component.add(iconLabel, BorderLayout.WEST);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 6));
        component.add(field, BorderLayout.CENTER);
        editorComponent = component;
        field.removeActionListener(delegate);

        this.setClickCountToStart(1000); // to avoid editing using mouse
        delegate = new EditorDelegate() {

            private DownloadFile downloadFile;

            public void setValue(Object value) {
                downloadFile = (DownloadFile) value;
                final String fileName = downloadFile.getFileName();
                iconLabel.setIcon(fileTypeIconProvider.getIconImageByFileType(downloadFile.getFileType(), false));
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


    @Override
    public Component getComponent() {
        return component;
    }
}
