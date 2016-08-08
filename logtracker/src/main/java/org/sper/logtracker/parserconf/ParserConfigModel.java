package org.sper.logtracker.parserconf;

import java.util.List;

import javax.swing.table.AbstractTableModel;

/**
 * Das Modell der Parser-Konfigurationen. Hier in der Form, wie sie vom ParserConfigDialog benötigt werden.
 * @author silvan.perego
 */
public class ParserConfigModel extends AbstractTableModel {

	public static final int STANDARD_PARSERS_COUNT = 3;
	private static final long serialVersionUID = 1L;
	private ParserConfigList logParserCatalog;
	private ParserConfigList logParserList = new ParserConfigList();
	private static final String[] COL_NAMES = new String[] {"Parser Name", "Parser Type"};
	
	public ParserConfigModel(ParserConfigList logParserCatalog) {
		this.logParserCatalog = logParserCatalog;
	}
	/**
	 * Kopiere die aktuelle Konfiguration. Die Kopie ist notwendig, da Änderungen des Benutzers erst beim Betätigen von
	 * "OK" aktiviert werden sollen.
	 * @throws CloneNotSupportedException 
	 */
	void loadFromSelectionModel() {
		logParserList.clear();
		for (ConfiguredLogParser<?> logParser : logParserCatalog)
			if (logParser.getName() != null)
				logParserList.add((ConfiguredLogParser<?>) logParser.clone());
	}
	
	@Override
	public int getRowCount() {
		return logParserList.size();
	}
	
	@Override
	public int getColumnCount() {
		return 2;
	}
	@Override
	public String getColumnName(int columnIndex) {
		return COL_NAMES[columnIndex];
	}
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return String.class;
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return logParserList.get(rowIndex).isEditable() && columnIndex == 0;
	}
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		ConfiguredLogParser<?> logParser = logParserList.get(rowIndex);
		return columnIndex == 0 ? logParser.getName() : logParser.getLogFileTypeDescriptor().toString();
	}
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		if (columnIndex == 0)
			logParserList.get(rowIndex).setName((String) aValue);
	}

	public ConfiguredLogParser<?> getParser(int i) {
		return logParserList.get(i);
	}
	
	public int addParser(ConfiguredLogParser<?> logParser) {
		logParserList.add(logParser);
		int newRowIdx = logParserList.size() - 1;
		fireTableRowsInserted(newRowIdx, newRowIdx);
		return newRowIdx;
	}
	
	public void deleteRow(int selRowIdx) {
		logParserList.remove(selRowIdx);
		fireTableRowsDeleted(selRowIdx, selRowIdx);
	}
	
	public void addParsers(List<ConfiguredLogParser<?>> logParserList) {
		this.logParserList.addAll(logParserList);
	}
	public void saveInSelectionModel() {
		logParserCatalog.clear();
		logParserCatalog.addAll(logParserList.subList(STANDARD_PARSERS_COUNT, logParserList.size()));
	}
	
	public ConfiguredLogParser<?> replaceRow(int rowIdx, FileTypeDescriptor fileTypeDesc) {
		ConfiguredLogParser<?> convertLogParser = fileTypeDesc.convertLogParser(logParserList.get(rowIdx));
		logParserList.set(rowIdx, convertLogParser);
		return convertLogParser;
	}

}
