import java.awt.*;
import javax.swing.*;
import org.jdesktop.swingx.*;


public class ContentPanel extends JPanel {
	public ContentPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		JScrollPane scrollPane = new JScrollPane();
		table = new JXTable();

		//======== this ========
		setLayout(new BorderLayout());

		//======== scrollPane ========
		{
			scrollPane.setViewportView(table);
		}
		add(scrollPane, BorderLayout.CENTER);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JXTable table;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
