package org.sper.logtracker.servstat;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.servstat.proc.CallsPerTime;
import org.sper.logtracker.servstat.proc.CategoryCollection;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.proc.DataPointGroup;
import org.sper.logtracker.servstat.proc.MeanExecTime;
import org.sper.logtracker.servstat.proc.MedianExecTime;
import org.sper.logtracker.servstat.proc.NewPointExtractor;
import org.sper.logtracker.servstat.proc.ServiceRouter;
import org.sper.logtracker.servstat.proc.TimeBasedCollector;
import org.sper.logtracker.servstat.proc.XYDataSeriesListener;

/**
 * Erstellt Pipeline-Element und verknüpft sie mit Parent-Elementen. Parent-Elemente werden automatisch wiedererkannt. D.h.
 * falls ein Parent-Element mehrfach in mehreren Pipelines benötigt wird, wird es nur trotzdem nur einmal erzeugt. Dadurch
 * entsteht eine Pipeline-Baumstruktur und die Mehrfachverarbeitungen werden vermieden.
 * @author sper
 */
public class PipelineFactory {
	
	private static final float LINEWIDTH = 1.5f;
	private Map<CategoryCollection, ServiceRouter> serviceRouterMap = new HashMap<CategoryCollection, ServiceRouter>();
	private Map<CategoryCollection, TimeBasedCollector> collectorMap = new HashMap<CategoryCollection, TimeBasedCollector>();
	private NewPointExtractor newPointExtractor;
	private int nservices;
	private long timeFrame;
	private long minPoints;
	private double magFact;
	private long perTimeMeasure;
	private static final Shape SQUARE  = new Rectangle2D.Double(-1.,-1.,2.,2.);
	private static final Path2D.Double CROSS = new Path2D.Double();
	private static final double CROSSLEN = 2.;
	
	static {
		CROSS.moveTo(-CROSSLEN, -CROSSLEN);
		CROSS.lineTo(CROSSLEN,  CROSSLEN);
		CROSS.moveTo(CROSSLEN, -CROSSLEN);
		CROSS.lineTo(-CROSSLEN,  CROSSLEN);
	}
	
	public interface PipelineCreator {
		void createPipeLine(CategoryCollection services, XYSeries series);
	
		void applyFormat(XYPlot xyPlot, int idx, Color color);
	}

	/**
	 * Erstellt eine ScatterPlot-Pipeline
	 */
	public class ScatterPlotPipelineCreator implements PipelineCreator {
		
		private Integer successRetCode;
		
		/**
		 * Konstruktor.
		 * @param successRetCode der Code, welcher ein erfolgreiches Ende eines Service-Aufrufs darstellt.
		 * 			Ist dieser Code = null, werden alle Services dargestellt, ist er Positiv, so wird
		 * 			nach den Services gefiltert, welche einen ReturnCode = successRetCode haben. Ist der Wert negativ,
		 * 			so wird nach den Services gefiltert, welche einen ReturnCode != -successRetCode haben.
		 */
		public ScatterPlotPipelineCreator(Integer successRetCode) {
			this.successRetCode = successRetCode;
		}

		@Override
		public void createPipeLine(CategoryCollection services, XYSeries series) {
			ServiceRouter serviceRouter = getOrCreateServiceRouter(services);
			final XYDataSeriesListener dataSeriesListener = new XYDataSeriesListener(series);
			if (successRetCode != null) {
				if (successRetCode > 0) {
					serviceRouter.registerListener(services,new DataListener<DataPoint>() {

						@Override
						public void receiveData(DataPoint data) {
							if (data.returnCode != null && data.returnCode.equals(successRetCode)) {
								dataSeriesListener.receiveData(data);
							}
						}

						@Override
						public void publishData() {
							dataSeriesListener.publishData();
						}
					});
				} else {
					serviceRouter.registerListener(services,new DataListener<DataPoint>() {

						@Override
						public void receiveData(DataPoint data) {
							if (data.returnCode == null || !data.returnCode.equals(-successRetCode)) {
								dataSeriesListener.receiveData(data);
							}
						}

						@Override
						public void publishData() {
							dataSeriesListener.publishData();
						}
					});
					
				}
			} else
				serviceRouter.registerListener(services, dataSeriesListener);
		}

