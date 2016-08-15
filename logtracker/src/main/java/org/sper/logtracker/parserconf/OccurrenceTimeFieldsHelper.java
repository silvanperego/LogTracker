package org.sper.logtracker.parserconf;

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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.parserconf.ConfiguredLogParser.OccTimeFieldDescription;

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
			occTimeVerifier = new TextVerifier(configDialog) {
				
				@Override
				protected String verifyText(String text) {
					if (text != null && text.length() > 0) {
						try {
							new SimpleDateFormat(text);
						} catch (IllegalArgumentException e) {
							return "Not a valid SimpleDateFormat pattern";
						}
					} else {
						return "Date Format is mandatory";
					}
					return null;
				}
			};
			timezoneVerifier = new TextVerifier(configDialog) {
	
				@Override
				protected String verifyText(String text) {
					if (text != null && text.length() > 0) {
						if (Arrays.asList(TimeZone.getAvailableIDs()).contains(text)) {
							return null;
						} else {
							return "Timezone is unknown.";
						}
					} else {
						return null;
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

	public void saveLoadedParser(OccTimeFieldDescription occTime) {
		occTime.setFieldIdx((Integer) occTimeGroupCombo.getSelectedItem());
		occTime.setFormatString(occTimeFormatString.getText());
		occTime.setLanguage(occTimeLanguage.getText());
		occTime.setTimezone(occTimeTimezone.getText());
	}

	public void enableDetailFields(boolean b) {
		occTimeGroupCombo.setEnabled(b);
		occTimeFormatString.setEnabled(b);
		occTimeLanguage.setEnabled(b);
		occTimeTimezone.setEnabled(b);
	}

	public void loadEditingFields(OccTimeFieldDescription occTime) {
		occTimeGroupCombo.setSelectedItem(occTime.getFieldIdx());
		occTimeFormatString.setText(occTime.getFormatString());
		occTimeLanguage.setText(occTime.getLanguage());
		occTimeTimezone.setText(occTime.getTimezone());
	}

	public void removeErrorMarks() {
		occTimeFormatString.setBackground(ParserConfigDialog.getStandardBackgroundColor());
		occTimeLanguage.setBackground(ParserConfigDialog.getStandardBackgroundColor());
		occTimeTimezone.setBackground(ParserConfigDialog.getStandardBackgroundColor());
	}

	public boolean verifyFormDataIsValid() {
		return occTimeVerifier.verify(occTimeFormatString);
	}

}
