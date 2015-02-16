package org.sper.logtracker.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.sper.logtracker.config.ConfigFileAction;
import org.sper.logtracker.config.ConfigFileOpenButton;
import org.sper.logtracker.config.ConfigFileSaveButton;
import org.sper.logtracker.config.ConfigurationAware;
import org.sper.logtracker.data.Console;
import org.sper.logtracker.data.Console.MessageListener;
import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.parserconf.ParserSelectionModel;
import org.sper.logtracker.servstat.ButtonColumn;

public class FileControlPanel extends JSplitPane implements MessageListener, ConfigurationAware {
	private static final long serialVersionUID = 1L;
	private JTable logFileTable;
	private DefaultTableModel logFileTableModel;
	private JButton applyFilesButton;
	private JTextArea warnings;
	private JButton btnClearLog;
	private LogTracker logTracker;
	private JCheckBox useObsVal;
	private JSpinner obsValSpinner;
	private JComboBox logFileFormatBox;
	private ParserSelectionModel parserModel;
	
	private static class ConfObj implements Serializable {
		private static final long serialVersionUID = 1L;
		String[] fname;
		Integer obsVal;
		String parserConfig;
	}

	public FileControlPanel(final LogTracker logTracker, List<String> fnameList) {
		super();
		setContinuousLayout(true);
		setOrientation(JSplitPane.VERTICAL_SPLIT);
		this.logTracker = logTracker;
		
		JPanel fileTopPanel = new JPanel();
		setLeftComponent(fileTopPanel);
		fileTopPanel.setLayout(new BoxLayout(fileTopPanel, BoxLayout.Y_AXIS));
		
		JToolBar toolBar = new JToolBar();
		fileTopPanel.add(toolBar);
		toolBar.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton btnLoadConfig = new ConfigFileOpenButton(logTracker.getFrame(), null, new ConfigFileAction() {
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
				logTracker.getConfiguration().loadConfiguration(selectedFile);
			}
		});
		btnLoadConfig.setToolTipText("Open Config File");
		toolBar.add(btnLoadConfig);
		
