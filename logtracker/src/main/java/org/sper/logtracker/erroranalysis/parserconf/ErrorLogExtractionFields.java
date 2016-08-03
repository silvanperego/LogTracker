package org.sper.logtracker.erroranalysis.parserconf;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.charset.Charset;

import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.OccurrenceTimeFieldsHelper;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.parserconf.TextVerifier;

public class ErrorLogExtractionFields extends JPanel implements ExtractionFieldHandler {

	private static final long serialVersionUID = 1L;
	private JComboBox severityComboBox;
	private JComboBox userIdComboBox;
	private JComboBox contentComboBox;
	private JTextField encodingField;
	private InputVerifier encodingVerifier;
	private OccurrenceTimeFieldsHelper timeFieldsHelper = new OccurrenceTimeFieldsHelper();;
	
	public ErrorLogExtractionFields(final ParserConfigDialog configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0 };
		gbl_extractionFields.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
		setLayout(gbl_extractionFields);
		timeFieldsHelper.addOccurrenceStartTimeFields(this, configDialog);
		{
			JLabel lblSeverityNameGroup = new JLabel("Message Severity");
			GridBagConstraints gbc_lblSeverityNameGroup = new GridBagConstraints();
			gbc_lblSeverityNameGroup.anchor = GridBagConstraints.WEST;
			gbc_lblSeverityNameGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblSeverityNameGroup.gridx = 0;
			gbc_lblSeverityNameGroup.gridy = 1;
			add(lblSeverityNameGroup, gbc_lblSeverityNameGroup);
		}
		{
			severityComboBox = new JComboBox();
			severityComboBox.setToolTipText("the capturing group index of the group containing the service name");
			severityComboBox.setModel(new DefaultComboBoxModel(new Integer[] { 1, 2, 3, 4 }));
			GridBagConstraints gbc_severityComboBox = new GridBagConstraints();
			gbc_severityComboBox.anchor = GridBagConstraints.WEST;
			gbc_severityComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_severityComboBox.gridx = 1;
			gbc_severityComboBox.gridy = 1;
			add(severityComboBox, gbc_severityComboBox);
		}
		{
			JLabel lblUserId = new JLabel("User Id");
			GridBagConstraints gbc_lblUserId = new GridBagConstraints();
			gbc_lblUserId.anchor = GridBagConstraints.WEST;
			gbc_lblUserId.insets = new Insets(0, 0, 5, 5);
			gbc_lblUserId.gridx = 0;
			gbc_lblUserId.gridy = 2;
			add(lblUserId, gbc_lblUserId);
		}
		{
			userIdComboBox = new JComboBox();
			userIdComboBox.setModel(new DefaultComboBoxModel(new Integer[] { null, 1, 2, 3, 4 }));
			GridBagConstraints gbc_userIdComboBox = new GridBagConstraints();
			gbc_userIdComboBox.anchor = GridBagConstraints.WEST;
			gbc_userIdComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_userIdComboBox.gridx = 1;
			gbc_userIdComboBox.gridy = 2;
			add(userIdComboBox, gbc_userIdComboBox);
		}
		{
			JLabel lblMessageContent = new JLabel("Message Content");
			GridBagConstraints gbc_lblMessageContent = new GridBagConstraints();
			gbc_lblMessageContent.anchor = GridBagConstraints.WEST;
			gbc_lblMessageContent.insets = new Insets(0, 0, 5, 5);
			gbc_lblMessageContent.gridx = 0;
			gbc_lblMessageContent.gridy = 3;
			add(lblMessageContent, gbc_lblMessageContent);
		}
		{
			contentComboBox = new JComboBox();
			contentComboBox.setModel(new DefaultComboBoxModel(new Integer[] { 1, 2, 3, 4 }));
			GridBagConstraints gbc_contentComboBox = new GridBagConstraints();
			gbc_contentComboBox.anchor = GridBagConstraints.WEST;
			gbc_contentComboBox.insets = new Insets(0, 0, 5, 5);
			gbc_contentComboBox.gridx = 1;
			gbc_contentComboBox.gridy = 3;
			add(contentComboBox, gbc_contentComboBox);
		}
		{
			JLabel lblEncoding = new JLabel("Encoding");
			GridBagConstraints gbc_lblEncoding = new GridBagConstraints();
			gbc_lblEncoding.anchor = GridBagConstraints.WEST;
			gbc_lblEncoding.insets = new Insets(0, 0, 0, 5);
			gbc_lblEncoding.gridx = 0;
			gbc_lblEncoding.gridy = 4;
			add(lblEncoding, gbc_lblEncoding);
		}
		{
			encodingField = new JTextField();
			GridBagConstraints gbc_textField = new GridBagConstraints();
			gbc_textField.insets = new Insets(0, 0, 0, 5);
			gbc_textField.anchor = GridBagConstraints.WEST;
			gbc_textField.gridx = 1;
			gbc_textField.gridy = 4;
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
	public void saveLoadedParser(ConfiguredLogParser<?> parser) {
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
	public void loadEditingFields(ConfiguredLogParser<?> parser) {
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
