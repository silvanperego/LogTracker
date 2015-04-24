package org.sper.logtracker.erroranalysis.ui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.data.xy.XYBarDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.sper.logtracker.erroranalysis.data.ErrorCategory;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;

class TemporalDistributionPlot {
	
	private static final long INTERVALS[] = new long[] {
		7 * 86400000,
		86400000,
		43200000,
		21600000,
		14400000,
		10800000,
		7200000,
		3600000,
		1800000,
		1200000,
		600000,
		300000,
		120000,
		60000,
		30000,
		20000,
		10000,
		5000,
		2000,
		1000,
		500,
		100,
		50,
		10
	};
	
	private static long combineInSlots(ErrorCategory cat, XYSeries xySeries) {
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		synchronized (cat) {
			for (RawErrorDataPoint dp : cat) {
				if (dp.occTime != null) {
					long occTime = (long) dp.occTime;
					if (occTime < min)
						min = occTime;
					if (occTime > max)
						max = occTime;
				}
			}
		}
		
		if (max > min) {
			long interval = max - min;
			long barint = -1;
			for (long l : INTERVALS) {
				if (interval / l > 10) {
					barint = l;
					break;
				}
			}
			if (barint > -1) {
				int offs = (int)(min / barint);
				int[] bar = new int[(int)((max + barint - 1) / barint) - offs];
				synchronized (cat) {
					for (RawErrorDataPoint dp : cat) {
						bar[(int)(dp.occTime / barint) - offs]++;
					}
				}
				for (int i = 0; i < bar.length; i++) {
					XYDataItem item = new XYDataItem(new Long((i + offs) * barint), new Integer(bar[i]));
					xySeries.add(item);
				}
				return barint;
			}
		}
		return 3600;
	}
	
	
	static ChartPanel createPlotOnData(ErrorCategory cat) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		XYSeries xySeries = new XYSeries("messages", true);
		XYBarDataset xyBarDataset = new XYBarDataset(xySeriesCollection, combineInSlots(cat, xySeries));
		xySeriesCollection.addSeries(xySeries);
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(
				null, "Time", "Occurrences", xyBarDataset, false, false, false);
		XYBarRenderer renderer = new XYBarRenderer(0.3);
		renderer.setSeriesFillPaint(0, Color.GREEN);
		jfreechart.getXYPlot().setRenderer(renderer);
		return new ChartPanel(jfreechart);
	}

}
