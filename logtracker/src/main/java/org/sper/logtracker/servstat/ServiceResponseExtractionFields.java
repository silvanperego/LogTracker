package org.sper.logtracker.servstat;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;

public class ServiceResponseExtractionFields extends JPanel implements ExtractionFieldHandler {

	private static final long serialVersionUID = 1L;
	private JTextField conversionFactorField;
	private JComboBox executionTimeBox;
	private JTextField occTimeFormatString;
	private JComboBox occTimeGroupCombo;
	private JTextField occTimeLanguage;
	private JComboBox serviceComboBox;
	private JTextField serviceExcludeField;
	private JComboBox userGroupBox;
	private Color standardBackgroundCol;
	private InputVerifier occTimeVerifier;
	private InputVerifier conversionFactorVerifier;

	
	public ServiceResponseExtractionFields(final ParserConfigDialog configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_extractionFields.rowHeights = new int[]{0, 0, 0};
		gbl_extractionFields.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		gbl_extractionFields.rowWeights = new double[]{0.0, 0.0, 0.0};
		setLayout(gbl_extractionFields);
		{
			JLabel lblOccurenceTimeGroup = new JLabel("Occurence Time Group Index:");
			GridBagConstraints gbc_lblOccurenceTimeGroup = new GridBagConstraints();
			gbc_lblOccurenceTimeGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblOccurenceTimeGroup.anchor = GridBagConstraints.EAST;
			gbc_lblOccurenceTimeGroup.gridx = 0;
			gbc_lblOccurenceTimeGroup.gridy = 0;
			add(lblOccurenceTimeGroup, gbc_lblOccurenceTimeGroup);
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
			add(occTimeGroupCombo, gbc_occTimeGroupCombo);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = 0;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblOccurrenceTimeFormat = new JLabel("Occurrence Time Format String:");
			GridBagConstraints gbc_lblOccurrenceTimeFormat = new GridBagConstraints();
			gbc_lblOccurrenceTimeFormat.insets = new Insets(0, 0, 5, 5);
			gbc_lblOccurrenceTimeFormat.gridx = 3;
			gbc_lblOccurrenceTimeFormat.gridy = 0;
			add(lblOccurrenceTimeFormat, gbc_lblOccurrenceTimeFormat);
		}
		{
			occTimeVerifier = new InputVerifier() {
				
				@Override
				public boolean verify(JComponent input) {
					String text = ((JTextField) input).getText();
					if (text != null && text.length() > 0) {
						try {
							new SimpleDateFormat(text);
						} catch (IllegalArgumentException e) {
							input.setBackground(Color.ORANGE);
							configDialog.setError("Not a valid SimpleDateFormat pattern");
							return false;
						}
					} else {
						input.setBackground(Color.ORANGE);
						configDialog.setError("Date Format is mandatory");
						return false;
					}
					input.setBackground(standardBackgroundCol);
					configDialog.setError(null);
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
				add(occTimePanel, gbc_occTimePanel);
				occTimePanel.setLayout(new BoxLayout(occTimePanel, BoxLayout.X_AXIS));
				occTimeFormatString = new JTextField();
				occTimeFormatString.setColumns(30);
				standardBackgroundCol = occTimeFormatString.getBackground();
				occTimePanel.add(occTimeFormatString);
				occTimeFormatString.setToolTipText("The date format of the occurrence time of the service call. The format must be specified as java - SimpleDateFormat pattern, as defined at http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDataFormat.html.");
				occTimeFormatString.setInputVerifier(occTimeVerifier);
				{
					JLabel lblLanguage = new JLabel("Language:");
					occTimePanel.add(lblLanguage);
				}
				{
					occTimeLanguage = new JTextField();
					occTimePanel.add(occTimeLanguage);
					occTimeLanguage.setColumns(3);
					occTimeLanguage.setToolTipText("The interpretation language for the occurrence time. (Only necessary, if months are specified as text.");
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
			add(lblServiceNameGroup, gbc_lblServiceNameGroup);
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
			add(serviceComboBox, gbc_serviceComboBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = 1;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblExcludeServices = new JLabel("Exclude Services");
			GridBagConstraints gbc_lblExcludeServices = new GridBagConstraints();
			gbc_lblExcludeServices.anchor = GridBagConstraints.WEST;
			gbc_lblExcludeServices.insets = new Insets(0, 0, 5, 5);
			gbc_lblExcludeServices.gridx = 3;
			gbc_lblExcludeServices.gridy = 1;
			add(lblExcludeServices, gbc_lblExcludeServices);
		}
		{
			serviceExcludeField = new JTextField();
			serviceExcludeField.setToolTipText("A comma separated list of service names, specifying services that should be ignored by the parser.");
			GridBagConstraints gbc_serviceExcludeField = new GridBagConstraints();
			gbc_serviceExcludeField.insets = new Insets(0, 0, 5, 0);
			gbc_serviceExcludeField.fill = GridBagConstraints.HORIZONTAL;
			gbc_serviceExcludeField.gridx = 4;
			gbc_serviceExcludeField.gridy = 1;
			add(serviceExcludeField, gbc_serviceExcludeField);
		}
		{
			JLabel lblExecutionTimeGroup = new JLabel("Execution Time Group Index:");
			GridBagConstraints gbc_lblExecutionTimeGroup = new GridBagConstraints();
			gbc_lblExecutionTimeGroup.anchor = GridBagConstraints.WEST;
			gbc_lblExecutionTimeGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblExecutionTimeGroup.gridx = 0;
			gbc_lblExecutionTimeGroup.gridy = 2;
			add(lblExecutionTimeGroup, gbc_lblExecutionTimeGroup);
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
			add(executionTimeBox, gbc_executionTimeBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = 2;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblConversionFactor = new JLabel("Conversion Factor");
			GridBagConstraints gbc_lblConversionFactor = new GridBagConstraints();
			gbc_lblConversionFactor.anchor = GridBagConstraints.WEST;
			gbc_lblConversionFactor.insets = new Insets(0, 0, 5, 5);
			gbc_lblConversionFactor.gridx = 3;
			gbc_lblConversionFactor.gridy = 2;
			add(lblConversionFactor, gbc_lblConversionFactor);
		}
		{
			conversionFactorField = new JFormattedTextField();
			conversionFactorField.setToolTipText("Service execution times will be multiplied by this factor before being handed over to the subsequent modules. The factor should be sized such, that the resulting unit is in seconds.");
			GridBagConstraints gbc_conversionFactorField = new GridBagConstraints();
			gbc_conversionFactorField.insets = new Insets(0, 0, 5, 0);
			gbc_conversionFactorField.fill = GridBagConstraints.HORIZONTAL;
			gbc_conversionFactorField.gridx = 4;
			gbc_conversionFactorField.gridy = 2;
			conversionFactorVerifier = new InputVerifier() {
				
				private final Pattern floatPat = Pattern.compile("\\d*(?:\\.\\d*)?");
				
				@Override
				public boolean verify(JComponent input) {
					boolean result = false;
					String text = ((JTextField) input).getText();
					if (text != null && text.length() > 0) {
						result = floatPat.matcher(text).matches();
						configDialog.setError(result ? null : "Must be a floating point number (consisting of digits and a decmal dot only)");
					} else
						configDialog.setError("Conversion Factor is mandatory!");
					conversionFactorField.setBackground(result ? standardBackgroundCol : Color.ORANGE);
					return result;
				}
			};
			conversionFactorField.setInputVerifier(conversionFactorVerifier);
			add(conversionFactorField, gbc_conversionFactorField);
		}
		{
			JLabel lblUserGroup = new JLabel("User Group Index:");
			GridBagConstraints gbc_UserGroup = new GridBagConstraints();
			gbc_UserGroup.anchor = GridBagConstraints.WEST;
			gbc_UserGroup.insets = new Insets(0, 0, 0, 5);
			gbc_UserGroup.gridx = 0;
			gbc_UserGroup.gridy = 3;
			add(lblUserGroup, gbc_UserGroup);
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
			add(userGroupBox, gbc_userGroupBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 0, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = 3;
			add(rigidArea, gbc_rigidArea);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#saveLoadedParser(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void saveLoadedParser(ConfiguredLogParser parser) {
		ServiceResponseLogParser loadedParser = (ServiceResponseLogParser) parser;
		if (loadedParser != null) {
			loadedParser.setOccTimeIdx((Integer) occTimeGroupCombo.getSelectedItem());
			loadedParser.setOccTimeFormatString(occTimeFormatString.getText());
			loadedParser.setOccTimeLanguage(occTimeLanguage.getText());
			loadedParser.setServiceIdx((Integer) serviceComboBox.getSelectedItem());
			loadedParser.setIgnoreServiceList(serviceExcludeField.getText());
			loadedParser.setResponseTimeIdx((Integer) executionTimeBox.getSelectedItem());
			loadedParser.setResponseTimeFactor(Double.parseDouble(conversionFactorField.getText()));
			loadedParser.setUserIdx((Integer) userGroupBox.getSelectedItem());
		}
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#enableDetailFields(boolean)
	 */
	@Override
	public void enableDetailFields(boolean b) {
		occTimeGroupCombo.setEnabled(b);
		occTimeFormatString.setEnabled(b);
		occTimeLanguage.setEnabled(b);
		serviceComboBox.setEnabled(b);
		serviceExcludeField.setEnabled(b);
		executionTimeBox.setEnabled(b);
		conversionFactorField.setEnabled(b);
		userGroupBox.setEnabled(b);
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#loadEditingFields(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void loadEditingFields(ConfiguredLogParser parser) {
		ServiceResponseLogParser logParser = (ServiceResponseLogParser) parser;
		occTimeGroupCombo.setSelectedItem(logParser.getOccTimeIdx());
		occTimeFormatString.setText(logParser.getOccTimeFormatString());
		occTimeLanguage.setText(logParser.getOccTimeLanguage());
		serviceComboBox.setSelectedItem(logParser.getServiceIdx());
		serviceExcludeField.setText(logParser.getIgnoreServiceList());
		executionTimeBox.setSelectedItem(logParser.getResponseTimeIdx());
		conversionFactorField.setText(Double.toString(logParser.getResponseTimeFactor()));
		userGroupBox.setSelectedItem(logParser.getUserIdx());
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#removeErrorMarks()
	 */
	@Override
	public void removeErrorMarks() {
		occTimeFormatString.setBackground(standardBackgroundCol);
		occTimeLanguage.setBackground(standardBackgroundCol);
		serviceExcludeField.setBackground(standardBackgroundCol);
		conversionFactorField.setBackground(standardBackgroundCol);
	}

	public static FileTypeDescriptor createTypeDescriptor(final ParserConfigDialog configDialog) {
		ServiceResponseExtractionFields extractionFields = new ServiceResponseExtractionFields(configDialog);
		return new FileTypeDescriptor(extractionFields, extractionFields, ServiceResponseLogParser.LOG_FILE_TYPE_NAME);
	}

	@Override
	public ConfiguredLogParser createParser(String parserName) {
		return new ServiceResponseLogParser(parserName);
	}

	@Override
	public ConfiguredLogParser convertLogParser(ConfiguredLogParser configuredLogParser) {
		return new ServiceResponseLogParser(configuredLogParser);
	}

	@Override
	public boolean verifyFormDataIsValid() {
		return occTimeVerifier.verify(occTimeFormatString) && conversionFactorVerifier.verify(conversionFactorField);
	}
}
