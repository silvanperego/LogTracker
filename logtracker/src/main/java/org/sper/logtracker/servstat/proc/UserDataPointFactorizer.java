package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.Factor;
import org.sper.logtracker.data.RawDataPoint;


public class UserDataPointFactorizer extends DataPointFactorizer<UserDataPoint> {
	
	private Factor user = new Factor();
	
	@Override
	public void receiveData(RawDataPoint data) {
		UserDataPoint dp = new UserDataPoint(getService().addString(data.service), user.addString(data.user), data.occTime, data.value);
		sendToListeners(dp);
	}

	@Override
	public Factor getUser() {
		return user;
	}

}
