package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.binding.adapter.Bindings;
import com.jgoodies.binding.beans.PropertyConnector;
import com.jgoodies.binding.value.ValueModel;
import com.jgoodies.common.collect.ArrayListModel;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.factories.Borders;
import com.jgoodies.forms.layout.*;
import cz.vity.freerapid.core.QuietMode;
import cz.vity.freerapid.core.UserProp;
import cz.vity.freerapid.swing.Swinger;
import org.jdesktop.application.Action;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * @author ntoskrnl
 */
public class AlertsTab extends UserPreferencesTab {

    AlertsTab(final UserPreferencesDialog dialog) {
        super(dialog);
    }

    @Override
    public void init() {
        bind(checkPlaySoundWhenComplete, UserProp.PLAY_SOUNDS_OK, true);
        bind(checkPlaySoundInCaseOfError, UserProp.PLAY_SOUNDS_FAILED, true);

        bind(checkConfirmExiting, UserProp.CONFIRM_EXITING, UserProp.CONFIRM_EXITING_DEFAULT);
        bind(checkConfirmFileDeletion, UserProp.CONFIRM_FILE_DELETE, UserProp.CONFIRM_FILE_DELETE_DEFAULT);
        final ValueModel confirmRemove = bind(checkConfirmFileRemove, UserProp.CONFIRM_REMOVE, UserProp.CONFIRM_REMOVE_DEFAULT);
        bind(checkConfirmDownloadingRemoveOnly, UserProp.CONFIRM_DOWNLOADING_REMOVE, UserProp.CONFIRM_DOWNLOADING_REMOVE_DEFAULT);
        PropertyConnector.connectAndUpdate(confirmRemove, checkConfirmDownloadingRemoveOnly, "enabled");

        final ValueModel valueModelQMActivation = dialog.getModel().getBufferedPreferences(UserProp.QUIET_MODE_ACTIVATION_MODE, UserProp.QUIET_MODE_ACTIVATION_MODE_DEFAULT);
        Bindings.bind(radioButtonActivateQMAlways, valueModelQMActivation, UserProp.QUIET_MODE_ACTIVATION_ALWAYS);
        Bindings.bind(radioButtonActivateQMWhenWindowsFound, valueModelQMActivation, UserProp.QUIET_MODE_ACTIVATION_WHEN_WINDOWS_FOUND);
        final class ItemListenerQMActivation implements ItemListener {
            @Override
            public void itemStateChanged(final ItemEvent e) {
                refresh();
            }

            public void refresh() {
                final boolean enabled = radioButtonActivateQMWhenWindowsFound.isSelected();
                actionMap.get("btnAddQuietModeDetectionStringAction").setEnabled(enabled);
                actionMap.get("btnRemoveQuietModeDetectionStringAction").setEnabled(enabled);
                refresh(panelSearchForWindows, enabled);
            }

            private void refresh(final Component component, final boolean enabled) {
                component.setEnabled(enabled);
                if (component instanceof Container) {
                    for (final Component c : ((Container) component).getComponents()) {
                        refresh(c, enabled);
                    }
                }
            }
        }
        final ItemListenerQMActivation itemListenerQMActivation = new ItemListenerQMActivation();
        radioButtonActivateQMWhenWindowsFound.addItemListener(itemListenerQMActivation);
        itemListenerQMActivation.refresh();

        final ArrayListModel<String> arrayListModel = new ArrayListModel<String>();
        arrayListModel.addAll(QuietMode.getInstance().getActivationStrings());
        listQuietModeDetectionStrings.setModel(arrayListModel);

        bind(checkCaseSensitiveSearchQM, UserProp.QUIET_MODE_CASE_SENSITIVE_SEARCH, UserProp.QUIET_MODE_CASE_SENSITIVE_SEARCH_DEFAULT);
        bind(checkNoSoundsInQM, UserProp.QUIET_MODE_NO_SOUNDS, UserProp.QUIET_MODE_NO_SOUNDS_DEFAULT);
        bind(checkNoCaptchaInQM, UserProp.QUIET_MODE_NO_CAPTCHA, UserProp.QUIET_MODE_NO_CAPTCHA_DEFAULT);
        bind(checkNoConfirmDialogsInQM, UserProp.QUIET_MODE_NO_CONFIRM_DIALOGS, UserProp.QUIET_MODE_NO_CONFIRM_DIALOGS_DEFAULT);
        bind(checkPlaySoundForQM, UserProp.QUIET_MODE_PLAY_SOUND_ON_ACTIVATE, UserProp.QUIET_MODE_PLAY_SOUND_ON_ACTIVATE_DEFAULT);

        setAction(btnAddQuietModeDetectionString, "btnAddQuietModeDetectionStringAction");
        setAction(btnRemoveQuietModeDetectionString, "btnRemoveQuietModeDetectionStringAction");
    }

