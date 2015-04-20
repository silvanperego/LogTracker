package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;

public class StatsDataPointFactorizer<T extends DataPoint> extends AbstractDataListener<RawStatsDataPoint, T> {
	
	private Factor service = new Factor();

	@SuppressWarnings("unchecked")
	@Override
	public void receiveData(RawStatsDataPoint data) {
		DataPoint dp = new DataPoint(service.addString(data.service), data.occTime, data.value);
		sendToListeners((T) dp);
	}

	public Factor getService() {
		return service;
	}

	public Factor getUser() {
		return null;
	}

}
