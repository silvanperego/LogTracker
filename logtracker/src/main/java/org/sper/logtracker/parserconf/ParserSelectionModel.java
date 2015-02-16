package org.sper.logtracker.parserconf;

import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.logreader.LogParser;

/**
 * Führt eine Liste aller Verfügbaren Log-File-Parser und ihrer Eigenschaften.
 * @author silvan.perego
 */
public class ParserSelectionModel extends AbstractListModel implements ComboBoxModel {
	
	private static final long serialVersionUID = 1L;
	private LogParserList logParserList = new LogParserList();
	private Object selectedItem;
	private DefaultParserProvider defaultParserProvider;
	
	public ParserSelectionModel(DefaultParserProvider defaultParserProvider) {
		this.defaultParserProvider = defaultParserProvider;
		logParserList.addAll(defaultParserProvider.getDefaultLogParsers());
	}

	void saveInSelectionModel(List<ConfiguredLogParser> newLogParser) {
		logParserList.clear();
		logParserList.addAll(defaultParserProvider.getDefaultLogParsers());
		for (ConfiguredLogParser logParser : newLogParser) {
			logParserList.add(logParserList.size() - 1, logParser);
		}
		fireContentsChanged(this, 0, logParserList.size());
	}
	
	@Override
	public int getSize() {
		return logParserList.size();
	}

	List<ConfiguredLogParser> getLogParserList() {
		return logParserList;
	}

	@Override
	public LogParser getElementAt(int index) {
		return logParserList.get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedItem = anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	public void addParsers(List<ConfiguredLogParser> logParserList) {
		this.logParserList.addParserConfigs(logParserList);
		fireContentsChanged(this, 0, getSize());
	}

}
