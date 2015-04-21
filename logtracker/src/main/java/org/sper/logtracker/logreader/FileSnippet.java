package org.sper.logtracker.logreader;

import java.io.File;

/**
 * Beschreibt eine einen Ausschnitt aus einem File deren Startposition und erste Zeile. 
 * @author silvan.perego
 */
public class FileSnippet {

	final public File file;
	final private long pos;
	private long endPos;
	final public String line;
	
	/**
	 * @param file
	 * @param pos
	 * @param line
	 */
	public FileSnippet(File file, long pos, String line) {
		this.file = file;
		this.pos = pos;
		this.line = line;
	}
	
	public void setEndPos(FileSnippet nextElement) {
		endPos = nextElement.pos;
	}
	
}
