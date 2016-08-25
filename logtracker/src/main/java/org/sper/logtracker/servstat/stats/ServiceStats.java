package org.sper.logtracker.servstat.stats;

import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.CALLS_PER_MINUTE_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.MEAN_RESPONSE_TIME_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.MEDIAN_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.NUMBER_OF_CALLS_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.NUMBER_OF_ERRORS_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.PERCENTILE_COL;
import static org.sper.logtracker.servstat.ui.ServiceControlTableModel.SERVICE_NAME_COL;

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
	private Integer successRetCode;
	private long errCount;
	
	ServiceStats(String serviceName, Integer successRetCode) {
		this.serviceName = serviceName;
		this.successRetCode = successRetCode;
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
		countErrors();
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

	private void countErrors() {
		errCount = 0;
		for (DataPoint pt : data) {
			if (successRetCode != null && !successRetCode.equals(pt.returnCode))
				errCount++;
		}
	}

	private void calcMeanExecTime() {
		double totExecTime = 0.;
		for (DataPoint pt : data) {
			totExecTime += pt.responseTime;
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
				return o1.responseTime.compareTo(o2.responseTime);
			}
		});
		int size = data.size();
		int halfsize = size / 2;
		if (size == 1)
			execMedian = data.get(0).responseTime;
		else if (size % 2 == 0)
			execMedian = data.get(halfsize).responseTime;
		else
			execMedian = (data.get(halfsize).responseTime + data.get(halfsize + 1).responseTime) / 2.;
		if (size > 9)
			percentile = data.get((int)(0.9 * size + 0.5)).responseTime;
	}

	public void fillTableModelRow(Object[] row) {
		row[SERVICE_NAME_COL] = serviceName;
		row[NUMBER_OF_CALLS_COL] = data.size();
		row[NUMBER_OF_ERRORS_COL] = errCount;
		row[CALLS_PER_MINUTE_COL] = callsPerMinute;
		row[MEAN_RESPONSE_TIME_COL] = meanExecTime;
		row[MEDIAN_COL] = execMedian;
		row[PERCENTILE_COL] = percentile;
	}

	public double getMean() {
		return meanExecTime;
	}
	
}
