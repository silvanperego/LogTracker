package org.sper.logtracker.ui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.GlobalConfigPanel;
import org.sper.logtracker.parserconf.ParserConfigList;
import org.sper.logtracker.parserconf.ParserConfigPanel;
import org.sper.logtracker.validation.ConfigurationSubPanel;

public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private ParserConfigPanel parserConfig;
	private SubmitAction submitAction = new SubmitAction();
	private JTabbedPane configTabPane;
	
	private class SubmitAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i < configTabPane.getTabCount(); i++) {
				ConfigurationSubPanel panel = (ConfigurationSubPanel) configTabPane.getComponentAt(i);
				if (!panel.verifyFormDataIsValid()) {
					configTabPane.setSelectedIndex(i);
					return;
				}
			}
			for (int i = 0; i < configTabPane.getTabCount(); i++) {
				ConfigurationSubPanel panel = (ConfigurationSubPanel) configTabPane.getComponentAt(i);
				panel.submit();
				setVisible(false);
			}
		}
		
	}
	
	public ConfigurationDialog(ParserConfigList parserConfigList, JFrame parentFrame, GlobalConfig globalConfig) {	
		setBounds(200, 200, 909, 681);
		configTabPane = new JTabbedPane(JTabbedPane.TOP);
		setTitle("Log-Tracker Configuration");
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().add(configTabPane, BorderLayout.CENTER);
		configTabPane.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent e) {
				ConfigurationSubPanel panel = (ConfigurationSubPanel) configTabPane.getSelectedComponent();
				getRootPane().setDefaultButton(panel.defaultButton());
			}
		});
		GlobalConfigPanel globalConfigPanel = new GlobalConfigPanel(parentFrame, this, submitAction, globalConfig);
		configTabPane.addTab("Global", globalConfigPanel);
		
		parserConfig = new ParserConfigPanel(parserConfigList, this, submitAction);
		configTabPane.addTab("Parser Configuration", parserConfig);
	}

	ParserConfigPanel getParserConfig() {
		return parserConfig;
	}
	
}