		JButton btnSaveConfig = new ConfigFileSaveButton(logTracker.getFrame(), null, new ConfigFileAction() {
			
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
				logTracker.getConfiguration().safeToFile(selectedFile);
			}
		});
		btnSaveConfig.setToolTipText("Save Config File");
		toolBar.add(btnSaveConfig);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		toolBar.add(horizontalGlue);
		
		JButton btnInfo = new JButton(new ImageIcon(FileControlPanel.class.getResource("/Info.png")));
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(logTracker.getFrame(), new JScrollPane(new WelcomePanel()), "About Log-Tracker", JOptionPane.PLAIN_MESSAGE);
			}
		});
		btnInfo.setHorizontalAlignment(SwingConstants.LEFT);
		toolBar.add(btnInfo);
		
		JSeparator separator = new JSeparator();
		fileTopPanel.add(separator);
		
		JPanel logFileContentPanel = new JPanel();
		logFileContentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		fileTopPanel.add(logFileContentPanel);
		logFileContentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel logFileChangePanel = new JPanel();
		logFileContentPanel.add(logFileChangePanel, BorderLayout.NORTH);
		logFileChangePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		logFileChangePanel.setLayout(new BoxLayout(logFileChangePanel, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		logFileChangePanel.add(panel);
		
		JLabel lblLogfiles = new JLabel("Log-Files:");
		panel.add(lblLogfiles);
		lblLogfiles.setHorizontalAlignment(SwingConstants.LEFT);
		
		JButton addLogFileButton = new JButton("");
		addLogFileButton.setToolTipText("Add Log-Files to Log-File table");
		addLogFileButton.setIcon(new ImageIcon(FileControlPanel.class.getResource("/addFile.png")));
		addLogFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] logFile = LogFileTableEditor.selectLogFile(logTracker.getFrame(), null, true);
				if (logFile != null)
					for (File file : logFile) {
						logFileTableModel.addRow(new Object[] {file.getPath(), null});
					}
			}

		});
		addLogFileButton.setHorizontalAlignment(SwingConstants.RIGHT);
		logFileChangePanel.add(addLogFileButton);
		
		JScrollPane logFileScrollPane = new JScrollPane();
		logFileContentPanel.add(logFileScrollPane, BorderLayout.CENTER);
		logFileScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
		logFileScrollPane.setPreferredSize(new Dimension(300, 120));
		
		logFileTable = new JTable();
		logFileScrollPane.setViewportView(logFileTable);
		logFileTableModel = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
					"File Name", "Del"
				}
			) {
				private static final long serialVersionUID = 1L;
				Class<?>[] columnTypes = new Class[] {
					String.class, Object.class
				};
				public Class<?> getColumnClass(int columnIndex) {
					return columnTypes[columnIndex];
				}
				boolean[] columnEditables = new boolean[] {
					true, true
				};
				public boolean isCellEditable(int row, int column) {
					return columnEditables[column];
				}
			};
			for (String fname : fnameList) {
				logFileTableModel.addRow(new Object[] {fname, null});
			}
		logFileTable.setModel(logFileTableModel);
		
		JPanel outerObsvalPanel = new JPanel();
		logFileContentPanel.add(outerObsvalPanel, BorderLayout.SOUTH);
		outerObsvalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		outerObsvalPanel.setLayout(new BoxLayout(outerObsvalPanel, BoxLayout.X_AXIS));
		
		JPanel obsvalPanel = new JPanel();
		outerObsvalPanel.add(obsvalPanel);
		obsvalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout flowLayout_1 = (FlowLayout) obsvalPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		useObsVal = new JCheckBox("Consider only last");
		obsValSpinner = new JSpinner();
		obsValSpinner.setEnabled(false);
		obsValSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(1), null, new Integer(1)));

		useObsVal.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				obsValSpinner.setEnabled(useObsVal.isSelected());
			}
		});
		
		JLabel lblLogFileParser = new JLabel("Log File Format:");
		obsvalPanel.add(lblLogFileParser);
		
		logFileFormatBox = new JComboBox();
		final LogFileTypeCatalog logFileTypeCatalog = new LogFileTypeCatalog();
		parserModel = new ParserSelectionModel(logFileTypeCatalog);
		logFileFormatBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (logFileFormatBox.getSelectedItem() == logFileTypeCatalog.getConfigureItem()) {
					ParserConfigDialog dialog = new ParserConfigDialog(parserModel);
					dialog.setLogFileTypeList(logFileTypeCatalog.getParserTypeList(dialog));
					dialog.setVisible(true);
					logFileFormatBox.setSelectedItem(null);
				}
				checkEnableApplyButton();
			}
		});
		logFileFormatBox.setModel(parserModel);
		logFileFormatBox.setToolTipText("Choose an appropriate Parser for your Log-Files");
		logTracker.getConfiguration().registerModule(new ConfigurationAware() {
			
			@Override
			public Serializable getConfig() {
				ConfiguredLogParser parserConfig = (ConfiguredLogParser) logFileFormatBox.getSelectedItem();
				if (parserConfig.isEditable()) {
					ArrayList<ConfiguredLogParser> configList = new ArrayList<ConfiguredLogParser>();
					configList.add(parserConfig);
					return configList;
				}
				return null;
			}
			
			@Override
			public String getCompKey() {
				return ConfiguredLogParser.CONFIG_NAME;
			}
			
			@Override
			public void applyConfig(Serializable cfg) {
				@SuppressWarnings("unchecked")
				ArrayList<ConfiguredLogParser> logParserList = (ArrayList<ConfiguredLogParser>) cfg;
				if (logParserList != null && !logParserList.isEmpty()) {
					parserModel.addParsers(logParserList);
					logFileFormatBox.setSelectedItem(logParserList.get(0));
				}
			}
		});
		obsvalPanel.add(logFileFormatBox);

		obsvalPanel.add(useObsVal);
		obsvalPanel.add(obsValSpinner);

		JLabel lblHours = new JLabel("hours");
		obsvalPanel.add(lblHours);

		applyFilesButton = new JButton("Apply");
		outerObsvalPanel.add(applyFilesButton);
		applyFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setupDataPipeLines();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		checkEnableApplyButton();
		logFileTableModel = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"File Name", "Del"
				}
				) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class[] {
				String.class, Object.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
				true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		for (String fname : fnameList) {
			logFileTableModel.addRow(new Object[] {fname, null});
		}
		logFileTable.setModel(logFileTableModel);
		
		JPanel warningPanel = new JPanel();
		setRightComponent(warningPanel);
		warningPanel.setLayout(new BorderLayout(0, 0));
		warnings = new JTextArea();
		warnings.setBackground(UIManager.getColor("EditorPane.disabledBackground"));
		warnings.setEditable(false);
		warnings.setAutoscrolls(true);
		JScrollPane scrollPane = new JScrollPane(warnings);
		warningPanel.add(scrollPane);
		
		JPanel logButtonPanel = new JPanel();
		warningPanel.add(logButtonPanel, BorderLayout.SOUTH);
		logButtonPanel.setLayout(new BorderLayout(0, 0));
		
		btnClearLog = new JButton("Clear Log");
		btnClearLog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				warnings.setText("");
				btnClearLog.setVisible(false);
			}
		});
		btnClearLog.setVisible(false);
		btnClearLog.setHorizontalAlignment(SwingConstants.RIGHT);
		logButtonPanel.add(btnClearLog, BorderLayout.EAST);
		btnClearLog.setIcon(new ImageIcon(LogTracker.class.getResource("/delFile.png")));
		logFileTableModel.addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent e) {
				checkEnableApplyButton();
			}
		});
		ButtonColumn cellRenderer = new ButtonColumn(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ButtonColumn.IndexedButton but = (ButtonColumn.IndexedButton) e.getSource();
				logFileTableModel.removeRow(but.getRow());
			}
		});
		TableColumn fileCol = logFileTable.getColumn("File Name");
		fileCol.setCellEditor(new LogFileTableEditor(logTracker.getFrame()));
		TableColumn delCol = logFileTable.getColumn("Del");
		delCol.setCellRenderer(cellRenderer);
		delCol.setCellEditor(cellRenderer);
		logFileTable.getColumnModel().getColumn(0).setResizable(false);
		logFileTable.getColumnModel().getColumn(0).setPreferredWidth(1000);
		logFileTable.getColumnModel().getColumn(1).setResizable(false);
		logFileTable.getColumnModel().getColumn(1).setPreferredWidth(30);
		Console.setListener(this);
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.ui.MessageListener#addMessage(java.lang.String)
	 */
	@Override
	public void addMessage(String text) {
		warnings.setText(warnings.getText() + text);
		btnClearLog.setVisible(true);
		logTracker.setTabIdx(0);
	}

	@Override
	public String getCompKey() {
		return "FileControl";
	}

	@Override
	public void applyConfig(Serializable cfg) {
		ConfObj conf = (ConfObj) cfg;
		logFileTableModel.setRowCount(0);
		for (String fname : conf.fname) {
			logFileTableModel.addRow(new Object[] {fname, null});
		}
		useObsVal.setSelected(conf.obsVal != null);
		if (conf.obsVal != null) {
			obsValSpinner.setValue(conf.obsVal);
		}
		if (logFileFormatBox.getSelectedIndex() < 0 && conf.parserConfig != null)
			for (int i = parserModel.getSize(); --i >= 0; ) {
				LogParser logParser = parserModel.getElementAt(i);
				if (conf.parserConfig.equals(logParser.getName())) {
					logFileFormatBox.setSelectedIndex(i);;
				}
			}
	}

	@Override
	public Serializable getConfig() {
		ConfObj conf = new ConfObj();
		conf.fname = new String[logFileTableModel.getRowCount()];
		for (int i = 0; i < logFileTableModel.getRowCount(); i++)
			conf.fname[i] = (String) logFileTableModel.getValueAt(i, 0);
		conf.obsVal = useObsVal.isSelected() ? (Integer) obsValSpinner.getValue() : null;
		conf.parserConfig = ((ConfiguredLogParser) logFileFormatBox.getSelectedItem()).getName();
		return conf;
	}

	void setupDataPipeLines() throws InterruptedException {
		JTabbedPane tabbedPane = logTracker.getTabbedPane();
		for (int i = tabbedPane.getTabCount() - 1; i > 0; i--)
			tabbedPane.remove(i);
		ConfiguredLogParser logParser = (ConfiguredLogParser) logFileFormatBox.getSelectedItem();
		FileTypeDescriptor logFileTypeDescriptor = logParser.getLogFileTypeDescriptor();
		logFileTypeDescriptor.createAndRegisterTabs(logTracker, logParser);
		List<String> fname = new ArrayList<String>();
		for (int i = 0; i < logFileTableModel.getRowCount(); i++) {
			fname.add((String) logFileTableModel.getValueAt(i, 0));
		}
		logFileTypeDescriptor.setupDataPipeLines(fname, logParser);
	}

	public Long getObsStart() {
		Long obsStart = null;
		if (useObsVal.isSelected()) {
			obsStart = System.currentTimeMillis()
					- (Integer) obsValSpinner.getValue() * 3600000;
		}
		return obsStart;
	}

	private boolean checkEnableApplyButton() {
		boolean result = logFileTableModel.getRowCount() > 0 && logFileFormatBox.getSelectedItem() != null;
		applyFilesButton.setEnabled(result);
		return result;
	}
	
	public JComboBox getLogFileFormatBox() {
		return logFileFormatBox;
	}
}
