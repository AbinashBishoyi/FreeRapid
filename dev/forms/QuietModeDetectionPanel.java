public class QuietModeDetectionPanel extends JPanel {
    public QuietModeDetectionPanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        ResourceBundle bundle = ResourceBundle.getBundle("QuietModePanel");
        JPanel panelActivateQM = new JPanel();
        radioButtonActivateQMAlways = new JRadioButton();
        radioButtonActivateQMWhenWindowsFound = new JRadioButton();
        panelSearchForWindows = new JPanel();
        labelSearchForWindows = new JLabel();
        panelQMChoice = new JScrollPane();
        listQuietModeDetectionStrings = new JList();
        btnAddQuietModeDetectionString = new JButton();
        btnRemoveQuietModeDetectionString = new JButton();
        checkCaseSensitiveSeachQM = new JCheckBox();
        JPanel panelQMOptions = new JPanel();
        checkNoSoundsInQM = new JCheckBox();
        checkNoCaptchaInQM = new JCheckBox();
        checkNoConfirmDialogsInQM = new JCheckBox();
        checkPlaySoundForQM = new JCheckBox();
        JLabel labelNoteForQM = new JLabel();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setLayout(new FormLayout(
                ColumnSpec.decodeSpecs("200dlu:grow"),
                new RowSpec[]{
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                }));

        //======== panelActivateQM ========
        {
            panelActivateQM.setBorder(new TitledBorder(bundle.getString("panelActivateQM.border")));
            panelActivateQM.setLayout(new FormLayout(
                    ColumnSpec.decodeSpecs("137dlu:grow"),
                    new RowSpec[]{
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.LINE_GAP_ROWSPEC,
                            FormSpecs.DEFAULT_ROWSPEC,
                            FormSpecs.LINE_GAP_ROWSPEC,
                            FormSpecs.DEFAULT_ROWSPEC
                    }));

            //---- radioButtonActivateQMAlways ----
            radioButtonActivateQMAlways.setText(bundle.getString("radioButtonActivateQMAlways.text"));
            panelActivateQM.add(radioButtonActivateQMAlways, cc.xy(1, 1));

            //---- radioButtonActivateQMWhenWindowsFound ----
            radioButtonActivateQMWhenWindowsFound.setText(bundle.getString("radioButtonActivateQMWhenWindowsFound.text"));
            panelActivateQM.add(radioButtonActivateQMWhenWindowsFound, cc.xy(1, 3));

            //======== panelSearchForWindows ========
            {
                panelSearchForWindows.setLayout(new FormLayout(
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
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC,
                                FormSpecs.LINE_GAP_ROWSPEC,
                                FormSpecs.DEFAULT_ROWSPEC
                        }));

                //---- labelSearchForWindows ----
                labelSearchForWindows.setText(bundle.getString("labelSearchForWindows.text"));
                panelSearchForWindows.add(labelSearchForWindows, cc.xy(3, 1));

                //======== panelQMChoice ========
                {

                    //---- listQuietModeDetectionStrings ----
                    listQuietModeDetectionStrings.setVisibleRowCount(6);
                    panelQMChoice.setViewportView(listQuietModeDetectionStrings);
                }
                panelSearchForWindows.add(panelQMChoice, cc.xywh(3, 3, 3, 5));

                //---- btnAddQuietModeDetectionString ----
                btnAddQuietModeDetectionString.setText(bundle.getString("btnAddQuietModeDetectionString.text"));
                panelSearchForWindows.add(btnAddQuietModeDetectionString, cc.xy(7, 3));

                //---- btnRemoveQuietModeDetectionString ----
                btnRemoveQuietModeDetectionString.setText(bundle.getString("btnRemoveQuietModeDetectionString.text"));
                panelSearchForWindows.add(btnRemoveQuietModeDetectionString, cc.xy(7, 5));

                //---- checkCaseSensitiveSeachQM ----
                checkCaseSensitiveSeachQM.setText(bundle.getString("checkCaseSensitiveSeachQM.text"));
                panelSearchForWindows.add(checkCaseSensitiveSeachQM, cc.xy(3, 9));
            }
            panelActivateQM.add(panelSearchForWindows, cc.xy(1, 5));
        }
        add(panelActivateQM, cc.xy(1, 1));

        //======== panelQMOptions ========
        {
            panelQMOptions.setBorder(new TitledBorder(bundle.getString("panelQMOptions.border")));
            panelQMOptions.setLayout(new FormLayout(
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
                    }));

            //---- checkNoSoundsInQM ----
            checkNoSoundsInQM.setText(bundle.getString("checkNoSoundsInQM.text"));
            panelQMOptions.add(checkNoSoundsInQM, cc.xy(1, 1));

            //---- checkNoCaptchaInQM ----
            checkNoCaptchaInQM.setText(bundle.getString("checkNoCaptchaInQM.text"));
            panelQMOptions.add(checkNoCaptchaInQM, cc.xy(1, 3));

            //---- checkNoConfirmDialogsInQM ----
            checkNoConfirmDialogsInQM.setText(bundle.getString("checkNoConfirmDialogsInQM.text"));
            panelQMOptions.add(checkNoConfirmDialogsInQM, cc.xy(1, 5));

            //---- checkPlaySoundForQM ----
            checkPlaySoundForQM.setText(bundle.getString("checkPlaySoundForQM.text"));
            panelQMOptions.add(checkPlaySoundForQM, cc.xy(1, 7));
        }
        add(panelQMOptions, cc.xy(1, 3));

        //---- labelNoteForQM ----
        labelNoteForQM.setText(bundle.getString("labelNoteForQM.text"));
        add(labelNoteForQM, cc.xy(1, 5));

        //---- buttonGroupActivation ----
        ButtonGroup buttonGroupActivation = new ButtonGroup();
        buttonGroupActivation.add(radioButtonActivateQMAlways);
        buttonGroupActivation.add(radioButtonActivateQMWhenWindowsFound);
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Open Source Project license - unknown
    private JRadioButton radioButtonActivateQMAlways;
    private JRadioButton radioButtonActivateQMWhenWindowsFound;
    private JPanel panelSearchForWindows;
    private JLabel labelSearchForWindows;
    private JScrollPane panelQMChoice;
    private JList listQuietModeDetectionStrings;
    private JButton btnAddQuietModeDetectionString;
    private JButton btnRemoveQuietModeDetectionString;
    private JCheckBox checkCaseSensitiveSeachQM;
    private JCheckBox checkNoSoundsInQM;
    private JCheckBox checkNoCaptchaInQM;
    private JCheckBox checkNoConfirmDialogsInQM;
    private JCheckBox checkPlaySoundForQM;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}
