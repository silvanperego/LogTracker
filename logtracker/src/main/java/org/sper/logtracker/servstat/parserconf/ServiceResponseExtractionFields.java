package org.sper.logtracker.servstat.parserconf;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.parserconf.CommonFieldsHelper;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FieldIdxComboBoxModel;
import org.sper.logtracker.parserconf.ParserConfigPanel;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;

import validation.TextVerifier;
import javax.swing.JCheckBox;

public class ServiceResponseExtractionFields extends JPanel implements ExtractionFieldHandler<ServiceResponseLogParser, RawStatsDataPoint> {

	private static final long serialVersionUID = 1L;
	private static final int N_IDXFIELDS = 6;
	private JTextField conversionFactorField;
	private JComboBox<Integer> executionTimeBox;
	private JComboBox<Integer> serviceComboBox;
	private JTextField serviceExcludeField;
	private JComboBox<Integer> userGroupBox;
	private InputVerifier conversionFactorVerifier;
	private JTextField successCodeField;
	private JComboBox<Integer> returnCodeGroupBox;
	private InputVerifier successCodeVerifier;
	private CommonFieldsHelper timeFieldsHelper = new CommonFieldsHelper();
	private JCheckBox chckbxSubstract;;

	
	public ServiceResponseExtractionFields(final ParserConfigPanel configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[]{0, 0, 0, 0, 0};
		gbl_extractionFields.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_extractionFields.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0};
		gbl_extractionFields.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
		setLayout(gbl_extractionFields);
		int gridy = timeFieldsHelper.addOccurrenceStartTimeFields(this, configDialog, N_IDXFIELDS, true);
		{
			JLabel lblServiceNameGroup = new JLabel("Service Name Group Index:");
			GridBagConstraints gbc_lblServiceNameGroup = new GridBagConstraints();
			gbc_lblServiceNameGroup.anchor = GridBagConstraints.WEST;
			gbc_lblServiceNameGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblServiceNameGroup.gridx = 0;
			gbc_lblServiceNameGroup.gridy = gridy;
			add(lblServiceNameGroup, gbc_lblServiceNameGroup);
		}
		{
			serviceComboBox = new JComboBox<>();
			serviceComboBox.setToolTipText("the capturing group index of the group containing the service name");
			serviceComboBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, false));
			GridBagConstraints gbc_serviceComboBox = new GridBagConstraints();
			gbc_serviceComboBox.anchor = GridBagConstraints.WEST;
			gbc_serviceComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_serviceComboBox.gridx = 1;
			gbc_serviceComboBox.gridy = gridy;
			add(serviceComboBox, gbc_serviceComboBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = gridy;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblExcludeServices = new JLabel("Exclude Services");
			GridBagConstraints gbc_lblExcludeServices = new GridBagConstraints();
			gbc_lblExcludeServices.anchor = GridBagConstraints.WEST;
			gbc_lblExcludeServices.insets = new Insets(0, 0, 5, 5);
			gbc_lblExcludeServices.gridx = 3;
			gbc_lblExcludeServices.gridy = gridy;
			add(lblExcludeServices, gbc_lblExcludeServices);
		}
		{
			serviceExcludeField = new JTextField();
			serviceExcludeField.setToolTipText("A comma separated list of service names, specifying services that should be ignored by the parser.");
			GridBagConstraints gbc_serviceExcludeField = new GridBagConstraints();
			gbc_serviceExcludeField.insets = new Insets(0, 0, 5, 0);
			gbc_serviceExcludeField.fill = GridBagConstraints.HORIZONTAL;
			gbc_serviceExcludeField.gridx = 4;
			gbc_serviceExcludeField.gridy = gridy++;
			add(serviceExcludeField, gbc_serviceExcludeField);
		}
		{
			JLabel lblExecutionTimeGroup = new JLabel("Execution Time Group Index:");
			GridBagConstraints gbc_lblExecutionTimeGroup = new GridBagConstraints();
			gbc_lblExecutionTimeGroup.anchor = GridBagConstraints.WEST;
			gbc_lblExecutionTimeGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblExecutionTimeGroup.gridx = 0;
			gbc_lblExecutionTimeGroup.gridy = gridy;
			add(lblExecutionTimeGroup, gbc_lblExecutionTimeGroup);
		}
		{
			executionTimeBox = new JComboBox<>();
			executionTimeBox.setToolTipText("the capturing group index of the group containing the service execution (or response) time");
			executionTimeBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, false));
			GridBagConstraints gbc_executionTimeBox = new GridBagConstraints();
			gbc_executionTimeBox.anchor = GridBagConstraints.WEST;
			gbc_executionTimeBox.insets = new Insets(0, 0, 5, 5);
			gbc_executionTimeBox.gridx = 1;
			gbc_executionTimeBox.gridy = gridy;
			add(executionTimeBox, gbc_executionTimeBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = gridy;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblConversionFactor = new JLabel("Conversion Factor");
			GridBagConstraints gbc_lblConversionFactor = new GridBagConstraints();
			gbc_lblConversionFactor.anchor = GridBagConstraints.WEST;
			gbc_lblConversionFactor.insets = new Insets(0, 0, 5, 5);
			gbc_lblConversionFactor.gridx = 3;
			gbc_lblConversionFactor.gridy = gridy;
			add(lblConversionFactor, gbc_lblConversionFactor);
		}
		{
			conversionFactorField = new JFormattedTextField();
			conversionFactorField.setToolTipText("Service execution times will be multiplied by this factor before being handed over to the subsequent modules. The factor should be sized such, that the resulting unit is in seconds.");
			GridBagConstraints gbc_conversionFactorField = new GridBagConstraints();
			gbc_conversionFactorField.insets = new Insets(0, 0, 5, 0);
			gbc_conversionFactorField.fill = GridBagConstraints.HORIZONTAL;
			gbc_conversionFactorField.gridx = 4;
			gbc_conversionFactorField.gridy = gridy++;
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
			gbc_UserGroup.gridy = gridy;
			add(lblUserGroup, gbc_UserGroup);
		}
		{
			userGroupBox = new JComboBox<>();
			userGroupBox.setToolTipText("the capturing group index of the group containing the user that called the service. This entry is optional.");
			userGroupBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, true));
			GridBagConstraints gbc_userGroupBox = new GridBagConstraints();
			gbc_userGroupBox.anchor = GridBagConstraints.WEST;
			gbc_userGroupBox.insets = new Insets(0, 0, 5, 5);
			gbc_userGroupBox.gridx = 1;
			gbc_userGroupBox.gridy = gridy;
			add(userGroupBox, gbc_userGroupBox);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = gridy++;
			add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblReturnCode = new JLabel("Return Code:");
			GridBagConstraints gbc_lblReturnCode = new GridBagConstraints();
			gbc_lblReturnCode.anchor = GridBagConstraints.WEST;
			gbc_lblReturnCode.insets = new Insets(0, 0, 5, 5);
			gbc_lblReturnCode.gridx = 0;
			gbc_lblReturnCode.gridy = gridy;
			add(lblReturnCode, gbc_lblReturnCode);
		}
		{
			returnCodeGroupBox = new JComboBox<>();
			returnCodeGroupBox.setToolTipText("the capturing group index of the return code of the service call.");
			returnCodeGroupBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, true));
			GridBagConstraints gbc_userGroupBox = new GridBagConstraints();
			gbc_userGroupBox.anchor = GridBagConstraints.WEST;
			gbc_userGroupBox.insets = new Insets(0, 0, 5, 5);
			gbc_userGroupBox.gridx = 1;
			gbc_userGroupBox.gridy = gridy;
			add(returnCodeGroupBox, gbc_userGroupBox);
		}
		{
			JLabel lblValueRepresentingok = new JLabel("Value representing \"OK\"");
			GridBagConstraints gbc_lblValueRepresentingok = new GridBagConstraints();
			gbc_lblValueRepresentingok.anchor = GridBagConstraints.WEST;
			gbc_lblValueRepresentingok.insets = new Insets(0, 0, 5, 5);
			gbc_lblValueRepresentingok.gridx = 3;
			gbc_lblValueRepresentingok.gridy = gridy;
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
			gbc_textField.gridy = gridy++;
			add(successCodeField, gbc_textField);
			successCodeField.setColumns(10);
			successCodeField.setInputVerifier(successCodeVerifier);
			{
				JLabel lblSubtractResponseTime = new JLabel("Substract Response Time:");
				GridBagConstraints gbc_lblSubtractResponseTime = new GridBagConstraints();
				gbc_lblSubtractResponseTime.anchor = GridBagConstraints.WEST;
				gbc_lblSubtractResponseTime.insets = new Insets(0, 0, 0, 5);
				gbc_lblSubtractResponseTime.gridx = 0;
				gbc_lblSubtractResponseTime.gridy = gridy;
				add(lblSubtractResponseTime, gbc_lblSubtractResponseTime);
			}
			{
				chckbxSubstract = new JCheckBox("");
				chckbxSubstract.setToolTipText("In many service response log files, the log entry is created when the service returns. By activating this option you can substract the execution time from the event time and create a plot that shows the start times of the service requests.");
				GridBagConstraints gbc_chckbxSubtract = new GridBagConstraints();
				gbc_chckbxSubtract.insets = new Insets(0, 0, 0, 5);
				gbc_chckbxSubtract.gridx = 1;
				gbc_chckbxSubtract.gridy = gridy++;
				add(chckbxSubstract, gbc_chckbxSubtract);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#saveLoadedParser(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void saveLoadedParser(ServiceResponseLogParser parser) {
		ServiceResponseLogParser loadedParser = (ServiceResponseLogParser) parser;
		if (loadedParser != null) {
			timeFieldsHelper.saveLoadedParser(parser);
			loadedParser.setServiceIdx((Integer) serviceComboBox.getSelectedItem());
			loadedParser.setIgnoreServiceList(serviceExcludeField.getText());
			loadedParser.setResponseTimeIdx((Integer) executionTimeBox.getSelectedItem());
			loadedParser.setResponseTimeFactor(Double.parseDouble(conversionFactorField.getText()));
			loadedParser.setUserIdx((Integer) userGroupBox.getSelectedItem());
			loadedParser.setReturnCodeIdx((Integer) returnCodeGroupBox.getSelectedItem());
			loadedParser.setSuccessCode(successCodeField.getText() != null && successCodeField.getText().length() > 0 ? Integer.parseInt(successCodeField.getText()) : null);
			loadedParser.setSubstract(chckbxSubstract.isSelected());
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
	public void loadEditingFields(ServiceResponseLogParser parser) {
		ServiceResponseLogParser logParser = (ServiceResponseLogParser) parser;
		timeFieldsHelper.loadEditingFields(parser);
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
		serviceExcludeField.setBackground(ParserConfigPanel.getStandardBackgroundColor());
		conversionFactorField.setBackground(ParserConfigPanel.getStandardBackgroundColor());
		successCodeField.setBackground(ParserConfigPanel.getStandardBackgroundColor());
	}

	@Override
	public boolean verifyFormDataIsValid() {
		return timeFieldsHelper.verifyFormDataIsValid() && conversionFactorVerifier.verify(conversionFactorField) && successCodeVerifier.verify(successCodeField);
	}
}
