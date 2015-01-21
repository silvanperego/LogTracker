package org.sper.logtracker.parserconf;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.sper.logtracker.logreader.ConfiguredLogParser;

/**
 * Das Modell der Parser-Konfigurationen. Hier in der Form, wie sie vom ParserConfigDialog benötigt werden.
 * @author silvan.perego
 */
public class ParserConfigModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private ParserSelectionModel parserSelectionModel;
	private LogParserList logParserList = new LogParserList();
	
	public ParserConfigModel(ParserSelectionModel parserSelectionModel) {
		this.parserSelectionModel = parserSelectionModel;
	}
	/**
	 * Kopiere die aktuelle Konfiguration. Die Kopie ist notwendig, da Änderungen des Benutzers erst beim Betätigen von
	 * "OK" aktiviert werden sollen.
	 * @throws CloneNotSupportedException 
	 */
	void loadFromSelectionModel() {
		logParserList.clear();
		List<ConfiguredLogParser> selList = parserSelectionModel.getLogParserList();
		for (ConfiguredLogParser logParser : selList)
			if (logParser.getName() != null)
				logParserList.add((ConfiguredLogParser) logParser.clone());
	}
	
	@Override
	public int getRowCount() {
		return logParserList.size();
	}
	
	@Override
	public int getColumnCount() {
		return 1;
	}
	@Override
	public String getColumnName(int columnIndex) {
		return "Parser Name";
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return logParserList.get(rowIndex).isEditable();
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return logParserList.get(rowIndex).getName();
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		logParserList.get(rowIndex).setName((String) aValue);
	}

	public ConfiguredLogParser getParser(int i) {
		return logParserList.get(i);
	}
	
	public int addParser(ConfiguredLogParser logParser) {
		logParserList.add(logParser);
		int newRowIdx = logParserList.size() - 1;
		fireTableRowsInserted(newRowIdx, newRowIdx);
		return newRowIdx;
	}
	
	public void deleteRow(int selRowIdx) {
		logParserList.remove(selRowIdx);
		fireTableRowsDeleted(selRowIdx, selRowIdx);
	}
	
	public void addParsers(List<ConfiguredLogParser> logParserList) {
		this.logParserList.addParserConfigs(logParserList);
	}
	public void saveInSelectionModel() {
		parserSelectionModel.saveInSelectionModel(logParserList.subList(2, logParserList.size()));
	}

}
