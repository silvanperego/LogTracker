package org.sper.logtracker.servstat.proc;

public class UserDataPoint extends DataPoint {

	private static final long serialVersionUID = 1L;
	public final Integer userIdx;

	public UserDataPoint(Integer svcIdx, Integer user, Long occTime, Double execTime) {
		super(svcIdx, occTime, execTime);
		this.userIdx = user;
	}

}
