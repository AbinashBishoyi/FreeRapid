import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.jgoodies.forms.builder.*;
import com.jgoodies.forms.factories.*;
import com.jgoodies.forms.layout.*;
import com.l2fprod.common.swing.*;
import org.jdesktop.swinghelper.buttonpanel.*;
import org.jdesktop.swingx.*;
/*
 * Created by JFormDesigner on Mon Aug 06 17:23:18 CEST 2007
 */



/**
 * @author Ladislav Vitasek
 */
public class UserPreferencesDialog extends JDialog {
	public UserPreferencesDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public UserPreferencesDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Open Source Project license - unknown
		ResourceBundle bundle = ResourceBundle.getBundle("UserPreferencesDialog");
		JPanel dialogPane = new JPanel();
		JPanel contentPanel = new JPanel();
		JXButtonPanel buttonBar = new JXButtonPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
		panelCard = new JPanel();
		panelGeneral = new JPanel();
		JPanel panelApplicationSettings = new JPanel();
		checkForNewVersion = new JCheckBox();
		checkAllowOnlyOneInstance = new JCheckBox();
		JPanel panelDownloadsSettings = new JPanel();
		checkContinueInterrupted = new JCheckBox();
		checkCloseWhenAllComplete = new JCheckBox();
		JLabel labelIfFilenameExists = new JLabel();
		comboFileExists = new JComboBox();
		panelSoundSettings = new JPanel();
		JPanel panelSound = new JPanel();
		checkPlaySoundInCaseOfError = new JCheckBox();
		checkPlaySoundWhenComplete = new JCheckBox();
		JPanel panelViews = new JPanel();
		JPanel panelAppearance = new JPanel();
		JLabel labelLaF = new JLabel();
		comboLaF = new JComboBox();
		JLabel labelRequiresRestart2 = new JLabel();
		checkDecoratedFrames = new JCheckBox();
		checkShowIconInSystemTray = new JCheckBox();
		checkHideWhenMinimized = new JCheckBox();
		panelConnectionSettings = new JPanel();
		JPanel panelConnections1 = new JPanel();
		JLabel labelMaxConcurrentDownloads = new JLabel();
		spinnerMaxConcurrentDownloads = new JSpinner();
		JPanel panelProxySettings = new JPanel();
		checkUseProxyList = new JCheckBox();
		fieldProxyListPath = new JTextField();
		btnProxyListPathSelect = new JButton();
		JLabel labelTextFileFormat = new JLabel();
		JPanel panelErrorHandling = new JPanel();
		JLabel labelErrorAttemptsCount = new JLabel();
		spinnerErrorAttemptsCount = new JSpinner();
		JLabel labelNoAutoreconnect = new JLabel();
		JLabel labelAutoReconnectTime = new JLabel();
		spinnerAutoReconnectTime = new JSpinner();
		JLabel labelSeconds = new JLabel();
		JLabel labelRequiresRestart = new JLabel();
		toolbar = new JButtonBar();
		CellConstraints cc = new CellConstraints();

