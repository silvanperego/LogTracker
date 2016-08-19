package org.sper.logtracker.correlation.parserconf;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.InputVerifier;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.parserconf.CommonFieldsHelper;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FieldIdxComboBoxModel;
import org.sper.logtracker.parserconf.ParserConfigDialog;

public class CorrelationDataFields extends JPanel implements ExtractionFieldHandler {

	private static final long serialVersionUID = 1L;
	private static final int N_IDXFIELDS = 5;
	private JComboBox<Integer> severityComboBox;
	private JComboBox<Integer> userIdComboBox;
	private JComboBox<Integer> contentComboBox;
	private JTextField encodingField;
	private InputVerifier encodingVerifier;
	private CommonFieldsHelper timeFieldsHelper = new CommonFieldsHelper();;
	
	public CorrelationDataFields(final ParserConfigDialog configDialog) {
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