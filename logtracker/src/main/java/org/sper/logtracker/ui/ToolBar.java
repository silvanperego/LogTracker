package org.sper.logtracker.ui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.sper.logtracker.config.Global;
import org.sper.logtracker.config.LogTrackerConfig;
import org.sper.logtracker.config.compat.ConfigFileAction;
import org.sper.logtracker.config.compat.ConfigFileOpenButton;
import org.sper.logtracker.config.compat.ConfigFileSaveButton;
import org.sper.logtracker.parserconf.ParserConfigDialog;

import bibliothek.gui.dock.common.CLocation;

public class ToolBar extends JToolBar {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtLogtracker;
	private LogTracker logTracker;

	public ToolBar(final LogTracker logTracker, final ParserConfigCatalog parserConfigList) {
		this.logTracker = logTracker;
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton btnLoadConfig = new ConfigFileOpenButton(logTracker.getFrame(), null, new ConfigFileAction() {
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
				logTracker.openFileControlWithConfiguration(CLocation.base().normalSouth(0.5), selectedFile, null);
			}
		});
		btnLoadConfig.setToolTipText("Open Config File");
		add(btnLoadConfig);
		
		JButton btnSaveConfig = new ConfigFileSaveButton(logTracker.getFrame(), null, new ConfigFileAction() {
			
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
				saveXMLConfig(selectedFile);
			}
		});
		btnSaveConfig.setToolTipText("Save Config File");
		add(btnSaveConfig);
		
		JButton newFileControlBtn = new JButton(new ImageIcon(ToolBar.class.getResource("/newFileConfig.png")));
		newFileControlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				logTracker.addNewFileControl(CLocation.base().normalSouth(0.5), null, null);
			}
		});
		newFileControlBtn.setToolTipText("Add new Log-File Source Config Box");
		add(newFileControlBtn);
		
		JButton btnConfigLogParsers = new JButton(new ImageIcon(ToolBar.class.getResource("/Zahnrad.png")));
		btnConfigLogParsers.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				ParserConfigDialog dialog = new ParserConfigDialog(parserConfigList);
				dialog.setLogFileTypeList(parserConfigList.getParserTypeList(dialog));
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
				JOptionPane.showMessageDialog(logTracker.getFrame(), new JScrollPane(new WelcomePanel()), "About Log-Tracker", JOptionPane.PLAIN_MESSAGE);
			}
		});
		
		JLabel lblTitle = new JLabel("Title:");
		add(lblTitle);
		
		txtLogtracker = new JTextField();
		txtLogtracker.addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				logTracker.setTitle(txtLogtracker.getText());
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
		txtLogtracker.setToolTipText("Dieser Text wird als Fenstertitel angezeigt");
		String defaultTitle = "LogTracker";
		txtLogtracker.setText(defaultTitle);
		logTracker.setTitle(defaultTitle);
		add(txtLogtracker);
		txtLogtracker.setColumns(20);
		btnInfo.setHorizontalAlignment(SwingConstants.LEFT);
		add(btnInfo);
	}

	private void saveXMLConfig(File selectedFile) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(LogTrackerConfig.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			LogTrackerConfig config = new LogTrackerConfig();
			Global global = new Global();
			global.setTitle(txtLogtracker.getText());
			config.setGlobal(global);
			marshaller.marshal(config, selectedFile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public void setText(String title) {
		txtLogtracker.setText(title);
		logTracker.setTitle(title);
	}

	public String getText() {
		return txtLogtracker.getText();
	}

}
