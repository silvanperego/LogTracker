package org.sper.logtracker.correlation.parserconf;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.charset.Charset;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.correlation.CorrelationLogParser;
import org.sper.logtracker.correlation.data.RawCorrelatedDataPoint;
import org.sper.logtracker.parserconf.CommonFieldsHelper;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FieldIdxComboBoxModel;
import org.sper.logtracker.parserconf.ParserConfigPanel;

import validation.TextVerifier;

public class CorrelationDataFields extends JPanel implements ExtractionFieldHandler<CorrelationLogParser, RawCorrelatedDataPoint> {

	private static final long serialVersionUID = 1L;
	private static final int N_IDXFIELDS = 5;
	private JComboBox<Integer> userIdComboBox;
	private JTextField encodingField;
	private InputVerifier encodingVerifier;
	private CommonFieldsHelper timeFieldsHelper = new CommonFieldsHelper();
	private JComboBox<Integer> serviceComboBox;;
	
	public CorrelationDataFields(final ParserConfigPanel configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0 };
		gbl_extractionFields.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		setLayout(gbl_extractionFields);
		int gridy = timeFieldsHelper.addOccurrenceStartTimeFields(this, configDialog, N_IDXFIELDS, false);
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
			serviceComboBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, true));
			GridBagConstraints gbc_serviceComboBox = new GridBagConstraints();
			gbc_serviceComboBox.anchor = GridBagConstraints.WEST;
			gbc_serviceComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_serviceComboBox.gridx = 1;
			gbc_serviceComboBox.gridy = gridy++;
			add(serviceComboBox, gbc_serviceComboBox);
		}
		{
			JLabel lblUserId = new JLabel("User Id");
			GridBagConstraints gbc_lblUserId = new GridBagConstraints();
			gbc_lblUserId.anchor = GridBagConstraints.WEST;
			gbc_lblUserId.insets = new Insets(0, 0, 5, 5);
			gbc_lblUserId.gridx = 0;
			gbc_lblUserId.gridy = gridy;
			add(lblUserId, gbc_lblUserId);
		}
		{
			userIdComboBox = new JComboBox<Integer>();
			userIdComboBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, true));
			GridBagConstraints gbc_userIdComboBox = new GridBagConstraints();
			gbc_userIdComboBox.anchor = GridBagConstraints.WEST;
			gbc_userIdComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_userIdComboBox.gridx = 1;
			gbc_userIdComboBox.gridy = gridy++;
			add(userIdComboBox, gbc_userIdComboBox);
		}
		{
			JLabel lblEncoding = new JLabel("Encoding");
			GridBagConstraints gbc_lblEncoding = new GridBagConstraints();
			gbc_lblEncoding.anchor = GridBagConstraints.WEST;
			gbc_lblEncoding.insets = new Insets(0, 0, 0, 5);
			gbc_lblEncoding.gridx = 0;
			gbc_lblEncoding.gridy = gridy;
			add(lblEncoding, gbc_lblEncoding);
		}
		{
			encodingField = new JTextField();
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 0, 5);
			gbc_textField.anchor = GridBagConstraints.WEST;
			gbc_textField.gridx = 1;
			gbc_textField.gridy = gridy++;
			add(encodingField, gbc_textField);
			encodingField.setColumns(6);
		}
		{
			encodingVerifier = new TextVerifier(configDialog) {

				@Override
				protected String verifyText(String text) {
					if (text != null && text.length() > 0 && !Charset.isSupported(text)) {
						return "This encoding is not supported.";
					}
					return null;
				}
			};
			encodingField.setInputVerifier(encodingVerifier);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#
	 * saveLoadedParser(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void saveLoadedParser(CorrelationLogParser loadedParser) {
		if (loadedParser != null) {
			timeFieldsHelper.saveLoadedParser(loadedParser);
			loadedParser.setUserIdIdx((Integer) userIdComboBox.getSelectedItem());
			loadedParser.setServiceNameIdx((Integer) serviceComboBox.getSelectedItem());
			loadedParser.setEncoding(encodingField.getText());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#
	 * enableDetailFields(boolean)
	 */
	@Override
	public void enableDetailFields(boolean b) {
		timeFieldsHelper.enableDetailFields(b);
		serviceComboBox.setEnabled(b);
		userIdComboBox.setEnabled(b);
		encodingField.setEnabled(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#
	 * loadEditingFields(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void loadEditingFields(CorrelationLogParser parser) {
		CorrelationLogParser logParser = (CorrelationLogParser) parser;
		timeFieldsHelper.loadEditingFields(parser);
		serviceComboBox.setSelectedItem(logParser.getServiceNameIdx());
		userIdComboBox.setSelectedItem(logParser.getUserIdIdx());
		encodingField.setText(logParser.getEncoding());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#
	 * removeErrorMarks()
	 */
	@Override
	public void removeErrorMarks() {
		timeFieldsHelper.removeErrorMarks();
	}

	@Override
	public boolean verifyFormDataIsValid() {
		return timeFieldsHelper.verifyFormDataIsValid() && encodingVerifier.verify(encodingField);
	}
}
