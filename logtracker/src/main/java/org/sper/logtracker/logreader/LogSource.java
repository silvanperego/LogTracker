package org.sper.logtracker.logreader;

import java.io.Serializable;

/**
 * Repräsentiert eine Log-File-Quelle
 * @author sper
 */
public class LogSource implements Serializable {

	private static final long serialVersionUID = 1L;
	private String fileName;
	private String sourceName;

	public LogSource() {
	}
	
	public LogSource(String fileName, String sourceName) {
		this.fileName = fileName;
		this.sourceName = sourceName;
	}

	public LogSource(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fname) {
		this.fileName = fname;
	}

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
	
}
