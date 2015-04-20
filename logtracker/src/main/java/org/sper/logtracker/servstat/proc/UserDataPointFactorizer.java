package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;


public class UserDataPointFactorizer extends StatsDataPointFactorizer<UserDataPoint> {
	
	private Factor user = new Factor();
	
	@Override
	public void receiveData(RawStatsDataPoint data) {
		UserDataPoint dp = new UserDataPoint(getService().addString(data.service), user.addString(data.user), data.occTime, data.value);
		sendToListeners(dp);
	}

	@Override
	public Factor getUser() {
		return user;
	}

}