    @Action
    public void btnAddQuietModeDetectionStringAction() {
        final JTextField textField = new JTextField();
        final int result = Swinger.showInputDialog(resourceMap.getString("addNewWindowPopupTitle"), textField, true);
        final String text = textField.getText().trim();
        if (result == Swinger.RESULT_OK && !text.isEmpty()) {
            dialog.getModel().setBuffering(true);
            @SuppressWarnings("unchecked")
            final ArrayListModel<String> listModel = (ArrayListModel<String>) listQuietModeDetectionStrings.getModel();
            final int indexOf = listModel.indexOf(text);
            if (indexOf != -1) {
                listQuietModeDetectionStrings.setSelectedIndex(indexOf);
            } else {
                listModel.add(text);
                listQuietModeDetectionStrings.setSelectedIndex(listModel.size() - 1);
            }
        }
    }

    @Action
    public void btnRemoveQuietModeDetectionStringAction() {
        final int[] selected = listQuietModeDetectionStrings.getSelectedIndices();
        if (selected.length > 0) {
            dialog.getModel().setBuffering(true);
            final ArrayListModel<?> listModel = (ArrayListModel<?>) listQuietModeDetectionStrings.getModel();
            for (int i = selected.length - 1; i >= 0; i--) {
                listModel.remove(selected[i]);
            }
            if (selected.length == 1) {
                listQuietModeDetectionStrings.setSelectedIndex(selected[0]);
            }
        }
    }

    @Override
    public void apply() {
        @SuppressWarnings("unchecked")
        final ArrayListModel<String> arrayListModel = (ArrayListModel<String>) listQuietModeDetectionStrings.getModel();
        QuietMode.getInstance().setActivationStrings(arrayListModel);
    }

    @Override
    public void build(final CellConstraints cc) {
        JPanel alertsPanelGeneral = new JPanel();
        JPanel panelSound = new JPanel();
        JPanel panelConfirmation = new JPanel();
        JPanel alertsPanelQM = new JPanel();
        JPanel panelActivateQM = new JPanel();
        JPanel panelQMOptions = new JPanel();
        JTabbedPane alertsTabbedPane = new JTabbedPane();

        checkPlaySoundInCaseOfError = new JCheckBox();
        checkPlaySoundInCaseOfError.setName("checkPlaySoundInCaseOfError");
        checkPlaySoundWhenComplete = new JCheckBox();
        checkPlaySoundWhenComplete.setName("checkPlaySoundWhenComplete");

        checkConfirmExiting = new JCheckBox();
        checkConfirmExiting.setName("checkConfirmExiting");
        checkConfirmFileDeletion = new JCheckBox();
        checkConfirmFileDeletion.setName("checkConfirmFileDeletion");
        checkConfirmFileRemove = new JCheckBox();
        checkConfirmFileRemove.setName("checkConfirmFileRemove");
        checkConfirmDownloadingRemoveOnly = new JCheckBox();
        checkConfirmDownloadingRemoveOnly.setName("checkConfirmDownloadingRemoveOnly");

        radioButtonActivateQMAlways = new JRadioButton();
        radioButtonActivateQMAlways.setName("radioButtonActivateQMAlways");
        radioButtonActivateQMWhenWindowsFound = new JRadioButton();
        radioButtonActivateQMWhenWindowsFound.setName("radioButtonActivateQMWhenWindowsFound");
        panelSearchForWindows = new JPanel();
        JLabel labelSearchForWindows = new JLabel();
        labelSearchForWindows.setName("labelSearchForWindows");
        listQuietModeDetectionStrings = new JList();
        listQuietModeDetectionStrings.setVisibleRowCount(6);
        JScrollPane panelQMChoice = new JScrollPane();
        panelQMChoice.setViewportView(listQuietModeDetectionStrings);
        btnAddQuietModeDetectionString = new JButton();
        btnAddQuietModeDetectionString.setName("btnAddQuietModeDetectionString");
        btnRemoveQuietModeDetectionString = new JButton();
        btnRemoveQuietModeDetectionString.setName("btnRemoveQuietModeDetectionString");
        checkCaseSensitiveSearchQM = new JCheckBox();
        checkCaseSensitiveSearchQM.setName("checkCaseSensitiveSearchQM");

        checkNoSoundsInQM = new JCheckBox();
        checkNoSoundsInQM.setName("checkNoSoundsInQM");
        checkNoCaptchaInQM = new JCheckBox();
        checkNoCaptchaInQM.setName("checkNoCaptchaInQM");
        checkNoConfirmDialogsInQM = new JCheckBox();
        checkNoConfirmDialogsInQM.setName("checkNoConfirmDialogsInQM");
        checkPlaySoundForQM = new JCheckBox();
        checkPlaySoundForQM.setName("checkPlaySoundForQM");

        JLabel labelNoteForQM = new JLabel();
        labelNoteForQM.setName("labelNoteForQM");

        this.setBorder(Borders.TABBED_DIALOG);

        //======== alertsTabbedPane ========
        {

            //======== alertsPanelGeneral ========
            {
                alertsPanelGeneral.setBorder(new EmptyBorder(4, 4, 4, 4));

                //======== panelSound ========
                {
                    panelSound.setBorder(new CompoundBorder(
                            new TitledBorder(null, resourceMap.getString("panelSound.border"), TitledBorder.LEADING, TitledBorder.TOP),
                            Borders.DLU2));

                    PanelBuilder panelSoundBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC
                            },
                            RowSpec.decodeSpecs("default, default")), panelSound);

                    panelSoundBuilder.add(checkPlaySoundInCaseOfError, cc.xy(3, 1));
                    panelSoundBuilder.add(checkPlaySoundWhenComplete, cc.xy(3, 2));
                }

