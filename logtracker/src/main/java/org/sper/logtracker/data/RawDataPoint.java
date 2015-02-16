package org.sper.logtracker.data;

/**
 * Ein Datenpunkt, bei welchem Service-Namen und User-Namen noch als String und nicht als Factor vorliegen.
 * @author silvan.perego
 *
 */
public class RawDataPoint {

	final public Long occTime;
	final public Double value;
	final public String service;
	final public String user;

	public RawDataPoint(Long occTime, Double value, String service, String user) {
		this.occTime = occTime;
		this.value = value;
		this.service = service;
		this.user = user;
	}
	
}
