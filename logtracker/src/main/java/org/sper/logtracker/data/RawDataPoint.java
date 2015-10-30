package org.sper.logtracker.data;

public class RawDataPoint {

	public final Long occTime;
	public final String user;
	public final String logSource;
	/**
	 * @param occTime
	 * @param user
	 */
	public RawDataPoint(Long occTime, String user, String logSource) {
		this.occTime = occTime;
		this.user = user;
		this.logSource = logSource;
	}

}