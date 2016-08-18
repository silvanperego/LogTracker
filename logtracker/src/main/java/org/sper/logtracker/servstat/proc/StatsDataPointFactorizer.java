package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;

public class StatsDataPointFactorizer extends AbstractDataListener<RawStatsDataPoint, DataPoint> {

	private Factor service = new Factor();
	private Factor user = new Factor();

	@Override
	public void receiveData(RawStatsDataPoint data) {
		DataPoint dp = new DataPoint(service.addString(data.service), data.occTime, data.value,
				data.user != null ? user.addString(data.user) : null, data.returnCode, data.logSource,
						data.correlationId);
		sendToListeners(dp);
	}

	public Factor getService() {
		return service;
	}

	public Factor getUser() {
		return user;
	}

}
