package org.sper.logtracker.erroranalysis.data;

import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.FileSnippet;

/**
 * Beinhaltet eine gefundene Fehlermeldung. Ein Objekt dieser Klasse beinhaltet nur die erste Zeile
 * der effektiven Meldung, sowie eine Beschreibung des zugrundeliegenden File-Ausschnitts. Damit soll
 * verhindert werden, dass Log-Files in ihrer Gesamtheit geladen werden müssen.
 * 
 * @author silvan.perego
 */
public class RawErrorDataPoint extends RawDataPoint {

	final public String severity;
	final public String msg;
	final public FileSnippet fileSnippet;
	/**
	 * @param occTime der Zeitstempel der Meldung
	 * @param user die User-ID, falls vorhanden
	 * @param severity der Schweregrad der Meldung
	 * @param msg der Meldungstext.
	 * @param fileSnippet Der File-Ausschnitt, welcher diese Fehlermeldung repräsentiert.
	 */
	public RawErrorDataPoint(Long occTime, String user, String severity, String msg, FileSnippet fileSnippet) {
		super(occTime, user);
		this.severity = severity;
		this.msg = msg;
		this.fileSnippet = fileSnippet;
	}

}
