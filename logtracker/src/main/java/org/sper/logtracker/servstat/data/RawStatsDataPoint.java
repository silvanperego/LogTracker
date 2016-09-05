package org.sper.logtracker.servstat.data;

import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.LogSource;

/**
 * Ein Datenpunkt, bei welchem Service-Namen und User-Namen noch als String und nicht als Factor vorliegen.
 * @author silvan.perego
 *
 */
public class RawStatsDataPoint extends RawDataPoint {

	final public Double value;
	final public String service;
	final public Integer returnCode;

	public RawStatsDataPoint(Long occTime, Double value, String service, String user, Integer returnCode, LogSource logSource, String correlationId) {
		super(occTime, user, logSource, correlationId);
		this.value = value;
		this.service = service;
		this.returnCode = returnCode;
	}
	
}
