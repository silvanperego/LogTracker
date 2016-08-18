package org.sper.logtracker.correlation.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.sper.logtracker.correlation.data.CorrelatedMessage;
import org.sper.logtracker.correlation.data.CorrelationCatalog;

public class CorrelatedMessagesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] COL_NAMES = {"Time", "Log-Source", "User", "Description"};
	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm.ss.SSS");
	
	private List<? extends CorrelatedMessage> msgList;

	public CorrelatedMessagesTableModel(String correlationId) {
		CorrelationCatalog<?> catalog = CorrelationCatalog.getInstance();
		msgList = catalog.getMessagesForCategory(correlationId);
	}
	
	@Override
	public int getRowCount() {
		return msgList.size();
	}

	@Override
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		CorrelatedMessage msg = msgList.get(rowIndex);
		switch (columnIndex) {
		case 0: return sdf.format(new Date(msg.getOccurrenceTime()));
		case 1: return msg.getLogSource();
		case 2: return msg.getUser();
		case 3: return msg.getDescription();
		}
		return null;
	}

	@Override
	public String getColumnName(int column) {
		return COL_NAMES[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}

	public CorrelatedMessage getDataPoint(int selectedRow) {
		if (selectedRow >= 0) {
			return msgList.get(selectedRow);
		}
		return null;
	}

}
