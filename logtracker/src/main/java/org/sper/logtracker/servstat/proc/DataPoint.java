package org.sper.logtracker.servstat.proc;

import org.jfree.data.xy.XYDataItem;

/**
 * Repräsentiert einen einzelnen Request mit Auftretenszeit und Ausführungsdauer.
 * @author silvan.perego
 *
 */
public class DataPoint extends XYDataItem {
	
	private static final long serialVersionUID = 1L;
	public final Long occTime;
	public final Double responseTime;
	public final String logSource;
	public final Integer svcIdx;
	public final Integer user;
	public final Integer returnCode;
	public final String correlationId;
	
	public DataPoint(Integer svcIdx, Long occTime, Double execTime, Integer user, Integer returnCode, String source, String correlationId) {
		super(occTime, execTime);
		this.svcIdx = svcIdx;
		this.logSource = source;
		this.occTime = occTime;
		this.responseTime = execTime;
		this.user = user;
		this.returnCode = returnCode;
		this.correlationId = correlationId;
	}

}
