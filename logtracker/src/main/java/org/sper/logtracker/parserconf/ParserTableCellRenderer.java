package org.sper.logtracker.parserconf;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class ParserTableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = 1L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		ConfiguredLogParser<?,?> logParser = ((ParserConfigModel)table.getModel()).getParser(row);
		Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		comp.setEnabled(logParser.isEditable());
		return comp;
	}


}
