package org.sper.logtracker.servstat.scatter;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.ui.ServiceStatsTabs;

public class ServiceScatterPlot implements DataListener<DataPoint> {

	private ChartPanel chartPanel;
	private ValueAxis domain;
	private XYPlot xyPlot;
	private long maxOccTime = 0;
	private Long lastOccTime = null;
	private SimpleDateFormat sdf;
	private ServiceStatsTabs tabs;

	public ServiceScatterPlot(ServiceStatsTabs tabs, GlobalConfig globalConfig) {
		this.tabs = tabs;
		sdf = new SimpleDateFormat(globalConfig.getTimestampFormatStr());
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
		chartPanel = new ChartPanel(jfreechart) {
			private static final long serialVersionUID = 1L;
		};
		chartPanel.addChartMouseListener(new ChartMouseListener() {
			
			@Override
			public void chartMouseMoved(ChartMouseEvent chartEv) {
				if (chartEv.getEntity() instanceof PlotEntity) {
					DataPoint dp = findClosestDataPoint(chartEv.getTrigger().getPoint());
					chartEv.getEntity().setToolTipText(dp != null ? calculateToolTip(dp) : null);
				}
			}
			
			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
			}
		});
		rangeAxis.setAutoRange(false);
	}

	private DataPoint findClosestDataPoint(Point mousePoint) {
		XYDataset dataset = xyPlot.getDataset();
		Rectangle2D plotArea = chartPanel.getScreenDataArea();
		double bestDistance = 50;
		DataPoint bestItem = null;
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			XYSeries series = ((XYSeriesCollection)dataset).getSeries(i);
			for (int j = 0; j < series.getItemCount(); j++) {
				DataPoint item = (DataPoint) series.getDataItem(j);
				final Point2D.Double java2dPoint = new Point2D.Double(
						domain.valueToJava2D(item.getXValue(), plotArea, xyPlot.getDomainAxisEdge()), 
						xyPlot.getRangeAxis().valueToJava2D(item.getYValue(), plotArea, xyPlot.getRangeAxisEdge()));
				if (plotArea.contains(java2dPoint)) {
					Point screenPoint = chartPanel.translateJava2DToScreen(java2dPoint);
					double dist = screenPoint.distance(mousePoint);
					if (dist < bestDistance) {
						bestDistance = dist;
						bestItem = item;
						System.out.println("Best Item: " + item);
					}
				}
			}
		}
		return bestItem;
	}

	private String calculateToolTip(DataPoint item) {
		StringBuilder sb = new StringBuilder();
		if (item.logSource != null) {
			sb.append(item.logSource);
			sb.append("-> ");
		}
		if (item.user != null) {
			sb.append(tabs.getFactorizer().getUser().getLabel(item.user));
			sb.append(": ");
		}
		sb.append(item.svcIdx != null ? tabs.getFactorizer().getService().getLabel(item.svcIdx) : "Others");
		sb.append(" | ");
		sb.append(sdf.format(new Date(item.getX().longValue())));
		sb.append(" | ");
		sb.append(item.getY());
		sb.append("sec");
		if (item.returnCode != null) {
			sb.append(", RetCode: ");
			sb.append(item.returnCode);
		}
		return sb.toString();
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
