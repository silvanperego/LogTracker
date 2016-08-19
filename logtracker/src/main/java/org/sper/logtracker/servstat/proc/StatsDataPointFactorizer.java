package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;

public abstract class StatsDataPointFactorizer<T extends DataPoint> extends AbstractDataListener<RawStatsDataPoint, T> {

	private Factor service = new Factor();
	private Factor user = new Factor();

	public static class SimpleStatsDataPointFactorizer extends StatsDataPointFactorizer<DataPoint> {

		@Override
		public void receiveData(RawStatsDataPoint data) {
			sendToListeners(new DataPoint(getService().addString(data.service), data.occTime, data.value,
					data.user != null ? getUser().addString(data.user) : null, data.returnCode, data.logSource,
					data.correlationId));
		}
	}

	public static class CorrelatedStatsDataPointFactorizer extends StatsDataPointFactorizer<CorrelatedServiceDataPoint> {

		@Override
		public void receiveData(RawStatsDataPoint data) {
			sendToListeners(new CorrelatedServiceDataPoint(getService().addString(data.service), data.occTime, data.value,
					data.user != null ? getUser().addString(data.user) : null, data.returnCode, data.logSource,
					data.correlationId, this));
		}

	}

	public Factor getService() {
		return service;
	}

	public Factor getUser() {
		return user;
	}

}
