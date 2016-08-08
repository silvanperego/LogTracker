package org.sper.logtracker.config.compat;

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

public class ConfigFileSaveButton extends JButton {

	private static final long serialVersionUID = 1L;

	public ConfigFileSaveButton(final Component parent, String buttonText, final ConfigFileAction action, final ActionListener... listeners) {
		super(UIManager.getIcon("FileView.floppyDriveIcon"));
		if (buttonText != null)
			setText(buttonText);
		addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				for (ActionListener actionListener : listeners) {
					actionListener.actionPerformed(e);
				}
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Safe Configuration to File");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
				FileFilter fileFilter = new FilesystemFilter("ltc", "LogTracker Configuration Files");
				fileChooser.setFileFilter(fileFilter);
				if (ConfigFileOpenButton.configFileDir != null)
					fileChooser.setCurrentDirectory(ConfigFileOpenButton.configFileDir);
				int response = fileChooser.showSaveDialog(parent);
				if (response == JFileChooser.APPROVE_OPTION) {
					try {
						ConfigFileOpenButton.configFileDir = fileChooser.getCurrentDirectory();
						File selectedFile = fileChooser.getSelectedFile();
						if (!selectedFile.exists() || 
								JOptionPane.showConfirmDialog(
										parent, 
										"File \"" + selectedFile.getName() + "\" already exists. Do you want to overwrite?", 
										"Confirm Save", 
										JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION)
							action.execConfigFileOperation(selectedFile);
					} catch (Exception saveex) {
						saveex.printStackTrace();
						JOptionPane.showMessageDialog(parent, saveex, "Error while writing Configuration File", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});
	}
}
