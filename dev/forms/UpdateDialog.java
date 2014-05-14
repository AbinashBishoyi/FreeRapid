import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import org.jdesktop.swinghelper.buttonpanel.*;
import org.jdesktop.swingx.*;


public class UpdateDialog extends JFrame {
	public UpdateDialog() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("UpdateDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JScrollPane scrollPane1 = new JScrollPane();
		table = new JXTable();
		JPanel topPanel = new JPanel();
		JLabel labelUpdateServer = new JLabel();
		server = new JLabel();
		labelUpdatesCount = new JLabel();
		JXButtonPanel buttonBar = new JXButtonPanel();
		progressBar = new JProgressBar();
		btnOK = new JButton();
		btnCancel = new JButton();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout(4, 4));

				//======== scrollPane1 ========
				{
					scrollPane1.setViewportView(table);
				}
				contentPanel.add(scrollPane1, BorderLayout.CENTER);

				//======== topPanel ========
				{

					//---- labelUpdateServer ----
					labelUpdateServer.setText(bundle.getString("labelUpdateServer.text"));

					//---- server ----
					server.setText(bundle.getString("server.text"));

					//---- labelUpdatesCount ----
					labelUpdatesCount.setText(bundle.getString("labelUpdatesCount.text"));

					PanelBuilder topPanelBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormSpecs.DEFAULT_COLSPEC,
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							FormSpecs.DEFAULT_COLSPEC,
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
							FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
							FormSpecs.DEFAULT_COLSPEC
						},
						RowSpec.decodeSpecs("default")), topPanel);

					topPanelBuilder.add(labelUpdateServer, cc.xy(1, 1));
					topPanelBuilder.add(server,            cc.xy(3, 1));
					topPanelBuilder.add(labelUpdatesCount, cc.xy(7, 1));
				}
				contentPanel.add(topPanel, BorderLayout.NORTH);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_PAD);
				buttonBar.setCyclic(true);

				//---- btnOK ----
				btnOK.setText(bundle.getString("btnOK.text"));

				//---- btnCancel ----
				btnCancel.setText(bundle.getString("btnCancel.text"));

				PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
					new ColumnSpec[] {
						FormSpecs.LABEL_COMPONENT_GAP_COLSPEC,
						FormSpecs.DEFAULT_COLSPEC,
						FormSpecs.GLUE_COLSPEC,
						ColumnSpec.decode("max(pref;42dlu)"),
						FormSpecs.RELATED_GAP_COLSPEC,
						FormSpecs.PREF_COLSPEC
					},
					RowSpec.decodeSpecs("pref")), buttonBar);
				((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{4, 6}});

				buttonBarBuilder.add(progressBar, cc.xy(2, 1));
				buttonBarBuilder.add(btnOK,       cc.xy(4, 1));
				buttonBarBuilder.add(btnCancel,   cc.xy(6, 1));
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
	private JXTable table;
	private JLabel server;
	private JLabel labelUpdatesCount;
	private JProgressBar progressBar;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
