import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;
/*
 * Created by JFormDesigner on Mon Sep 24 21:04:26 CEST 2007
 */



/**
 * @author Ladislav Vitasek
 */
public class ConnectDialog extends JDialog {
	public ConnectDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public ConnectDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("ConnectDialog");
		dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JLabel labelHostName = new JLabel();
		fieldHostName = new JTextField();
		JLabel labelPortNumber = new JLabel();
		fieldPort = new JTextField();
		checkAuthentification = new JCheckBox();
		labelLoginName = new JLabel();
		fieldUserName = new JTextField();
		labelPassword = new JLabel();
		fieldPassword = new JPasswordField();
		checkStorePassword = new JCheckBox();
		labelWarning = new JLabel();
		JXButtonPanel buttonBar = new JXButtonPanel();
		btnOk = new JButton();
		btnCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle(bundle.getString("this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(Borders.DIALOG);
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormSpecs.PREF_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.PREFERRED, FormSpec.DEFAULT_GROW),
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC
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
						FormSpecs.DEFAULT_ROWSPEC
					}));

				//---- labelHostName ----
				labelHostName.setText(bundle.getString("labelHostName.text"));
				labelHostName.setLabelFor(fieldHostName);
				contentPanel.add(labelHostName, cc.xy(1, 1));

				//---- fieldHostName ----
				fieldHostName.setColumns(8);
				contentPanel.add(fieldHostName, cc.xy(3, 1));

				//---- labelPortNumber ----
				labelPortNumber.setText(bundle.getString("labelPortNumber.text"));
				labelPortNumber.setLabelFor(fieldPort);
				contentPanel.add(labelPortNumber, cc.xy(5, 1));

				//---- fieldPort ----
				fieldPort.setColumns(6);
				contentPanel.add(fieldPort, cc.xy(7, 1));

				//---- checkAuthentification ----
				checkAuthentification.setText(bundle.getString("checkAuthentification.text"));
				contentPanel.add(checkAuthentification, new CellConstraints(1, 3, 3, 1, CellConstraints.DEFAULT, CellConstraints.DEFAULT, new Insets( 0, 7, 0, 0)));

				//---- labelLoginName ----
				labelLoginName.setText(bundle.getString("labelLoginName.text"));
				labelLoginName.setLabelFor(fieldUserName);
				contentPanel.add(labelLoginName, cc.xy(1, 5));
				contentPanel.add(fieldUserName, cc.xy(3, 5));

				//---- labelPassword ----
				labelPassword.setText(bundle.getString("labelPassword.text"));
				labelPassword.setLabelFor(fieldPassword);
				contentPanel.add(labelPassword, cc.xy(1, 7));
				contentPanel.add(fieldPassword, cc.xy(3, 7));

				//---- checkStorePassword ----
				checkStorePassword.setText(bundle.getString("checkStorePassword.text"));
				contentPanel.add(checkStorePassword, cc.xywh(5, 7, 3, 1));

				//---- labelWarning ----
				labelWarning.setText(bundle.getString("labelWarning.text"));
				labelWarning.setForeground(Color.red);
				contentPanel.add(labelWarning, cc.xywh(1, 9, 7, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_PAD);
				buttonBar.setLayout(new FormLayout(
					new ColumnSpec[] {
						FormSpecs.GLUE_COLSPEC,
						FormSpecs.BUTTON_COLSPEC,
						FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.BUTTON_COLSPEC
					},
					RowSpec.decodeSpecs("pref")));
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}});

				//---- btnOk ----
				btnOk.setText(bundle.getString("btnOk.text"));
				buttonBar.add(btnOk, cc.xy(2, 1));

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));
				buttonBar.add(btnCancel, cc.xy(4, 1));
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
	private JTextField fieldHostName;
	private JTextField fieldPort;
	private JCheckBox checkAuthentification;
	private JLabel labelLoginName;
	private JTextField fieldUserName;
	private JLabel labelPassword;
	private JPasswordField fieldPassword;
	private JCheckBox checkStorePassword;
	private JLabel labelWarning;
	private JButton btnOk;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
