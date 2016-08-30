package org.sper.logtracker.servstat.parserconf;

import java.util.List;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigPanel;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;
import org.sper.logtracker.servstat.ui.ServiceStatsTabs;

import bibliothek.gui.dock.common.CControl;

public class ServiceResponseFileTypeDescriptor implements FileTypeDescriptor<ServiceResponseLogParser, RawStatsDataPoint> {

	private ParserConfigPanel parserConfigDialog;
	private ServiceResponseExtractionFields fields;
	private ServiceStatsTabs serviceStatsTabs;

	@Override
	public ExtractionFieldHandler<ServiceResponseLogParser, RawStatsDataPoint> createExtractionFieldPanel(ParserConfigPanel parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new ServiceResponseExtractionFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public void createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?,?> logParser, GlobalConfig globalConfig) throws InterruptedException {
		serviceStatsTabs = new ServiceStatsTabs(control, configuration, (ServiceResponseLogParser) logParser, globalConfig);
	}

	@Override
	public String toString() {
		return "Service Calls and Response Times";
	}

	@Override
	public ServiceResponseLogParser createParser(String string) {
		return new ServiceResponseLogParser(string);
	}

	@Override
	public ServiceResponseLogParser convertLogParser(ConfiguredLogParser<?,?> other) {
		return new ServiceResponseLogParser(other);
	}

	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?,?> logParser, Long obsStart, ActivityMonitor activityMonitor, GlobalConfig globalConfig) {
		serviceStatsTabs.setupDataPipeLines(logSource, logParser, obsStart, activityMonitor);
	}

	@Override
	public void removeDockables(CControl control) {
		serviceStatsTabs.removeDockables(control);
	}

	@Override
	public Object getControlDataConfig() {
		return serviceStatsTabs.getControlDataConfig();
	}

	@Override
	public void applyConfig(Object controlData) {
		serviceStatsTabs.applyConfig(controlData);
	}

}
