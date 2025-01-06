package org.sper.logtracker.parserconf;

import org.sper.logtracker.validation.SimpleDateFormatVerifier;
import org.sper.logtracker.validation.TextVerifier;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.TimeZone;

/**
 * Hilfsklasse für die Darstellung der Felder zur Konfiguration der Konfigurationsfelder, die in allen Detail-Konfigurationen vorkommen..
 * @author silvan.perego
 *
 */
public class CommonFieldsHelper {

	private JTextField occTimeFormatString;
	private JComboBox<Integer> occTimeGroupCombo;
	private JTextField occTimeLanguage;
	private JTextField occTimeTimezone;
	private InputVerifier occTimeVerifier;
	private InputVerifier timezoneVerifier;
	private JComboBox<Integer> correlationGroupCombo;

	/**
	 * Füge Standard-Konfigurationsfelder für Occurrence-Start-Time und Correlation-ID hinzu.
	 * @param panel das Panel, dem die Felder hinzugefügt werden. Es sollte über einen Gridbag-Constraint verfügen.
	 * @param configDialog der configDialog der den Feldern zugrunde liegt.
	 * @param nIdxFields der maximale Wert des Index für die Index-Dropdowns.
	 * @param correlationIdIsOptional true, falls die correlationId vom User leergelassen werden darf.
	 * @return die Anzahl Zeilen, die durch die Standard-Felder beansprucht werden.
	 */
	public int addOccurrenceStartTimeFields(final JPanel panel, final ParserConfigPanel configDialog, int nIdxFields, boolean correlationIdIsOptional) {
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
			occTimeGroupCombo = new JComboBox<>();
			occTimeGroupCombo.setToolTipText(
					"the capturing group index of the group containing the service call occurrence time");
			occTimeGroupCombo.setModel(new FieldIdxComboBoxModel(nIdxFields, false));
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
			gbc_lblOccurrenceTimeFormat.anchor = GridBagConstraints.WEST;
			gbc_lblOccurrenceTimeFormat.gridx = 3;
			gbc_lblOccurrenceTimeFormat.gridy = 0;
			panel.add(lblOccurrenceTimeFormat, gbc_lblOccurrenceTimeFormat);
		}
		{
			occTimeVerifier = new SimpleDateFormatVerifier(configDialog);
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
				occTimeFormatString.setColumns(20);
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
		{
			JLabel lblOccurenceTimeGroup = new JLabel("Correlation-ID Group Index:");
			GridBagConstraints gbc_lblOccurenceTimeGroup = new GridBagConstraints();
			gbc_lblOccurenceTimeGroup.insets = new Insets(0, 0, 5, 5);
			gbc_lblOccurenceTimeGroup.anchor = GridBagConstraints.WEST;
			gbc_lblOccurenceTimeGroup.gridx = 0;
			gbc_lblOccurenceTimeGroup.gridy = 1;
			panel.add(lblOccurenceTimeGroup, gbc_lblOccurenceTimeGroup);
		}
		{
			correlationGroupCombo = new JComboBox<>();
			correlationGroupCombo.setToolTipText(
					"the capturing group index of the group containing the message correlation ID.");
			correlationGroupCombo.setModel(new FieldIdxComboBoxModel(nIdxFields, correlationIdIsOptional));
			GridBagConstraints gbc_occTimeGroupCombo = new GridBagConstraints();
			gbc_occTimeGroupCombo.insets = new Insets(0, 0, 5, 5);
			gbc_occTimeGroupCombo.anchor = GridBagConstraints.WEST;
			gbc_occTimeGroupCombo.gridx = 1;
			gbc_occTimeGroupCombo.gridy = 1;
			panel.add(correlationGroupCombo, gbc_occTimeGroupCombo);
		}
		return 2;
	}

	public void saveLoadedParser(ConfiguredLogParser<?,?> parser) {
		parser.getOccTime().setFieldIdx((Integer) occTimeGroupCombo.getSelectedItem());
		parser.getOccTime().setFormatString(occTimeFormatString.getText());
		parser.getOccTime().setLanguage(occTimeLanguage.getText());
		parser.getOccTime().setTimezone(occTimeTimezone.getText());
		parser.setCorrelationIdIdx((Integer) correlationGroupCombo.getSelectedItem());
	}

	public void enableDetailFields(boolean b) {
		occTimeGroupCombo.setEnabled(b);
		occTimeFormatString.setEnabled(b);
		occTimeLanguage.setEnabled(b);
		occTimeTimezone.setEnabled(b);
		correlationGroupCombo.setEnabled(b);
	}

	public void loadEditingFields(ConfiguredLogParser<?,?> parser) {
		occTimeGroupCombo.setSelectedItem(parser.getOccTime().getFieldIdx());
		occTimeFormatString.setText(parser.getOccTime().getFormatString());
		occTimeLanguage.setText(parser.getOccTime().getLanguage());
		occTimeTimezone.setText(parser.getOccTime().getTimezone());
		correlationGroupCombo.setSelectedItem(parser.getCorrelationIdIdx());
	}

	public void removeErrorMarks() {
		occTimeFormatString.setBackground(ParserConfigPanel.getStandardBackgroundColor());
		occTimeLanguage.setBackground(ParserConfigPanel.getStandardBackgroundColor());
		occTimeTimezone.setBackground(ParserConfigPanel.getStandardBackgroundColor());
	}

	public boolean verifyFormDataIsValid() {
		return occTimeVerifier.verify(occTimeFormatString);
	}

}
