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
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		iconLabel = new JLabel();
		pathLabel = new JLabel();
		JLabel labelFrom = new JLabel();
		fieldFrom = new JTextField();
		JLabel labelSize = new JLabel();
		fieldSize = new JTextField();
		JLabel labelDescription = new JLabel();
		JScrollPane scrollPane1 = new JScrollPane();
		descriptionArea = ComponentFactory.getTextArea();
		JPanel optionsPanel = new JPanel();
		JLabel saveToLabel = new JLabel();
		comboPath = new JComboBox();
		btnSelectPath = new JButton();
		progressBar = new JProgressBar();
		JLabel labelRemaining = new JLabel();
		remainingLabel = new JLabel();
		JLabel labelEstimateTime = new JLabel();
		estTimeLabel = new JLabel();
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
			dialogPane.setBorder(Borders.DIALOG);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{

				//---- iconLabel ----
				iconLabel.setText(bundle.getString("iconLabel.text"));

				//---- pathLabel ----
				pathLabel.setText(bundle.getString("pathLabel.text"));
				pathLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

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

				//---- progressBar ----
				progressBar.setFont(new Font("Tahoma", Font.BOLD, 16));

				//---- labelRemaining ----
				labelRemaining.setText(bundle.getString("labelRemaining.text"));

				//---- remainingLabel ----
				remainingLabel.setText(bundle.getString("remainingLabel.text"));
				remainingLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

				//---- labelEstimateTime ----
				labelEstimateTime.setText(bundle.getString("labelEstimateTime.text"));

				//---- estTimeLabel ----
				estTimeLabel.setText(bundle.getString("estTimeLabel.text"));
				estTimeLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

				//---- labelCurrentSpeed ----
				labelCurrentSpeed.setText(bundle.getString("labelCurrentSpeed.text"));

				//---- currentSpeedLabel ----
				currentSpeedLabel.setText(bundle.getString("currentSpeedLabel.text"));
				currentSpeedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

				//---- labelAverageSpeed ----
				labelAverageSpeed.setText(bundle.getString("labelAverageSpeed.text"));

				//---- avgSpeedLabel ----
				avgSpeedLabel.setText(bundle.getString("avgSpeedLabel.text"));
				avgSpeedLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec(Sizes.dluX(49)),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						ColumnSpec.decode("max(min;70dlu)")
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
						new RowSpec(RowSpec.FILL, Sizes.bounded(Sizes.PREFERRED, Sizes.dluY(40), Sizes.dluY(50)), FormSpec.DEFAULT_GROW),
						FormSpecs.LINE_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC,
						RowSpec.decode("fill:max(pref;20dlu)"),
						FormSpecs.LINE_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC,
						FormSpecs.DEFAULT_ROWSPEC
					}), contentPanel);

				contentPanelBuilder.add(iconLabel,         cc.xywh(1,  1, 1, 5));
				contentPanelBuilder.add(pathLabel,         cc.xywh(3,  1, 7, 1));
				contentPanelBuilder.add(labelFrom,         cc.xy  (3,  3));
				contentPanelBuilder.add(fieldFrom,         cc.xywh(5,  3, 5, 1));
				contentPanelBuilder.add(labelSize,         cc.xy  (3,  5));
				contentPanelBuilder.add(fieldSize,         cc.xywh(5,  5, 3, 1));
				contentPanelBuilder.add(labelDescription,  cc.xy  (1,  7));
				contentPanelBuilder.add(scrollPane1,       cc.xywh(1,  9, 9, 1));
				contentPanelBuilder.add(optionsPanel,      cc.xywh(1, 11, 9, 1));
				contentPanelBuilder.add(progressBar,       cc.xywh(1, 13, 9, 1));
				contentPanelBuilder.add(labelRemaining,    cc.xy  (1, 15));
				contentPanelBuilder.add(remainingLabel,    cc.xywh(3, 15, 3, 1));
				contentPanelBuilder.add(labelEstimateTime, cc.xy  (7, 15));
				contentPanelBuilder.add(estTimeLabel,      cc.xy  (9, 15));
				contentPanelBuilder.add(labelCurrentSpeed, cc.xy  (1, 17));
				contentPanelBuilder.add(currentSpeedLabel, cc.xywh(3, 17, 3, 1));
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
	private JLabel iconLabel;
	private JLabel pathLabel;
	private JTextField fieldFrom;
	private JTextField fieldSize;
	private JTextArea descriptionArea;
	private JComboBox comboPath;
	private JButton btnSelectPath;
	private JProgressBar progressBar;
	private JLabel remainingLabel;
	private JLabel estTimeLabel;
	private JLabel currentSpeedLabel;
	private JLabel avgSpeedLabel;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
