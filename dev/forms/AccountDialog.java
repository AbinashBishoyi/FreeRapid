import java.awt.*;
import java.util.*;
import javax.swing.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;


public class AccountDialog extends JDialog {
	public AccountDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public AccountDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("AccountDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JLabel labelIcon = new JLabel();
		JLabel labelUserName = new JLabel();
		fieldUserName = ComponentFactory.getTextField()
		JLabel labelPassword = new JLabel();
		fieldPassword = ComponentFactory.getPasswordField()
		JPanel buttonBar = new JPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
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

				//---- labelIcon ----
				labelIcon.setIcon(new ImageIcon("C:\\develope\\freerapid\\src\\cz\\vity\\freerapid\\gui\\dialogs\\resources\\icons\\connection_password.gif"));

				//---- labelUserName ----
				labelUserName.setText(bundle.getString("labelUserName.text"));
				labelUserName.setLabelFor(fieldUserName);

				//---- labelPassword ----
				labelPassword.setText(bundle.getString("labelPassword.text"));
				labelPassword.setLabelFor(fieldPassword);

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec("max(pref;35dlu)"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec("min(pref;40dlu):grow"),
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC
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
						FormFactory.UNRELATED_GAP_ROWSPEC
					}), contentPanel);

				contentPanelBuilder.add(labelIcon,     cc.xywh(1, 1, 2, 7, CellConstraints.FILL, CellConstraints.CENTER));
				contentPanelBuilder.add(labelUserName, cc.xy  (3, 1));
				contentPanelBuilder.add(fieldUserName, cc.xywh(3, 3, 3, 1));
				contentPanelBuilder.add(labelPassword, cc.xy  (3, 5));
				contentPanelBuilder.add(fieldPassword, cc.xywh(3, 7, 3, 1));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						new ColumnSpec("55px:grow"),
						FormFactory.BUTTON_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.BUTTON_COLSPEC,
						FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC
					},
					RowSpec.decodeSpecs("default")), buttonBar);

				buttonBarBuilder.add(btnOK,     cc.xy(2, 1));
				buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
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
	private JTextField fieldUserName;
	private JPasswordField fieldPassword;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
