package org.sper.logtracker.erroranalysis.parserconf;

import java.util.List;

import javax.swing.JOptionPane;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.data.CorrelationCatalog;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.erroranalysis.data.LogLineCatalog;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.erroranalysis.ui.LogLinePanel;
import org.sper.logtracker.erroranalysis.ui.LogLineTableModel;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.TrackingDockables;
import org.sper.logtracker.proc.PipelineHelper;
import org.sper.logtracker.util.DockUtils;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;

public class ErrorLogTypeDockables implements TrackingDockables {

	private KeepAliveElement keepAliveElement;
	private DefaultMultipleCDockable logLineDockable;
	private LogLineTableModel logLineTableModel;
	private LogLinePanel logLinePanel;
	
	public ErrorLogTypeDockables(CControl control, Configuration configuration, ConfiguredLogParser<?,?> logParser, GlobalConfig globalConfig, CLocation parentLocation) {
		logLinePanel = new LogLinePanel(globalConfig);
		logLineTableModel = logLinePanel.getTableModel();
		logLineDockable = new DefaultMultipleCDockable(null, "Log-Messages", logLinePanel);
		control.addDockable(logLineDockable);
		parentLocation = DockUtils.aside(parentLocation);
		logLineDockable.setLocation(parentLocation);
		logLineDockable.setVisible(true);
	}

	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?, ?> logParser, Long obsStart, ActivityMonitor activityMonitor, GlobalConfig globalConfig) {
		try {
			DataListener<RawErrorDataPoint> logLineCatalog = new LogLineCatalog(logLineTableModel, globalConfig);
			if (keepAliveElement != null) {
				keepAliveElement.endOfLife();
			}
			if (logParser.getCorrelationIdIdx() != null)
				keepAliveElement = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, activityMonitor, logLineCatalog, CorrelationCatalog.getInstance());
			else
				keepAliveElement = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, activityMonitor, logLineCatalog);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(logLinePanel, e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void removeDockables(CControl control) {
		control.removeDockable(logLineDockable);
		logLineDockable = null;
		if (keepAliveElement != null)
			keepAliveElement.endOfLife();
	}

	@Override
	public Object getControlDataConfig() {
		return null;
	}

	@Override
	public void applyConfig(Object controlData) {
		// Es gibt keine spezifische Konfiguration für diesen File-Typ. (Die Methode sollte eigentlich gar nie aufgerufen werden).
	}

}
