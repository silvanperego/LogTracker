package org.sper.logtracker.servstat.proc;

import org.jfree.data.xy.XYDataItem;

/**
 * Repräsentiert einen einzelnen Request mit Auftretenszeit und Ausführungsdauer.
 * @author silvan.perego
 *
 */
public class DataPoint extends XYDataItem {
	
	private static final long serialVersionUID = 1L;
	final public Long occTime;
	final public Double value;
	final public Integer svcIdx;
	final public Integer user;
	final public Integer returnCode;
	
	public DataPoint(Integer svcIdx, Long occTime, Double execTime, Integer user, Integer returnCode) {
		super(occTime, execTime);
		this.svcIdx = svcIdx;
		this.occTime = occTime;
		this.value = execTime;
		this.user = user;
		this.returnCode = returnCode;
	}

}
