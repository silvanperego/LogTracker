package org.sper.logtracker.ui;

import org.jfree.ui.FilesystemFilter;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.io.File;

class LogFileTableEditor extends AbstractCellEditor implements TableCellEditor {

	private static final long serialVersionUID = 1L;
	private Object editorVal;
	private JFrame frame;
	private static File logFileDir;
	
	public LogFileTableEditor(JFrame frame) {
		this.frame = frame;
	}
	
	@Override
	public Object getCellEditorValue() {
		return editorVal;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		File[] logFiles = selectLogFile(frame, (String) value, false);
		if (logFiles != null) {
			editorVal = logFiles[0].getPath();
			table.getModel().setValueAt(editorVal, row, column);
		}
		stopCellEditing();
		return null;
	}

	static File[] selectLogFile(JFrame frame, String selectedFile, boolean multiSelectionEnabled) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select a Log-File");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		if (selectedFile != null)
			fileChooser.setSelectedFile(new File(selectedFile));
		FileFilter fileFilter = new FilesystemFilter("log", "Log-Files");
		fileChooser.setFileFilter(fileFilter);
		fileChooser.setMultiSelectionEnabled(multiSelectionEnabled);
		if (logFileDir != null)
			fileChooser.setCurrentDirectory(logFileDir);
		int response = fileChooser.showOpenDialog(frame);
		if (response == JFileChooser.APPROVE_OPTION) {
			logFileDir = fileChooser.getCurrentDirectory();
			if (multiSelectionEnabled)
				return fileChooser.getSelectedFiles();
			else
				return new File[] {fileChooser.getSelectedFile()};
		}
		return null;
	}
}
