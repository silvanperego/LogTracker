package org.sper.logtracker.servstat.ui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.event.MouseInputAdapter;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.data.CorrelationCatalog;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.TrackingDockables;
import org.sper.logtracker.proc.PipelineHelper;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.proc.CategoryCollection;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.proc.NewPointExtractor;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer.CorrelatedStatsDataPointFactorizer;
import org.sper.logtracker.servstat.scatter.ServiceScatterPlot;
import org.sper.logtracker.servstat.stats.StatsCalculator;
import org.sper.logtracker.servstat.stats.StatsCalculator.CategoryExtractor;
import org.sper.logtracker.servstat.ui.detail.ServiceCallDetailViewer;
import org.sper.logtracker.util.DockUtils;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;

public class ServiceStatsDockables implements TrackingDockables {
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
	private GlobalConfig globalConfig;

	public final class ApplyControlAction implements ActionListener {
  	public void actionPerformed(ActionEvent e) {
  		setupDataSeries();
  		graphDockable.toFront();
  	}
  }

  class ShowServiceDetailAction extends MouseInputAdapter {
    private JTable controlTable;
    private Function<Integer, String> rowToFilterVal;
    private Function<DataPoint, Integer> dataPointToIndex;
    private String filterQualifier;
    private Function<StatsDataPointFactorizer<DataPoint>, Factor> getRelevantFactor;
	private int maxRelevCol;
    
    public ShowServiceDetailAction(JTable controlTable, String filterQualifier, 
        Function<Integer, String> rowToFilterVal, Function<StatsDataPointFactorizer<DataPoint>, Factor> getRelevantFactor, Function<DataPoint, Integer> dataPointToIndex,
        int maxRelevCol) {
      this.controlTable = controlTable;
      this.filterQualifier = filterQualifier;
      this.rowToFilterVal = rowToFilterVal;
      this.getRelevantFactor = getRelevantFactor;
      this.dataPointToIndex = dataPointToIndex;
	this.maxRelevCol = maxRelevCol;
    }
  
    @Override
    public void mouseClicked(MouseEvent e) {
    	int row = controlTable.rowAtPoint(e.getPoint());
    	int col = controlTable.columnAtPoint(e.getPoint());
    	if (col >= 0 && col < maxRelevCol && row >= 0 && row < controlTable.getRowCount()) {
    		String filterVal = rowToFilterVal.apply(row);
    		Integer filterIdx = getRelevantFactor.apply(factorizer).getStringIndex(filterVal);
    		List<DataPoint> filteredList;
    		synchronized (newPointExtractor) {
    			filteredList = newPointExtractor.stream()
    					.filter(p -> {Integer val = dataPointToIndex.apply(p); return val != null && val.intValue() == filterIdx;})
    					.sorted((a, b) -> b.occTime.compareTo(a.occTime))
    					.limit(1000)
    					.collect(Collectors.toList());
    		}
    		new ServiceCallDetailViewer(filterQualifier + ' ' + filterVal, filteredList, factorizer, globalConfig).setVisible(true);
    	}
    }

  }
  
  public ServiceStatsDockables(CControl control, Configuration configuration, ServiceResponseLogParser logParser, GlobalConfig globalConfig, CLocation parentLocation) throws InterruptedException {
		this.globalConfig = globalConfig;
		serviceControlPanel = new ServiceControlPanel(this);
		int stackpos = 0;
		serviceControlDockable = createDockable(control, stackpos++, "Services/Filter", serviceControlPanel, parentLocation);
		if (configuration != null)
			configuration.registerModule(serviceControlPanel);
		providesUsers = logParser.providesUsers();
		if (providesUsers) {
			userPanel = new UserPanel(this);
			if (configuration != null)
				configuration.registerModule(userPanel);
			userDockable = createDockable(control, stackpos++, "Users", userPanel, parentLocation);
			userDockable.setVisible(true);
		}
		serviceControlDockable.setVisible(true);
		plot = new ServiceScatterPlot(this, globalConfig);
		graphDockable = createDockable(control, stackpos++, "Graph", plot.getPanel(), DockUtils.aside(parentLocation));
		successRetCode = logParser.getSuccessCode();
		graphDockable.setVisible(true);
	}

	private DefaultMultipleCDockable createDockable(CControl control, int stackpos, String title, Component comp, CLocation location) {
		final DefaultMultipleCDockable dockable = new DefaultMultipleCDockable(null, title, comp);
		control.addDockable(dockable);
		dockable.setLocation(location);
		return dockable;
	}

	public void setupDataSeries() {
		CategoryCollection users = null;
		if (providesUsers)
			users = userPanel.createUsersFilter(factorizer.getUser());
		newPointExtractor.removeListeners();
		newPointExtractor.addListener(plot);
		serviceControlPanel.applyToSeriesCollection(newPointExtractor, factorizer.getService(), plot.getXyPlot(), users, successRetCode);
  		plot.setMaxRange(20.);
		newPointExtractor.resendData();
	}
	
	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?, ?> logParser, Long obsStart,
			ActivityMonitor activityMonitor, GlobalConfig globalConfig) {
		try {
			serviceControlPanel.cleanTable();
			factorizer = createFactorizer((ServiceResponseLogParser) logParser);
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
			terminationPointer = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, activityMonitor, factorizer);

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
	private StatsDataPointFactorizer<DataPoint> createFactorizer(ServiceResponseLogParser logParser) {
		if (logParser.getCorrelationIdIdx() != null) {
			CorrelatedStatsDataPointFactorizer cfact = new StatsDataPointFactorizer.CorrelatedStatsDataPointFactorizer();
			cfact.addListener(CorrelationCatalog.getInstance());
			return (StatsDataPointFactorizer) cfact;
		} else
			return new StatsDataPointFactorizer.SimpleStatsDataPointFactorizer();
	}

	@Override
	public void removeDockables(CControl control) {
		control.removeDockable(serviceControlDockable);
		control.removeDockable(graphDockable);
		if (userDockable != null)
			control.removeDockable(userDockable);
		if (terminationPointer != null)
			terminationPointer.endOfLife();
	}

	@Override
	public Object getControlDataConfig() {
		final ServiceControlData config = serviceControlPanel.getConfig();
		if (userPanel != null)
			userPanel.addUserExludes(config);
		return config;
	}

	@Override
	public void applyConfig(Object controlData) {
		ServiceControlData scd = (ServiceControlData) controlData;
		serviceControlPanel.applyXmlConfig(scd);
		if (userPanel != null)
			userPanel.applyConfig(scd);
		newPointExtractor.addListener(new DataListener<DataPoint>() {
			@Override
			public void receiveData(DataPoint data) {
			}
			
			@Override
			public void publishData() {
				EventQueue.invokeLater(() -> setupDataSeries());
			}
		});
	}

	public StatsDataPointFactorizer<DataPoint> getFactorizer() {
		return factorizer;
	}

}