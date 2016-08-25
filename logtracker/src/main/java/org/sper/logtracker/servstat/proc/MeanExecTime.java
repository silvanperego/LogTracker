package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.AbstractDataListener;

/**
 * Berechne die Durchschnittliche Antwortzeit der Services in einer {@link DataPointGroup} und leitet das Resultat weiter.
 * @author sper
 */
public class MeanExecTime extends AbstractDataListener<DataPointGroup, DataPoint> {

	private double magFact;

	public MeanExecTime(double magFact) {
		this.magFact = magFact;
	}
	
	@Override
	public void receiveData(DataPointGroup dpg) {
		double sum = 0;
		for (DataPoint dp : dpg) {
			sum += dp.responseTime;
		}
		sendToListeners(new DataPoint(null, dpg.getMidTime(), sum / dpg.size() * magFact, null, null, null, null));
	}
	
}
