package org.sper.logtracker.servstat.parserconf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.OccurrenceTimeFieldsHelper;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.parserconf.TextVerifier;
import org.sper.logtracker.servstat.ServiceResponseLogParser;

public class ServiceResponseExtractionFields extends JPanel implements ExtractionFieldHandler {

	private static final Integer[] GROUP_IDX_ITEMS_WITH_NULL = new Integer[] {null, 1, 2, 3, 4, 5};
	private static final Integer[] GROUP_IDX_ITEMS = new Integer[] {1, 2, 3, 4, 5};
	private static final long serialVersionUID = 1L;
	private JTextField conversionFactorField;
	private JComboBox executionTimeBox;
	private JComboBox serviceComboBox;
	private JTextField serviceExcludeField;
	private JComboBox userGroupBox;
	private InputVerifier conversionFactorVerifier;
	private JTextField successCodeField;
	private JComboBox returnCodeGroupBox;
	private InputVerifier successCodeVerifier;
	private OccurrenceTimeFieldsHelper timeFieldsHelper = new OccurrenceTimeFieldsHelper();;

	
	public ServiceResponseExtractionFields(final ParserConfigDialog configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_extractionFields.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_extractionFields.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		gbl_extractionFields.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
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
		timeFieldsHelper.addOccurrenceStartTimeFields(this, configDialog);
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
			serviceComboBox.setModel(new DefaultComboBoxModel(GROUP_IDX_ITEMS));
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
			executionTimeBox.setModel(new DefaultComboBoxModel(GROUP_IDX_ITEMS));
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
			conversionFactorVerifier = new TextVerifier(configDialog) {
				
				private final Pattern floatPat = Pattern.compile("\\d*(?:\\.\\d*)?");
				
				@Override
				protected String verifyText(String text) {
					boolean result = false;
					if (text != null && text.length() > 0) {
						result = floatPat.matcher(text).matches();
						return result ? null : "Must be a floating point number (consisting of digits and a decmal dot only)";
					}
					return "Conversion Factor is mandatory!";
				}
			};
			conversionFactorField.setInputVerifier(conversionFactorVerifier);
			add(conversionFactorField, gbc_conversionFactorField);
		}
		{
			JLabel lblUserGroup = new JLabel("User Group Index:");
			GridBagConstraints gbc_UserGroup = new GridBagConstraints();
			gbc_UserGroup.anchor = GridBagConstraints.WEST;
			gbc_UserGroup.insets = new Insets(0, 0, 5, 5);
			gbc_UserGroup.gridx = 0;
			gbc_UserGroup.gridy = 3;
			add(lblUserGroup, gbc_UserGroup);
		}
		{
			userGroupBox = new JComboBox();
			userGroupBox.setToolTipText("the capturing group index of the group containing the user that called the service. This entry is optional.");
			userGroupBox.setModel(new DefaultComboBoxModel(GROUP_IDX_ITEMS_WITH_NULL));
			GridBagConstraints gbc_userGroupBox = new GridBagConstraints();
			gbc_userGroupBox.anchor = GridBagConstraints.WEST;
			gbc_userGroupBox.insets = new Insets(0, 0, 5, 5);
			gbc_userGroupBox.gridx = 1;
			gbc_userGroupBox.gridy = 3;
			add(userGroupBox, gbc_userGroupBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = 3;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblReturnCode = new JLabel("Return Code:");
			GridBagConstraints gbc_lblReturnCode = new GridBagConstraints();
			gbc_lblReturnCode.anchor = GridBagConstraints.WEST;
			gbc_lblReturnCode.insets = new Insets(0, 0, 0, 5);
			gbc_lblReturnCode.gridx = 0;
			gbc_lblReturnCode.gridy = 4;
			add(lblReturnCode, gbc_lblReturnCode);
		}
		{
			returnCodeGroupBox = new JComboBox();
			returnCodeGroupBox.setToolTipText("the capturing group index of the return code of the service call.");
			returnCodeGroupBox.setModel(new DefaultComboBoxModel(GROUP_IDX_ITEMS_WITH_NULL));
			GridBagConstraints gbc_userGroupBox = new GridBagConstraints();
			gbc_userGroupBox.anchor = GridBagConstraints.WEST;
			gbc_userGroupBox.insets = new Insets(0, 0, 5, 5);
			gbc_userGroupBox.gridx = 1;
			gbc_userGroupBox.gridy = 4;
			add(returnCodeGroupBox, gbc_userGroupBox);
		}
		{
			JLabel lblValueRepresentingok = new JLabel("Value representing \"OK\"");
			GridBagConstraints gbc_lblValueRepresentingok = new GridBagConstraints();
			gbc_lblValueRepresentingok.anchor = GridBagConstraints.WEST;
			gbc_lblValueRepresentingok.insets = new Insets(0, 0, 0, 5);
			gbc_lblValueRepresentingok.gridx = 3;
			gbc_lblValueRepresentingok.gridy = 4;
			add(lblValueRepresentingok, gbc_lblValueRepresentingok);
		}
		{
			successCodeVerifier = new TextVerifier(configDialog) {
				
				@Override
				protected String verifyText(String text) {
					boolean result = true;
					if (returnCodeGroupBox.getSelectedItem() != null) {
						try {
							Integer.parseInt(text);
						} catch (NumberFormatException e) {
							result = false;
						}
					}
					return result ? null : "The return code must be an integer number!";
				}
			};
			successCodeField = new JTextField();
			successCodeField.setToolTipText("The return code that should be considered as \"successful execution without errors\". In http calls, this is normally 200. In Program calls 0.");
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.fill = GridBagConstraints.HORIZONTAL;
			gbc_textField.gridx = 4;
			gbc_textField.gridy = 4;
			add(successCodeField, gbc_textField);
			successCodeField.setColumns(10);
			successCodeField.setInputVerifier(successCodeVerifier);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#saveLoadedParser(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void saveLoadedParser(ConfiguredLogParser<?> parser) {
		ServiceResponseLogParser loadedParser = (ServiceResponseLogParser) parser;
		if (loadedParser != null) {
			timeFieldsHelper.saveLoadedParser(parser.getOccTime());
			loadedParser.setServiceIdx((Integer) serviceComboBox.getSelectedItem());
			loadedParser.setIgnoreServiceList(serviceExcludeField.getText());
			loadedParser.setResponseTimeIdx((Integer) executionTimeBox.getSelectedItem());
			loadedParser.setResponseTimeFactor(Double.parseDouble(conversionFactorField.getText()));
			loadedParser.setUserIdx((Integer) userGroupBox.getSelectedItem());
			loadedParser.setReturnCodeIdx((Integer) returnCodeGroupBox.getSelectedItem());
			loadedParser.setSuccessCode(successCodeField.getText() != null && successCodeField.getText().length() > 0 ? Integer.parseInt(successCodeField.getText()) : null);
		}
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#enableDetailFields(boolean)
	 */
	@Override
	public void enableDetailFields(boolean b) {
		timeFieldsHelper.enableDetailFields(b);
		serviceComboBox.setEnabled(b);
		serviceExcludeField.setEnabled(b);
		executionTimeBox.setEnabled(b);
		conversionFactorField.setEnabled(b);
		userGroupBox.setEnabled(b);
		successCodeField.setEnabled(b);
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#loadEditingFields(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void loadEditingFields(ConfiguredLogParser<?> parser) {
		ServiceResponseLogParser logParser = (ServiceResponseLogParser) parser;
		timeFieldsHelper.loadEditingFields(parser.getOccTime());
		serviceComboBox.setSelectedItem(logParser.getServiceIdx());
		serviceExcludeField.setText(logParser.getIgnoreServiceList());
		executionTimeBox.setSelectedItem(logParser.getResponseTimeIdx());
		conversionFactorField.setText(Double.toString(logParser.getResponseTimeFactor()));
		userGroupBox.setSelectedItem(logParser.getUserIdx());
		returnCodeGroupBox.setSelectedItem(logParser.getReturnCodeIdx());
		successCodeField.setText(logParser.getSuccessCode() != null ? logParser.getSuccessCode().toString() : null);
	}

	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#removeErrorMarks()
	 */
	@Override
	public void removeErrorMarks() {
		timeFieldsHelper.removeErrorMarks();
		serviceExcludeField.setBackground(ParserConfigDialog.getStandardBackgroundColor());
		conversionFactorField.setBackground(ParserConfigDialog.getStandardBackgroundColor());
		successCodeField.setBackground(ParserConfigDialog.getStandardBackgroundColor());
	}

	@Override
	public boolean verifyFormDataIsValid() {
		return timeFieldsHelper.verifyFormDataIsValid() && conversionFactorVerifier.verify(conversionFactorField) && successCodeVerifier.verify(successCodeField);
	}
}
