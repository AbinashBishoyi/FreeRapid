package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.utilities.FileUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;

/**
 * @author ntoskrnl
 */
public class MiscTab extends UserPreferencesTab {

    MiscTab(final UserPreferencesDialog dialog) {
        super(dialog);
    }

    @Override
    public void init() {
        bind(checkGenerateTXTDescription, UserProp.GENERATE_DESCRIPTION_BY_FILENAME, UserProp.GENERATE_DESCRIPTION_BY_FILENAME_DEFAULT);
        bind(checkGenerateDescIon, UserProp.GENERATE_DESCRIPT_ION_FILE, UserProp.GENERATE_DESCRIPT_ION_FILE_DEFAULT);
        bind(checkGenerateHidden, UserProp.GENERATE_DESCRIPTION_FILES_HIDDEN, UserProp.GENERATE_DESCRIPTION_FILES_HIDDEN_DEFAULT);
        bind(checkPrepareFile, UserProp.ANTI_FRAGMENT_FILES, UserProp.ANTI_FRAGMENT_FILES_DEFAULT);
        bind(checkUseRecycleBin, UserProp.USE_RECYCLE_BIN, UserProp.USE_RECYCLE_BIN_DEFAULT);
        bind(checkOpenIncompleteFiles, UserProp.OPEN_INCOMPLETE_FILES, UserProp.OPEN_INCOMPLETE_FILES_DEFAULT);
        bind(checkUseTemporaryFiles, UserProp.USE_TEMPORARY_FILES, UserProp.USE_TEMPORARY_FILES_DEFAULT);
    }

    @Override
    public void build(final CellConstraints cc) {
        JPanel panelDescSettings = new JPanel();
        JPanel panelAdvancedSettings = new JPanel();

        checkGenerateTXTDescription = new JCheckBox();
        checkGenerateTXTDescription.setName("checkGenerateTXTDescription");
        checkGenerateDescIon = new JCheckBox();
        checkGenerateDescIon.setName("checkGenerateDescIon");
        checkGenerateHidden = new JCheckBox();
        checkGenerateHidden.setName("checkGenerateHidden");
        checkPrepareFile = new JCheckBox();
        checkPrepareFile.setName("checkPrepareFile");
        checkUseRecycleBin = new JCheckBox();
        checkUseRecycleBin.setName("checkUseRecycleBin");
        checkUseRecycleBin.setEnabled(FileUtils.supportsRecycleBin());
        checkOpenIncompleteFiles = new JCheckBox();
        checkOpenIncompleteFiles.setName("checkOpenIncompleteFiles");
        checkUseTemporaryFiles = new JCheckBox();
        checkUseTemporaryFiles.setName("checkUseTemporaryFiles");

        this.setBorder(Borders.TABBED_DIALOG);

        //======== panelDesc ========
        {
            panelDescSettings.setBorder(new TitledBorder(null, resourceMap.getString("panelDesc.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelDescBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC
                    },
                    RowSpec.decodeSpecs("default, default, default")), panelDescSettings);

            panelDescBuilder.add(checkGenerateDescIon, cc.xy(3, 1));
            panelDescBuilder.add(checkGenerateTXTDescription, cc.xy(3, 2));
            panelDescBuilder.add(checkGenerateHidden, cc.xy(3, 3));
        }

        //======== panelAdvanced ========
        {
            panelAdvancedSettings.setBorder(new TitledBorder(null, resourceMap.getString("panelAdvanced.border"), TitledBorder.LEADING, TitledBorder.TOP));

            PanelBuilder panelDescBuilder = new PanelBuilder(new FormLayout(
                    new ColumnSpec[]{
                            new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                            FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                            FormSpecs.DEFAULT_COLSPEC
                    },
                    RowSpec.decodeSpecs("default, default, default, default")), panelAdvancedSettings);

            panelDescBuilder.add(checkPrepareFile, cc.xy(3, 1));
            panelDescBuilder.add(checkUseRecycleBin, cc.xy(3, 2));
            panelDescBuilder.add(checkOpenIncompleteFiles, cc.xy(3, 3));
            panelDescBuilder.add(checkUseTemporaryFiles, cc.xy(3, 4));
        }

        PanelBuilder thisBuilder = new PanelBuilder(new FormLayout(
                ColumnSpec.decodeSpecs("default:grow"),
                new RowSpec[]{
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC
                }), this);

        thisBuilder.add(panelDescSettings, cc.xy(1, 1));
        thisBuilder.add(panelAdvancedSettings, cc.xy(1, 3));
    }

    private JCheckBox checkGenerateTXTDescription;
    private JCheckBox checkGenerateDescIon;
    private JCheckBox checkGenerateHidden;
    private JCheckBox checkPrepareFile;
    private JCheckBox checkUseRecycleBin;
    private JCheckBox checkOpenIncompleteFiles;
    private JCheckBox checkUseTemporaryFiles;

}
