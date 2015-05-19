package org.sper.logtracker.servstat.ui;

import java.awt.Color;
import java.util.Vector;

import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.PipelineFactory;
import org.sper.logtracker.servstat.PipelineFactory.PipelineCreator;
import org.sper.logtracker.servstat.proc.CategoryCollection;
import org.sper.logtracker.servstat.proc.NewPointExtractor;
import org.sper.logtracker.servstat.stats.AbstractTableModel;
import org.sper.logtracker.servstat.stats.ServiceStats;

public class ServiceControlTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	
	public static final int SERVICE_NAME_COL = 0, NUMBER_OF_CALLS_COL = 1, NUMBER_OF_ERRORS_COL = 2,
			CALLS_PER_MINUTE_COL = 3, MEAN_RESPONSE_TIME_COL = 4, MEDIAN_COL = 5, PERCENTILE_COL = 6,
			DO_SCATTER_COL = 7, DO_PER_SECOND_COL = 8, DO_MEAN_TIME_COL = 9, DO_MEDIAN_TIME_COL = 10, COLOR_COL = 11,
			NCOLS = 12, FIRST_STAT_COL = NUMBER_OF_CALLS_COL, LAST_STAT_COL = PERCENTILE_COL, FIRST_SWITCH_COL = DO_SCATTER_COL;
	
	Class<?>[] columnTypes = new Class[] { String.class, Long.class, Long.class, Double.class,
			Double.class, Double.class, Double.class, Boolean.class,
			Boolean.class, Boolean.class, Boolean.class, Color.class };
	
	public ServiceControlTableModel() {
		super(new Object[][] {},
				new String[] { "Service", "Calls", "Errors", "Calls Per Minute",
						"Mean Response Time", "Median ", "90% Percentile",
						"Scatter", "perSecond", "Mean", "Median", "Color" });
	}

	public Class<?> getColumnClass(int columnIndex) {
		return columnTypes[columnIndex];
	}
	
	private XYSeries createXYSeries(int modelColumn, String serviceName, XYPlot xyPlot, PipelineCreator creator, Color color, String seriesSuffix) {
		XYSeriesCollection collection = (XYSeriesCollection)xyPlot.getDataset();
		XYSeries xySeries = new XYSeries(serviceName + ' ' + getColumnName(modelColumn) + seriesSuffix);
		xySeries.setNotify(false);
		collection.addSeries(xySeries);
		int idx = collection.getSeriesCount() - 1;
		creator.applyFormat(xyPlot, idx, color);
		return xySeries;
	}
	
	private void createSeriesOfType(Factor services, PipelineCreator creator, int modelColumn, XYPlot xyPlot, String seriesSuffix) {
		CategoryCollection others = new CategoryCollection();
		Integer othersIdx = null;
		for (int i = 0; i < getRowCount(); i++) {
			String serviceName = (String)getValueAt(i, 0);
			if (((Boolean)getValueAt(i, modelColumn)).booleanValue()) {
				if ("Total".equals(serviceName))
					othersIdx = i;
				else {
					XYSeries xySeries = createXYSeries(modelColumn, serviceName, xyPlot, creator, (Color)getValueAt(i, COLOR_COL), seriesSuffix);
					CategoryCollection servColl = new CategoryCollection();
					servColl.addFactoryCat(services.getStringIndex(serviceName));
					creator.createPipeLine(servColl, xySeries);
				}
			} else
				others.addFactoryCat(services.getStringIndex(serviceName));
		}
		if (othersIdx != null) {
			XYSeries series = createXYSeries(modelColumn, "Others", xyPlot, creator, (Color)getValueAt(othersIdx, COLOR_COL), seriesSuffix);
			creator.createPipeLine(others, series);
		}
	}

	public void applyToSeriesCollection(NewPointExtractor newPointExtractor, Factor services, XYPlot xyPlot, CategoryCollection usersFilter, double statsMagFact, Integer successRetCode) {
		XYSeriesCollection seriesCollection = (XYSeriesCollection)xyPlot.getDataset();
		newPointExtractor.setUserFilter(usersFilter);
		seriesCollection.removeAllSeries();
		PipelineFactory factory = new PipelineFactory(newPointExtractor, getRowCount(), 60000, 1000, 20, statsMagFact, xyPlot, usersFilter);
		if (successRetCode == null) {
			createSeriesOfType(services, factory.new ScatterPlotPipelineCreator(null), DO_SCATTER_COL, xyPlot, "");
		} else {
			createSeriesOfType(services, factory.new ScatterPlotPipelineCreator(successRetCode), DO_SCATTER_COL, xyPlot, "");
			createSeriesOfType(services, factory.new ScatterPlotPipelineCreator(-successRetCode), DO_SCATTER_COL, xyPlot, " errors");
		}
		createSeriesOfType(services, factory.callsPerTimeCreator, DO_PER_SECOND_COL, xyPlot, "");
		createSeriesOfType(services, factory.meanTimeCreator, DO_MEAN_TIME_COL, xyPlot, "");
		createSeriesOfType(services, factory.medianTimeCreator, DO_MEDIAN_TIME_COL, xyPlot, "");
	}

	@Override
	public void createOrReplaceTableRow(Boolean total, ServiceStats stats) {
		Object[] obj = new Object[] {
				null, null, null, null, null, null, null, total, total, total, total, total ? Color.BLACK : null
		};
		stats.fillTableModelRow(obj);
		createOrReplaceTableRow(obj);
	}

	@SuppressWarnings("rawtypes")
	public Vector getColumnIdentifiers() {
		return columnIdentifiers;
	}
}
