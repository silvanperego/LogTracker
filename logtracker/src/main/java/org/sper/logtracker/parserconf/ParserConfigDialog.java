package org.sper.logtracker.parserconf;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
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
import javax.swing.text.JTextComponent;

import org.sper.logtracker.config.ConfigFileAction;
import org.sper.logtracker.config.ConfigFileOpenButton;
import org.sper.logtracker.config.ConfigFileSaveButton;
import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.config.ConfigurationAware;
import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogParser;

public class ParserConfigDialog extends JDialog implements ConfigurationAware {
	
	private class VerifyingPart {
		private JComponent component;
		private InputVerifier inputVerifier;

		VerifyingPart(JComponent component, InputVerifier inputVerifier) {
			super();
			this.component = component;
			this.inputVerifier = inputVerifier;
		}
		
		boolean verify() {
			return inputVerifier.verify(component);
		}
	}

	private class PatternInputVerifier extends InputVerifier {
		
		private boolean nullable;

		PatternInputVerifier(boolean nullable) {
			this.nullable = nullable;
		}
		
		@Override
		public boolean verify(JComponent input) {
			String text = ((JTextComponent) input).getText();
			if (text != null && text.length() > 0) {
				try {
					Pattern.compile(text);
				} catch (PatternSyntaxException e) {
					input.setBackground(Color.ORANGE);
					setError("Not a valid Java Regex pattern");
					return false;
				}
			} else if (!nullable) {
				input.setBackground(Color.ORANGE);
				setError("Pattern is mandatory");
				return false;
			}
			input.setBackground(standardBackgroundCol);
			setError(null);
			return true;
		}
	}

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTable logParserTable;
	private JTextField inclusionPattern;
	private final ButtonGroup inclusionButtonGroup = new ButtonGroup();
	private final ButtonGroup inclExclButtonGroup = new ButtonGroup();
	private JTextArea dataExtractionPatField;
	private JTextField serviceExcludeField;
	private JTextField conversionFactorField;
	private ParserConfigModel parserConfigModel;
	private JComboBox occTimeGroupCombo;
	private JComboBox serviceComboBox;
	private JComboBox executionTimeBox;
	private JComboBox userGroupBox;
	private JTextField occTimeFormatString;
	private JRadioButton rdbtnWith;
	private JRadioButton rdbtnContaining;
	private JRadioButton rdbtnIncluded;
	private JRadioButton rdbtnExcluded;
	private JLabel errorText;
	private ConfiguredLogParser loadedParser;
	private JButton okButton;
	private Color standardBackgroundCol;
	private boolean isNewParser = false;
	private JButton btnCopy;
	private JButton btnCreateNew;
	private List<VerifyingPart> verifierList = new ArrayList<VerifyingPart>();
	private JButton btnDelete;
	private boolean editable;
	private JButton btnSave;
	private JButton btnLoadFromFile;
	private Configuration config;
	private JTextField occTimeLanguage;

