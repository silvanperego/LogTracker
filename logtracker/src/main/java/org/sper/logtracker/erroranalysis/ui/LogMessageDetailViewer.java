package org.sper.logtracker.erroranalysis.ui;

import java.awt.Component;
import java.util.Date;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import org.sper.logtracker.erroranalysis.data.ErrorCategory;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;

public class LogMessageDetailViewer extends AbstractCellEditor implements
		TableCellEditor {

	private static final long serialVersionUID = 1L;
	private ErrorCategory cat;

	/**
	 * @param owner
	 */
	public LogMessageDetailViewer() {
	}

	@Override
	public Object getCellEditorValue() {
		return cat;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		CategoryViewer viewer = new CategoryViewer(table);
		DefaultTableModel tableModel = viewer.getTableModel();
		cat = (ErrorCategory) value;
		for (RawErrorDataPoint dp : cat) {
			tableModel.addRow(new Object[] {
					new Date(dp.occTime),
					dp.user,
					dp
			});
		}
		viewer.setVisible(true);
		stopCellEditing();
		return null;
	}

}
