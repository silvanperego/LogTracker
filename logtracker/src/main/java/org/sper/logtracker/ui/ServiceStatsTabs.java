package org.sper.logtracker.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.KeepAliveLogReader;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.proc.CategoryCollection;
import org.sper.logtracker.proc.DataPoint;
import org.sper.logtracker.proc.DataPointFactorizer;
import org.sper.logtracker.proc.MultiPipeCollector;
import org.sper.logtracker.proc.NewPointExtractor;
import org.sper.logtracker.proc.UserDataPoint;
import org.sper.logtracker.proc.UserDataPointFactorizer;
import org.sper.logtracker.scatter.ServiceScatterPlot;
import org.sper.logtracker.scatter.TooltipGenerator;
import org.sper.logtracker.stats.StatsCalculator;
import org.sper.logtracker.stats.StatsCalculator.CategoryExtractor;

public class ServiceStatsTabs {
	private ServiceScatterPlot plot;
	private NewPointExtractor newPointExtractor;
	@SuppressWarnings("rawtypes")
	private DataPointFactorizer factorizer;
	private ServiceControlPanel serviceControlPanel;
	private UserPanel userPanel;
	private StatsCalculator<DataPoint> serviceStatsCalculator;
	private JTabbedPane tabbedPane;
	private KeepAliveElement terminationPointer;
	private LogTracker logTracker;

	public ServiceStatsTabs(JTabbedPane tabbedPane, LogTracker logTracker, Configuration configuration) throws InterruptedException {
		this.tabbedPane = tabbedPane;
		this.logTracker = logTracker;
		serviceControlPanel = new ServiceControlPanel(this);
		tabbedPane.addTab("Services/Filter", null, serviceControlPanel, null);
		configuration.registerModule(serviceControlPanel);
		userPanel = new UserPanel(this);
		userPanel.setVisible(false);
		configuration.registerModule(userPanel);
		plot = new ServiceScatterPlot();
		tabbedPane.addTab("Graph", null, plot.getPanel(), null);
	}

	final class ApplyControlAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			setupDataSeries();
			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
			plot.setMaxRange(20.);
		}
	}

	public void setupDataSeries() {
		CategoryCollection users = null;
		if (((LogParser) logTracker.getFileControlPanel().getLogFileFormatBox().getSelectedItem()).providesUsers())
			users = userPanel.createUsersFilter(((UserDataPointFactorizer)factorizer).getUser());
		newPointExtractor.removeListeners();
		newPointExtractor.addListener(plot);
		serviceControlPanel.applyToSeriesCollection(newPointExtractor, factorizer.getService(), plot.getXyPlot(), users);
		newPointExtractor.resendData();
	}
	
	@SuppressWarnings("unchecked")
	void setupDataPipeLines(List<String> fname, LogParser logParser) {
		try {
			serviceControlPanel.cleanTable();
			factorizer = logParser.providesUsers() ? new UserDataPointFactorizer() : new DataPointFactorizer<DataPoint>();
			serviceStatsCalculator = new StatsCalculator<DataPoint>(factorizer.getService(), new CategoryExtractor<DataPoint>() {

				@Override
				public Integer cat(DataPoint dp) {
					return dp.svcIdx;
				}
			}, serviceControlPanel.getTable(), true, serviceControlPanel.getPublishingSemaphore(), serviceControlPanel.getApplyButton());
			factorizer.addListener(serviceStatsCalculator);
			userPanel.clearTable();
			if (logParser.providesUsers()) {
				if (tabbedPane.getTabCount() < 4)
					tabbedPane.insertTab("Users", null, userPanel, null, 2);
				factorizer.addListener(new StatsCalculator<UserDataPoint>(factorizer.getUser(), new CategoryExtractor<UserDataPoint>() {
					@Override
					public Integer cat(UserDataPoint dp) {
						return dp.userIdx;
					}
				}, userPanel.getTable(), false, null, userPanel.getApplyButon()));
			} else
				if (tabbedPane.getTabCount() == 4)
					tabbedPane.remove(2);
			newPointExtractor = new NewPointExtractor();
			factorizer.addListener(newPointExtractor);
			if (terminationPointer != null)
				terminationPointer.endOfLife();
			Long obsStart = logTracker.getFileControlPanel().getObsStart();
			if (fname.size() == 1) {
				LogLineParser logLineParser = new LogLineParser(logParser, obsStart);
				logLineParser.registerListener(factorizer);
				KeepAliveLogReader keepAliveLogReader = new KeepAliveLogReader(new File(fname.get(0)), logLineParser);
				terminationPointer = keepAliveLogReader;
				keepAliveLogReader.start();
			} else {
				// Bei mehreren Input-Files müssen die Daten durch einen MutliPipeCollector zusammengefasst werden.
				MultiPipeCollector pipeCollector = new MultiPipeCollector();
				pipeCollector.addListener(factorizer);
				for (String fn : fname) {
					LogLineParser logLineParser = new LogLineParser(logParser, obsStart);
					KeepAliveLogReader keepAliveElement = new KeepAliveLogReader(new File(fn), logLineParser);
					pipeCollector.addFeeder(logLineParser, keepAliveElement);
				}
				pipeCollector.run();
				terminationPointer = pipeCollector;
			}

			Factor services = factorizer.getService();
			Factor users = factorizer.getUser();
			XYToolTipGenerator toolTipGenerator = new TooltipGenerator(services, users);
			plot.getXyPlot().getRenderer().setBaseToolTipGenerator(toolTipGenerator);
			tabbedPane.setSelectedIndex(1);
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(logTracker.getFrame(), e1, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}