package org.sper.logtracker.servstat;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ButtonColumn implements TableCellRenderer, TableCellEditor {

	private ActionListener aa;
	private Map<Integer, IndexedButton> buttonMap = new HashMap<Integer, IndexedButton>();
	private Icon delIcon;
	
	public static class IndexedButton extends JButton {
		
		private static final long serialVersionUID = 1L;
		private int row;
		
		IndexedButton(int row, Icon icon) {
			super(icon);
			this.row = row;
		}

		public int getRow() {
			return row;
		}
		
	}

	public ButtonColumn(ActionListener a) {
		this.aa = a;
		delIcon = new ImageIcon(ButtonColumn.class.getResource("/delFile.png"));
	}
	
	private IndexedButton getOrCreateButton(int row) {
		IndexedButton button = buttonMap.get(row);
		if (button == null) {
			button = new IndexedButton(row, delIcon);
			button.addActionListener(aa);
			button.setPreferredSize(new Dimension(16, 16));
			buttonMap.put(row, button);
		}
		return button;
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		return getOrCreateButton(row);
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value,
			boolean isSelected, int row, int column) {
		return getOrCreateButton(row);
	}

	@Override
	public boolean isCellEditable(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean shouldSelectCell(EventObject anEvent) {
		return true;
	}

	@Override
	public boolean stopCellEditing() {
		return true;
	}

	@Override
	public void cancelCellEditing() {
	}

	@Override
	public void addCellEditorListener(CellEditorListener l) {
	}

	@Override
	public void removeCellEditorListener(CellEditorListener l) {
	}

}
