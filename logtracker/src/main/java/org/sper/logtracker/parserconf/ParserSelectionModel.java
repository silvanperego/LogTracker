package org.sper.logtracker.parserconf;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

import org.sper.logtracker.logreader.LogParser;

/**
 * Führt eine Liste aller Verfügbaren Log-File-Parser und ihrer Eigenschaften.
 * @author silvan.perego
 */
public class ParserSelectionModel extends AbstractListModel<LogParser<?>> implements ComboBoxModel<LogParser<?>> {
	
	private static final long serialVersionUID = 1L;
	private Object selectedItem;
	private List<ConfiguredLogParser<?>> parserList;
	
	public ParserSelectionModel(ParserConfigList parserList) {
		this.parserList = parserList;
		parserList.addChangeListener(() -> fireContentsChanged(this, 0, getSize()));
	}

	@Override
	public int getSize() {
		return parserList.size();
	}

	List<ConfiguredLogParser<?>> getparserList() {
		return parserList;
	}

	@Override
	public LogParser<?> getElementAt(int index) {
		return parserList.get(index);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		this.selectedItem = anItem;
	}

	@Override
	public Object getSelectedItem() {
		return selectedItem;
	}

	public void addParsers(ArrayList<ConfiguredLogParser<?>> parserList2) {
		parserList.addAll(parserList2);
		fireContentsChanged(this, 0, getSize());
	}

}
