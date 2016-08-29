package org.sper.logtracker.erroranalysis.parserconf;

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

import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.parserconf.CommonFieldsHelper;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FieldIdxComboBoxModel;
import org.sper.logtracker.parserconf.ParserConfigPanel;

import validation.TextVerifier;

public class ErrorLogExtractionFields extends JPanel implements ExtractionFieldHandler<ErrorLogParser, RawErrorDataPoint> {

	private static final long serialVersionUID = 1L;
	private static final int N_IDXFIELDS = 5;
	private JComboBox<Integer> severityComboBox;
	private JComboBox<Integer> userIdComboBox;
	private JComboBox<Integer> contentComboBox;
	private JTextField encodingField;
	private InputVerifier encodingVerifier;
	private CommonFieldsHelper timeFieldsHelper = new CommonFieldsHelper();;
	
	public ErrorLogExtractionFields(final ParserConfigPanel configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0 };
		gbl_extractionFields.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		setLayout(gbl_extractionFields);
		int gridy = timeFieldsHelper.addOccurrenceStartTimeFields(this, configDialog, N_IDXFIELDS, true);
		{
			JLabel lblSeverityNameGroup = new JLabel("Message Severity");
			GridBagConstraints gbc_lblSeverityNameGroup = new GridBagConstraints();
			gbc_lblSeverityNameGroup.anchor = GridBagConstraints.WEST;
			gbc_lblSeverityNameGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblSeverityNameGroup.gridx = 0;
			gbc_lblSeverityNameGroup.gridy = gridy;
			add(lblSeverityNameGroup, gbc_lblSeverityNameGroup);
		}
		{
			severityComboBox = new JComboBox<>();
			severityComboBox.setToolTipText("the capturing group index of the group containing the service name");
			severityComboBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, false));
			GridBagConstraints gbc_severityComboBox = new GridBagConstraints();
			gbc_severityComboBox.anchor = GridBagConstraints.WEST;
			gbc_severityComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_severityComboBox.gridx = 1;
			gbc_severityComboBox.gridy = gridy++;
			add(severityComboBox, gbc_severityComboBox);
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
			JLabel lblMessageContent = new JLabel("Message Content");
			GridBagConstraints gbc_lblMessageContent = new GridBagConstraints();
			gbc_lblMessageContent.anchor = GridBagConstraints.WEST;
			gbc_lblMessageContent.insets = new Insets(0, 0, 5, 5);
			gbc_lblMessageContent.gridx = 0;
			gbc_lblMessageContent.gridy = gridy;
			add(lblMessageContent, gbc_lblMessageContent);
		}
		{
			contentComboBox = new JComboBox<Integer>();
			contentComboBox.setModel(new FieldIdxComboBoxModel(N_IDXFIELDS, true));
			GridBagConstraints gbc_contentComboBox = new GridBagConstraints();
			gbc_contentComboBox.anchor = GridBagConstraints.WEST;
			gbc_contentComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_contentComboBox.gridx = 1;
			gbc_contentComboBox.gridy = gridy++;
			add(contentComboBox, gbc_contentComboBox);
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
	public void saveLoadedParser(ErrorLogParser parser) {
		ErrorLogParser loadedParser = (ErrorLogParser) parser;
		if (loadedParser != null) {
			loadedParser.setSeverityIdx((Integer) severityComboBox.getSelectedItem());
			timeFieldsHelper.saveLoadedParser(parser);
			loadedParser.setUserIdIdx((Integer) userIdComboBox.getSelectedItem());
			loadedParser.setMsgIdx((Integer) contentComboBox.getSelectedItem());
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
		severityComboBox.setEnabled(b);
		userIdComboBox.setEnabled(b);
		contentComboBox.setEnabled(b);
		encodingField.setEnabled(b);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sper.logtracker.logreader.servstat.ExtractionFieldHandler#
	 * loadEditingFields(org.sper.logtracker.logreader.ConfiguredLogParser)
	 */
	@Override
	public void loadEditingFields(ErrorLogParser parser) {
		ErrorLogParser logParser = (ErrorLogParser) parser;
		timeFieldsHelper.loadEditingFields(parser);
		severityComboBox.setSelectedItem(logParser.getSeverityIdx());
		userIdComboBox.setSelectedItem(logParser.getUserIdIdx());
		contentComboBox.setSelectedItem(logParser.getMsgIdx());
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
