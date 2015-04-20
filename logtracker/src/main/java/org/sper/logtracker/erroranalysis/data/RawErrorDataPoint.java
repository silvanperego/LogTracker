package org.sper.logtracker.erroranalysis.data;

import org.sper.logtracker.data.RawDataPoint;

public class RawErrorDataPoint extends RawDataPoint {

	final public String severity;
	final public String msg;
	/**
	 * @param occTime
	 * @param user
	 * @param severity
	 * @param msg
	 */
	public RawErrorDataPoint(Long occTime, String user, String severity, String msg) {
		super(occTime, user);
		this.severity = severity;
		this.msg = msg;
	}

}
