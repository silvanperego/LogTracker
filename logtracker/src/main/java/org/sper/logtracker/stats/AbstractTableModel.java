package org.sper.logtracker.stats;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

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
		@SuppressWarnings("unchecked")
		Vector<Vector<Object>> data = getDataVector();
		for (int i = 0; i < data.size(); i++) {
			Vector<Object> row = data.get(i);
			if (obj[0].equals(row.get(0))) {
				for (int j = 1; j < 6; j++)
					row.setElementAt(obj[j], j);;
				return;
			}
		}
		// Falls so eine Row noch nicht existiert, erstelle eine neue.
		addRow(obj);
	}

}