		@Override
		public void applyFormat(XYPlot xyPlot, int idx, Color color) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
			renderer.setSeriesLinesVisible(idx, false);
			renderer.setSeriesShapesVisible(idx, true);
			boolean normalPipeline = successRetCode == null || successRetCode > 0;
			renderer.setSeriesShape(idx, normalPipeline ? SQUARE : CROSS);
			renderer.setSeriesShapesFilled(idx, true);
			renderer.setSeriesPaint(idx, color);
			renderer.setSeriesVisibleInLegend(idx, normalPipeline);
		}

	}

	public PipelineFactory(NewPointExtractor newPointExtractor, int nservices, int timeFrame, long perTimeMeasure, int minPoints, double magFact, XYPlot xyPlot, CategoryCollection users) {
		this.nservices = nservices;
		this.timeFrame = timeFrame;
		this.minPoints = minPoints;
		this.magFact = magFact;
		this.perTimeMeasure = perTimeMeasure;
		this.newPointExtractor = newPointExtractor;
	}

	private ServiceRouter getOrCreateServiceRouter(CategoryCollection services) {
		ServiceRouter router = serviceRouterMap.get(services);
		if (router == null) {
			router = new ServiceRouter(nservices);
			serviceRouterMap.put(services, router);
			newPointExtractor.addListener(router);
		}
		return router;
	}
	
	private void createCollectedPipeline(CategoryCollection services,
			XYSeries series, AbstractDataListener<DataPointGroup, DataPoint> callsPerTime) {
		XYDataSeriesListener dataSeriesListener = new XYDataSeriesListener(series);
		callsPerTime.addListener(dataSeriesListener);
		TimeBasedCollector collector = getOrCreateTimeBasedCollector(services);
		collector.addListener(callsPerTime);
	}

	private TimeBasedCollector getOrCreateTimeBasedCollector(CategoryCollection services) {
		TimeBasedCollector collector = collectorMap.get(services);
		if (collector == null) {
			collector = new TimeBasedCollector(timeFrame, minPoints);
			ServiceRouter router = getOrCreateServiceRouter(services);
			router.registerListener(services, collector);
		}
		return collector;
	}
	
	public NewPointExtractor getNewPointExtractor() {
		return newPointExtractor;
	}

	public final PipelineCreator callsPerTimeCreator = new PipelineCreator() {

		private final Stroke dashed = new BasicStroke(LINEWIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3,5}, 0);
		
		@Override
		public void createPipeLine(CategoryCollection services, XYSeries series) {
			createCollectedPipeline(services, series, new CallsPerTime(perTimeMeasure));
		}

		@Override
		public void applyFormat(XYPlot xyPlot, int idx, Color color) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
			renderer.setSeriesLinesVisible(idx, true);
			renderer.setSeriesShapesVisible(idx, false);
			renderer.setSeriesStroke(idx, dashed);
			renderer.setSeriesPaint(idx, color);
		}

	};
	
	public final PipelineCreator meanTimeCreator = new PipelineCreator() {
		
		private final Stroke solid = new BasicStroke(LINEWIDTH);

		@Override
		public void createPipeLine(CategoryCollection services, XYSeries series) {
			createCollectedPipeline(services, series, new MeanExecTime(magFact));
		}

		@Override
		public void applyFormat(XYPlot xyPlot, int idx, Color color) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
			renderer.setSeriesLinesVisible(idx, true);
			renderer.setSeriesShapesVisible(idx, false);
			renderer.setSeriesPaint(idx, color);
			renderer.setSeriesStroke(idx, solid);
		}
	};
	
	public final PipelineCreator medianTimeCreator = new PipelineCreator() {
		
		private final Stroke dotdashed = new BasicStroke(LINEWIDTH, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{2,4,6,4}, 0);
		
		@Override
		public void createPipeLine(CategoryCollection services, XYSeries series) {
			createCollectedPipeline(services, series, new MedianExecTime(magFact));
		}

		@Override
		public void applyFormat(XYPlot xyPlot, int idx, Color color) {
			XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer)xyPlot.getRenderer();
			renderer.setSeriesLinesVisible(idx, true);
			renderer.setSeriesShapesVisible(idx, false);
			renderer.setSeriesStroke(idx, dotdashed);
			renderer.setSeriesPaint(idx, color);
		}
	};
	
}
