package org.sper.logtracker.erroranalysis.ui;

import org.sper.logtracker.erroranalysis.data.ErrorCategory;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class LogMessageRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		ErrorCategory cat = (ErrorCategory) value;
		JLabel msg = (JLabel) super.getTableCellRendererComponent(table, cat.getLatestMessage().msg, isSelected, hasFocus, row, column);
		if (value != null) {
			msg.setToolTipText(value.toString());
		}
		return msg;
	}

}
