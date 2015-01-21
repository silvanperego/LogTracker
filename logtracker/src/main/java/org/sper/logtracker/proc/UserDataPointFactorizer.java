package org.sper.logtracker.proc;

import org.sper.logtracker.data.Factor;


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
