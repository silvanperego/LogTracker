package org.sper.logtracker.servstat;

import java.awt.Color;
import java.util.Vector;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.proc.CategoryCollection;
import org.sper.logtracker.proc.NewPointExtractor;
import org.sper.logtracker.servstat.PipelineFactory.PipelineCreator;
import org.sper.logtracker.stats.ServiceStats;

public class ServiceControlTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	Class<?>[] columnTypes = new Class[] { String.class, Long.class, Double.class,
			Double.class, Double.class, Double.class, Boolean.class,
			Boolean.class, Boolean.class, Boolean.class, Color.class };
	
	public ServiceControlTableModel() {
		super(new Object[][] {},
				new String[] { "Service", "Calls", "Calls Per Minute",
						"Mean Response Time", "Median ", "90% Percentile",
						"Scatter", "perSecond", "Mean", "Median", "Color" });
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	
	private XYSeries createXYSeries(int modelColumn, String serviceName, XYPlot xyPlot, PipelineCreator creator, Color color) {
		XYSeriesCollection collection = (XYSeriesCollection)xyPlot.getDataset();
		XYSeries xySeries = new XYSeries(serviceName + ' ' + getColumnName(modelColumn));
		xySeries.setNotify(false);
		collection.addSeries(xySeries);
		int idx = collection.getSeriesCount() - 1;
		creator.applyFormat(xyPlot, idx, color);
		return xySeries;
	}
	
	private void createSeriesOfType(Factor services, PipelineCreator creator, int modelColumn, XYPlot xyPlot) {
		CategoryCollection others = new CategoryCollection();
		Integer othersIdx = null;
		for (int i = 0; i < getRowCount(); i++) {
			String serviceName = (String)getValueAt(i, 0);
			if (((Boolean)getValueAt(i, modelColumn)).booleanValue()) {
				if ("Total".equals(serviceName))
					othersIdx = i;
				else {
					XYSeries xySeries = createXYSeries(modelColumn, serviceName, xyPlot, creator, (Color)getValueAt(i, 10));
					CategoryCollection servColl = new CategoryCollection();
					servColl.addFactoryCat(services.getStringIndex(serviceName));
					creator.createPipeLine(servColl, xySeries);
				}
			} else
				others.addFactoryCat(services.getStringIndex(serviceName));
		}
		if (othersIdx != null) {
			XYSeries series = createXYSeries(modelColumn, "Others", xyPlot, creator, (Color)getValueAt(othersIdx, 10));
			creator.createPipeLine(others, series);
		}
	}

	public void applyToSeriesCollection(NewPointExtractor newPointExtractor, Factor services, XYPlot xyPlot, CategoryCollection usersFilter, double statsMagFact) {
		XYSeriesCollection seriesCollection = (XYSeriesCollection)xyPlot.getDataset();
		newPointExtractor.setUserFilter(usersFilter);
		seriesCollection.removeAllSeries();
		PipelineFactory factory = new PipelineFactory(newPointExtractor, getRowCount(), 60000, 1000, 20, statsMagFact, xyPlot, usersFilter);
		createSeriesOfType(services, factory.scatterCreator, 6, xyPlot);
		createSeriesOfType(services, factory.callsPerTimeCreator, 7, xyPlot);
		createSeriesOfType(services, factory.meanTimeCreator, 8, xyPlot);
		createSeriesOfType(services, factory.medianTimeCreator, 9, xyPlot);
	}

	@Override
	public void createOrReplaceTableRow(Boolean total, ServiceStats stats) {
		Object[] obj = new Object[] {
				null, null, null, null, null, null, total, total, total, total, total ? Color.BLACK : null
		};
		stats.fillTableModelRow(obj);
		createOrReplaceTableRow(obj);
	}

	@SuppressWarnings("rawtypes")
	public Vector getColumnIdentifiers() {
		return columnIdentifiers;
	}
}
