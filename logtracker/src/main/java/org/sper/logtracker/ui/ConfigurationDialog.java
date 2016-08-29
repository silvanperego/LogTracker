package org.sper.logtracker.ui;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.parserconf.ParserConfigList;

public class ConfigurationDialog extends JDialog {
	private static final long serialVersionUID = 1L;
	private ParserConfigDialog parserConfig;

	public ConfigurationDialog(ParserConfigList parserConfigList) {	
		setBounds(200, 200, 909, 681);
		JTabbedPane configTabPane = new JTabbedPane(JTabbedPane.TOP);
		setTitle("Log-Tracker Configuration");
		setModalityType(ModalityType.APPLICATION_MODAL);
		getContentPane().add(configTabPane, BorderLayout.CENTER);
		
		JPanel globalConfigPanel = new JPanel();
		configTabPane.addTab("Global", globalConfigPanel);
		
		parserConfig = new ParserConfigDialog(parserConfigList, this);
		configTabPane.addTab("Parser Configuration", parserConfig);
	}

	ParserConfigDialog getParserConfig() {
		return parserConfig;
	}
	
}
