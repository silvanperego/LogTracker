package org.sper.logtracker.erroranalysis.ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.table.DefaultTableModel;

import org.sper.logtracker.erroranalysis.data.ErrorCategory;

public final class LogLineTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;
	Class<?>[] columnTypes = new Class[] { String.class, String.class, Integer.class, String.class };

	LogLineTableModel() {
		super(new String[] { "Severity", "Latest occurrence", "# of Messages", "Latest Content" }, 0);
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}

	public void setRowColor(Component comp, int row) {
	  try {
	    Color relevanceColor = ((ErrorCategory) getValueAt(row, 3)).getRelevanceColor();
	    comp.setBackground(relevanceColor);
	  } catch (Exception e) {
	    e.printStackTrace();
	  }
	}

  @Override
  public boolean isCellEditable(int row, int column) {
    return false;
  }

}