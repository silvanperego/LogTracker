package org.sper.logtracker.config;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import validation.ConfigurationSubPanel;
import validation.SimpleDateFormatVerifier;

public class GlobalConfigPanel extends JPanel implements ConfigurationSubPanel {

	private static final long serialVersionUID = 1L;
	private JTextField titleTextField;
	private JTextField timestampFormatTextField;
	private JButton okButton;
	private JFrame parentFrame;
	private JLabel errorText;
	private SimpleDateFormatVerifier inputVerifier;
	private GlobalConfig globalConfig;
	
	public GlobalConfigPanel(JFrame parentFrame, final JDialog parentDialog, ActionListener submitAction, GlobalConfig globalConfig) {
		this.parentFrame = parentFrame;
		this.globalConfig = globalConfig;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		JPanel dialogFieldsPanel = new JPanel();
		add(dialogFieldsPanel);
		GridBagLayout gbl_dialogFieldsPanel = new GridBagLayout();
		gbl_dialogFieldsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_dialogFieldsPanel.rowHeights = new int[]{0, 0, 0, 0, 0};
		gbl_dialogFieldsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_dialogFieldsPanel.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
		dialogFieldsPanel.setLayout(gbl_dialogFieldsPanel);
		
		JLabel lblWindowTitle = new JLabel("Window Title:");
		GridBagConstraints gbc_lblWindowTitle = new GridBagConstraints();
		gbc_lblWindowTitle.anchor = GridBagConstraints.WEST;
		gbc_lblWindowTitle.insets = new Insets(0, 0, 5, 5);
		gbc_lblWindowTitle.gridx = 0;
		gbc_lblWindowTitle.gridy = 0;
		dialogFieldsPanel.add(lblWindowTitle, gbc_lblWindowTitle);
		
		titleTextField = new JTextField();
		GridBagConstraints gbc_txtTimestampFormatString = new GridBagConstraints();
		gbc_txtTimestampFormatString.insets = new Insets(0, 0, 5, 0);
		gbc_txtTimestampFormatString.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtTimestampFormatString.gridx = 1;
		gbc_txtTimestampFormatString.gridy = 0;
		dialogFieldsPanel.add(titleTextField, gbc_txtTimestampFormatString);
		titleTextField.setToolTipText("The Title of the application Window.");
		titleTextField.setText(parentFrame.getTitle());
		titleTextField.setColumns(20);
		
		JLabel lblTimestampFormatString = new JLabel("Timestamp Format String:");
		GridBagConstraints gbc_lblTimestampFormatString = new GridBagConstraints();
		gbc_lblTimestampFormatString.anchor = GridBagConstraints.WEST;
		gbc_lblTimestampFormatString.insets = new Insets(0, 0, 5, 5);
		gbc_lblTimestampFormatString.gridx = 0;
		gbc_lblTimestampFormatString.gridy = 1;
		dialogFieldsPanel.add(lblTimestampFormatString, gbc_lblTimestampFormatString);
		
		timestampFormatTextField = new JTextField();
		timestampFormatTextField.setToolTipText("The display format of timestamps is defined by this formatting string. The formatting rules are those of java.text.SimpleDateFormat.");
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 0);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 1;
		dialogFieldsPanel.add(timestampFormatTextField, gbc_textField);
		timestampFormatTextField.setColumns(30);
		timestampFormatTextField.setText(globalConfig.getTimestampFormatStr());
		inputVerifier = new SimpleDateFormatVerifier(this);
		timestampFormatTextField.setInputVerifier(inputVerifier);
		
		errorText = new JLabel("");
		errorText.setForeground(Color.RED);
		GridBagConstraints gbc_errorText = new GridBagConstraints();
		gbc_errorText.gridwidth = 2;
		gbc_errorText.anchor = GridBagConstraints.WEST;
		gbc_errorText.insets = new Insets(0, 0, 0, 5);
		gbc_errorText.gridx = 0;
		gbc_errorText.gridy = 3;
		dialogFieldsPanel.add(errorText, gbc_errorText);
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		add(buttonPanel, BorderLayout.SOUTH);
		
		okButton = new JButton("OK");
		okButton.setToolTipText("Activate the Configuration Changes.");
		okButton.setEnabled(true);
		okButton.setActionCommand("OK");
		okButton.addActionListener(submitAction);
		buttonPanel.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Undo all configuration edits.");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(e -> parentDialog.setVisible(false));
		buttonPanel.add(cancelButton);
	}

	@Override
	public void setError(String txt) {
		errorText.setText(txt);
		enableComponents();
	}

	private boolean inError() {
		return errorText.getText() != null && !errorText.getText().isEmpty();
	}
	
	private void enableComponents() {
		okButton.setEnabled(!inError());
	}

	@Override
	public boolean verifyFormDataIsValid() {
		return inputVerifier.verify(timestampFormatTextField);
	}

	@Override
	public void submit() {
		parentFrame.setTitle(titleTextField.getText());
		globalConfig.setTimestampFormatStr(timestampFormatTextField.getText());
	}

	@Override
	public JButton defaultButton() {
		return okButton;
	}

}
