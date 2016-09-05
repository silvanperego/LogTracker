package org.sper.logtracker.data;

import org.sper.logtracker.logreader.LogSource;

public class RawDataPoint {

	public final Long occTime;
	public final String user;
	public final LogSource logSource;
	public final String correlationId;

	/**
	 * Erzeuge einen Raw Data Point
	 * @param occTime der Zeitpunkt des Auftretens der Meldung.
	 * @param user der Benutzer, der die Meldung bewirkt hat.
	 * @param logSource der Name der Quelle der Meldung gemäss Konfiguration.
	 * @param correlationId die Korrelations-ID der Meldung. Diese dient der Zuordnung von Meldungen zu Abläufen im System.
	 */
	public RawDataPoint(Long occTime, String user, LogSource logSource, String correlationId) {
		this.occTime = occTime;
		this.user = user;
		this.logSource = logSource;
		this.correlationId = correlationId;
	}

}