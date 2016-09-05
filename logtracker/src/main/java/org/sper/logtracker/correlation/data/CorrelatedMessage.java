package org.sper.logtracker.correlation.data;

import org.sper.logtracker.logreader.LogSource;

/**
 * Repräsentiert ein Datenpunkt mit Correlation-ID.
 * @author silvan.perego
 *
 */
public interface CorrelatedMessage {

	String getCorrelationId();
	
	Long getOccurrenceTime();
	
	LogSource getLogSource();
	
	String getUser();
	
	String getDescription();
	
	String getDetail();
	
	default Double getResponseTime() {
		return null;
	}
}