		//======== this ========
		setTitle(bundle.getString("this.title"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout());

				//======== buttonBar ========
				{
					buttonBar.setBorder(Borders.createEmptyBorder("5dlu, 4dlu, 4dlu, 4dlu"));
					buttonBar.setCyclic(true);

					//---- btnOK ----
					btnOK.setText(bundle.getString("btnOK.text"));

					//---- btnCancel ----
					btnCancel.setText(bundle.getString("btnCancel.text"));

					PanelBuilder buttonBarBuilder = new PanelBuilder(new FormLayout(
						new ColumnSpec[] {
							FormFactory.GLUE_COLSPEC,
							new ColumnSpec("max(pref;42dlu)"),
							FormFactory.RELATED_GAP_COLSPEC,
							FormFactory.PREF_COLSPEC
						},
						RowSpec.decodeSpecs("pref")), buttonBar);
					((FormLayout)buttonBar.getLayout()).setColumnGroups(new int[][] {{2, 4}});

					buttonBarBuilder.add(btnOK,     cc.xy(2, 1));
					buttonBarBuilder.add(btnCancel, cc.xy(4, 1));
				}
				contentPanel.add(buttonBar, BorderLayout.SOUTH);

				//======== panelCard ========
				{
					panelCard.setLayout(new CardLayout());

					//======== panelGeneral ========
					{
						panelGeneral.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== panelApplicationSettings ========
						{
							panelApplicationSettings.setBorder(new TitledBorder(null, bundle.getString("panelApplicationSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- checkForNewVersion ----
							checkForNewVersion.setText(bundle.getString("checkForNewVersion.text"));

							//---- checkAllowOnlyOneInstance ----
							checkAllowOnlyOneInstance.setText(bundle.getString("checkAllowOnlyOneInstance.text"));

							PanelBuilder panelApplicationSettingsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								RowSpec.decodeSpecs("default, default")), panelApplicationSettings);

							panelApplicationSettingsBuilder.add(checkForNewVersion,        cc.xy(3, 1));
							panelApplicationSettingsBuilder.add(checkAllowOnlyOneInstance, cc.xy(3, 2));
						}

						//======== panelDownloadsSettings ========
						{
							panelDownloadsSettings.setBorder(new TitledBorder(null, bundle.getString("panelDownloadsSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- checkContinueInterrupted ----
							checkContinueInterrupted.setText(bundle.getString("checkContinueInterrupted.text"));

							//---- checkCloseWhenAllComplete ----
							checkCloseWhenAllComplete.setText(bundle.getString("checkCloseWhenAllComplete.text"));

							//---- labelIfFilenameExists ----
							labelIfFilenameExists.setText(bundle.getString("labelIfFilenameExists.text"));

							PanelBuilder panelDownloadsSettingsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelDownloadsSettings);

							panelDownloadsSettingsBuilder.add(checkContinueInterrupted,  cc.xywh(3, 1, 5, 1));
							panelDownloadsSettingsBuilder.add(checkCloseWhenAllComplete, cc.xywh(3, 2, 5, 1));
							panelDownloadsSettingsBuilder.add(labelIfFilenameExists,     cc.xywh(3, 4, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
							panelDownloadsSettingsBuilder.add(comboFileExists,           cc.xy  (5, 4));
						}

						PanelBuilder panelGeneralBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.LINE_GAP_ROWSPEC,
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
							}), panelGeneral);

						panelGeneralBuilder.add(panelApplicationSettings, cc.xy(1, 1));
						panelGeneralBuilder.add(panelDownloadsSettings,   cc.xy(1, 3));
					}
					panelCard.add(panelGeneral, "CARD1");

					//======== panelSoundSettings ========
					{
						panelSoundSettings.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== panelSound ========
						{
							panelSound.setBorder(new TitledBorder(null, bundle.getString("panelSound.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- checkPlaySoundInCaseOfError ----
							checkPlaySoundInCaseOfError.setText(bundle.getString("checkPlaySoundInCaseOfError.text"));

							//---- checkPlaySoundWhenComplete ----
							checkPlaySoundWhenComplete.setText(bundle.getString("checkPlaySoundWhenComplete.text"));

							PanelBuilder panelSoundBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								RowSpec.decodeSpecs("default, default")), panelSound);

							panelSoundBuilder.add(checkPlaySoundInCaseOfError, cc.xy(3, 1));
							panelSoundBuilder.add(checkPlaySoundWhenComplete,  cc.xy(3, 2));
						}

						PanelBuilder panelSoundSettingsBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}), panelSoundSettings);

						panelSoundSettingsBuilder.add(panelSound, cc.xy(1, 1));
					}
					panelCard.add(panelSoundSettings, "CARD2");

					//======== panelViews ========
					{
						panelViews.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== panelAppearance ========
						{
							panelAppearance.setBorder(new CompoundBorder(
								new TitledBorder(null, bundle.getString("panelAppearance.border"), TitledBorder.LEADING, TitledBorder.TOP),
								Borders.DLU2_BORDER));

							//---- labelLaF ----
							labelLaF.setText(bundle.getString("labelLaF.text"));
							labelLaF.setLabelFor(comboLaF);

							//---- labelRequiresRestart2 ----
							labelRequiresRestart2.setText(bundle.getString("labelRequiresRestart2.text"));

							//---- checkDecoratedFrames ----
							checkDecoratedFrames.setText(bundle.getString("checkDecoratedFrames.text"));

							//---- checkShowIconInSystemTray ----
							checkShowIconInSystemTray.setText(bundle.getString("checkShowIconInSystemTray.text"));

							//---- checkHideWhenMinimized ----
							checkHideWhenMinimized.setText(bundle.getString("checkHideWhenMinimized.text"));

							PanelBuilder panelAppearanceBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.PREF_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.MIN_ROWSPEC,
									FormFactory.RELATED_GAP_ROWSPEC
								}), panelAppearance);

							panelAppearanceBuilder.add(labelLaF,                  cc.xy  (3, 1));
							panelAppearanceBuilder.add(comboLaF,                  cc.xy  (5, 1));
							panelAppearanceBuilder.add(labelRequiresRestart2,     cc.xy  (7, 1));
							panelAppearanceBuilder.add(checkDecoratedFrames,      cc.xywh(3, 2, 5, 1));
							panelAppearanceBuilder.add(checkShowIconInSystemTray, cc.xywh(3, 3, 5, 1));
							panelAppearanceBuilder.add(checkHideWhenMinimized,    cc.xywh(3, 4, 5, 1));
						}

