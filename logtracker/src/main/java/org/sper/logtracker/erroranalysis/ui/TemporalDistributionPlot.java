package org.sper.logtracker.erroranalysis.ui;

import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.event.ChartChangeEventType;
import org.jfree.chart.event.PlotChangeEvent;
import org.jfree.chart.event.PlotChangeListener;
import org.jfree.chart.plot.XYPlot;
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
	
	private long activeLowerBound = 0, activeUpperBound = 0;
	
	private long combineInSlots(ErrorCategory cat, XYSeries xySeries) {
		long min = Long.MAX_VALUE;
		long max = Long.MIN_VALUE;
		synchronized (cat) {
			for (RawErrorDataPoint dp : cat.getErrorsAsList()) {
				if (dp.occTime != null) {
					long occTime = (long) dp.occTime;
					if (occTime < min)
						min = occTime;
					if (occTime > max)
						max = occTime;
				}
			}
		}
		
		return calculateBars(cat, xySeries, min, max);
	}


	private long reCalculateBars(ErrorCategory cat, XYSeries xySeries, long min, long max) {
		if (min == activeLowerBound && max == activeUpperBound)
			return -1;
		return calculateBars(cat, xySeries, min, max);
	}


	private long calculateBars(ErrorCategory cat, XYSeries xySeries, long min,	long max) {
		activeLowerBound = min;
		activeUpperBound = max;
		xySeries.clear();
		if (max > min) {
			long interval = max - min;
			long barint = -1;
			long belowval = min;
			long aboveval = max;
			for (long l : INTERVALS) {
				if (interval / l > 15) {
					barint = l;
					break;
				}
			}
			if (barint > -1) {
				int offs = (int)(min / barint);
				int[] bar = new int[(int)((max + barint - 1) / barint) - offs];
				synchronized (cat) {
					for (RawErrorDataPoint dp : cat.getErrorsAsList()) {
						if (dp.occTime >= min && dp.occTime <= max)
							bar[(int)(dp.occTime / barint) - offs]++;
						else if (dp.occTime < belowval)
							belowval = dp.occTime;
						else if (dp.occTime > aboveval)
							aboveval = dp.occTime;
					}
				}
				for (int i = 0; i < bar.length; i++) {
					XYDataItem item = new XYDataItem(new Long((i + offs) * barint), new Integer(bar[i]));
					xySeries.add(item);
				}
				if (belowval < min) {
					XYDataItem item = new XYDataItem(new Long(belowval), Integer.valueOf(1));
					xySeries.add(item);
				}
				if (aboveval > max) {
					XYDataItem item = new XYDataItem(new Long(aboveval), Integer.valueOf(1));
					xySeries.add(item);
				}
				return barint;
			}
		}
		synchronized (cat) {
			XYDataItem item = new XYDataItem(new Long(cat.getLatestMessage().occTime), new Integer(cat.getNumMessages()));
			xySeries.add(item);
		}
		return 3600;
	}
	
	
	ChartPanel createPlotOnData(final ErrorCategory cat) {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		final XYSeries xySeries = new XYSeries("messages", true);
		final XYBarDataset xyBarDataset = new XYBarDataset(xySeriesCollection, combineInSlots(cat, xySeries));
		xySeriesCollection.addSeries(xySeries);
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart(
				null, "Time", "Occurrences", xyBarDataset, false, false, false);
		XYBarRenderer renderer = new XYBarRenderer(0.3);
		renderer.setSeriesFillPaint(0, Color.GREEN);
		final XYPlot xyPlot = jfreechart.getXYPlot();
		xyPlot.setRenderer(renderer);
		xyPlot.addChangeListener(new PlotChangeListener() {
			
			@Override
			public void plotChanged(PlotChangeEvent event) {
				if (event.getType() == ChartChangeEventType.GENERAL) {
					long lowerBound = (long) xyPlot.getDomainAxis().getLowerBound();
					long upperBound = (long) xyPlot.getDomainAxis().getUpperBound();
					long interval = reCalculateBars(cat, xySeries, lowerBound, upperBound);
					if (interval > 0)
						xyBarDataset.setBarWidth(interval);
				}
			}
		});
		return new ChartPanel(jfreechart);
	}

}
