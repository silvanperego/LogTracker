package org.sper.logtracker.erroranalysis.data;

import org.sper.logtracker.correlation.data.CorrelatedMessage;
import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.FileSnippet;
import org.sper.logtracker.logreader.LogSource;

/**
 * Beinhaltet eine gefundene Fehlermeldung. Ein Objekt dieser Klasse beinhaltet nur die erste Zeile
 * der effektiven Meldung, sowie eine Beschreibung des zugrundeliegenden File-Ausschnitts. Damit soll
 * verhindert werden, dass Log-Files in ihrer Gesamtheit geladen werden müssen.
 * 
 * @author silvan.perego
 */
public class RawErrorDataPoint extends RawDataPoint implements CorrelatedMessage {

	final public String severity;
	final public String msg;
	final public FileSnippet fileSnippet;
	/**
	 * @param occTime der Zeitstempel der Meldung
	 * @param user die User-ID, falls vorhanden
	 * @param severity der Schweregrad der Meldung
	 * @param msg der Meldungstext.
	 * @param fileSnippet Der File-Ausschnitt, welcher diese Fehlermeldung repräsentiert.
	 * @param correlationId die Korrelations-ID der Meldung.
	 */
	public RawErrorDataPoint(Long occTime, String user, String severity, String msg, LogSource logSource, FileSnippet fileSnippet, String correlationId) {
		super(occTime, user, logSource, correlationId);
		this.severity = severity;
		this.msg = msg;
		this.fileSnippet = fileSnippet;
	}

	@Override
	public String toString() {
		return msg;
	}

	@Override
	public String getCorrelationId() {
		return correlationId;
	}

	@Override
	public Long getOccurrenceTime() {
		return occTime;
	}

	@Override
	public LogSource getLogSource() {
		return logSource;
	}

	@Override
	public String getUser() {
		return user;
	}

	@Override
	public String getDescription() {
		return msg;
	}

	@Override
	public String getDetail() {
		return fileSnippet.getContents();
	}

}
