package org.sper.logtracker.proc;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.Factor;

public class DataPointFactorizer<T extends DataPoint> extends AbstractDataListener<RawDataPoint, T> {
	
	private Factor service = new Factor();

	@SuppressWarnings("unchecked")
	@Override
	public void receiveData(RawDataPoint data) {
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
