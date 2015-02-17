package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.Console;

/**
 * Gruppiert Datenpunkte und fasst die in Gruppen desselben Zeitschnitts zusammen.
 * @author sper
 */
public class TimeBasedCollector extends AbstractDataListener<DataPoint, DataPointGroup> {

	private long lasttime = 0, numpoints = 0;
	private long timeFrame;
	private long minPoints;
	private DataPointGroup ptGroup = new DataPointGroup();
	
	/**
	 * Konstruktor.
	 * @param timeFrame das ZeitInterval, in dem Punkte gesammelt und zu einer Liste zusammengefasst werden.
	 * @param minPoints werden in einem Time-Frame weniger als minPoints gesammelt, dann wird das Frame nicht abgeschlossen,
	 * 		sondern eine zusätzliche Zeitspanne von timeFrame abgewartet.
	 */
	public TimeBasedCollector(long timeFrame, long minPoints) {
		this.timeFrame = timeFrame;
		this.minPoints = minPoints;
	}
	
	@Override
	public void receiveData(DataPoint dp) {
		long time = dp.occTime;
		if (time < lasttime) {
			Console.addMessage("Data point was received out of temporal order:" + dp + '\n');
		} else {
			long roundtime = time - time % timeFrame;
			if (roundtime > lasttime) {
				if (numpoints >= minPoints) {
					ptGroup.setTimeFrame(lasttime, roundtime);
					sendToListeners(ptGroup);
					ptGroup = new DataPointGroup();
					lasttime = roundtime;
					numpoints = 0;
				} else if (numpoints == 0)
					lasttime = roundtime;
			}
			ptGroup.add(dp);
			numpoints++;
		}
	}
	
}
