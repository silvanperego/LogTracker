package org.sper.logtracker.parserconf;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
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

/**
 * Hilfsklasse für die Darstellung der Felder zur Konfiguration der Occurrence-Time.
 * @author silvan.perego
 *
 */
public class OccurrenceTimeFieldsHelper {

	private JTextField occTimeFormatString;
	private JComboBox occTimeGroupCombo;
	private JTextField occTimeLanguage;
	private JTextField occTimeTimezone;
	private InputVerifier occTimeVerifier;
	private InputVerifier timezoneVerifier;
	private Color standardBackgroundCol;

	public void addOccurrenceStartTimeFields(final JPanel panel, final ParserConfigDialog configDialog) {
		{
			JLabel lblOccurenceTimeGroup = new JLabel("Occurence Time Group Index:");
			GridBagConstraints gbc_lblOccurenceTimeGroup = new GridBagConstraints();
			gbc_lblOccurenceTimeGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblOccurenceTimeGroup.anchor = GridBagConstraints.EAST;
			gbc_lblOccurenceTimeGroup.gridx = 0;
			gbc_lblOccurenceTimeGroup.gridy = 0;
			panel.add(lblOccurenceTimeGroup, gbc_lblOccurenceTimeGroup);
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
			panel.add(occTimeGroupCombo, gbc_occTimeGroupCombo);
		}
		{
			Component rigidArea = Box.createRigidArea(new Dimension(20, 20));
			GridBagConstraints gbc_rigidArea = new GridBagConstraints();
			gbc_rigidArea.insets = new Insets(0, 0, 5, 5);
			gbc_rigidArea.gridx = 2;
			gbc_rigidArea.gridy = 0;
			panel.add(rigidArea, gbc_rigidArea);
		}
		{
			JLabel lblOccurrenceTimeFormat = new JLabel("Time Format String:");
			GridBagConstraints gbc_lblOccurrenceTimeFormat = new GridBagConstraints();
			gbc_lblOccurrenceTimeFormat.insets = new Insets(0, 0, 5, 5);
			gbc_lblOccurrenceTimeFormat.gridx = 3;
			gbc_lblOccurrenceTimeFormat.gridy = 0;
			panel.add(lblOccurrenceTimeFormat, gbc_lblOccurrenceTimeFormat);
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
				panel.add(occTimePanel, gbc_occTimePanel);
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
					JLabel lblTimezone = new JLabel("TimeZone:");
					occTimePanel.add(lblTimezone);
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
	}

	public void saveLoadedParser(ConfiguredLogParser<?> loadedParser) {
		loadedParser.setOccTimeIdx((Integer) occTimeGroupCombo.getSelectedItem());
		loadedParser.setOccTimeFormatString(occTimeFormatString.getText());
		loadedParser.setOccTimeLanguage(occTimeLanguage.getText());
		loadedParser.setOccTimeTimezone(occTimeTimezone.getText());
	}

	public void enableDetailFields(boolean b) {
		occTimeGroupCombo.setEnabled(b);
		occTimeFormatString.setEnabled(b);
		occTimeLanguage.setEnabled(b);
	}

	public void loadEditingFields(ConfiguredLogParser<?> logParser) {
		occTimeGroupCombo.setSelectedItem(logParser.getOccTimeIdx());
		occTimeFormatString.setText(logParser.getOccTimeFormatString());
		occTimeLanguage.setText(logParser.getOccTimeLanguage());
		occTimeTimezone.setText(logParser.getOccTimeTimezone());
	}

	public void removeErrorMarks() {
		occTimeFormatString.setBackground(standardBackgroundCol);
		occTimeLanguage.setBackground(standardBackgroundCol);
	}

	public boolean verifyFormDataIsValid() {
		return occTimeVerifier.verify(occTimeFormatString);
	}

}
