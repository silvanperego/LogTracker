package org.sper.logtracker.parserconf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.LogTrackerConfig;
import org.sper.logtracker.config.XMLConfigSupport;
import org.sper.logtracker.config.compat.ConfigFileAction;
import org.sper.logtracker.config.compat.ConfigFileOpenButton;
import org.sper.logtracker.config.compat.ConfigFileSaveButton;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.config.compat.ConfigurationAware;
import org.sper.logtracker.logreader.LogParser;

import validation.ConfigurationSubPanel;
import validation.TextVerifier;

public class ParserConfigPanel extends JPanel implements ConfigurationAware, ConfigurationSubPanel {
	
	private class PatternInputVerifier extends TextVerifier {
		
		private boolean nullable;

		PatternInputVerifier(boolean nullable) {
			super(ParserConfigPanel.this);
			this.nullable = nullable;
		}
		
		@Override
		protected String verifyText(String text) {
			if (text != null && text.length() > 0) {
				try {
					Pattern.compile(text);
				} catch (PatternSyntaxException e) {
					return "Not a valid Java Regex pattern";
				}
			} else if (!nullable) {
				return "Pattern is mandatory";
			}
			return null;
		}
	}

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTable logParserTable;
	private JTextField inclusionPattern;
	private final ButtonGroup inclusionButtonGroup = new ButtonGroup();
	private final ButtonGroup inclExclButtonGroup = new ButtonGroup();
	private JTextArea dataExtractionPatField;
	private ParserConfigModel parserConfigModel;
	private JRadioButton rdbtnWith;
	private JRadioButton rdbtnContaining;
	private JRadioButton rdbtnIncluded;
	private JRadioButton rdbtnExcluded;
	private JLabel errorText;
	private ConfiguredLogParser<?,?> loadedParser;
	private JButton okButton;
	private boolean isNewParser = false;
	private JButton btnCopy;
	private JButton btnCreateNew;
	private List<VerifyingPart> verifierList = new ArrayList<VerifyingPart>();
	private JButton btnDelete;
	private boolean editable;
	private JButton btnSave;
	private JButton btnLoadFromFile;
	private Configuration config;
	@SuppressWarnings("rawtypes")
	private ExtractionFieldHandler extractionFields;
	private JLabel lblLogFileType;
	private JComboBox<FileTypeDescriptor<?,?>> logFileTypeCombo;
	private DefaultComboBoxModel<FileTypeDescriptor<?,?>> logFileTypeComboModel;
	private JPanel dataExtractionPanel;
	private FileTypeDescriptor<?,?> fileTypeDesc;
	private static Color standardBackgroundColor = new JTextField().getBackground();
	/**
	 * Create the dialog.
	 * @param parserConfigList Die Liste der vorhandenen Parserkonfigurationen.
	 * @param dialog der {@link JDialog} der diese Config-Panel beinhaltet.
	 */
	public ParserConfigPanel(ParserConfigList parserConfigList, final JDialog dialog, final ActionListener submitAction) {
		setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JLabel lblAvailableLogparsers = new JLabel("Available Log-Parsers");
			contentPanel.add(lblAvailableLogparsers, BorderLayout.NORTH);
		}
		{
			JScrollPane scrollPane = new JScrollPane();
			scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				logParserTable = new JTable() {

					private static final long serialVersionUID = 1L;

					@Override
					public void changeSelection(int rowIndex, int columnIndex,
							boolean toggle, boolean extend) {
						if (logParserTable.getSelectedRow() != rowIndex) {
							if (isNewParser && !verifyFormDataIsValid()) {
								if (JOptionPane.showConfirmDialog(ParserConfigPanel.this, "This Configuration is not valid and will be discarded!", "Confirm Discard", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
									int selRowIdx = logParserTable.getSelectedRow();
									parserConfigModel.deleteRow(selRowIdx);
									isNewParser = false;
									loadedParser = null;
									super.changeSelection(rowIndex, columnIndex, toggle, extend);
								}
							} else if (inError()) {
								if (JOptionPane.showConfirmDialog(ParserConfigPanel.this, "Edits for this Configuration are discarded!", "Confirm Discard", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
									loadEditingFields(loadedParser);
									removeErrorMarks();
									super.changeSelection(rowIndex, columnIndex, toggle, extend);								
								}
							} else {
								super.changeSelection(rowIndex, columnIndex, toggle, extend);
								isNewParser = false;
							}
						} else
							super.changeSelection(rowIndex, columnIndex, toggle, extend);
					}
				};
				logParserTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
					
					@Override
					public void valueChanged(ListSelectionEvent e) {
						int[] selectedRows = logParserTable.getSelectedRows();
						if (selectedRows.length == 1) {
							saveLoadedParser();
							loadedParser = null;
							loadEditingFields(parserConfigModel.getParser(selectedRows[0]));
							removeErrorMarks();
							enableComponents();
						}
					}
				});
				logParserTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				logParserTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				parserConfigModel = new ParserConfigModel(parserConfigList);
				parserConfigModel.loadFromSelectionModel();
				logParserTable.setModel(parserConfigModel);
				logParserTable.getColumnModel().getColumn(0).setResizable(true);
				logParserTable.getColumnModel().getColumn(0).setPreferredWidth(407);
				logParserTable.getColumnModel().getColumn(0).setCellRenderer(new ParserTableCellRenderer());
				logParserTable.getColumnModel().getColumn(1).setResizable(true);
				logParserTable.getColumnModel().getColumn(1).setPreferredWidth(120);
				logParserTable.getColumnModel().getColumn(1).setCellRenderer(new ParserTableCellRenderer());
				scrollPane.setViewportView(logParserTable);
				logParserTable.setAlignmentX(Component.LEFT_ALIGNMENT);
			}
		}
		{
			dataExtractionPanel = new JPanel();
			contentPanel.add(dataExtractionPanel, BorderLayout.SOUTH);
			dataExtractionPanel.setLayout(new BoxLayout(dataExtractionPanel, BoxLayout.Y_AXIS));
			{
				JPanel editFields = new JPanel();
				dataExtractionPanel.add(editFields);
				editFields.setAlignmentX(Component.LEFT_ALIGNMENT);
				GridBagLayout gbl_editFields = new GridBagLayout();
				gbl_editFields.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
				gbl_editFields.rowHeights = new int[]{0, 0, 0};
				gbl_editFields.columnWeights = new double[]{0.0, 1.0, 0.0, 1.0, 0.0, 0.0};
				gbl_editFields.rowWeights = new double[]{0.0, 1.0, 0.0};
				editFields.setLayout(gbl_editFields);
				{
					lblLogFileType = new JLabel("Log File Type");
					GridBagConstraints gbc_lblLogFileType = new GridBagConstraints();
					gbc_lblLogFileType.anchor = GridBagConstraints.WEST;
					gbc_lblLogFileType.insets = new Insets(0, 0, 5, 5);
					gbc_lblLogFileType.gridx = 0;
					gbc_lblLogFileType.gridy = 0;
					editFields.add(lblLogFileType, gbc_lblLogFileType);
				}
				{
					logFileTypeCombo = new JComboBox<>();
					logFileTypeCombo.addItemListener(new ItemListener() {
						public void itemStateChanged(ItemEvent e) {
							if (e.getStateChange() == ItemEvent.SELECTED) {
								setLogFileType();
								if (loadedParser != null)
									loadedParser = parserConfigModel.replaceRow(logParserTable.getSelectedRow(), fileTypeDesc);
							}
						}
					});
					logFileTypeComboModel = new DefaultComboBoxModel<>();
					logFileTypeCombo.setModel(logFileTypeComboModel);
					GridBagConstraints gbc_logFileTypeCombo = new GridBagConstraints();
					gbc_logFileTypeCombo.gridwidth = 4;
					gbc_logFileTypeCombo.insets = new Insets(0, 0, 5, 5);
					gbc_logFileTypeCombo.fill = GridBagConstraints.HORIZONTAL;
					gbc_logFileTypeCombo.gridx = 1;
					gbc_logFileTypeCombo.gridy = 0;
					editFields.add(logFileTypeCombo, gbc_logFileTypeCombo);
				}
				{
					JLabel lblLines = new JLabel("Lines");
					GridBagConstraints gbc_lblLines = new GridBagConstraints();
					gbc_lblLines.anchor = GridBagConstraints.WEST;
					gbc_lblLines.insets = new Insets(0, 0, 5, 5);
					gbc_lblLines.gridx = 0;
					gbc_lblLines.gridy = 1;
					editFields.add(lblLines, gbc_lblLines);
				}
				{
					JPanel inclusionTypePanel = new JPanel();
					GridBagConstraints gbc_inclusionTypePanel = new GridBagConstraints();
					gbc_inclusionTypePanel.insets = new Insets(0, 0, 5, 5);
					gbc_inclusionTypePanel.anchor = GridBagConstraints.WEST;
					gbc_inclusionTypePanel.gridx = 1;
					gbc_inclusionTypePanel.gridy = 1;
					editFields.add(inclusionTypePanel, gbc_inclusionTypePanel);
					inclusionTypePanel.setLayout(new GridLayout(0, 1, 0, 0));
					{
						rdbtnWith = new JRadioButton("matching");
						rdbtnWith.setToolTipText("Only matching lines are qualified.");
						rdbtnWith.setSelected(true);
						inclusionButtonGroup.add(rdbtnWith);
						inclusionTypePanel.add(rdbtnWith);
					}
					{
						rdbtnContaining = new JRadioButton("containing");
						rdbtnContaining.setToolTipText("Lines that contain the pattern are qualified.");
						inclusionButtonGroup.add(rdbtnContaining);
						inclusionTypePanel.add(rdbtnContaining);
					}
				}
				{
					JLabel lblPattern = new JLabel("Pattern");
					GridBagConstraints gbc_lblPattern = new GridBagConstraints();
					gbc_lblPattern.anchor = GridBagConstraints.WEST;
					gbc_lblPattern.insets = new Insets(0, 0, 5, 5);
					gbc_lblPattern.gridx = 2;
					gbc_lblPattern.gridy = 1;
					editFields.add(lblPattern, gbc_lblPattern);
				}
				{
					inclusionPattern = new JTextField();
					inclusionPattern.setToolTipText("Use a Regex Pattern (Java-Format) to specify text snippets");
					GridBagConstraints gbc_inclusionPattern = new GridBagConstraints();
					gbc_inclusionPattern.weightx = 1.0;
					gbc_inclusionPattern.insets = new Insets(0, 0, 5, 5);
					gbc_inclusionPattern.fill = GridBagConstraints.HORIZONTAL;
					gbc_inclusionPattern.gridx = 3;
					gbc_inclusionPattern.gridy = 1;
					PatternInputVerifier inputVerifier = new PatternInputVerifier(true);
					inclusionPattern.setInputVerifier(inputVerifier);
					addVerifier(new VerifyingPart(inclusionPattern, inputVerifier));
					editFields.add(inclusionPattern, gbc_inclusionPattern);
				}
				{
					JLabel lblAre = new JLabel("are");
					GridBagConstraints gbc_lblAre = new GridBagConstraints();
					gbc_lblAre.anchor = GridBagConstraints.WEST;
					gbc_lblAre.insets = new Insets(0, 0, 5, 5);
					gbc_lblAre.gridx = 4;
					gbc_lblAre.gridy = 1;
					editFields.add(lblAre, gbc_lblAre);
				}
				{
					JPanel panel = new JPanel();
					GridBagConstraints gbc_panel = new GridBagConstraints();
					gbc_panel.anchor = GridBagConstraints.WEST;
					gbc_panel.insets = new Insets(0, 0, 5, 0);
					gbc_panel.gridx = 5;
					gbc_panel.gridy = 1;
					editFields.add(panel, gbc_panel);
					panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
					{
						rdbtnIncluded = new JRadioButton("included");
						rdbtnIncluded.setToolTipText("Qualified lines are interpreted by the parser.");
						rdbtnIncluded.setSelected(true);
						inclExclButtonGroup.add(rdbtnIncluded);
						panel.add(rdbtnIncluded);
					}
					{
						rdbtnExcluded = new JRadioButton("excluded");
						rdbtnExcluded.setToolTipText("Qualified lines are ignored by the parser.");
						inclExclButtonGroup.add(rdbtnExcluded);
						panel.add(rdbtnExcluded);
					}
				}
				{
					JLabel lblExtractionPattern = new JLabel("Extraction Pattern");
					GridBagConstraints gbc_lblExtractionPattern = new GridBagConstraints();
					gbc_lblExtractionPattern.anchor = GridBagConstraints.EAST;
					gbc_lblExtractionPattern.insets = new Insets(0, 0, 0, 5);
					gbc_lblExtractionPattern.gridx = 0;
					gbc_lblExtractionPattern.gridy = 2;
					editFields.add(lblExtractionPattern, gbc_lblExtractionPattern);
				}
				{
					JScrollPane extractScrollPane = new JScrollPane();
					extractScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					GridBagConstraints gbc_extractScrollPane = new GridBagConstraints();
					gbc_extractScrollPane.fill = GridBagConstraints.BOTH;
					gbc_extractScrollPane.gridwidth = 5;
					gbc_extractScrollPane.gridx = 1;
					gbc_extractScrollPane.gridy = 2;
					editFields.add(extractScrollPane, gbc_extractScrollPane);
					{
						dataExtractionPatField = new JTextArea();
						dataExtractionPatField.setToolTipText("<html>\r\nUse a Regex Pattern to describe the line format of your log files. The Pattern must include capturing groups. Those will be used for extracting data from the file.\r\n<p>\r\nA decription of the Regex Syntax can be found at http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html\r\n</html>");
						extractScrollPane.setViewportView(dataExtractionPatField);
						dataExtractionPatField.setRows(4);
						dataExtractionPatField.setLineWrap(true);
						PatternInputVerifier inputVerifier = new PatternInputVerifier(false);
						dataExtractionPatField.setInputVerifier(inputVerifier);
						addVerifier(new VerifyingPart(dataExtractionPatField, inputVerifier));
					}
				}
			}
			{
				Component verticalStrut = Box.createVerticalStrut(12);
				dataExtractionPanel.add(verticalStrut);
			}
			{
				errorText = new JLabel("");
				errorText.setForeground(Color.RED);
				dataExtractionPanel.add(errorText);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			add(buttonPane, BorderLayout.SOUTH);
			{
				btnCreateNew = new JButton("Create New");
				btnCreateNew.setToolTipText("Create a new, empty Log-File-Configuration entry.");
				btnCreateNew.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						saveLoadedParser();
						ConfiguredLogParser<?,?> logParser = fileTypeDesc.createParser("New Parser");
						logParser.setEditable(true);
						int newRowIdx = parserConfigModel.addParser(logParser);
						logParserTable.getSelectionModel().setSelectionInterval(newRowIdx, newRowIdx);
						isNewParser = true;
						editable = true;
						enableComponents();
					}
				});
				{
					btnLoadFromFile = new ConfigFileOpenButton(this, "Load From File", new ConfigFileAction() {
						
						@Override
						public void execConfigFileOperation(File selectedFile) throws Exception {
							int before = parserConfigModel.getRowCount();
							config.loadConfiguration(selectedFile);
							int after = parserConfigModel.getRowCount();
							if (after > before) {
								parserConfigModel.fireTableRowsInserted(before, after - 1);
							}
						}
					}, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							saveLoadedParser();
						}
					});
					btnLoadFromFile.setToolTipText("Load Log-File-Parser configurations from a LogTracker Config-File.");
					buttonPane.add(btnLoadFromFile);
				}
				{
					btnSave = new ConfigFileSaveButton(this, "Save to File", new ConfigFileAction() {
						
						@Override
						public void execConfigFileOperation(File selectedFile) throws Exception {
							safeConfigToFile(selectedFile);
						}

					}, new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							saveLoadedParser();
						}
					});
					btnSave.setToolTipText("Save the selected Log-File-Configurations in a Log-Tracker Configuration File.");
					buttonPane.add(btnSave);
				}
				buttonPane.add(btnCreateNew);
			}
			{
				btnCopy = new JButton("Copy");
				btnCopy.setToolTipText("Copy an existing Log-File-Configuration to a new editable entry.");
				btnCopy.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveLoadedParser();
						int selectedRow = logParserTable.getSelectedRow();
						LogParser<?> oldParser = parserConfigModel.getParser(selectedRow);
						ConfiguredLogParser<?,?> newParser = (ConfiguredLogParser<?,?>) oldParser.clone();
						newParser.setName(oldParser.getName() + " - Copy");
						int newRowIdx = parserConfigModel.addParser(newParser);
						logParserTable.getSelectionModel().setSelectionInterval(newRowIdx, newRowIdx);
						editable = true;
						newParser.setEditable(true);
						enableComponents();
					}
				});
				buttonPane.add(btnCopy);
			}
			{
				btnDelete = new JButton("Delete");
				btnDelete.setToolTipText("Delete the selected Log-File-Configurations.");
				btnDelete.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int selectedRow = logParserTable.getSelectedRow();
						parserConfigModel.deleteRow(selectedRow);
						parserConfigModel.fireTableRowsDeleted(selectedRow, selectedRow);
						enableComponents();
						removeErrorMarks();
					}
				});
				buttonPane.add(btnDelete);
			}
			{
				okButton = new JButton("OK");
				okButton.setToolTipText("Activate the Configuration Changes.");
				okButton.addActionListener(submitAction);
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setToolTipText("Undo all configuration edits.");
				cancelButton.addActionListener(e ->dialog.setVisible(false));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		config = new Configuration();
		config.registerModule(this);
		enableComponents();
	}
	
	public void setLogFileTypeList(List<FileTypeDescriptor<?,?>> list) {
		for (FileTypeDescriptor<?,?> typeDescriptor : list) {
			logFileTypeComboModel.addElement(typeDescriptor);
		}
		logFileTypeCombo.setSelectedIndex(0);
		setLogFileType();
		enableDetailFields(false);
		logParserTable.getSelectionModel().setSelectionInterval(0, 0);
	}

	private void setLogFileType() {
		fileTypeDesc = (FileTypeDescriptor<?,?>)logFileTypeCombo.getSelectedItem();
		extractionFields = fileTypeDesc.createExtractionFieldPanel(this);
		if (dataExtractionPanel.getComponentCount() == 4) {
			dataExtractionPanel.remove(2);
		}
		dataExtractionPanel.add((Component) extractionFields, 2);
		dataExtractionPanel.revalidate();
	}

	@SuppressWarnings("unchecked")
	protected void saveLoadedParser() {
		if (loadedParser != null) {
			loadedParser.setIncludeLines(rdbtnIncluded.isSelected());
			loadedParser.setIncludeContaining(rdbtnContaining.isSelected());
			String incPat = inclusionPattern.getText();
			loadedParser.setIncludeExcludePattern(incPat != null && incPat.length() > 0 ?  Pattern.compile(incPat) : null);
			loadedParser.setLinePattern(Pattern.compile(dataExtractionPatField.getText()));
			extractionFields.saveLoadedParser(loadedParser);
			loadedParser.setEditable(editable);
		}
	}

	private void enableDetailFields(boolean b) {
		inclusionPattern.setEnabled(b);
		dataExtractionPatField.setEnabled(b);
		if (extractionFields != null)
			extractionFields.enableDetailFields(b);
	}

	@SuppressWarnings("unchecked")
	private void loadEditingFields(ConfiguredLogParser<?,?> logParser) {
		for (int i = 0; i < logFileTypeCombo.getItemCount(); i++) {
			if (logFileTypeCombo.getItemAt(i) == logParser.getLogFileTypeDescriptor()) {
				logFileTypeCombo.setSelectedIndex(i);
				extractionFields = ((FileTypeDescriptor<?,?>) logFileTypeCombo.getSelectedItem()).createExtractionFieldPanel(this);
				break;
			}
		}
		(logParser.isIncludeLines() ? rdbtnIncluded : rdbtnExcluded).setSelected(true);
		(logParser.isIncludeContaining() ? rdbtnContaining : rdbtnWith).setSelected(true);
		inclusionPattern.setText(logParser.getIncludeExcludePattern() != null ? logParser.getIncludeExcludePattern().pattern() : null);
		dataExtractionPatField.setText(logParser.getLinePattern() != null ? logParser.getLinePattern().pattern() : null);
		extractionFields.loadEditingFields(logParser);
		loadedParser = logParser;
		editable = logParser.isEditable();
	}

	private void removeErrorMarks() {
		inclusionPattern.setBackground(standardBackgroundColor);
		dataExtractionPatField.setBackground(standardBackgroundColor);
		extractionFields.removeErrorMarks();
		setError(null);
	}
	
	/* (non-Javadoc)
	 * @see org.sper.logtracker.parserconf.ConfigurationSubPanel#setError(java.lang.String)
	 */
	@Override
	public void setError(String txt) {
		errorText.setText(txt);
		enableComponents();
	}
	
	private boolean inError() {
		return errorText.getText() != null && !errorText.getText().isEmpty();
	}
	
	void addVerifier(VerifyingPart vpart) {
		verifierList.add(vpart);
	}
	
	/* (non-Javadoc)
	 * @see org.sper.logtracker.parserconf.ConfigurationSubPanel#verifyFormDataIsValid()
	 */
	@Override
	public boolean verifyFormDataIsValid() {
		boolean valid = true;
		for (VerifyingPart part : verifierList)
			valid &= part.verify();
		valid &= extractionFields.verifyFormDataIsValid();
		if (!valid)
			setError("Configuration is not valid");
		return valid;
	}
	
	private void enableComponents() {
		boolean singleSelected = logParserTable.getSelectedRowCount() == 1;
		boolean multiSelect = logParserTable.getSelectedRowCount() > 1;
		boolean inError = inError();
		btnCopy.setEnabled(!isNewParser && !inError && singleSelected);
		btnCreateNew.setEnabled(!inError);
		btnDelete.setEnabled(editable && (singleSelected || multiSelect));
		okButton.setEnabled(true);
		btnLoadFromFile.setEnabled(!inError && !isNewParser);
		boolean editableConfigs = logParserTable.getSelectedRowCount() > 0;
		for (int rowIdx : logParserTable.getSelectedRows()) {
			ConfiguredLogParser<?,?> logParser = parserConfigModel.getParser(rowIdx);
			editableConfigs &= logParser.isEditable();
		}
		btnSave.setEnabled(editableConfigs);
		logFileTypeCombo.setEnabled(isNewParser);
		enableDetailFields(singleSelected && editable);
	}

	@Override
	public String getCompKey() {
		return ConfiguredLogParser.CONFIG_NAME;
	}
	
	@Override
	public void applyConfig(Serializable cfg) {
		@SuppressWarnings("unchecked")
		ArrayList<ConfiguredLogParser<?,?>> logParserList = (ArrayList<ConfiguredLogParser<?,?>>) cfg;
		parserConfigModel.addParsers(logParserList);
	}
	
	public Serializable getConfig() {
		ArrayList<ConfiguredLogParser<?,?>> configList = new ArrayList<ConfiguredLogParser<?,?>>();
		for (int row : logParserTable.getSelectedRows()) {
			ConfiguredLogParser<?,?> parserConfig = parserConfigModel.getParser(row);
			if (parserConfig.isEditable()) {
				configList.add(parserConfig);
			}
		}
		return configList;
	}

	protected void safeConfigToFile(File selectedFile) {
		LogTrackerConfig config = new LogTrackerConfig();
		GlobalConfig global = new GlobalConfig();
		config.setGlobal(global);
		for (int row : logParserTable.getSelectedRows()) {
			ConfiguredLogParser<?,?> parserConfig = parserConfigModel.getParser(row);
			if (parserConfig.isEditable()) {
				global.getLogParser().add(parserConfig);
			}
		}
		new XMLConfigSupport().saveXMLConfig(selectedFile, config);
	}

	@Override
	public boolean isDynamicModule() {
		return false;
	}

	public static Color getStandardBackgroundColor() {
		return standardBackgroundColor;
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.parserconf.ConfigurationSubPanel#submit()
	 */
	@Override
	public void submit() {
		if (!inError()) {
			saveLoadedParser();
			parserConfigModel.saveInSelectionModel();
		}
	}

	@Override
	public JButton defaultButton() {
		return okButton;
	}
	
}
