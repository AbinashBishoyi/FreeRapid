import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swingx.*;


public class PluginDetailPanel extends JPanel {
	public PluginDetailPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("PluginDetails");
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
		labelVersion.setText(bundle.getString("labelVersion.text"));

		//---- versionLabel ----
		versionLabel.setText(bundle.getString("versionLabel.text"));

		//---- labelAuthor ----
		labelAuthor.setText(bundle.getString("labelAuthor.text"));

		//---- authorLabel ----
		authorLabel.setText(bundle.getString("authorLabel.text"));

		//---- labelServices ----
		labelServices.setText(bundle.getString("labelServices.text"));

		//---- servicesLabel ----
		servicesLabel.setText(bundle.getString("servicesLabel.text"));

		//---- checkboxPluginIsActive ----
		checkboxPluginIsActive.setText(bundle.getString("checkboxPluginIsActive.text"));

		//---- checkboxUpdatePlugins ----
		checkboxUpdatePlugins.setText(bundle.getString("checkboxUpdatePlugins.text"));

		//---- checkboxClipboardMonitoring ----
		checkboxClipboardMonitoring.setText(bundle.getString("checkboxClipboardMonitoring.text"));

		//---- labelMaxConnections ----
		labelMaxConnections.setText(bundle.getString("labelMaxConnections.text"));
		labelMaxConnections.setPreferredSize(new Dimension(180, 14));
		labelMaxConnections.setLabelFor(spinnerMaxPluginConnections);

		//---- spinnerMaxPluginConnections ----
		spinnerMaxPluginConnections.setModel(new SpinnerNumberModel(1, 1, null, 1));

		//---- labelPriority ----
		labelPriority.setText(bundle.getString("labelPriority.text"));
		labelPriority.setLabelFor(spinnerPluginPriority);

		//---- spinnerPluginPriority ----
		spinnerPluginPriority.setModel(new SpinnerNumberModel(1, 1, 1000, 1));

		//---- btnPriorityUp ----
		btnPriorityUp.setText(bundle.getString("btnPriorityUp.text"));

		//---- btnPriorityDown ----
		btnPriorityDown.setText(bundle.getString("btnPriorityDown.text"));

		//---- pluginHyperlink ----
		pluginHyperlink.setText(bundle.getString("pluginHyperlink.text"));

		PanelBuilder builder = new PanelBuilder(new FormLayout(
			new ColumnSpec[] {
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
			new RowSpec[] {
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
		((FormLayout)getLayout()).setColumnGroups(new int[][] {{7, 9}});

		builder.add(titleSeparator,              cc.xywh(1,  1, 11, 1));
		builder.add(labelVersion,                cc.xy  (1,  3));
		builder.add(versionLabel,                cc.xywh(3,  3,  9, 1));
		builder.add(labelAuthor,                 cc.xy  (1,  5));
		builder.add(authorLabel,                 cc.xywh(3,  5,  9, 1));
		builder.add(labelServices,               cc.xy  (1,  7));
		builder.add(servicesLabel,               cc.xywh(3,  7,  7, 1));
		builder.add(checkboxPluginIsActive,      cc.xywh(1,  9, 11, 1));
		builder.add(checkboxUpdatePlugins,       cc.xywh(1, 11, 11, 1));
		builder.add(checkboxClipboardMonitoring, cc.xywh(1, 13, 11, 1));
		builder.add(labelMaxConnections,         cc.xywh(1, 15,  3, 1));
		builder.add(spinnerMaxPluginConnections, cc.xy  (5, 15));
		builder.add(labelPriority,               cc.xywh(1, 17,  3, 1));
		builder.add(spinnerPluginPriority,       cc.xy  (5, 17));
		builder.add(btnPriorityUp,               cc.xy  (7, 17));
		builder.add(btnPriorityDown,             cc.xy  (9, 17));
		builder.add(pluginHyperlink,             cc.xywh(1, 19,  9, 1));
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


	
}

