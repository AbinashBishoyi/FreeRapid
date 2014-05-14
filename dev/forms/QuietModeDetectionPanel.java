import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;


public class QuietModeDetectionPanel extends JPanel {
	public QuietModeDetectionPanel() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("QuietModePanel");
		JPanel panelQM1 = new JPanel();
		label1 = new JLabel();
		scrollPane1 = new JScrollPane();
		listQuietModeDetectionStrings = new JList();
		btnAddQuietModeDetectionString = new JButton();
		btnRemoveQuietModeDetectionString = new JButton();
		checkCaseInsensitiveSeachQM = new JCheckBox();
		JPanel panelQM2 = new JPanel();
		checkDontPlaySoundQM = new JCheckBox();
		checkNoCaptchaInQM = new JCheckBox();
		checkConfirmDialogsQM = new JCheckBox();
		checkPlaySoundForQM = new JCheckBox();
		JLabel labelNoteForQM = new JLabel();
		CellConstraints cc = new CellConstraints();

		//======== this ========

		//======== panelQM1 ========
		{
			panelQM1.setBorder(new TitledBorder(bundle.getString("panelQM1.border")));

			//---- label1 ----
			label1.setText(bundle.getString("label1.text"));

			//======== scrollPane1 ========
			{

				//---- listQuietModeDetectionStrings ----
				listQuietModeDetectionStrings.setVisibleRowCount(6);
				scrollPane1.setViewportView(listQuietModeDetectionStrings);
			}

			//---- btnAddQuietModeDetectionString ----
			btnAddQuietModeDetectionString.setText(bundle.getString("btnAddQuietModeDetectionString.text"));

			//---- btnRemoveQuietModeDetectionString ----
			btnRemoveQuietModeDetectionString.setText(bundle.getString("btnRemoveQuietModeDetectionString.text"));

			//---- checkCaseInsensitiveSeachQM ----
			checkCaseInsensitiveSeachQM.setText(bundle.getString("checkCaseInsensitiveSeachQM.text"));

			PanelBuilder panelQM1Builder = new PanelBuilder(new FormLayout(
				new ColumnSpec[] {
					new ColumnSpec(Sizes.dluX(137)),
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC
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
					FormFactory.DEFAULT_ROWSPEC
				}), panelQM1);

			panelQM1Builder.add(label1,                            cc.xy  (1, 1));
			panelQM1Builder.add(scrollPane1,                       cc.xywh(1, 3, 3, 5));
			panelQM1Builder.add(btnAddQuietModeDetectionString,    cc.xy  (5, 3));
			panelQM1Builder.add(btnRemoveQuietModeDetectionString, cc.xy  (5, 5));
			panelQM1Builder.add(checkCaseInsensitiveSeachQM,       cc.xy  (1, 9));
		}

		//======== panelQM2 ========
		{
			panelQM2.setBorder(new TitledBorder(bundle.getString("panelQM2.border")));

			//---- checkDontPlaySoundQM ----
			checkDontPlaySoundQM.setText(bundle.getString("checkDontPlaySoundQM.text"));

			//---- checkNoCaptchaInQM ----
			checkNoCaptchaInQM.setText(bundle.getString("checkNoCaptchaInQM.text"));

			//---- checkConfirmDialogsQM ----
			checkConfirmDialogsQM.setText(bundle.getString("checkConfirmDialogsQM.text"));

			//---- checkPlaySoundForQM ----
			checkPlaySoundForQM.setText(bundle.getString("checkPlaySoundForQM.text"));

			PanelBuilder panelQM2Builder = new PanelBuilder(new FormLayout(
				new ColumnSpec[] {
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
					new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(105), FormSpec.DEFAULT_GROW)
				},
				new RowSpec[] {
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.LINE_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC
				}), panelQM2);

			panelQM2Builder.add(checkDontPlaySoundQM,  cc.xy(1, 1));
			panelQM2Builder.add(checkNoCaptchaInQM,    cc.xy(1, 3));
			panelQM2Builder.add(checkConfirmDialogsQM, cc.xy(1, 5));
			panelQM2Builder.add(checkPlaySoundForQM,   cc.xy(1, 7));
		}

		//---- labelNoteForQM ----
		labelNoteForQM.setText(bundle.getString("labelNoteForQM.text"));

		PanelBuilder builder = new PanelBuilder(new FormLayout(
			ColumnSpec.decodeSpecs("198dlu:grow"),
			new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.LINE_GAP_ROWSPEC,
				new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
			}), this);

		builder.add(panelQM1,       cc.xy(1, 1));
		builder.add(panelQM2,       cc.xy(1, 3));
		builder.add(labelNoteForQM, cc.xy(1, 5));
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JLabel label1;
	private JScrollPane scrollPane1;
	private JList listQuietModeDetectionStrings;
	private JButton btnAddQuietModeDetectionString;
	private JButton btnRemoveQuietModeDetectionString;
	private JCheckBox checkCaseInsensitiveSeachQM;
	private JCheckBox checkDontPlaySoundQM;
	private JCheckBox checkNoCaptchaInQM;
	private JCheckBox checkConfirmDialogsQM;
	private JCheckBox checkPlaySoundForQM;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
