package org.sper.logtracker.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URL;

import javax.help.HelpSet;
import javax.help.HelpSetException;
import javax.help.JHelp;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;

import org.sper.logtracker.config.XMLConfigSupport;
import org.sper.logtracker.config.compat.ConfigFileAction;
import org.sper.logtracker.config.compat.ConfigFileOpenButton;
import org.sper.logtracker.config.compat.ConfigFileSaveButton;

public class ToolBar extends JToolBar {
	
	private static final long serialVersionUID = 1L;

	public ToolBar(final LogTracker logTracker, final ParserConfigCatalog parserConfigList) {
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton btnLoadConfig = new ConfigFileOpenButton(logTracker, null, new ConfigFileAction() {
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
				logTracker.applyConfigurationFile(selectedFile);
			}
		});
		btnLoadConfig.setToolTipText("Open Config File");
		add(btnLoadConfig);
		
		JButton btnSaveConfig = new ConfigFileSaveButton(logTracker, null, new ConfigFileAction() {
			
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
				new XMLConfigSupport().saveXMLConfig(selectedFile, logTracker.createConfigurationTree());
			}
		});
		btnSaveConfig.setToolTipText("Save Config File");
		add(btnSaveConfig);
		
		JButton newFileControlBtn = new JButton(new ImageIcon(ToolBar.class.getResource("/newFileConfig.png")));
		newFileControlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				logTracker.addNewFileControl(0.3);
			}
		});
		newFileControlBtn.setToolTipText("Add new Log-File Source Config Box");
		add(newFileControlBtn);
		
		JButton btnConfigLogParsers = new JButton(new ImageIcon(ToolBar.class.getResource("/Zahnrad.png")));
		btnConfigLogParsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				ConfigurationDialog dialog = new ConfigurationDialog(parserConfigList, logTracker, logTracker.getGlobalConfig());
				dialog.getParserConfig().setLogFileTypeList(parserConfigList.getParserTypeList(dialog.getParserConfig()));
				dialog.setVisible(true);
			}
		});
		btnConfigLogParsers.setToolTipText("Configure Log Parsers");
		add(btnConfigLogParsers);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalGlue.setPreferredSize(new Dimension(400, 0));
		add(horizontalGlue);
		
		JButton btnInfo = new JButton(new ImageIcon(FileControlPanel.class.getResource("/Info.png")));
		btnInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					URL helpSetUrl = HelpSet.findHelpSet(getClass().getClassLoader(), "jhelpset.hs");
					HelpSet helpSet = new HelpSet(getClass().getClassLoader(), helpSetUrl);
					helpSet.setHomeID("chap_Overview");
					JHelp jHelp = new JHelp(helpSet);
//					HelpBroker helpBroker = helpSet.createHelpBroker();
					JFrame helpFrame = new JFrame();
					helpFrame.setSize(800, 800);
					helpFrame.getContentPane().add(jHelp);
					helpFrame.setVisible(true);
				} catch (HelpSetException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(logTracker, new JScrollPane(new WelcomePanel()), "About Log-Tracker", JOptionPane.PLAIN_MESSAGE);
				}
			}
		});
		String defaultTitle = "LogTracker";
		logTracker.setTitle(defaultTitle);
		btnInfo.setHorizontalAlignment(SwingConstants.LEFT);
		add(btnInfo);
	}

}
