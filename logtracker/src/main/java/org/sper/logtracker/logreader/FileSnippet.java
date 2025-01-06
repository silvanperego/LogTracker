package org.sper.logtracker.logreader;

import org.sper.logtracker.data.Console;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Beschreibt eine einen Ausschnitt aus einem File deren Startposition und erste Zeile. 
 * @author silvan.perego
 */
public class FileSnippet {

	final public File file;
	final private long pos;
	private long endPos;
	final public String line;
	private String encoding;
	
	/**
	 * @param file
	 * @param pos
	 * @param line
	 */
	public FileSnippet(File file, long pos, String line, String encoding) {
		this.file = file;
		this.pos = pos;
		this.line = line;
		this.encoding = encoding;
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
			return new String(buff, encoding);
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
