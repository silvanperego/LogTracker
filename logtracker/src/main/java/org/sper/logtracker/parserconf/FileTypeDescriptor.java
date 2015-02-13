package org.sper.logtracker.parserconf;

import javax.swing.JPanel;

/**
 * Eine Beschreibung einer spezifischen Log-File-Typs, wie z.B. ServiceResponseTime-Logs oder Error-Logs.
 * @author silvan.perego
 */
public class FileTypeDescriptor {

	/**
	 * @return ein JPanel mit den Konfigurationsfeldern für diesen Log-File-Type.
	 */
	private JPanel extractionFieldPanel;
	
	/**
	 * @return ein Handler, welcher die Logik für das extractionFieldPanel zur Verfügung stellt.
	 */
	private ExtractionFieldHandler extractionFieldHandler;

	/**
	 * @return Der Name dieses Log-File-Typs.
	 */
	private String toString;

	/**
	 * Konstruktor.
	 * @param extractionFieldPanel
	 * @param extractionFieldHandler
	 * @param toString
	 */
	public FileTypeDescriptor(JPanel extractionFieldPanel,
			ExtractionFieldHandler extractionFieldHandler, String toString) {
		this.extractionFieldPanel = extractionFieldPanel;
		this.extractionFieldHandler = extractionFieldHandler;
		this.toString = toString;
	}

	public JPanel getExtractionFieldPanel() {
		return extractionFieldPanel;
	}

	public ExtractionFieldHandler getExtractionFieldHandler() {
		return extractionFieldHandler;
	}

	@Override
	public String toString() {
		return toString;
	}
	
}