						PanelBuilder panelViewsBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
							}), panelViews);

						panelViewsBuilder.add(panelAppearance, cc.xy(1, 1));
					}
					panelCard.add(panelViews, "CARD4");

					//======== panelConnectionSettings ========
					{
						panelConnectionSettings.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== panelConnections1 ========
						{
							panelConnections1.setBorder(new TitledBorder(null, bundle.getString("panelConnections1.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- labelMaxConcurrentDownloads ----
							labelMaxConcurrentDownloads.setText(bundle.getString("labelMaxConcurrentDownloads.text"));

							//---- spinnerMaxConcurrentDownloads ----
							spinnerMaxConcurrentDownloads.setModel(new SpinnerNumberModel(0, 0, 5, 1));

							PanelBuilder panelConnections1Builder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec("max(default;30dlu)")
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC
								}), panelConnections1);

							panelConnections1Builder.add(labelMaxConcurrentDownloads,   cc.xy(3, 1));
							panelConnections1Builder.add(spinnerMaxConcurrentDownloads, cc.xy(5, 1));
						}

						//======== panelProxySettings ========
						{
							panelProxySettings.setBorder(new TitledBorder(null, bundle.getString("panelProxySettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- checkUseProxyList ----
							checkUseProxyList.setText(bundle.getString("checkUseProxyList.text"));

							//---- btnProxyListPathSelect ----
							btnProxyListPathSelect.setText(bundle.getString("btnProxyListPathSelect.text"));

							//---- labelTextFileFormat ----
							labelTextFileFormat.setText(bundle.getString("labelTextFileFormat.text"));

							PanelBuilder panelProxySettingsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.dluX(200), FormSpec.DEFAULT_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC
								}), panelProxySettings);

							panelProxySettingsBuilder.add(checkUseProxyList,      cc.xy(3, 1));
							panelProxySettingsBuilder.add(fieldProxyListPath,     cc.xy(5, 1));
							panelProxySettingsBuilder.add(btnProxyListPathSelect, cc.xy(7, 1));
							panelProxySettingsBuilder.add(labelTextFileFormat,    cc.xy(5, 2));
						}

						//======== panelErrorHandling ========
						{
							panelErrorHandling.setBorder(new TitledBorder(null, bundle.getString("panelErrorHandling.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- labelErrorAttemptsCount ----
							labelErrorAttemptsCount.setText(bundle.getString("labelErrorAttemptsCount.text"));

							//---- spinnerErrorAttemptsCount ----
							spinnerErrorAttemptsCount.setModel(new SpinnerNumberModel(0, 0, 10, 1));

							//---- labelNoAutoreconnect ----
							labelNoAutoreconnect.setText(bundle.getString("labelNoAutoreconnect.text"));

							//---- labelAutoReconnectTime ----
							labelAutoReconnectTime.setText(bundle.getString("labelAutoReconnectTime.text"));

							//---- spinnerAutoReconnectTime ----
							spinnerAutoReconnectTime.setModel(new SpinnerNumberModel(0, 0, 1000, 5));

							//---- labelSeconds ----
							labelSeconds.setText(bundle.getString("labelSeconds.text"));

							PanelBuilder panelErrorHandlingBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec("max(pref;30dlu)"),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC
								}), panelErrorHandling);

							panelErrorHandlingBuilder.add(labelErrorAttemptsCount,   cc.xy(3, 1));
							panelErrorHandlingBuilder.add(spinnerErrorAttemptsCount, cc.xy(5, 1));
							panelErrorHandlingBuilder.add(labelNoAutoreconnect,      cc.xy(7, 1));
							panelErrorHandlingBuilder.add(labelAutoReconnectTime,    cc.xy(3, 3));
							panelErrorHandlingBuilder.add(spinnerAutoReconnectTime,  cc.xy(5, 3));
							panelErrorHandlingBuilder.add(labelSeconds,              cc.xy(7, 3));
						}

						//---- labelRequiresRestart ----
						labelRequiresRestart.setText(bundle.getString("labelRequiresRestart.text"));

						PanelBuilder panelConnectionSettingsBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.LINE_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}), panelConnectionSettings);

						panelConnectionSettingsBuilder.add(panelConnections1,    cc.xy(1, 1));
						panelConnectionSettingsBuilder.add(panelProxySettings,   cc.xy(1, 3));
						panelConnectionSettingsBuilder.add(panelErrorHandling,   cc.xy(1, 5));
						panelConnectionSettingsBuilder.add(labelRequiresRestart, cc.xy(1, 7));
					}
					panelCard.add(panelConnectionSettings, "CARD3");
				}
				contentPanel.add(panelCard, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);
			dialogPane.add(toolbar, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JButton btnOK;
	private JButton btnCancel;
	private JPanel panelCard;
	private JPanel panelGeneral;
	private JCheckBox checkForNewVersion;
	private JCheckBox checkAllowOnlyOneInstance;
	private JCheckBox checkContinueInterrupted;
	private JCheckBox checkCloseWhenAllComplete;
	private JComboBox comboFileExists;
	private JPanel panelSoundSettings;
	private JCheckBox checkPlaySoundInCaseOfError;
	private JCheckBox checkPlaySoundWhenComplete;
	private JComboBox comboLaF;
	private JCheckBox checkDecoratedFrames;
	private JCheckBox checkShowIconInSystemTray;
	private JCheckBox checkHideWhenMinimized;
	private JPanel panelConnectionSettings;
	private JSpinner spinnerMaxConcurrentDownloads;
	private JCheckBox checkUseProxyList;
	private JTextField fieldProxyListPath;
	private JButton btnProxyListPathSelect;
	private JSpinner spinnerErrorAttemptsCount;
	private JSpinner spinnerAutoReconnectTime;
	private JButtonBar toolbar;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
