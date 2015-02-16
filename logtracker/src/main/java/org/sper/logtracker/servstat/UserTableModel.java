package org.sper.logtracker.servstat;

import org.sper.logtracker.data.Factor;
import org.sper.logtracker.proc.CategoryCollection;
import org.sper.logtracker.stats.AbstractTableModel;
import org.sper.logtracker.stats.ServiceStats;

public class UserTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	Class<?>[] columnTypes = new Class[] { String.class, Long.class, Double.class,
			Double.class, Double.class, Double.class, Boolean.class };
	
	public UserTableModel() {
		super(new Object[][] {},
				new String[] { "User", "Calls", "Calls Per Minute",
						"Mean Response Time", "Median ", "90% Percentile",
						"Show" });
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	
	public CategoryCollection createUsersFilter(Factor users) {
		CategoryCollection cats = new CategoryCollection();
		for (int i = 0; i < getRowCount(); i++) {
			if (((Boolean)getValueAt(i, 6)).booleanValue())
				cats.addFactoryCat(users.getStringIndex((String)getValueAt(i, 0)));
		}
		return cats;
	}
	
	@Override
	public void createOrReplaceTableRow(Boolean total, ServiceStats stats) {
		Object[] obj = new Object[] {
				null, null, null, null, null, null, Boolean.TRUE
		};
		stats.fillTableModelRow(obj);
		createOrReplaceTableRow(obj);
	}
}
