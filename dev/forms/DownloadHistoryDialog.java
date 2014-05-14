import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;
import org.jdesktop.swingx.*;


public class DownloadHistoryDialog extends JDialog {
	public DownloadHistoryDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public DownloadHistoryDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("DownloadHistoryDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JPanel panel1 = new JPanel();
		combobox = new JComboBox();
		JLabel labelFilter = new JLabel();
		fieldFilter = new JTextField();
		JScrollPane scrollPane2 = new JScrollPane();
		table = new JXTable();
		JXButtonPanel buttonBar = new JXButtonPanel();
		clearHistoryBtn = new JButton();
		okButton = new JButton();
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

				//======== panel1 ========
				{
					panel1.setBorder(new TitledBorder(bundle.getString("panel1.border")));

					//---- labelFilter ----
					labelFilter.setText(bundle.getString("labelFilter.text"));
					labelFilter.setLabelFor(fieldFilter);

					PanelBuilder panel1Builder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							ColumnSpec.decode("max(pref;80dlu)"),
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							FormSpecs.DEFAULT_COLSPEC,
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(Sizes.dluX(100)),
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
						},
						RowSpec.decodeSpecs("default")), panel1);

					panel1Builder.add(combobox,    cc.xy(1, 1));
					panel1Builder.add(labelFilter, cc.xy(3, 1));
					panel1Builder.add(fieldFilter, cc.xy(5, 1));
				}

				//======== scrollPane2 ========
				{
					scrollPane2.setViewportView(table);
				}

				PanelBuilder contentPanelBuilder = new PanelBuilder(new FormLayout(
					ColumnSpec.decodeSpecs("default:grow"),
					new RowSpec[] {
						FormSpecs.DEFAULT_ROWSPEC,
						FormSpecs.LINE_GAP_ROWSPEC,
						new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
					}), contentPanel);

				contentPanelBuilder.add(panel1,      cc.xy(1, 1));
				contentPanelBuilder.add(scrollPane2, cc.xy(1, 3));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));

				//---- clearHistoryBtn ----
				clearHistoryBtn.setText(bundle.getString("clearHistoryBtn.text"));

				//---- okButton ----
				okButton.setText(bundle.getString("okButton.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
						FormSpecs.UNRELATED_GAP_COLSPEC,
						ColumnSpec.decode("max(pref;55dlu)")
					},
					RowSpec.decodeSpecs("fill:pref")), buttonBar);

				buttonBarBuilder.add(clearHistoryBtn, cc.xy(1, 1));
				buttonBarBuilder.add(okButton,        cc.xy(5, 1));
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
	private JComboBox combobox;
	private JTextField fieldFilter;
	private JXTable table;
	private JButton clearHistoryBtn;
	private JButton okButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
