package org.sper.logtracker.parserconf;

import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.parserconf.ParserConfigList.ChangeListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Führt eine Liste aller Verfügbaren Log-File-Parser und ihrer Eigenschaften.
 * @author silvan.perego
 */
public class ParserSelectionModel extends AbstractListModel<LogParser<?>> implements ComboBoxModel<LogParser<?>>, ChangeListener {
	
	private static final long serialVersionUID = 1L;
	private Object selectedItem;
	private ParserConfigList parserList;
	
	public ParserSelectionModel(ParserConfigList parserList) {
		this.parserList = parserList;
		parserList.addChangeListener(this);
	}

	@Override
	public int getSize() {
		return parserList.size();
	}

	List<ConfiguredLogParser<?,?>> getparserList() {
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

	public void addParsers(ArrayList<ConfiguredLogParser<?,?>> parserList2) {
		parserList.addAll(parserList2);
		fireContentsChanged(this, 0, getSize());
	}
	
	public void unregister() {
		parserList.removeChangeListener(this);
	}

	@Override
	public void modelChanged() {
		fireContentsChanged(this, 0, getSize());
		if (selectedItem != null) {
			String name = ((ConfiguredLogParser<?,?>) selectedItem).getName();
			selectedItem = parserList.stream().filter(p -> p.getName().equals(name)).findAny().orElse(null);
		}
	}

}
