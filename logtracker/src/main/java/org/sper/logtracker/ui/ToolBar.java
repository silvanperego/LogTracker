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

import org.sper.logtracker.config.ConfigFileAction;
import org.sper.logtracker.config.ConfigFileOpenButton;
import org.sper.logtracker.config.ConfigFileSaveButton;
import org.sper.logtracker.config.Configuration;

import bibliothek.gui.dock.common.CLocation;

public class ToolBar extends JToolBar {
	
	private static final long serialVersionUID = 1L;
	private JTextField txtLogtracker;
	private LogTracker logTracker;

	public ToolBar(final LogTracker logTracker) {
		this.logTracker = logTracker;
		setAlignmentX(Component.LEFT_ALIGNMENT);
		
		JButton btnLoadConfig = new ConfigFileOpenButton(logTracker.getFrame(), null, new ConfigFileAction() {
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
//				logTracker.getConfiguration().loadConfiguration(selectedFile);
			}
		});
		btnLoadConfig.setToolTipText("Open Config File");
		add(btnLoadConfig);
		
		JButton btnSaveConfig = new ConfigFileSaveButton(logTracker.getFrame(), null, new ConfigFileAction() {
			
			@Override
			public void execConfigFileOperation(File selectedFile) throws Exception {
//				logTracker.getConfiguration().safeToFile(selectedFile);
			}
		});
		btnSaveConfig.setToolTipText("Save Config File");
		add(btnSaveConfig);
		
		JButton newFileControlBtn = new JButton(new ImageIcon(ToolBar.class.getResource("/newFileConfig.png")));
		newFileControlBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent paramActionEvent) {
				logTracker.addNewFileControl(new Configuration(), CLocation.base().normalSouth(0.5), null);
			}
		});
		newFileControlBtn.setToolTipText("Add new Log-File Source Config Box");
		add(newFileControlBtn);
		
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

	public void setText(String title) {
		txtLogtracker.setText(title);
		logTracker.setTitle(title);
	}

	public String getText() {
		return txtLogtracker.getText();
	}

}
