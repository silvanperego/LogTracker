package org.sper.logtracker.correlation;

/**
 * Repräsentiert ein Datenpunkt mit Correlation-ID.
 * @author silvan.perego
 *
 */
public interface CorrelatedMessage {

	String getCorrelationId();
	
	Long getOccurrenceTime();
	
	String getLogSource();
	
	String getUser();
	
	String getDescription();
	
	String getDetail();
}
