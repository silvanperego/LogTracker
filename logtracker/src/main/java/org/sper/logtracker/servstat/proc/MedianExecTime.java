package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.AbstractDataListener;

/**
 * Berechne den Median der Antwortzeit der Services in einer {@link DataPointGroup} und leitet das Resultat weiter.
 * @author sper
 */
public class MedianExecTime extends AbstractDataListener<DataPointGroup, DataPoint> {

	private double magFact;

	public MedianExecTime(double magFact) {
		this.magFact = magFact;
	}
	
	@Override
	public void receiveData(DataPointGroup dpg) {
		dpg.sort();
		int halfSize = dpg.size() / 2;
		double median = dpg.size() % 1 == 0 ? dpg.get(halfSize).value : 0.5 * (dpg.get(halfSize).value + dpg.get(halfSize + 1).value);
		sendToListeners(new DataPoint(null, dpg.getMidTime(), median * magFact, null, null, null));
	}
	
}
