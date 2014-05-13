import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;


public class InformationDialog extends JFrame {
	public InformationDialog() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("InformationDialog");
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		iconLabel = new JLabel();
		pathLabel = new JLabel();
		JLabel labelFrom = new JLabel();
		fieldFrom = new JTextField();
		JLabel labelSize = new JLabel();
		fieldSize = new JTextField();
		JLabel labelDescription = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		descriptionArea = new JTextArea();
		JPanel optionsPanel = new JPanel();
		JLabel saveToLabel = new JLabel();
		comboBox1 = new JComboBox();
		btnSelectPath = new JButton();
		progressBar = new JProgressBar();
		JLabel labelRemaining = new JLabel();
		remainingLabel = new JLabel();
		JLabel labelCurrentSpeed = new JLabel();
		currentSpeedLabel = new JLabel();
		JLabel labelAverageSpeed = new JLabel();
		avgSpeedLabel = new JLabel();
		JXButtonPanel buttonBar = new JXButtonPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG_BORDER);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//---- iconLabel ----
				iconLabel.setText(bundle.getString("iconLabel.text"));

				//---- pathLabel ----
				pathLabel.setText(bundle.getString("pathLabel.text"));

				//---- labelFrom ----
				labelFrom.setText(bundle.getString("labelFrom.text"));

				//---- fieldFrom ----
				fieldFrom.setBorder(null);
				fieldFrom.setOpaque(false);
				fieldFrom.setText(bundle.getString("fieldFrom.text"));

				//---- labelSize ----
				labelSize.setText(bundle.getString("labelSize.text"));

				//---- fieldSize ----
				fieldSize.setBorder(null);
				fieldSize.setOpaque(false);

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
					saveToLabel.setLabelFor(comboBox1);

					//---- btnSelectPath ----
					btnSelectPath.setText(bundle.getString("btnSelectPath.text"));

					PanelBuilder optionsPanelBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormFactory.DEFAULT_COLSPEC,
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
							FormFactory.DEFAULT_COLSPEC
						},
						RowSpec.decodeSpecs("default")), optionsPanel);

					optionsPanelBuilder.add(saveToLabel,   cc.xy(1, 1));
					optionsPanelBuilder.add(comboBox1,     cc.xy(3, 1));
					optionsPanelBuilder.add(btnSelectPath, cc.xy(5, 1));
				}

				//---- labelRemaining ----
				labelRemaining.setText(bundle.getString("labelRemaining.text"));

				//---- remainingLabel ----
				remainingLabel.setText(bundle.getString("remainingLabel.text"));

				//---- labelCurrentSpeed ----
				labelCurrentSpeed.setText(bundle.getString("labelCurrentSpeed.text"));

				//---- currentSpeedLabel ----
				currentSpeedLabel.setText(bundle.getString("currentSpeedLabel.text"));

				//---- labelAverageSpeed ----
				labelAverageSpeed.setText(bundle.getString("labelAverageSpeed.text"));

				//---- avgSpeedLabel ----
				avgSpeedLabel.setText(bundle.getString("avgSpeedLabel.text"));

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(Sizes.dluX(54)),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("max(min;50dlu)")
					},
					new RowSpec[] {
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(50)), FormSpec.NO_GROW),
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						new RowSpec("fill:max(pref;20dlu)"),
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC,
						FormFactory.LINE_GAP_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC
					}), contentPanel);

				contentPanelBuilder.add(iconLabel,         cc.xywh(1,  1, 1, 5));
				contentPanelBuilder.add(pathLabel,         cc.xywh(3,  1, 7, 1));
				contentPanelBuilder.add(labelFrom,         cc.xy  (3,  3));
				contentPanelBuilder.add(fieldFrom,         cc.xywh(5,  3, 5, 1));
				contentPanelBuilder.add(labelSize,         cc.xy  (3,  5));
				contentPanelBuilder.add(fieldSize,         cc.xy  (5,  5));
				contentPanelBuilder.add(labelDescription,  cc.xy  (1,  7));
				contentPanelBuilder.add(scrollPane1,       cc.xywh(1,  9, 9, 1));
				contentPanelBuilder.add(optionsPanel,      cc.xywh(1, 11, 9, 1));
				contentPanelBuilder.add(progressBar,       cc.xywh(1, 13, 9, 1));
				contentPanelBuilder.add(labelRemaining,    cc.xy  (1, 15));
				contentPanelBuilder.add(remainingLabel,    cc.xy  (3, 15));
				contentPanelBuilder.add(labelCurrentSpeed, cc.xy  (1, 17));
				contentPanelBuilder.add(currentSpeedLabel, cc.xy  (3, 17));
				contentPanelBuilder.add(labelAverageSpeed, cc.xy  (7, 17));
				contentPanelBuilder.add(avgSpeedLabel,     cc.xy  (9, 17));
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
						FormFactory.UNRELATED_GAP_COLSPEC,
						FormFactory.PREF_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC
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
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel iconLabel;
	private JLabel pathLabel;
	private JTextField fieldFrom;
	private JTextField fieldSize;
	private JTextArea descriptionArea;
	private JComboBox comboBox1;
	private JButton btnSelectPath;
	private JProgressBar progressBar;
	private JLabel remainingLabel;
	private JLabel currentSpeedLabel;
	private JLabel avgSpeedLabel;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
