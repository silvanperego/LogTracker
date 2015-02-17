package org.sper.logtracker.servstat.stats;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.sper.logtracker.servstat.proc.DataPoint;

/**
 * Repräsentiert die Statistiken für einen einzelnen Service. 
 * @author silvan.perego
 */
public class ServiceStats {
	
	private String serviceName;
	private List<DataPoint> data = new ArrayList<DataPoint>();
	private long startTime, endTime;
	private Double meanExecTime;
	private Double callsPerMinute;
	private Double execMedian;
	private Double percentile;
	
	ServiceStats(String serviceName) {
		this.serviceName = serviceName;
	}
	
	ServiceStats(String serviceName, ServiceStats timeFrame) {
	}
	
	void addDataPoint(DataPoint dpt) {
		data.add(dpt);
	}
	
	void calcStatistics(ServiceStats timeFrame) {
		if (!data.isEmpty()) {
			this.startTime = timeFrame.startTime;
			this.endTime = timeFrame.endTime;
			calcStatsInternal();
		}
	}

	void calcStatistics() {
		if (!data.isEmpty()) {
			calcTimeFrame();
			calcStatsInternal();
		}
	}

	private void calcStatsInternal() {
		calcMeanExecTime();
		calcCallsPerMinute();
		calcMedianExecTimeAndPercentile();
	}

	private void calcTimeFrame() {
		startTime = endTime = data.get(0).occTime;
		for (DataPoint pt : data) {
			if (pt.occTime < startTime)
				startTime = pt.occTime;
			if (pt.occTime > endTime)
				endTime = pt.occTime;
		}
	}

	private void calcMeanExecTime() {
		double totExecTime = 0.;
		for (DataPoint pt : data) {
			totExecTime += pt.value;
		}
		meanExecTime = totExecTime / data.size();
	}
	
	private void calcCallsPerMinute() {
		if (endTime > startTime)
			callsPerMinute = (double)data.size() / (endTime - startTime) * 60000;
	}

	private void calcMedianExecTimeAndPercentile() {
		Collections.sort(data, new Comparator<DataPoint>() {

			@Override
			public int compare(DataPoint o1, DataPoint o2) {
				return o1.value.compareTo(o2.value);
			}
		});
		int size = data.size();
		int halfsize = size / 2;
		if (size == 1)
			execMedian = data.get(0).value;
		else if (size % 2 == 0)
			execMedian = data.get(halfsize).value;
		else
			execMedian = (data.get(halfsize).value + data.get(halfsize + 1).value) / 2.;
		if (size > 9)
			percentile = data.get((int)(0.9 * size + 0.5)).value;
	}

	public void fillTableModelRow(Object[] row) {
		row[0] = serviceName;
		row[1] = data.size();
		row[2] = callsPerMinute;
		row[3] = meanExecTime;
		row[4] = execMedian;
		row[5] = percentile;
	}

	public double getMean() {
		return meanExecTime;
	}
	
}
