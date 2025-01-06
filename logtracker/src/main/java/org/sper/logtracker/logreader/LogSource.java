package org.sper.logtracker.logreader;

import jakarta.xml.bind.annotation.XmlAttribute;
import org.sper.logtracker.correlation.data.CorrelatedMessage;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * Repräsentiert eine Log-File-Quelle
 * @author sper
 */
public class LogSource implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileName;
	private String sourceName;
	private static Set<DataPointSelectionAction> selectionActionList = new HashSet<>();

	public static interface DataPointSelectionAction {
		void pointSelected(CorrelatedMessage dp);
	}
	
	public LogSource() {
	}
	
	public LogSource(String fileName, String sourceName) {
		this.fileName = fileName;
		this.sourceName = sourceName;
	}

	public LogSource(String fileName) {
		this.fileName = fileName;
	}

	@XmlAttribute
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fname) {
		this.fileName = fname;
	}

	@XmlAttribute
	public String getSourceName() {
		if (sourceName == null && fileName != null) {
			int pos = fileName.lastIndexOf('/');
			sourceName = pos < 0 || pos >= fileName.length() ? fileName : fileName.substring(pos + 1);			
		}
		return sourceName;
	}
	
	public void setSourceName(String sourceName) {		
		this.sourceName = sourceName;
	}
	
	public Object[] modelEntry() {
		return new Object[] { fileName, getSourceName(), null };
	}

	/**
	 * Registriere eine Action, die aufgerufen werden soll, wenn eine Data-Point dieser
	 * Source-Selektiert wurde.
	 * @param action
	 */
	public void addSelectionAction(DataPointSelectionAction action) {
		selectionActionList.add(action);
	}

	/**
	 * Lösche eine registrierte Action.
	 * @param action
	 */
	public void removeSelectionAction(DataPointSelectionAction action) {
		selectionActionList.remove(action);
	}
	
	public void triggerSelectionActions(CorrelatedMessage dp) {
		selectionActionList.stream().forEach(sa -> sa.pointSelected(dp));
	}

	@Override
	public String toString() {
		return getSourceName();
	}
	
}
