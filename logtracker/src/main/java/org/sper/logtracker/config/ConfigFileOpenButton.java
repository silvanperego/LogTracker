package org.sper.logtracker.config;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;

import org.jfree.ui.FilesystemFilter;

public class ConfigFileOpenButton extends JButton {

	private static final long serialVersionUID = 1L;
	static File configFileDir;
	
	public ConfigFileOpenButton(final Component parent, String buttonText, final ConfigFileAction action, final ActionListener... listeners) {
		super(UIManager.getIcon("FileView.directoryIcon"));
		if (buttonText != null)
			setText(buttonText);
		addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				for (ActionListener actionListener : listeners) {
					actionListener.actionPerformed(e);
				}
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Load Configuration File");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
				FileFilter fileFilter = new FilesystemFilter("ltc", "LogTracker Configuration Files");
				fileChooser.setFileFilter(fileFilter);
				if (configFileDir != null)
					fileChooser.setCurrentDirectory(configFileDir);
				int response = fileChooser.showOpenDialog(parent);
				if (response == JFileChooser.APPROVE_OPTION) {
					try {
						configFileDir = fileChooser.getCurrentDirectory();
						action.execConfigFileOperation(fileChooser.getSelectedFile());
					} catch (Exception ex) {
						ex.printStackTrace();
						JOptionPane.showMessageDialog(parent, ex, "Error while loading Configuration File", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
		
	}

}
