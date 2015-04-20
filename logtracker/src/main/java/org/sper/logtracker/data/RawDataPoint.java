package org.sper.logtracker.data;

public class RawDataPoint {

	public final Long occTime;
	public final String user;
	/**
	 * @param occTime
	 * @param user
	 */
	public RawDataPoint(Long occTime, String user) {
		this.occTime = occTime;
		this.user = user;
	}

}