package org.sper.logtracker.proc;

import org.sper.logtracker.data.AbstractDataListener;

/**
 * Zählt die Services in einer {@link DataPointGroup} und leitet das Resultat weiter.
 * @author sper
 */
public class CallsPerTime extends AbstractDataListener<DataPointGroup, DataPoint> {

	private long timespan;

	public CallsPerTime(long timespan) {
		this.timespan = timespan;
	}
	
	@Override
	public void receiveData(DataPointGroup dpg) {
		sendToListeners(new DataPoint(null, dpg.getMidTime(), (double) dpg.size() / dpg.getTimespan() * timespan));
	}
	
}