                //======== panelConfirmation ========
                {
                    panelConfirmation.setBorder(new CompoundBorder(
                            new TitledBorder(null, resourceMap.getString("panelConfirmation.border"), TitledBorder.LEADING, TitledBorder.TOP),
                            Borders.DLU2));

                    PanelBuilder panelConfirmationBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    new ColumnSpec(Sizes.dluX(9)),
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                            },
                            new RowSpec[]{
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,

                            }), panelConfirmation);

                    panelConfirmationBuilder.add(checkConfirmExiting, cc.xyw(3, 1, 3));
                    panelConfirmationBuilder.add(checkConfirmFileDeletion, cc.xy(7, 1));
                    panelConfirmationBuilder.add(checkConfirmFileRemove, cc.xyw(3, 2, 3));
                    panelConfirmationBuilder.add(checkConfirmDownloadingRemoveOnly, cc.xy(5, 3));
                }

                PanelBuilder alertsPanelGeneralBuilder = new PanelBuilder(new FormLayout(
                        ColumnSpec.decodeSpecs("default:grow"),
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.RELATED_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.RELATED_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }), alertsPanelGeneral);

                alertsPanelGeneralBuilder.add(panelSound, cc.xy(1, 1));
                alertsPanelGeneralBuilder.add(panelConfirmation, cc.xy(1, 3));
            }
            alertsTabbedPane.addTab(resourceMap.getString("alertsPanelGeneral.tab.title"), alertsPanelGeneral);

            //======== alertsPanelQM ========
            {
                alertsPanelQM.setBorder(new EmptyBorder(4, 4, 4, 4));

                //======== panelActivateQM ========
                {
                    panelActivateQM.setBorder(new TitledBorder(resourceMap.getString("panelActivateQM.border")));
                    final PanelBuilder panelActivateQMBuilder = new PanelBuilder(new FormLayout(
                            ColumnSpec.decodeSpecs("default:grow"),
                            new RowSpec[]{
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC
                            }), panelActivateQM);

                    panelActivateQMBuilder.add(radioButtonActivateQMAlways, cc.xy(1, 1));
                    panelActivateQMBuilder.add(radioButtonActivateQMWhenWindowsFound, cc.xy(1, 3));

                    //======== panelSearchForWindows ========
                    {
                        final PanelBuilder panelSearchForWindowsBuilder = new PanelBuilder(new FormLayout(
                                new ColumnSpec[]{
                                        new ColumnSpec(Sizes.dluX(20)),
                                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                        new ColumnSpec(Sizes.dluX(140)),
                                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormSpecs.DEFAULT_COLSPEC,
                                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                        FormSpecs.MIN_COLSPEC
                                },
                                new RowSpec[]{
                                        FormSpecs.DEFAULT_ROWSPEC,
                                        FormSpecs.LINE_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC,
                                        FormSpecs.LINE_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC,
                                        new RowSpec(RowSpec.FILL, Sizes.ZERO, FormSpec.DEFAULT_GROW),
                                        FormSpecs.LINE_GAP_ROWSPEC,
                                        FormSpecs.DEFAULT_ROWSPEC
                                }), panelSearchForWindows);

                        panelSearchForWindowsBuilder.add(labelSearchForWindows, cc.xy(3, 1));
                        panelSearchForWindowsBuilder.add(panelQMChoice, cc.xywh(3, 3, 3, 4));
                        panelSearchForWindowsBuilder.add(btnAddQuietModeDetectionString, cc.xy(7, 3));
                        panelSearchForWindowsBuilder.add(btnRemoveQuietModeDetectionString, cc.xy(7, 5));
                        panelSearchForWindowsBuilder.add(checkCaseSensitiveSearchQM, cc.xy(3, 8));

                    }
                    panelActivateQMBuilder.add(panelSearchForWindows, cc.xy(1, 5));
                }

                //======== panelQMOptions ========
                {
                    panelQMOptions.setBorder(new TitledBorder(resourceMap.getString("panelQMOptions.border")));
                    final PanelBuilder panelQMOptionsBuilder = new PanelBuilder(new FormLayout(
                            new ColumnSpec[]{
                                    FormSpecs.DEFAULT_COLSPEC,
                                    FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                                    new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(105), FormSpec.DEFAULT_GROW)
                            },
                            new RowSpec[]{
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC,
                                    FormSpecs.LINE_GAP_ROWSPEC,
                                    FormSpecs.DEFAULT_ROWSPEC
                            }), panelQMOptions);

                    panelQMOptionsBuilder.add(checkNoSoundsInQM, cc.xy(1, 1));
                    panelQMOptionsBuilder.add(checkNoCaptchaInQM, cc.xy(1, 3));
                    panelQMOptionsBuilder.add(checkNoConfirmDialogsInQM, cc.xy(1, 5));
                    panelQMOptionsBuilder.add(checkPlaySoundForQM, cc.xy(1, 7));
                }

                final PanelBuilder alertsPanelQMBuilder = new PanelBuilder(new FormLayout(
                        ColumnSpec.decodeSpecs("default:grow"),
                        new RowSpec[]{
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                        }), alertsPanelQM);

                alertsPanelQMBuilder.add(panelActivateQM, cc.xy(1, 1));
                alertsPanelQMBuilder.add(panelQMOptions, cc.xy(1, 3));
                alertsPanelQMBuilder.add(labelNoteForQM, cc.xy(1, 5));
            }
            alertsTabbedPane.addTab(resourceMap.getString("alertsPanelQM.tab.title"), alertsPanelQM);

        }

        PanelBuilder thisBuilder = new PanelBuilder(new FormLayout(
                ColumnSpec.decodeSpecs("default:grow"),
                new RowSpec[]{
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
                        FormSpecs.RELATED_GAP_ROWSPEC,
                        RowSpec.decode("5px")
                }), this);

        thisBuilder.add(alertsTabbedPane, cc.xy(1, 1));
    }

    private JRadioButton radioButtonActivateQMAlways;
    private JRadioButton radioButtonActivateQMWhenWindowsFound;
    private JPanel panelSearchForWindows;
    private JList listQuietModeDetectionStrings;
    private JButton btnAddQuietModeDetectionString;
    private JButton btnRemoveQuietModeDetectionString;
    private JCheckBox checkCaseSensitiveSearchQM;
    private JCheckBox checkNoSoundsInQM;
    private JCheckBox checkNoCaptchaInQM;
    private JCheckBox checkNoConfirmDialogsInQM;
    private JCheckBox checkPlaySoundForQM;
    private JCheckBox checkPlaySoundInCaseOfError;
    private JCheckBox checkPlaySoundWhenComplete;
    private JCheckBox checkConfirmExiting;
    private JCheckBox checkConfirmFileDeletion;
    private JCheckBox checkConfirmFileRemove;
    private JCheckBox checkConfirmDownloadingRemoveOnly;

}
