package org.sper.logtracker.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Ein Factor bildet String-Datenwerte ab, indem er die Texte in einer Wertetabelle als Kategorien
 * erfasst und in der eigentlichen Liste nur noch die entsprechenden Indices abbildet.
 * Dies erlaubt einerseits eine effiziente Speicherung der Werte. Andererseits ist die Behandlung ähnlicher,
 * als wenn die Strings abgelegt würden.
 * @author silvan.perego
 *
 */
public class Factor implements Iterable<String> {

	private List<String> labels = new ArrayList<String>();
	private List<NewCategoryListener> listenerList = new ArrayList<NewCategoryListener>();
	
	public int addString(String val) {
		int indexOf = labels.indexOf(val);
		if (indexOf < 0) {
			labels.add(val);
			indexOf = labels.size() - 1;
			for (NewCategoryListener listener : listenerList) {
				listener.newCategory(val, indexOf);
			}
		}
		return indexOf;
	}
	
	public String getLabel(int idx) {
		return labels.get(idx);
	}
	
	public int getStringIndex(String val) {
		return labels.indexOf(val);
	}
	
	public int nEntry() {
		return labels.size();
	}
	
	public void addCategoryListener(NewCategoryListener listener) {
		listenerList.add(listener);
	}

	@Override
	public Iterator<String> iterator() {
		return labels.iterator();
	}
	
}
