package org.sper.logtracker.logreader;


/**
 * Ein Reader, welcher neue Log-Einträge entgegennimmt und Module notifiziert, welche wenn eine neue Verarbeitungseinheit
 * zur Verfügung steht.
 * @author silvan.perego
 *
 */
public interface LogParser extends Cloneable {

	/**
	 * Teilt dem Listener mit, dass eine neue Log-File-Zeile entdeckt wurde.
	 * @param readLine die entdeckte Log-File-Zeile.
	 * @param logLineParser 
	 */
	void scanLine(String readLine, LogLineParser logLineParser, Long obsStart);

	/**
	 * @return true, falls dieser Log-Parser Usernamen liefert.
	 */
	boolean providesUsers();

	String getName();
	
	void setName(String name);
	
	Object clone();
	
}
