package org.sper.logtracker.logreader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.sper.logtracker.data.Console;

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

	public String getContents() {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.getChannel().position(pos);
			byte[] buff;
			if (endPos > pos)
				buff = new byte[(int)(endPos - pos)];
			else
				buff = new byte[(int)(file.length() - pos)];
			fis.read(buff);
			return new String(buff);
		} catch (IOException e) {
			Console.addMessage("Error while reading Log-File Content: ");
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
				}
		}
		return null;
	}
	
}
