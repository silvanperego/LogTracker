package org.sper.logtracker.servstat.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.swing.JOptionPane;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.CorrelationCatalog;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.proc.PipelineHelper;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;
import org.sper.logtracker.servstat.proc.CategoryCollection;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.proc.NewPointExtractor;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer.CorrelatedStatsDataPointFactorizer;
import org.sper.logtracker.servstat.scatter.ServiceScatterPlot;
import org.sper.logtracker.servstat.scatter.TooltipGenerator;
import org.sper.logtracker.servstat.stats.StatsCalculator;
import org.sper.logtracker.servstat.stats.StatsCalculator.CategoryExtractor;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;

public class ServiceStatsTabs {
	private ServiceScatterPlot plot;
	private NewPointExtractor newPointExtractor;
	private StatsDataPointFactorizer<DataPoint> factorizer;
	private ServiceControlPanel serviceControlPanel;
	private UserPanel userPanel;
	private StatsCalculator serviceStatsCalculator;
	private KeepAliveElement terminationPointer;
	private boolean providesUsers;
	private Integer successRetCode;
	private DefaultMultipleCDockable serviceControlDockable;
	private DefaultMultipleCDockable graphDockable;
	private DefaultMultipleCDockable userDockable;

	public ServiceStatsTabs(CControl control, Configuration configuration, ServiceResponseLogParser logParser) throws InterruptedException {
		serviceControlPanel = new ServiceControlPanel(this);
		int stackpos = 0;
		serviceControlDockable = createDockable(control, stackpos++, "Services/Filter", serviceControlPanel);
		if (configuration != null)
			configuration.registerModule(serviceControlPanel);
		providesUsers = logParser.providesUsers();
		if (providesUsers) {
			userPanel = new UserPanel(this);
			if (configuration != null)
				configuration.registerModule(userPanel);
			userDockable = createDockable(control, stackpos++, "Users", userPanel);
			userDockable.setVisible(true);
		}
		plot = new ServiceScatterPlot();
		graphDockable = createDockable(control, stackpos++, "Graph", plot.getPanel());
		successRetCode = logParser.getSuccessCode();
		graphDockable.setVisible(true);
		serviceControlDockable.setVisible(true);
	}

	private DefaultMultipleCDockable createDockable(CControl control, int stackpos, String title, Component comp) {
		final DefaultMultipleCDockable dockable = new DefaultMultipleCDockable(null, title, comp);
		control.addDockable(dockable);
		dockable.setLocation(CLocation.base().normalEast(0.6).stack(stackpos));
		return dockable;
	}

	public final class ApplyControlAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setupDataSeries();
			plot.setMaxRange(20.);
			graphDockable.toFront();
		}
	}

	public void setupDataSeries() {
		CategoryCollection users = null;
		if (providesUsers)
			users = userPanel.createUsersFilter(factorizer.getUser());
		newPointExtractor.removeListeners();
		newPointExtractor.addListener(plot);
		serviceControlPanel.applyToSeriesCollection(newPointExtractor, factorizer.getService(), plot.getXyPlot(), users, successRetCode);
		newPointExtractor.resendData();
	}
	
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<RawStatsDataPoint> logParser, Long obsStart) {
		try {
			serviceControlPanel.cleanTable();
			factorizer = createFactorizer(logParser);
			serviceStatsCalculator = new StatsCalculator(factorizer.getService(), new CategoryExtractor() {

				@Override
				public Integer cat(DataPoint dp) {
					return dp.svcIdx;
				}
			}, serviceControlPanel.getTable(), true, serviceControlPanel.getPublishingSemaphore(), serviceControlPanel.getApplyButton(), successRetCode);
			factorizer.addListener(serviceStatsCalculator);
			if (userPanel != null)
				userPanel.clearTable();
			if (logParser.providesUsers()) {
				factorizer.addListener(new StatsCalculator(factorizer.getUser(), new CategoryExtractor() {
					@Override
					public Integer cat(DataPoint dp) {
						return dp.user;
					}
				}, userPanel.getTable(), false, null, userPanel.getApplyButon(), successRetCode));
			}
			newPointExtractor = new NewPointExtractor();
			factorizer.addListener(newPointExtractor);
			if (terminationPointer != null)
				terminationPointer.endOfLife();
			terminationPointer = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, factorizer);

			Factor services = factorizer.getService();
			Factor users = factorizer.getUser();
			XYToolTipGenerator toolTipGenerator = new TooltipGenerator(services, users);
			plot.getXyPlot().getRenderer().setBaseToolTipGenerator(toolTipGenerator);
			serviceControlDockable.toFront();
		} catch (Exception e1) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e1.printStackTrace(pw);
			JOptionPane.showMessageDialog(serviceControlPanel, sw.toString(), "Error", JOptionPane.ERROR_MESSAGE);
			pw.close();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private StatsDataPointFactorizer<DataPoint> createFactorizer(ConfiguredLogParser<RawStatsDataPoint> logParser) {
		if (logParser.getCorrelationIdIdx() != null) {
			CorrelatedStatsDataPointFactorizer cfact = new StatsDataPointFactorizer.CorrelatedStatsDataPointFactorizer();
			cfact.addListener(CorrelationCatalog.getInstance());
			return (StatsDataPointFactorizer) cfact;
		} else
			return new StatsDataPointFactorizer.SimpleStatsDataPointFactorizer();
	}

	public void removeDockables(CControl control) {
		control.removeDockable(serviceControlDockable);
		control.removeDockable(graphDockable);
		if (userDockable != null)
			control.removeDockable(userDockable);
		if (terminationPointer != null)
			terminationPointer.endOfLife();
	}

	public Object getControlDataConfig() {
		final ServiceControlData config = serviceControlPanel.getConfig();
		if (userPanel != null)
			userPanel.addUserExludes(config);
		return config;
	}

	public void applyConfig(Object controlData) {
		ServiceControlData scd = (ServiceControlData) controlData;
		serviceControlPanel.applyXmlConfig(scd);
		if (userPanel != null)
			userPanel.applyConfig(scd);
	}

}