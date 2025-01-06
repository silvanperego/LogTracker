package org.sper.logtracker.config;

import org.sper.logtracker.validation.ConfigurationSubPanel;
import org.sper.logtracker.validation.SimpleDateFormatVerifier;
import org.sper.logtracker.validation.TextVerifier;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;

public class GlobalConfigPanel extends JPanel implements ConfigurationSubPanel {

	private static final long serialVersionUID = 1L;
	private JTextField titleTextField;
	private JTextField timestampFormatTextField;
	private JButton okButton;
	private JFrame parentFrame;
	private JLabel errorText;
	private SimpleDateFormatVerifier inputVerifier;
	private GlobalConfig globalConfig;
	private JTextField rangeAxisMaxField;
	private TextVerifier rangeAxisMaxVerifier;
	
	public GlobalConfigPanel(JFrame parentFrame, final JDialog parentDialog, ActionListener submitAction, GlobalConfig globalConfig) {
		this.parentFrame = parentFrame;
		this.globalConfig = globalConfig;
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));
		
		JPanel dialogFieldsPanel = new JPanel();
		add(dialogFieldsPanel);
		GridBagLayout gbl_dialogFieldsPanel = new GridBagLayout();
		gbl_dialogFieldsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_dialogFieldsPanel.rowHeights = new int[]{0, 0, 0, 0};
		gbl_dialogFieldsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_dialogFieldsPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 1.0};
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
		
		JLabel lblTimestampFormatString = new JLabel("Time Stamp Format String:");
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
		
		JLabel lblInitalTimeRange = new JLabel("Inital Time Range Max:");
		GridBagConstraints gbc_lblInitalTimeRange = new GridBagConstraints();
		gbc_lblInitalTimeRange.anchor = GridBagConstraints.WEST;
		gbc_lblInitalTimeRange.insets = new Insets(0, 0, 5, 5);
		gbc_lblInitalTimeRange.gridx = 0;
		gbc_lblInitalTimeRange.gridy = 2;
		dialogFieldsPanel.add(lblInitalTimeRange, gbc_lblInitalTimeRange);
		
		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.insets = new Insets(0, 0, 5, 0);
		gbc_panel.gridx = 1;
		gbc_panel.gridy = 2;
		dialogFieldsPanel.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		
		rangeAxisMaxField = new JTextField();
		rangeAxisMaxField.setToolTipText("this value will be taken as the inital RangeAxis Maximum in Scatter Plots.");
		panel.add(rangeAxisMaxField);
		rangeAxisMaxField.setColumns(4);
		rangeAxisMaxField.setText(Double.toString(globalConfig.getRangeAxisMax()));
		rangeAxisMaxVerifier = new TextVerifier(this) {
			
			@Override
			protected String verifyText(String text) {
				if (text == null || text.length() == 0)
					return "Field is mandatory";
				try {
					double max = Double.parseDouble(text);
					if (max <= 0.)
						return "Only positive values are allowed.";
				} catch (NumberFormatException e) {
					return "This is not a valid floating point number";
				}
				
				return null;
			}
		};
		rangeAxisMaxField.setInputVerifier(rangeAxisMaxVerifier);
		
		errorText = new JLabel("");
		errorText.setForeground(Color.RED);
		GridBagConstraints gbc_errorText = new GridBagConstraints();
		gbc_errorText.gridwidth = 2;
		gbc_errorText.anchor = GridBagConstraints.WEST;
		gbc_errorText.gridx = 0;
		gbc_errorText.gridy = 4;
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
		return inputVerifier.verify(timestampFormatTextField) && rangeAxisMaxVerifier.verify(rangeAxisMaxField);
	}

	@Override
	public void submit() {
		parentFrame.setTitle(titleTextField.getText());
		globalConfig.setTimestampFormatStr(timestampFormatTextField.getText());
		globalConfig.setRangeAxisMax(Double.parseDouble(rangeAxisMaxField.getText()));
	}

	@Override
	public JButton defaultButton() {
		return okButton;
	}

}
