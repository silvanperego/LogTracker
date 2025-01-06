package org.sper.logtracker.servstat.scatter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.PlotEntity;
import org.jfree.chart.entity.XYItemEntity;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.Range;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.correlation.data.CorrelatedMessage;
import org.sper.logtracker.correlation.ui.CorrelatedMessagesViewer;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.logreader.LogSource.DataPointSelectionAction;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.ui.ServiceStatsDockables;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ServiceScatterPlot implements DataListener<DataPoint>, DataPointSelectionAction {

	private ChartPanel chartPanel;
	private ValueAxis domain;
	private XYPlot xyPlot;
	private long maxOccTime = 0;
	private Long lastOccTime = null;
	private SimpleDateFormat sdf;
	private ServiceStatsDockables tabs;
	private BestItem bestItem;
	private JMenuItem menuItem;
	private List<LogSource> logSource;
	private BestItem selectedItem;
	
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
				chartPanel.repaint(pt.x - 10, pt.y - 10, 20, 20);
		}
		
		void paint(Graphics g, int color) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			Point pt = screenPoint();
			if (pt != null) {
				g.setColor(new Color(color, true));
				g.fillOval(pt.x - 8, pt.y - 8, 16, 16);
				g.setColor(new Color(color | 0xff000000, true));
				g2.setStroke(new BasicStroke((float) 1.5));
				g.drawLine(pt.x - 5, pt.y - 5, pt.x - 3, pt.y - 3);
				g.drawLine(pt.x + 5, pt.y - 5, pt.x + 3, pt.y - 3);
				g.drawLine(pt.x - 5, pt.y + 5, pt.x - 3, pt.y + 3);
				g.drawLine(pt.x + 5, pt.y + 5, pt.x + 3, pt.y + 3);
				g2.setStroke(new BasicStroke((float) 2.2));
				g.drawOval(pt.x - 8, pt.y - 8, 16, 16);
			}
		}
	}

	public ServiceScatterPlot(ServiceStatsDockables tabs, GlobalConfig globalConfig) {
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
				if (bestItem != null)
					bestItem.paint(g, 0x25a000a0);
				if (selectedItem != null)
					selectedItem.paint(g, 0x2500a0a0);
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
				if (!popupMenu.isVisible()) {
					if (chartEv.getEntity() instanceof PlotEntity || chartEv.getEntity() instanceof XYItemEntity) {
						BestItem dp = findClosestDataPoint(chartEv.getTrigger().getPoint());
						chartEv.getEntity().setToolTipText(dp != null ? calculateToolTip(dp.dataItem) : null);
						if (dp != null) {
							if  (bestItem == null || dp.dataItem != bestItem.dataItem) {
								if (bestItem != null)
									bestItem.repaint();
								dp.repaint();
								setBestItem(dp);
								return;
							}
						}
					}
					if (bestItem != null) {
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
		menuItem.setEnabled(bestItem != null && bestItem.dataItem instanceof CorrelatedMessage);
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
		Point java2dPoint = new Point(
				(int) domain.valueToJava2D(item.getXValue(), plotArea, xyPlot.getDomainAxisEdge()), 
				(int) xyPlot.getRangeAxis().valueToJava2D(item.getYValue(), plotArea, xyPlot.getRangeAxisEdge()));
		Point screenPoint = null;
		if (plotArea.contains(java2dPoint))
			screenPoint = java2dPoint;
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

	public void setLogSources(List<LogSource> logSource) {
		deregisterLogSources();
		this.logSource = logSource;
		logSource.stream().forEach(ls -> ls.addSelectionAction(this));
	}

	private void deregisterLogSources() {
		if (this.logSource != null)
			this.logSource.stream().forEach(ls -> ls.removeSelectionAction(this));
	}

	public void cascadeDelete() {
		deregisterLogSources();
	}
	
	@Override
	public void pointSelected(CorrelatedMessage dp) {
		if (selectedItem != null && selectedItem.dataItem != dp)
			selectedItem.repaint();
		if (dp instanceof DataPoint) {
			selectedItem = new BestItem((DataPoint) dp);
			selectedItem.repaint();
		} else
			selectedItem = null;
	}

}
