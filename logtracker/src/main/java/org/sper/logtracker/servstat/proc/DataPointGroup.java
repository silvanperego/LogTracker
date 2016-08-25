package org.sper.logtracker.servstat.proc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Repräsentiert eine Gruppe von Data-Points.
 * @author sper
 *
 */
public class DataPointGroup extends ArrayList<DataPoint> {

	private static final long serialVersionUID = 1L;
	private long minTime, maxTime;
	
	public void setTimeFrame(long minTime, long maxTime) {
		this.minTime = minTime;
		this.maxTime = maxTime;
	}
	
	public long getMidTime() {
		return (minTime + maxTime) / 2;
	}
	
	public long getTimespan() {
		return maxTime - minTime;
	}
	
	public void sort() {
		Collections.sort(this, new Comparator<DataPoint>() {

			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return o1.responseTime.compareTo(o2.responseTime);
			}
		});
	}
	
}
