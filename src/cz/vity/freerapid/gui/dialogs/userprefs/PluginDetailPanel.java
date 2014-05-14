package cz.vity.freerapid.gui.dialogs.userprefs;

import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTitledSeparator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * @author Vity
 */
class PluginDetailPanel extends JPanel {

    public PluginDetailPanel() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Open Source Project license - unknown
        //ResourceBundle bundle = ResourceBundle.getBundle("PluginDetails");
        titleSeparator = new JXTitledSeparator();
        JLabel labelVersion = new JLabel();
        versionLabel = new JLabel();
        JLabel labelAuthor = new JLabel();
        authorLabel = new JLabel();
        JLabel labelServices = new JLabel();
        servicesLabel = new JLabel();
        checkboxPluginIsActive = new JCheckBox();
        checkboxUpdatePlugins = new JCheckBox();
        checkboxClipboardMonitoring = new JCheckBox();
        JLabel labelMaxConnections = new JLabel();
        labelMaxConnections.setPreferredSize(new Dimension(80, 20));
        labelMaxConnections.setMinimumSize(new Dimension(80, 20));
        spinnerMaxPluginConnections = new JSpinner();
        JLabel labelPriority = new JLabel();
        spinnerPluginPriority = new JSpinner();
        btnPriorityUp = new JButton();
        btnPriorityDown = new JButton();
        pluginHyperlink = new JXHyperlink();
        CellConstraints cc = new CellConstraints();

        //======== this ========
        setBorder(new EmptyBorder(3, 3, 0, 2));

        //---- titleSeparator ----
        titleSeparator.setTitle(" ");

        //---- labelVersion ----
        labelVersion.setName("labelVersion");

        //---- versionLabel ----
        versionLabel.setName("versionLabel");

        //---- labelAuthor ----
        labelAuthor.setName("labelAuthor");

        //---- authorLabel ----
        authorLabel.setName("authorLabel");

        //---- labelServices ----
        labelServices.setName("labelServices");

        //---- servicesLabel ----
        servicesLabel.setName("servicesLabel");

        //---- checkboxPluginIsActive ----
        checkboxPluginIsActive.setName("checkboxPluginIsActive");

        //---- checkboxUpdatePlugins ----
        checkboxUpdatePlugins.setName("checkboxUpdatePlugins");

        //---- checkboxClipboardMonitoring ----
        checkboxClipboardMonitoring.setName("checkboxClipboardMonitoring");

        //---- labelMaxConnections ----
        labelMaxConnections.setName("labelMaxConnections");
        labelMaxConnections.setPreferredSize(new Dimension(180, 14));

        //---- spinnerMaxPluginConnections ----
        spinnerMaxPluginConnections.setModel(new SpinnerNumberModel(1, 1, null, 1));

        //---- labelPriority ----
        labelPriority.setName("labelPriority");

        //---- spinnerPluginPriority ----
        spinnerPluginPriority.setModel(new SpinnerNumberModel(1, 1, 1000, 1));

        //---- btnPriorityUp ----
        btnPriorityUp.setName("btnPriorityUp");

        //---- btnPriorityDown ----
        btnPriorityDown.setName("btnPriorityDown");

        //---- pluginHyperlink ----
        pluginHyperlink.setName("pluginHyperlink");

        PanelBuilder builder = new PanelBuilder(new FormLayout(
                new ColumnSpec[]{
                        FormSpecs.PREF_COLSPEC,
                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                        ColumnSpec.decode("max(pref;30dlu)"),
                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                        FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                        FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                        FormSpecs.DEFAULT_COLSPEC,
                        FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
                        new ColumnSpec(ColumnSpec.FILL, Sizes.MINIMUM, FormSpec.DEFAULT_GROW)
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
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        FormSpecs.DEFAULT_ROWSPEC,
                        FormSpecs.LINE_GAP_ROWSPEC,
                        new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
                }), this);
        ((FormLayout) getLayout()).setColumnGroups(new int[][]{{7, 9}});

        builder.add(titleSeparator, cc.xywh(1, 1, 11, 1));
        builder.add(labelVersion, cc.xy(1, 3));
        builder.add(versionLabel, cc.xywh(3, 3, 9, 1));
        builder.add(labelAuthor, cc.xy(1, 5));
        builder.add(authorLabel, cc.xywh(3, 5, 9, 1));
        builder.add(labelServices, cc.xy(1, 7));
        builder.add(servicesLabel, cc.xywh(3, 7, 7, 1));
        builder.add(checkboxPluginIsActive, cc.xywh(1, 9, 11, 1));
        builder.add(checkboxUpdatePlugins, cc.xywh(1, 11, 11, 1));
        builder.add(checkboxClipboardMonitoring, cc.xywh(1, 13, 11, 1));
        builder.add(labelMaxConnections, cc.xywh(1, 15, 3, 1));
        builder.add(spinnerMaxPluginConnections, cc.xy(5, 15));
        builder.add(labelPriority, cc.xywh(1, 17, 3, 1));
        builder.add(spinnerPluginPriority, cc.xy(5, 17));
        builder.add(btnPriorityUp, cc.xy(7, 17));
        builder.add(btnPriorityDown, cc.xy(9, 17));
        builder.add(pluginHyperlink, cc.xywh(1, 19, 9, 1));
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Open Source Project license - unknown
    private JXTitledSeparator titleSeparator;
    private JLabel versionLabel;
    private JLabel authorLabel;
    private JLabel servicesLabel;
    private JCheckBox checkboxPluginIsActive;
    private JCheckBox checkboxUpdatePlugins;
    private JCheckBox checkboxClipboardMonitoring;
    private JSpinner spinnerMaxPluginConnections;
    private JSpinner spinnerPluginPriority;
    private JButton btnPriorityUp;
    private JButton btnPriorityDown;
    private JXHyperlink pluginHyperlink;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    public JLabel getAuthorLabel() {
        return authorLabel;
    }

    public JButton getBtnPriorityDown() {
        return btnPriorityDown;
    }

    public JButton getBtnPriorityUp() {
        return btnPriorityUp;
    }

    public JCheckBox getCheckboxClipboardMonitoring() {
        return checkboxClipboardMonitoring;
    }

    public JCheckBox getCheckboxPluginIsActive() {
        return checkboxPluginIsActive;
    }

    public JCheckBox getCheckboxUpdatePlugins() {
        return checkboxUpdatePlugins;
    }

    public JXHyperlink getPluginHyperlink() {
        return pluginHyperlink;
    }

    public JLabel getServicesLabel() {
        return servicesLabel;
    }

    public JSpinner getSpinnerMaxPluginConnections() {
        return spinnerMaxPluginConnections;
    }

    public JSpinner getSpinnerPluginPriority() {
        return spinnerPluginPriority;
    }

    public JXTitledSeparator getTitleSeparator() {
        return titleSeparator;
    }

    public JLabel getVersionLabel() {
        return versionLabel;
    }


}