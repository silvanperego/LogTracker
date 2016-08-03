package org.sper.logtracker.erroranalysis.parserconf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.ParserConfigDialog;

public class ErrorLogExtractionFields extends JPanel implements ExtractionFieldHandler {

	private static final long serialVersionUID = 1L;
	private JTextField occTimeFormatString;
	private JComboBox occTimeGroupCombo;
	private JTextField occTimeLanguage;
	private JComboBox severityComboBox;
	private Color standardBackgroundCol;
	private InputVerifier occTimeVerifier;
	private JComboBox userIdComboBox;
	private JComboBox contentComboBox;
	private JTextField encodingField;
	private InputVerifier encodingVerifier;
	private InputVerifier timezoneVerifier;
	private JTextField occTimeTimezone;

	public ErrorLogExtractionFields(final ParserConfigDialog configDialog) {
		super();
		setAlignmentX(Component.LEFT_ALIGNMENT);
		GridBagLayout gbl_extractionFields = new GridBagLayout();
		gbl_extractionFields.columnWidths = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.rowHeights = new int[] { 0, 0, 0, 0, 0 };
		gbl_extractionFields.columnWeights = new double[] { 0.0, 1.0, 0.0, 0.0, 1.0 };
		gbl_extractionFields.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0 };
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
			occTimeGroupCombo.setToolTipText(
					"the capturing group index of the group containing the service call occurrence time");
			occTimeGroupCombo.setModel(new DefaultComboBoxModel(new Integer[] { null, 1, 2, 3, 4 }));
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
			JLabel lblOccurrenceTimeFormat = new JLabel("Time Format String:");
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
			timezoneVerifier = new InputVerifier() {

				@Override
				public boolean verify(JComponent input) {
					String text = ((JTextField) input).getText();
					if (text != null && text.length() > 0) {
						if (Arrays.asList(TimeZone.getAvailableIDs()).contains(text)) {
							input.setBackground(standardBackgroundCol);
							configDialog.setError(null);
							return true;
						} else {
							input.setBackground(Color.ORANGE);
							configDialog.setError("Timezone is unknown.");
							return false;
						}
					} else {
						text = null;
						input.setBackground(standardBackgroundCol);
						return true;
					}
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
				standardBackgroundCol = occTimeFormatString.getBackground();
				occTimePanel.add(occTimeFormatString);
				occTimeFormatString.setToolTipText(
						"The date format of the occurrence time of the service call. The format must be specified as java - SimpleDateFormat pattern, as defined at http://docs.oracle.com/javase/6/docs/api/java/text/SimpleDataFormat.html.");
				occTimeFormatString.setInputVerifier(occTimeVerifier);
				{
					JLabel lblLanguage = new JLabel("Language:");
					occTimePanel.add(lblLanguage);
				}
				{
					occTimeLanguage = new JTextField();
					occTimePanel.add(occTimeLanguage);
					occTimeLanguage.setColumns(3);
					occTimeLanguage.setToolTipText(
							"The interpretation language for the occurrence time. (Only necessary, if months are specified as text.");
				}
				{
					JLabel lblLanguage = new JLabel("TimeZone:");
					occTimePanel.add(lblLanguage);
				}
				{
					occTimeTimezone = new JTextField();
					occTimePanel.add(occTimeTimezone);
					occTimeTimezone.setColumns(7);
					occTimeTimezone.setToolTipText("The timezone of the Log-File Entries.");
					occTimeTimezone.setInputVerifier(timezoneVerifier);
				}
			}
		}
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
			encodingVerifier = new InputVerifier() {

				@Override
				public boolean verify(JComponent input) {
					String text = ((JTextField) input).getText();
					if (text != null && text.length() > 0 && !Charset.isSupported(text)) {
						input.setBackground(Color.ORANGE);
						configDialog.setError("This encoding is not supported.");
						return false;
					}
					input.setBackground(standardBackgroundCol);
					configDialog.setError(null);
					return true;
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
			loadedParser.setOccTimeIdx((Integer) occTimeGroupCombo.getSelectedItem());
			loadedParser.setOccTimeFormatString(occTimeFormatString.getText());
			loadedParser.setOccTimeLanguage(occTimeLanguage.getText());
			loadedParser.setUserIdIdx((Integer) userIdComboBox.getSelectedItem());
			loadedParser.setMsgIdx((Integer) contentComboBox.getSelectedItem());
			loadedParser.setEncoding(encodingField.getText());
			loadedParser.setOccTimeTimezone(occTimeTimezone.getText());
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
		occTimeGroupCombo.setEnabled(b);
		occTimeFormatString.setEnabled(b);
		occTimeLanguage.setEnabled(b);
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
		occTimeGroupCombo.setSelectedItem(logParser.getOccTimeIdx());
		occTimeFormatString.setText(logParser.getOccTimeFormatString());
		occTimeLanguage.setText(logParser.getOccTimeLanguage());
		occTimeTimezone.setText(logParser.getOccTimeTimezone());
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
		occTimeFormatString.setBackground(standardBackgroundCol);
		occTimeLanguage.setBackground(standardBackgroundCol);
	}

	@Override
	public boolean verifyFormDataIsValid() {
		return occTimeVerifier.verify(occTimeFormatString) && encodingVerifier.verify(encodingField);
	}
}
