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

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.sper.logtracker.config.ConfigurationAware;
import org.sper.logtracker.data.Console;
import org.sper.logtracker.data.Console.MessageListener;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.parserconf.ParserSelectionModel;
import org.sper.logtracker.servstat.ui.ButtonColumn;

public class FileControlPanel extends JPanel implements ConfigurationAware {
	private static final long serialVersionUID = 1L;
	private JTable logFileTable;
	private DefaultTableModel logFileTableModel;
	private JButton applyFilesButton;
	private LogTracker logTracker;
	private JCheckBox useObsVal;
	private JSpinner obsValSpinner;
	private JComboBox logFileFormatBox;
	private ParserSelectionModel parserModel;
	private FileTypeDescriptor activeLogFileType;
	private ToolBar toolBar;
	
	private static class ConfObj implements Serializable {
		private static final long serialVersionUID = 1L;
		String[] fname;
		Integer obsVal;
		String parserConfig;
	}
	
	private static class ConfObj2 implements Serializable {
		private static final long serialVersionUID = 1L;
		LogSource[] logSource;
		Integer obsVal;
		String parserConfig, title;
	}

	public FileControlPanel(final LogTracker logTracker, List<LogSource> fnameList, MessageListener listener, ToolBar toolBar) {
		super();
		this.logTracker = logTracker;
		this.toolBar = toolBar;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		JPanel logFileContentPanel = new JPanel();
		logFileContentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		add(logFileContentPanel);
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
						LogSource source = new LogSource(file.getPath());
						logFileTableModel.addRow(source.modelEntry());
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
		
		JPanel outerObsvalPanel = new JPanel();
		logFileContentPanel.add(outerObsvalPanel, BorderLayout.SOUTH);
		outerObsvalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		outerObsvalPanel.setLayout(new BoxLayout(outerObsvalPanel, BoxLayout.Y_AXIS));
		
		JPanel obsvalPanel = new JPanel();
		outerObsvalPanel.add(obsvalPanel);
		obsvalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout flowLayout_1 = (FlowLayout) obsvalPanel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
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
					logFileFormatBox.setSelectedItem(null);
					dialog.setVisible(true);
				}
				checkEnableApplyButton();
			}
		});
		logFileFormatBox.setModel(parserModel);
		logFileFormatBox.setToolTipText("Choose an appropriate Parser for your Log-Files");
		logTracker.getConfiguration().registerModule(new ConfigurationAware() {
			
			@Override
			public Serializable getConfig() {
				ConfiguredLogParser<?> parserConfig = (ConfiguredLogParser<?>) logFileFormatBox.getSelectedItem();
				if (parserConfig != null && parserConfig.isEditable()) {
					ArrayList<ConfiguredLogParser<?>> configList = new ArrayList<ConfiguredLogParser<?>>();
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
				ArrayList<ConfiguredLogParser<?>> logParserList = (ArrayList<ConfiguredLogParser<?>>) cfg;
				if (logParserList != null && !logParserList.isEmpty()) {
					parserModel.addParsers(logParserList);
					logFileFormatBox.setSelectedItem(logParserList.get(0));
				}
			}

			@Override
			public boolean isDynamicModule() {
				return false;
			}
		});
		obsvalPanel.add(logFileFormatBox);
		
		JPanel considerOnlyPanel = new JPanel();
		considerOnlyPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout flowLayout_2 = (FlowLayout) considerOnlyPanel.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		outerObsvalPanel.add(considerOnlyPanel);
		
		useObsVal = new JCheckBox("Consider only last");
		considerOnlyPanel.add(useObsVal);
		obsValSpinner = new JSpinner();
		considerOnlyPanel.add(obsValSpinner);
		obsValSpinner.setEnabled(false);
		obsValSpinner.setModel(new SpinnerNumberModel(new Integer(2), new Integer(1), null, new Integer(1)));
		
				JLabel lblHours = new JLabel("hours");
				considerOnlyPanel.add(lblHours);
		
				useObsVal.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						obsValSpinner.setEnabled(useObsVal.isSelected());
					}
				});
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
		FlowLayout flowLayout_3 = (FlowLayout) buttonPanel.getLayout();
		flowLayout_3.setAlignment(FlowLayout.RIGHT);
		outerObsvalPanel.add(buttonPanel);

		applyFilesButton = new JButton("Apply");
		buttonPanel.add(applyFilesButton);
		applyFilesButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					setupFileProcessing();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		});
		logFileTableModel = new DefaultTableModel(
				new Object[][] {
				},
				new String[] {
						"File Name", "Name", "Del"
				}
				) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;
			Class<?>[] columnTypes = new Class[] {
					String.class, String.class, Object.class
			};
			public Class<?> getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			boolean[] columnEditables = new boolean[] {
					true, true, true
			};
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		};
		checkEnableApplyButton();
		for (LogSource source : fnameList) {
			logFileTableModel.addRow(source.modelEntry());
		}
		logFileTable.setModel(logFileTableModel);
		
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
		logFileTable.getColumnModel().getColumn(0).setResizable(true);
		logFileTable.getColumnModel().getColumn(0).setPreferredWidth(800);
		logFileTable.getColumnModel().getColumn(1).setResizable(true);
		logFileTable.getColumnModel().getColumn(1).setPreferredWidth(200);
		logFileTable.getColumnModel().getColumn(2).setResizable(false);
		logFileTable.getColumnModel().getColumn(2).setPreferredWidth(30);
		Console.setListener(listener);
	}

	@Override
	public String getCompKey() {
		return "FileControl";
	}

	@Override
	public void applyConfig(Serializable cfg) {
		logFileTableModel.setRowCount(0);
		if (cfg instanceof ConfObj) {
			ConfObj conf = (ConfObj) cfg;
			for (String fname : conf.fname) {
				LogSource source = new LogSource(fname);
				logFileTableModel.addRow(source.modelEntry());
			}
			useObsVal.setSelected(conf.obsVal != null);
			if (conf.obsVal != null) {
				obsValSpinner.setValue(conf.obsVal);
			}
			if (logFileFormatBox.getSelectedIndex() < 0 && conf.parserConfig != null)
				for (int i = parserModel.getSize(); --i >= 0; ) {
					LogParser<?> logParser = parserModel.getElementAt(i);
					if (conf.parserConfig.equals(logParser.getName())) {
						logFileFormatBox.setSelectedIndex(i);;
					}
				}
		} else if (cfg instanceof ConfObj2) {
			ConfObj2 conf = (ConfObj2) cfg;
			if (conf.title != null) {
				toolBar.setText(conf.title);
			}
			for (LogSource source : conf.logSource) {
				logFileTableModel.addRow(source.modelEntry());
			}
			useObsVal.setSelected(conf.obsVal != null);
			if (conf.obsVal != null) {
				obsValSpinner.setValue(conf.obsVal);
			}
			if (logFileFormatBox.getSelectedIndex() < 0 && conf.parserConfig != null)
				for (int i = parserModel.getSize(); --i >= 0; ) {
					LogParser<?> logParser = parserModel.getElementAt(i);
					if (conf.parserConfig.equals(logParser.getName())) {
						logFileFormatBox.setSelectedIndex(i);;
					}
				}
		}
	}

	@Override
	public Serializable getConfig() {
		ConfObj2 conf = new ConfObj2();
		conf.logSource = new LogSource[logFileTableModel.getRowCount()];
		for (int i = 0; i < logFileTableModel.getRowCount(); i++)
			conf.logSource[i] = new LogSource((String) logFileTableModel.getValueAt(i, 0), (String) logFileTableModel.getValueAt(i, 1));
		conf.obsVal = useObsVal.isSelected() ? (Integer) obsValSpinner.getValue() : null;
		conf.title = toolBar.getText();
		ConfiguredLogParser<?> selectedItem = (ConfiguredLogParser<?>) logFileFormatBox.getSelectedItem();
		if (selectedItem != null)
			conf.parserConfig = selectedItem.getName();
		return conf;
	}

	void setupFileProcessing() throws InterruptedException {
		ConfiguredLogParser<?> logParser = (ConfiguredLogParser<?>) logFileFormatBox.getSelectedItem();
		FileTypeDescriptor logFileTypeDescriptor = logParser.getLogFileTypeDescriptor();
		if (activeLogFileType != logFileTypeDescriptor) {
			if (activeLogFileType != null)
				activeLogFileType.removeDockables(logTracker.getControl());
			logTracker.getConfiguration().resetDynamicModules();
			logFileTypeDescriptor.createAndRegisterDockables(logTracker.getControl(), logTracker.getConfiguration(), logParser);
			activeLogFileType = logFileTypeDescriptor;
		}
		List<LogSource> logSource = new ArrayList<LogSource>();
		for (int i = 0; i < logFileTableModel.getRowCount(); i++) {
			logSource.add(new LogSource((String) logFileTableModel.getValueAt(i, 0), (String) logFileTableModel.getValueAt(i, 1)));
		}
		logFileTypeDescriptor.setupDataPipeLines(logSource, logParser, getObsStart());
		logTracker.getConfiguration().resetActiveConfig();
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

	@Override
	public boolean isDynamicModule() {
		return false;
	}
}
