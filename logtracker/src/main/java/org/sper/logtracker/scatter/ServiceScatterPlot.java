package org.sper.logtracker.scatter;

import java.awt.Color;
import java.awt.Component;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeriesCollection;

import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.proc.DataPoint;

public class ServiceScatterPlot implements DataListener<DataPoint> {

	private ChartPanel chartPanel;
	private ValueAxis domain;
	private XYPlot xyPlot;
	private long maxOccTime = 0;
	private Long lastOccTime = null;

	public ServiceScatterPlot() {
		XYSeriesCollection xySeriesCollection = new XYSeriesCollection();
		JFreeChart jfreechart = ChartFactory.createTimeSeriesChart("Service-Calls",
				"Occurrence", "Exec Time (sec)", xySeriesCollection, true, true, false);
		xyPlot = (XYPlot) jfreechart.getPlot();
		Color lightGray = new Color(0xe8e8e8);
		xyPlot.setBackgroundPaint(lightGray);
		jfreechart.getLegend().setBackgroundPaint(lightGray);
		domain = xyPlot.getDomainAxis();
		domain.setVerticalTickLabels(true);
		ValueAxis rangeAxis = xyPlot.getRangeAxis();
		chartPanel = new ChartPanel(jfreechart);
		rangeAxis.setAutoRange(false);
	}

	public Component getPanel() throws InterruptedException {
		return chartPanel;
	}

	public XYPlot getXyPlot() {
		return xyPlot;
	}
	
	public void setMaxRange(double maxRange) {
		xyPlot.getRangeAxis().setRange(0.f, maxRange);
	}

	@Override
	public void receiveData(DataPoint data) {
		long occTime = data.getX().longValue();
		if (occTime > maxOccTime)
			maxOccTime = occTime;
	}

	@Override
	public void publishData() {
		long upperBound = (long)domain.getUpperBound();
		if (lastOccTime != null && upperBound > lastOccTime && maxOccTime > upperBound)
			domain.setRange(Range.shift(domain.getRange(), maxOccTime - lastOccTime + 
					(upperBound - domain.getLowerBound())*0.05));
		lastOccTime = maxOccTime;
	}

}
