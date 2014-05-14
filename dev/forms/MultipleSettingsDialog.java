import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;


public class MultipleSettingsDialog extends JDialog {
	public MultipleSettingsDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public MultipleSettingsDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("MultipleSettingsDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JLabel iconLabel = new JLabel();
		titleLabel = new JLabel();
		JLabel labelSize = new JLabel();
		fieldSize = new JTextField();
		JLabel labelDescription = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		descriptionArea = ComponentFactory.getTextArea();
		JPanel optionsPanel = new JPanel();
		JLabel saveToLabel = new JLabel();
		comboPath = new JComboBox();
		btnSelectPath = new JButton();
		JXButtonPanel buttonBar = new JXButtonPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//---- iconLabel ----
				iconLabel.setText(bundle.getString("iconLabel.text"));

				//---- titleLabel ----
				titleLabel.setText(bundle.getString("titleLabel.text"));
				titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

				//---- labelSize ----
				labelSize.setText(bundle.getString("labelSize.text"));

				//---- fieldSize ----
				fieldSize.setBorder(null);
				fieldSize.setOpaque(false);
				fieldSize.setEditable(false);

				//---- labelDescription ----
				labelDescription.setText(bundle.getString("labelDescription.text"));

				//======== scrollPane1 ========
				{
					scrollPane1.setViewportView(descriptionArea);
				}

				//======== optionsPanel ========
				{

					//---- saveToLabel ----
					saveToLabel.setText(bundle.getString("saveToLabel.text"));
					saveToLabel.setLabelFor(comboPath);

					//---- comboPath ----
					comboPath.setEditable(true);

					//---- btnSelectPath ----
					btnSelectPath.setText(bundle.getString("btnSelectPath.text"));

					PanelBuilder optionsPanelBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormSpecs.DEFAULT_COLSPEC,
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							FormSpecs.DEFAULT_COLSPEC
						},
						RowSpec.decodeSpecs("default")), optionsPanel);

					optionsPanelBuilder.add(saveToLabel,   cc.xy(1, 1));
					optionsPanelBuilder.add(comboPath,     cc.xy(3, 1));
					optionsPanelBuilder.add(btnSelectPath, cc.xy(5, 1));
				}

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(Sizes.dluX(49)),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						ColumnSpec.decode("max(min;70dlu)")
					},
					new RowSpec[] {
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.TOP, Sizes.PREFERRED, FormSpec.NO_GROW),
						FormSpecs.RELATED_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(50)), FormSpec.DEFAULT_GROW),
						FormSpecs.LINE_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC
					}), contentPanel);

				contentPanelBuilder.add(iconLabel,        cc.xywh(1,  1, 1, 5, CellConstraints.DEFAULT, CellConstraints.FILL));
				contentPanelBuilder.add(titleLabel,       cc.xywh(3,  1, 5, 1));
				contentPanelBuilder.add(labelSize,        cc.xy  (3,  3));
				contentPanelBuilder.add(fieldSize,        cc.xywh(5,  3, 3, 1));
				contentPanelBuilder.add(labelDescription, cc.xy  (1,  7));
				contentPanelBuilder.add(scrollPane1,      cc.xywh(1,  9, 7, 1));
				contentPanelBuilder.add(optionsPanel,     cc.xywh(1, 11, 7, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

				//---- okButton ----
				okButton.setText(bundle.getString("okButton.text"));

				//---- cancelButton ----
				cancelButton.setText(bundle.getString("cancelButton.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormSpecs.UNRELATED_GAP_COLSPEC,
						ColumnSpec.decode("max(pref;55dlu)"),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC
					},
					RowSpec.decodeSpecs("fill:pref")), buttonBar);
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{3, 5}});

				buttonBarBuilder.add(okButton,     cc.xy(3, 1));
				buttonBarBuilder.add(cancelButton, cc.xy(5, 1));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JLabel titleLabel;
	private JTextField fieldSize;
	private JTextArea descriptionArea;
	private JComboBox comboPath;
	private JButton btnSelectPath;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