	/**
	 * Create the dialog.
	 * @param parserSelectionModel 
	 */
	public ParserConfigDialog(ParserSelectionModel parserSelectionModel) {
		setTitle("Log-Files Parser Configuration");
		setModalityType(ModalityType.APPLICATION_MODAL);
		setBounds(100, 100, 795, 681);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
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
								if (JOptionPane.showConfirmDialog(ParserConfigDialog.this, "This Configuration is not valid and will be discarded!", "Confirm Discard", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
									int selRowIdx = logParserTable.getSelectedRow();
									parserConfigModel.deleteRow(selRowIdx);
									isNewParser = false;
									super.changeSelection(rowIndex, columnIndex, toggle, extend);
								}
							} else if (inError()) {
								if (JOptionPane.showConfirmDialog(ParserConfigDialog.this, "Edits for this Configuration are discarded!", "Confirm Discard", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
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
						saveLoadedParser();
						loadedParser = null;
						int[] selectedRows = logParserTable.getSelectedRows();
						if (selectedRows.length == 1)
							loadEditingFields(parserConfigModel.getParser(selectedRows[0]));
						removeErrorMarks();
						enableComponents();
					}
				});
				logParserTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
				logParserTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
				parserConfigModel = new ParserConfigModel(parserSelectionModel);
				parserConfigModel.loadFromSelectionModel();
				logParserTable.setModel(parserConfigModel);
				logParserTable.getColumnModel().getColumn(0).setResizable(false);
				logParserTable.getColumnModel().getColumn(0).setPreferredWidth(527);
				logParserTable.getColumnModel().getColumn(0).setCellRenderer(new ParserTableCellRenderer());
				scrollPane.setViewportView(logParserTable);
				logParserTable.setAlignmentX(Component.LEFT_ALIGNMENT);
			}
		}
		{
			JPanel dataExtractionPanel = new JPanel();
			contentPanel.add(dataExtractionPanel, BorderLayout.SOUTH);
			dataExtractionPanel.setLayout(new BoxLayout(dataExtractionPanel, BoxLayout.Y_AXIS));
			{
				JPanel editFields = new JPanel();
				dataExtractionPanel.add(editFields);
				editFields.setAlignmentX(Component.LEFT_ALIGNMENT);
				GridBagLayout gbl_editFields = new GridBagLayout();
				gbl_editFields.columnWidths = new int[]{0, 0, 0, 0, 0, 0};
				gbl_editFields.rowHeights = new int[]{0, 0};
				gbl_editFields.columnWeights = new double[]{0.0, 0.0, 0.0, 1.0, 0.0, 0.0};
				gbl_editFields.rowWeights = new double[]{1.0, 0.0};
				editFields.setLayout(gbl_editFields);
				{
					JLabel lblLines = new JLabel("Lines");
					GridBagConstraints gbc_lblLines = new GridBagConstraints();
					gbc_lblLines.anchor = GridBagConstraints.WEST;
					gbc_lblLines.insets = new Insets(0, 0, 5, 5);
					gbc_lblLines.gridx = 0;
					gbc_lblLines.gridy = 0;
					editFields.add(lblLines, gbc_lblLines);
				}
				{
					JPanel inclusionTypePanel = new JPanel();
					GridBagConstraints gbc_inclusionTypePanel = new GridBagConstraints();
					gbc_inclusionTypePanel.insets = new Insets(0, 0, 5, 5);
					gbc_inclusionTypePanel.anchor = GridBagConstraints.WEST;
					gbc_inclusionTypePanel.gridx = 1;
					gbc_inclusionTypePanel.gridy = 0;
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
					gbc_lblPattern.gridy = 0;
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
					gbc_inclusionPattern.gridy = 0;
					PatternInputVerifier inputVerifier = new PatternInputVerifier(true);
					inclusionPattern.setInputVerifier(inputVerifier);
					verifierList.add(new VerifyingPart(inclusionPattern, inputVerifier));
					standardBackgroundCol = inclusionPattern.getBackground();
					editFields.add(inclusionPattern, gbc_inclusionPattern);
				}
				{
					JLabel lblAre = new JLabel("are");
					GridBagConstraints gbc_lblAre = new GridBagConstraints();
					gbc_lblAre.anchor = GridBagConstraints.WEST;
					gbc_lblAre.insets = new Insets(0, 0, 5, 5);
					gbc_lblAre.gridx = 4;
					gbc_lblAre.gridy = 0;
					editFields.add(lblAre, gbc_lblAre);
				}
				{
					JPanel panel = new JPanel();
					GridBagConstraints gbc_panel = new GridBagConstraints();
					gbc_panel.anchor = GridBagConstraints.WEST;
					gbc_panel.insets = new Insets(0, 0, 5, 0);
					gbc_panel.gridx = 5;
					gbc_panel.gridy = 0;
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
					gbc_lblExtractionPattern.insets = new Insets(0, 0, 5, 5);
					gbc_lblExtractionPattern.gridx = 0;
					gbc_lblExtractionPattern.gridy = 1;
					editFields.add(lblExtractionPattern, gbc_lblExtractionPattern);
				}
				{
					JScrollPane extractScrollPane = new JScrollPane();
					extractScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
					GridBagConstraints gbc_extractScrollPane = new GridBagConstraints();
					gbc_extractScrollPane.insets = new Insets(0, 0, 5, 0);
					gbc_extractScrollPane.fill = GridBagConstraints.BOTH;
					gbc_extractScrollPane.gridwidth = 5;
					gbc_extractScrollPane.gridx = 1;
					gbc_extractScrollPane.gridy = 1;
					editFields.add(extractScrollPane, gbc_extractScrollPane);
					{
						dataExtractionPatField = new JTextArea();
						dataExtractionPatField.setToolTipText("<html>\r\nUse a Regex Pattern to describe the line format of your log files. The Pattern must include capturing groups. Those will be used for extracting data from the file.\r\n<p>\r\nA decription of the Regex Syntax can be found at http://docs.oracle.com/javase/6/docs/api/java/util/regex/Pattern.html\r\n</html>");
						extractScrollPane.setViewportView(dataExtractionPatField);
						dataExtractionPatField.setRows(4);
						dataExtractionPatField.setLineWrap(true);
						PatternInputVerifier inputVerifier = new PatternInputVerifier(false);
						dataExtractionPatField.setInputVerifier(inputVerifier);
						verifierList.add(new VerifyingPart(dataExtractionPatField, inputVerifier));
					}
				}
			}
			{
				Component verticalStrut = Box.createVerticalStrut(12);
				dataExtractionPanel.add(verticalStrut);
			}
			{
				JPanel extractionFields = new JPanel();
				extractionFields.setAlignmentX(Component.LEFT_ALIGNMENT);
				dataExtractionPanel.add(extractionFields);
				GridBagLayout gbl_extractionFields = new GridBagLayout();
				gbl_extractionFields.columnWidths = new int[]{0, 0, 0, 0, 0};
				gbl_extractionFields.rowHeights = new int[]{0, 0, 0};
				gbl_extractionFields.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
				gbl_extractionFields.rowWeights = new double[]{0.0, 0.0, 0.0};
				extractionFields.setLayout(gbl_extractionFields);
				{
					JLabel lblOccurenceTimeGroup = new JLabel("Occurence Time Group Index:");
					GridBagConstraints gbc_lblOccurenceTimeGroup = new GridBagConstraints();
					gbc_lblOccurenceTimeGroup.insets = new Insets(0, 0, 5, 5);
					gbc_lblOccurenceTimeGroup.anchor = GridBagConstraints.EAST;
					gbc_lblOccurenceTimeGroup.gridx = 0;
					gbc_lblOccurenceTimeGroup.gridy = 0;
					extractionFields.add(lblOccurenceTimeGroup, gbc_lblOccurenceTimeGroup);
				}
				{
					occTimeGroupCombo = new JComboBox();
					occTimeGroupCombo.setToolTipText("the capturing group index of the group containing the service call occurrence time");
					occTimeGroupCombo.setModel(new DefaultComboBoxModel(new Integer[] {1, 2, 3, 4}));
					GridBagConstraints gbc_occTimeGroupCombo = new GridBagConstraints();
					gbc_occTimeGroupCombo.insets = new Insets(0, 0, 5, 5);
					gbc_occTimeGroupCombo.anchor = GridBagConstraints.WEST;
					gbc_occTimeGroupCombo.gridx = 1;
					gbc_occTimeGroupCombo.gridy = 0;
					extractionFields.add(occTimeGroupCombo, gbc_occTimeGroupCombo);
				}
				{
					Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
					GridBagConstraints gbc_rigidArea = new GridBagConstraints();
					gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
					gbc_rigidArea.gridx = 2;
					gbc_rigidArea.gridy = 0;
					extractionFields.add(rigidArea, gbc_rigidArea);
				}
				{
					JLabel lblOccurrenceTimeFormat = new JLabel("Occurrence Time Format String:");
					GridBagConstraints gbc_lblOccurrenceTimeFormat = new GridBagConstraints();
					gbc_lblOccurrenceTimeFormat.insets = new Insets(0, 0, 5, 5);
					gbc_lblOccurrenceTimeFormat.gridx = 3;
					gbc_lblOccurrenceTimeFormat.gridy = 0;
					extractionFields.add(lblOccurrenceTimeFormat, gbc_lblOccurrenceTimeFormat);
				}
				{
					InputVerifier inputVerifier = new InputVerifier() {
						
						@Override
						public boolean verify(JComponent input) {
							String text = ((JTextField) input).getText();
							if (text != null && text.length() > 0) {
								try {
									new SimpleDateFormat(text);
								} catch (IllegalArgumentException e) {
									input.setBackground(Color.ORANGE);
									setError("Not a valid SimpleDateFormat pattern");
									return false;
								}
							} else {
								input.setBackground(Color.ORANGE);
								setError("Date Format is mandatory");
								return false;
							}
							input.setBackground(standardBackgroundCol);
							setError(null);
							return true;
						}
					};
					{
						JPanel occTimePanel = new JPanel();
						GridBagConstraints gbc_occTimePanel = new GridBagConstraints();
						gbc_occTimePanel.fill = GridBagConstraints.BOTH;
						gbc_occTimePanel.insets = new Insets(0, 0, 5, 0);
						gbc_occTimePanel.gridx = 4;
						gbc_occTimePanel.gridy = 0;
						extractionFields.add(occTimePanel, gbc_occTimePanel);
						occTimePanel.setLayout(new BoxLayout(occTimePanel, BoxLayout.X_AXIS));
						occTimeFormatString = new JTextField();
						occTimeFormatString.setColumns(30);
						occTimePanel.add(occTimeFormatString);
						occTimeFormatString.setToolTipText("The date format of the occurrence time of the service call. The format must be specified as java - SimpleDateFormat pattern, as defined at http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDataFormat.html.");
						occTimeFormatString.setInputVerifier(inputVerifier);
						verifierList.add(new VerifyingPart(occTimeFormatString, inputVerifier));
						{
							JLabel lblLanguage = new JLabel("Language:");
							occTimePanel.add(lblLanguage);
						}
						{
							occTimeLanguage = new JTextField();
							occTimePanel.add(occTimeLanguage);
							occTimeLanguage.setColumns(3);
							occTimeLanguage.setToolTipText("The interpretation language for the occurrence time. (Only necessary, if months are specified as text.");
//							occTimeLanguage.setInputVerifier(new InputVerifier() {
//								
//								@Override
//								public boolean verify(JComponent input) {
//									String text = ((JTextField) input).getText();
//									if (text != null && text.length() > 0 && Locale.forLanguageTag(text) == null) {
//										input.setBackground(Color.ORANGE);
//										setError("Not a valid language code");
//										input.setBackground(Color.ORANGE);
//										return false;
//									}
//									input.setBackground(standardBackgroundCol);
//									return true;
//								}
//							});
						}
					}
				}
				{
					JLabel lblServiceNameGroup = new JLabel("Service Name Group Index:");
					GridBagConstraints gbc_lblServiceNameGroup = new GridBagConstraints();
					gbc_lblServiceNameGroup.anchor = GridBagConstraints.WEST;
					gbc_lblServiceNameGroup.insets = new Insets(0, 0, 5, 5);
					gbc_lblServiceNameGroup.gridx = 0;
					gbc_lblServiceNameGroup.gridy = 1;
					extractionFields.add(lblServiceNameGroup, gbc_lblServiceNameGroup);
				}
				{
					serviceComboBox = new JComboBox();
					serviceComboBox.setToolTipText("the capturing group index of the group containing the service name");
					serviceComboBox.setModel(new DefaultComboBoxModel(new Integer[] {1, 2, 3, 4}));
					GridBagConstraints gbc_serviceComboBox = new GridBagConstraints();
					gbc_serviceComboBox.anchor = GridBagConstraints.WEST;
					gbc_serviceComboBox.insets = new Insets(0, 0, 5, 5);
					gbc_serviceComboBox.gridx = 1;
					gbc_serviceComboBox.gridy = 1;
					extractionFields.add(serviceComboBox, gbc_serviceComboBox);
				}
				{
					Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
					GridBagConstraints gbc_rigidArea = new GridBagConstraints();
					gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
					gbc_rigidArea.gridx = 2;
					gbc_rigidArea.gridy = 1;
					extractionFields.add(rigidArea, gbc_rigidArea);
				}
				{
					JLabel lblExcludeServices = new JLabel("Exclude Services");
					GridBagConstraints gbc_lblExcludeServices = new GridBagConstraints();
					gbc_lblExcludeServices.anchor = GridBagConstraints.WEST;
					gbc_lblExcludeServices.insets = new Insets(0, 0, 5, 5);
					gbc_lblExcludeServices.gridx = 3;
					gbc_lblExcludeServices.gridy = 1;
					extractionFields.add(lblExcludeServices, gbc_lblExcludeServices);
				}
				{
					serviceExcludeField = new JTextField();
					serviceExcludeField.setToolTipText("A comma separated list of service names, specifying services that should be ignored by the parser.");
					GridBagConstraints gbc_serviceExcludeField = new GridBagConstraints();
					gbc_serviceExcludeField.insets = new Insets(0, 0, 5, 0);
					gbc_serviceExcludeField.fill = GridBagConstraints.HORIZONTAL;
					gbc_serviceExcludeField.gridx = 4;
					gbc_serviceExcludeField.gridy = 1;
					extractionFields.add(serviceExcludeField, gbc_serviceExcludeField);
				}
				{
					JLabel lblExecutionTimeGroup = new JLabel("Execution Time Group Index:");
					GridBagConstraints gbc_lblExecutionTimeGroup = new GridBagConstraints();
					gbc_lblExecutionTimeGroup.anchor = GridBagConstraints.WEST;
					gbc_lblExecutionTimeGroup.insets = new Insets(0, 0, 5, 5);
					gbc_lblExecutionTimeGroup.gridx = 0;
					gbc_lblExecutionTimeGroup.gridy = 2;
					extractionFields.add(lblExecutionTimeGroup, gbc_lblExecutionTimeGroup);
				}
				{
					executionTimeBox = new JComboBox();
					executionTimeBox.setToolTipText("the capturing group index of the group containing the service execution (or response) time");
					executionTimeBox.setModel(new DefaultComboBoxModel(new Integer[] {1, 2, 3, 4}));
					GridBagConstraints gbc_executionTimeBox = new GridBagConstraints();
					gbc_executionTimeBox.anchor = GridBagConstraints.WEST;
					gbc_executionTimeBox.insets = new Insets(0, 0, 5, 5);
					gbc_executionTimeBox.gridx = 1;
					gbc_executionTimeBox.gridy = 2;
					extractionFields.add(executionTimeBox, gbc_executionTimeBox);
				}
				{
					Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
					GridBagConstraints gbc_rigidArea = new GridBagConstraints();
					gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
					gbc_rigidArea.gridx = 2;
					gbc_rigidArea.gridy = 2;
					extractionFields.add(rigidArea, gbc_rigidArea);
				}
				{
					JLabel lblConversionFactor = new JLabel("Conversion Factor");
					GridBagConstraints gbc_lblConversionFactor = new GridBagConstraints();
					gbc_lblConversionFactor.anchor = GridBagConstraints.WEST;
					gbc_lblConversionFactor.insets = new Insets(0, 0, 5, 5);
					gbc_lblConversionFactor.gridx = 3;
					gbc_lblConversionFactor.gridy = 2;
					extractionFields.add(lblConversionFactor, gbc_lblConversionFactor);
				}
				{
					conversionFactorField = new JFormattedTextField();
					conversionFactorField.setToolTipText("Service execution times will be multiplied by this factor before being handed over to the subsequent modules. The factor should be sized such, that the resulting unit is in seconds.");
					GridBagConstraints gbc_conversionFactorField = new GridBagConstraints();
					gbc_conversionFactorField.insets = new Insets(0, 0, 5, 0);
					gbc_conversionFactorField.fill = GridBagConstraints.HORIZONTAL;
					gbc_conversionFactorField.gridx = 4;
					gbc_conversionFactorField.gridy = 2;
					InputVerifier inputVerifier = new InputVerifier() {
						
						private final Pattern floatPat = Pattern.compile("\\d*(?:\\.\\d*)?");
						
						@Override
						public boolean verify(JComponent input) {
							boolean result = false;
							String text = ((JTextField) input).getText();
							if (text != null && text.length() > 0) {
								result = floatPat.matcher(text).matches();
								setError(result ? null : "Must be a floating point number (consisting of digits and a decmal dot only)");
							} else
								setError("Conversion Factor is mandatory!");
							conversionFactorField.setBackground(result ? standardBackgroundCol : Color.ORANGE);
							return result;
						}
					};
					conversionFactorField.setInputVerifier(inputVerifier);
					verifierList.add(new VerifyingPart(conversionFactorField, inputVerifier));
					extractionFields.add(conversionFactorField, gbc_conversionFactorField);
				}
				{
					JLabel lblUserGroup = new JLabel("User Group Index:");
					GridBagConstraints gbc_UserGroup = new GridBagConstraints();
					gbc_UserGroup.anchor = GridBagConstraints.WEST;
					gbc_UserGroup.insets = new Insets(0, 0, 0, 5);
					gbc_UserGroup.gridx = 0;
					gbc_UserGroup.gridy = 3;
					extractionFields.add(lblUserGroup, gbc_UserGroup);
				}
				{
					userGroupBox = new JComboBox();
					userGroupBox.setToolTipText("the capturing group index of the group containing the user that called the service. This entry is optional.");
					userGroupBox.setModel(new DefaultComboBoxModel(new Integer[] {null, 1, 2, 3, 4}));
					GridBagConstraints gbc_userGroupBox = new GridBagConstraints();
					gbc_userGroupBox.anchor = GridBagConstraints.WEST;
					gbc_userGroupBox.insets = new Insets(0, 0, 0, 5);
					gbc_userGroupBox.gridx = 1;
					gbc_userGroupBox.gridy = 3;
					extractionFields.add(userGroupBox, gbc_userGroupBox);
				}
				{
					Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
					GridBagConstraints gbc_rigidArea = new GridBagConstraints();
					gbc_rigidArea.insets = new Insets(0, 0, 0, 5);
					gbc_rigidArea.gridx = 2;
					gbc_rigidArea.gridy = 3;
					extractionFields.add(rigidArea, gbc_rigidArea);
				}
			}
			{
				errorText = new JLabel("");
				errorText.setForeground(Color.RED);
				dataExtractionPanel.add(errorText);
			}
			enableDetailFields(false);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				btnCreateNew = new JButton("Create New");
				btnCreateNew.setToolTipText("Create a new, empty Log-File-Configuration entry.");
				btnCreateNew.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent e) {
						saveLoadedParser();
						ConfiguredLogParser logParser = new ConfiguredLogParser("New Parser");
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
							config.safeToFile(selectedFile);
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
						LogParser oldParser = parserConfigModel.getParser(selectedRow);
						ConfiguredLogParser newParser = (ConfiguredLogParser) oldParser.clone();
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
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveLoadedParser();
						parserConfigModel.saveInSelectionModel();
						setVisible(false);
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setToolTipText("Undo all configuration edits.");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		config = new Configuration();
		config.registerModule(this);
		enableComponents();
	}

	protected void saveLoadedParser() {
		if (loadedParser != null) {
			loadedParser.setIncludeLines(rdbtnIncluded.isSelected());
			loadedParser.setIncludeContaining(rdbtnContaining.isSelected());
			String incPat = inclusionPattern.getText();
			loadedParser.setIncludeExcludePattern(incPat != null && incPat.length() > 0 ?  Pattern.compile(incPat) : null);
			loadedParser.setLinePattern(Pattern.compile(dataExtractionPatField.getText()));
			loadedParser.setOccTimeIdx((Integer) occTimeGroupCombo.getSelectedItem());
			loadedParser.setOccTimeFormatString(occTimeFormatString.getText());
			loadedParser.setOccTimeLanguage(occTimeLanguage.getText());
			loadedParser.setServiceIdx((Integer) serviceComboBox.getSelectedItem());
			loadedParser.setIgnoreServiceList(serviceExcludeField.getText());
			loadedParser.setResponseTimeIdx((Integer) executionTimeBox.getSelectedItem());
			loadedParser.setResponseTimeFactor(Double.parseDouble(conversionFactorField.getText()));
			loadedParser.setUserIdx((Integer) userGroupBox.getSelectedItem());
			loadedParser.setEditable(editable);
		}
	}

	private void enableDetailFields(boolean b) {
		inclusionPattern.setEnabled(b);
		dataExtractionPatField.setEnabled(b);
		occTimeGroupCombo.setEnabled(b);
		occTimeFormatString.setEnabled(b);
		occTimeLanguage.setEnabled(b);
		serviceComboBox.setEnabled(b);
		serviceExcludeField.setEnabled(b);
		executionTimeBox.setEnabled(b);
		conversionFactorField.setEnabled(b);
		userGroupBox.setEnabled(b);
	}

	protected void loadEditingFields(ConfiguredLogParser logParser) {
		(logParser.isIncludeLines() ? rdbtnIncluded : rdbtnExcluded).setSelected(true);
		(logParser.isIncludeContaining() ? rdbtnContaining : rdbtnWith).setSelected(true);
		inclusionPattern.setText(logParser.getIncludeExcludePattern() != null ? logParser.getIncludeExcludePattern().pattern() : null);
		dataExtractionPatField.setText(logParser.getLinePattern() != null ? logParser.getLinePattern().pattern() : null);
		occTimeGroupCombo.setSelectedItem(logParser.getOccTimeIdx());
		occTimeFormatString.setText(logParser.getOccTimeFormatString());
		occTimeLanguage.setText(logParser.getOccTimeLanguage());
		serviceComboBox.setSelectedItem(logParser.getServiceIdx());
		serviceExcludeField.setText(logParser.getIgnoreServiceList());
		executionTimeBox.setSelectedItem(logParser.getResponseTimeIdx());
		conversionFactorField.setText(Double.toString(logParser.getResponseTimeFactor()));
		loadedParser = logParser;
		userGroupBox.setSelectedItem(logParser.getUserIdx());
		editable = logParser.isEditable();
	}

	public void removeErrorMarks() {
		inclusionPattern.setBackground(standardBackgroundCol);
		dataExtractionPatField.setBackground(standardBackgroundCol);
		occTimeFormatString.setBackground(standardBackgroundCol);
		occTimeLanguage.setBackground(standardBackgroundCol);
		serviceExcludeField.setBackground(standardBackgroundCol);
		conversionFactorField.setBackground(standardBackgroundCol);
		setError(null);
	}
	
	private void setError(String txt) {
		errorText.setText(txt);
		enableComponents();
	}
	
	private boolean inError() {
		return errorText.getText() != null && !errorText.getText().isEmpty();
	}
	
	private boolean verifyFormDataIsValid() {
		boolean valid = true;
		for (VerifyingPart part : verifierList)
			valid &= part.verify();
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
		okButton.setEnabled(!inError && !isNewParser);
		btnLoadFromFile.setEnabled(!inError && !isNewParser);
		boolean editableConfigs = logParserTable.getSelectedRowCount() > 0;
		for (int rowIdx : logParserTable.getSelectedRows()) {
			ConfiguredLogParser logParser = parserConfigModel.getParser(rowIdx);
			editableConfigs &= logParser.isEditable();
		}
		btnSave.setEnabled(editableConfigs);
		enableDetailFields(singleSelected && editable);
	}

	@Override
	public String getCompKey() {
		return ConfiguredLogParser.CONFIG_NAME;
	}
	
	@Override
	public void applyConfig(Serializable cfg) {
		@SuppressWarnings("unchecked")
		ArrayList<ConfiguredLogParser> logParserList = (ArrayList<ConfiguredLogParser>) cfg;
		parserConfigModel.addParsers(logParserList);
	}
	
	@Override
	public Serializable getConfig() {
		ArrayList<ConfiguredLogParser> configList = new ArrayList<ConfiguredLogParser>();
		for (int row : logParserTable.getSelectedRows()) {
			ConfiguredLogParser parserConfig = parserConfigModel.getParser(row);
			if (parserConfig.isEditable()) {
				configList.add(parserConfig);
			}
		}
		return configList;
	}
	
}
