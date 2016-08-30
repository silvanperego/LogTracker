package org.sper.logtracker.servstat.scatter;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;

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
import org.sper.logtracker.correlation.data.CorrelatedMessage;
import org.sper.logtracker.correlation.ui.CorrelatedMessagesViewer;
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
	private BestItem bestItem;
	private JMenuItem menuItem;
	
	private class BestItem {
		DataPoint dataItem;
		
		BestItem(DataPoint dataItem) {
			this.dataItem = dataItem;
		}
		
		Point screenPoint() {
			return convertToScreenPoint(dataItem);
		}
		
		void repaint() {
			Point pt = screenPoint();
			if (pt != null)
				chartPanel.repaint(pt.x - 9, pt.y - 9, 18, 18);
		}
		
		void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			Point pt = screenPoint();
			if (pt != null) {
				g.setColor(new Color(0xa0a000a0, true));
				g2.setStroke(new BasicStroke((float) 2.5));
				g.drawLine(pt.x - 5, pt.y - 5, pt.x - 3, pt.y - 3);
				g.drawLine(pt.x + 5, pt.y - 5, pt.x + 3, pt.y - 3);
				g.drawLine(pt.x - 5, pt.y + 5, pt.x - 3, pt.y + 3);
				g.drawLine(pt.x + 5, pt.y + 5, pt.x + 3, pt.y + 3);
				g.drawOval(pt.x - 8, pt.y - 8, 16, 16);
			}
		}
	}

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

			@Override
			public void paint(Graphics g) {
				super.paint(g);
				if (bestItem != null) {
					bestItem.paint(g);
				}
			}
		};
		JPopupMenu popupMenu = chartPanel.getPopupMenu();
		menuItem = new JMenuItem("Show correlated Messages");
		menuItem.addActionListener(aev -> new CorrelatedMessagesViewer(chartPanel, ((CorrelatedMessage)bestItem.dataItem).getCorrelationId(), globalConfig).setVisible(true));
		popupMenu.add(new JSeparator());
		popupMenu.add(menuItem);
		chartPanel.addChartMouseListener(new ChartMouseListener() {
			
			@Override
			public void chartMouseMoved(ChartMouseEvent chartEv) {
				if (!popupMenu.isVisible() && chartEv.getEntity() instanceof PlotEntity) {
					BestItem dp = findClosestDataPoint(chartEv.getTrigger().getPoint());
					chartEv.getEntity().setToolTipText(dp != null ? calculateToolTip(dp.dataItem) : null);
					if (dp != null) {
						if  (bestItem == null || dp.dataItem != bestItem.dataItem) {
							if (bestItem != null)
								bestItem.repaint();
							dp.repaint();
							setBestItem(dp);
						}
					} else if (bestItem != null) {
						bestItem.repaint();
						bestItem = null;
					}
				}
			}

			@Override
			public void chartMouseClicked(ChartMouseEvent arg0) {
			}
		});
		rangeAxis.setAutoRange(false);
	}

	private void setBestItem(BestItem dp) {
		bestItem = dp;
		menuItem.setVisible(bestItem != null && bestItem.dataItem instanceof CorrelatedMessage);
	}
	
	private BestItem findClosestDataPoint(Point mousePoint) {
		XYDataset dataset = xyPlot.getDataset();
		double bestDistance = 50;
		BestItem bestItem = null;
		for (int i = 0; i < dataset.getSeriesCount(); i++) {
			XYSeries series = ((XYSeriesCollection)dataset).getSeries(i);
			for (int j = 0; j < series.getItemCount(); j++) {
				DataPoint item = (DataPoint) series.getDataItem(j);
				Point screenPoint = convertToScreenPoint(item);
				if (screenPoint != null) {
					double dist = screenPoint.distance(mousePoint);
					if (dist < bestDistance) {
						bestDistance = dist;
						bestItem = new BestItem(item);
					}
				}
			}
		}
		return bestItem;
	}

	private Point convertToScreenPoint(DataPoint item) {
		Rectangle2D plotArea = chartPanel.getScreenDataArea();
		Point2D.Double java2dPoint = new Point2D.Double(
				domain.valueToJava2D(item.getXValue(), plotArea, xyPlot.getDomainAxisEdge()), 
				xyPlot.getRangeAxis().valueToJava2D(item.getYValue(), plotArea, xyPlot.getRangeAxisEdge()));
		Point screenPoint = null;
		if (plotArea.contains(java2dPoint))
			screenPoint = chartPanel.translateJava2DToScreen(java2dPoint);
		return screenPoint;
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
