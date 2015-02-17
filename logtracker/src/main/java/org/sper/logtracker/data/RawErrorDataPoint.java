package org.sper.logtracker.data;

public class RawErrorDataPoint {

	final public Long occTime;
	final public String user;
	final public String severity;
	final public String msg;
	/**
	 * @param occTime
	 * @param user
	 * @param severity
	 * @param msg
	 */
	public RawErrorDataPoint(Long occTime, String user, String severity,
			String msg) {
		this.occTime = occTime;
		this.user = user;
		this.severity = severity;
		this.msg = msg;
	}

}
