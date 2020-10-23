package org.sper.logtracker.servstat.stats;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import org.sper.logtracker.servstat.ui.ServiceControlTableModel;

public abstract class AbstractTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	public AbstractTableModel() {
		super();
	}

	public AbstractTableModel(int rowCount, int columnCount) {
		super(rowCount, columnCount);
	}

	public AbstractTableModel(@SuppressWarnings("rawtypes") Vector columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	public AbstractTableModel(Object[] columnNames, int rowCount) {
		super(columnNames, rowCount);
	}

	@SuppressWarnings("rawtypes")
	public AbstractTableModel(Vector data, Vector columnNames) {
		super(data, columnNames);
	}

	public AbstractTableModel(Object[][] data, Object[] columnNames) {
		super(data, columnNames);
	}

	public abstract void createOrReplaceTableRow(Boolean total, ServiceStats stats);

	protected void createOrReplaceTableRow(Object[] obj) {
		// Suche im bestehenden TableModel nach einer bereits bestehenden Row
		@SuppressWarnings("rawtypes")
		Vector data = getDataVector();
		for (int i = 0; i < data.size(); i++) {
			@SuppressWarnings("unchecked")
			Vector<Object> row = (Vector<Object>) data.get(i);
			if (obj[0].equals(row.get(0))) {
				for (int j = 1; j <= ServiceControlTableModel.LAST_STAT_COL; j++)
					row.setElementAt(obj[j], j);;
				return;
			}
		}
		// Falls so eine Row noch nicht existiert, erstelle eine neue.
		addRow(obj);
	}

}
