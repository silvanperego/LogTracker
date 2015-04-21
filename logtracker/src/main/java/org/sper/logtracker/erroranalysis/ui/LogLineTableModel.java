package org.sper.logtracker.erroranalysis.ui;

import javax.swing.table.DefaultTableModel;

public final class LogLineTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	Class[] columnTypes = new Class[] { String.class, String.class };
	boolean[] columnEditables = new boolean[] { false, false };

	LogLineTableModel() {
		super(new String[] { "Severity", "Content" }, 0);
	}

	public Class getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	public boolean isCellEditable(int row, int column) {
		return columnEditables[column];
	}
}