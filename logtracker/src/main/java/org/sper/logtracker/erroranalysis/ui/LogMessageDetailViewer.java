package org.sper.logtracker.erroranalysis.ui;

import java.awt.Component;

import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

import org.sper.logtracker.erroranalysis.data.ErrorCategory;

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
		cat = (ErrorCategory) value;
		CategoryViewer viewer = new CategoryViewer(table, cat);
		viewer.setVisible(true);
		stopCellEditing();
		return null;
	}

}
