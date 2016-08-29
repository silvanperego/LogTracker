package org.sper.logtracker.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.parserconf.ParserConfigList;

public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private ParserConfigDialog parserConfig;
	private JTextField titleTextField;
	private JFrame parentFrame;
	private SubmitAction submitAction = new SubmitAction();
	private JTabbedPane configTabPane;
	private JButton okButton;
	
	private class SubmitAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			if (parserConfig.verifyFormDataIsValid()) {
				parentFrame.setTitle(titleTextField.getText());
				parserConfig.submit();
				setVisible(false);
			} else {
				configTabPane.setSelectedIndex(1);
			}
		}
		
	}
	
	public ConfigurationDialog(ParserConfigList parserConfigList, JFrame parentFrame) {	
		this.parentFrame = parentFrame;
		setBounds(200, 200, 909, 681);
		configTabPane = new JTabbedPane(JTabbedPane.TOP);
		setTitle("Log-Tracker Configuration");
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().add(configTabPane, BorderLayout.CENTER);
		configTabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				switch (configTabPane.getSelectedIndex()) {
				case 0 : getRootPane().setDefaultButton(okButton); break;
				case 1 : parserConfig.setDefaultButton(); break;
				}
			}
		});
		JPanel globalConfigPanel = new JPanel();
		globalConfigPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		configTabPane.addTab("Global", globalConfigPanel);
		globalConfigPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel dialogFieldsPanel = new JPanel();
		globalConfigPanel.add(dialogFieldsPanel);
		GridBagLayout gbl_dialogFieldsPanel = new GridBagLayout();
		gbl_dialogFieldsPanel.columnWidths = new int[]{0, 0, 0};
		gbl_dialogFieldsPanel.rowHeights = new int[]{0, 0};
		gbl_dialogFieldsPanel.columnWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		gbl_dialogFieldsPanel.rowWeights = new double[]{0.0, Double.MIN_VALUE};
		dialogFieldsPanel.setLayout(gbl_dialogFieldsPanel);
		
		JLabel lblWindowTitle = new JLabel("Window Title:");
		GridBagConstraints gbc_lblWindowTitle = new GridBagConstraints();
		gbc_lblWindowTitle.anchor = GridBagConstraints.EAST;
		gbc_lblWindowTitle.insets = new Insets(0, 0, 0, 5);
		gbc_lblWindowTitle.gridx = 0;
		gbc_lblWindowTitle.gridy = 0;
		dialogFieldsPanel.add(lblWindowTitle, gbc_lblWindowTitle);
		
		titleTextField = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 1;
		gbc_textField.gridy = 0;
		dialogFieldsPanel.add(titleTextField, gbc_textField);
		titleTextField.setToolTipText("Dieser Text wird als Fenstertitel angezeigt");
		titleTextField.setText(parentFrame.getTitle());
		titleTextField.setColumns(20);
		
		JPanel buttonPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) buttonPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		globalConfigPanel.add(buttonPanel, BorderLayout.SOUTH);
		
		okButton = new JButton("OK");
		okButton.setToolTipText("Activate the Configuration Changes.");
		okButton.setEnabled(true);
		okButton.setActionCommand("OK");
		okButton.addActionListener(submitAction);
		buttonPanel.add(okButton);
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.setToolTipText("Undo all configuration edits.");
		cancelButton.setActionCommand("Cancel");
		cancelButton.addActionListener(e -> setVisible(false));
		buttonPanel.add(cancelButton);
		
		parserConfig = new ParserConfigDialog(parserConfigList, this, submitAction);
		configTabPane.addTab("Parser Configuration", parserConfig);
		getRootPane().setDefaultButton(okButton);
	}

	ParserConfigDialog getParserConfig() {
		return parserConfig;
	}
	
}
