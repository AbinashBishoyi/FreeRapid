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
		toolbar = new JButtonBar();
		panelCard = new JPanel();
		JPanel panelGeneral = new JPanel();
		JPanel panelGeneralSettings = new JPanel();
		checkShowIconInSystemTray = new JCheckBox();
		JPanel panelAlarm = new JPanel();
		JPanel panelWhenAlarmGoesOff = new JPanel();
		JPanel panelAlarmDefaults = new JPanel();
		JPanel panelPlugins = new JPanel();
		JTabbedPane pluginTabbedPane = new JTabbedPane();
		JPanel pluginPanelSettings = new JPanel();
		JScrollPane scrollPane1 = new JScrollPane();
		pluginTable = new JXTable();
		JXButtonPanel pluginsButtonPanel = new JXButtonPanel();
		JLabel labelPluginInfo = new JLabel();
		popmenuButton = ComponentFactory.getPopdownButton()
		btnPluginOptions = new JButton();
		JPanel pluginPanelUpdates = new JPanel();
		check4PluginUpdatesAutomatically = new JCheckBox();
		checkConfirmUpdating = new JCheckBox();
		checkDownloadNotExistingPlugins = new JCheckBox();
		JLabel labelUpdateFromServer = new JLabel();
		comboPluginServers = new JComboBox();
		btnResetDefaultPluginServer = new JButton();
		buttonBar = new JXButtonPanel();
		btnOK = new JButton();
		btnCancel = new JButton();
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

				//======== toolbar ========
				{
					toolbar.setBorder(LineBorder.createBlackLineBorder());
					toolbar.setLayout(null);
				}
				contentPanel.add(toolbar, BorderLayout.NORTH);

				//======== panelCard ========
				{
					panelCard.setLayout(new CardLayout());

					//======== panelGeneral ========
					{
						panelGeneral.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== panelGeneralSettings ========
						{
							panelGeneralSettings.setBorder(new TitledBorder(null, bundle.getString("panelGeneralSettings.border"), TitledBorder.LEADING, TitledBorder.TOP));

							//---- checkShowIconInSystemTray ----
							checkShowIconInSystemTray.setText(bundle.getString("checkShowIconInSystemTray.text"));

							PanelBuilder panelGeneralSettingsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								RowSpec.decodeSpecs("default")), panelGeneralSettings);

							panelGeneralSettingsBuilder.add(checkShowIconInSystemTray, cc.xy(3, 1));
						}

						PanelBuilder panelGeneralBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW),
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.NO_GROW)
							}), panelGeneral);

						panelGeneralBuilder.add(panelGeneralSettings, cc.xy(1, 1));
					}
					panelCard.add(panelGeneral, "CARD1");

					//======== panelAlarm ========
					{
						panelAlarm.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== panelWhenAlarmGoesOff ========
						{
							panelWhenAlarmGoesOff.setBorder(new TitledBorder(null, bundle.getString("panelWhenAlarmGoesOff.border"), TitledBorder.LEADING, TitledBorder.TOP));

							PanelBuilder panelWhenAlarmGoesOffBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelWhenAlarmGoesOff);
							((FormLayout)panelWhenAlarmGoesOff.getLayout()).setColumnGroups(new int[][] {{9, 11}});

						}

						//======== panelAlarmDefaults ========
						{
							panelAlarmDefaults.setBorder(new TitledBorder(null, bundle.getString("panelAlarmDefaults.border"), TitledBorder.LEADING, TitledBorder.TOP));

							PanelBuilder panelAlarmDefaultsBuilder = new PanelBuilder(new FormLayout(
								new ColumnSpec[] {
									new ColumnSpec(ColumnSpec.LEFT, Sizes.dluX(0), FormSpec.NO_GROW),
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC,
									FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
									FormFactory.DEFAULT_COLSPEC
								},
								new RowSpec[] {
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC,
									FormFactory.NARROW_LINE_GAP_ROWSPEC,
									FormFactory.DEFAULT_ROWSPEC
								}), panelAlarmDefaults);

						}

						PanelBuilder panelAlarmBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								FormFactory.DEFAULT_ROWSPEC,
								FormFactory.RELATED_GAP_ROWSPEC,
								FormFactory.DEFAULT_ROWSPEC
							}), panelAlarm);

						panelAlarmBuilder.add(panelWhenAlarmGoesOff, cc.xy(1, 1));
						panelAlarmBuilder.add(panelAlarmDefaults,    cc.xy(1, 3));
					}
					panelCard.add(panelAlarm, "CARD2");

					//======== panelPlugins ========
					{
						panelPlugins.setBorder(Borders.TABBED_DIALOG_BORDER);

						//======== pluginTabbedPane ========
						{

							//======== pluginPanelSettings ========
							{
								pluginPanelSettings.setBorder(new CompoundBorder(
									new EmptyBorder(4, 4, 4, 4),
									new EtchedBorder()));
								pluginPanelSettings.setLayout(new BorderLayout());

								//======== scrollPane1 ========
								{
									scrollPane1.setViewportView(pluginTable);
								}
								pluginPanelSettings.add(scrollPane1, BorderLayout.CENTER);

								//======== pluginsButtonPanel ========
								{
									pluginsButtonPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

									//---- labelPluginInfo ----
									labelPluginInfo.setText(bundle.getString("labelPluginInfo.text"));

									//---- popmenuButton ----
									popmenuButton.setText(bundle.getString("popmenuButton.text"));

									//---- btnPluginOptions ----
									btnPluginOptions.setText(bundle.getString("btnPluginOptions.text"));

									PanelBuilder pluginsButtonPanelBuilder = new PanelBuilder(new FormLayout(
										new ColumnSpec[] {
											FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
											FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
											new ColumnSpec(ColumnSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
											FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
											FormFactory.DEFAULT_COLSPEC,
											FormFactory.UNRELATED_GAP_COLSPEC,
											FormFactory.DEFAULT_COLSPEC,
											FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
											FormFactory.UNRELATED_GAP_COLSPEC
										},
										RowSpec.decodeSpecs("default")), pluginsButtonPanel);

									pluginsButtonPanelBuilder.add(labelPluginInfo,  cc.xy(3, 1));
									pluginsButtonPanelBuilder.add(popmenuButton,    cc.xy(5, 1));
									pluginsButtonPanelBuilder.add(btnPluginOptions, cc.xy(7, 1));
								}
								pluginPanelSettings.add(pluginsButtonPanel, BorderLayout.SOUTH);
							}
							pluginTabbedPane.addTab(bundle.getString("pluginPanelSettings.tab.title"), pluginPanelSettings);


							//======== pluginPanelUpdates ========
							{
								pluginPanelUpdates.setBorder(new CompoundBorder(
									new EmptyBorder(4, 4, 4, 4),
									new TitledBorder(bundle.getString("pluginPanelUpdates.border"))));

								//---- check4PluginUpdatesAutomatically ----
								check4PluginUpdatesAutomatically.setText(bundle.getString("check4PluginUpdatesAutomatically.text"));

								//---- checkConfirmUpdating ----
								checkConfirmUpdating.setText(bundle.getString("checkConfirmUpdating.text"));

								//---- checkDownloadNotExistingPlugins ----
								checkDownloadNotExistingPlugins.setText(bundle.getString("checkDownloadNotExistingPlugins.text"));

								//---- labelUpdateFromServer ----
								labelUpdateFromServer.setText(bundle.getString("labelUpdateFromServer.text"));
								labelUpdateFromServer.setLabelFor(comboPluginServers);

								//---- comboPluginServers ----
								comboPluginServers.setEditable(true);

								//---- btnResetDefaultPluginServer ----
								btnResetDefaultPluginServer.setText(bundle.getString("btnResetDefaultPluginServer.text"));

								PanelBuilder pluginPanelUpdatesBuilder = new PanelBuilder(new FormLayout(
									new ColumnSpec[] {
										FormFactory.DEFAULT_COLSPEC,
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										new ColumnSpec(ColumnSpec.FILL, Sizes.bounded(Sizes.DEFAULT, Sizes.dluX(50), Sizes.dluX(75)), FormSpec.DEFAULT_GROW),
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										FormFactory.DEFAULT_COLSPEC,
										FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
										FormFactory.UNRELATED_GAP_COLSPEC
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
										new RowSpec(RowSpec.CENTER, Sizes.DEFAULT, FormSpec.DEFAULT_GROW)
									}), pluginPanelUpdates);

								pluginPanelUpdatesBuilder.add(check4PluginUpdatesAutomatically, cc.xywh(1, 1, 3, 1));
								pluginPanelUpdatesBuilder.add(checkConfirmUpdating,             cc.xywh(1, 3, 3, 1));
								pluginPanelUpdatesBuilder.add(checkDownloadNotExistingPlugins,  cc.xywh(1, 5, 3, 1));
								pluginPanelUpdatesBuilder.add(labelUpdateFromServer,            cc.xywh(1, 7, 1, 1, CellConstraints.RIGHT, CellConstraints.DEFAULT));
								pluginPanelUpdatesBuilder.add(comboPluginServers,               cc.xy  (3, 7));
								pluginPanelUpdatesBuilder.add(btnResetDefaultPluginServer,      cc.xy  (5, 7));
							}
							pluginTabbedPane.addTab(bundle.getString("pluginPanelUpdates.tab.title"), pluginPanelUpdates);

						}

						PanelBuilder panelPluginsBuilder = new PanelBuilder(new FormLayout(
							ColumnSpec.decodeSpecs("default:grow"),
							new RowSpec[] {
								new RowSpec(RowSpec.FILL, Sizes.DEFAULT, FormSpec.DEFAULT_GROW),
								FormFactory.RELATED_GAP_ROWSPEC,
								new RowSpec("15px")
							}), panelPlugins);

						panelPluginsBuilder.add(pluginTabbedPane, cc.xy(1, 1));
					}
					panelCard.add(panelPlugins, "CARD6");
				}
				contentPanel.add(panelCard, BorderLayout.CENTER);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(Borders.BUTTON_BAR_GAP_BORDER);
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
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Open Source Project license - unknown
	private JButtonBar toolbar;
	private JPanel panelCard;
	private JCheckBox checkShowIconInSystemTray;
	private JXTable pluginTable;
	private JToggleButton popmenuButton;
	private JButton btnPluginOptions;
	private JCheckBox check4PluginUpdatesAutomatically;
	private JCheckBox checkConfirmUpdating;
	private JCheckBox checkDownloadNotExistingPlugins;
	private JComboBox comboPluginServers;
	private JButton btnResetDefaultPluginServer;
	private JXButtonPanel buttonBar;
	private JButton btnOK;
	private JButton btnCancel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
