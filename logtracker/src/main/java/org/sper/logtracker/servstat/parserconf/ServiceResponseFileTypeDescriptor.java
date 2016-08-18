package org.sper.logtracker.servstat.parserconf;

import java.util.List;

import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;
import org.sper.logtracker.servstat.ui.ServiceStatsTabs;

import bibliothek.gui.dock.common.CControl;

public class ServiceResponseFileTypeDescriptor implements FileTypeDescriptor {

	private ParserConfigDialog parserConfigDialog;
	private ServiceResponseExtractionFields fields;
	private ServiceStatsTabs serviceStatsTabs;

	@Override
	public ExtractionFieldHandler createExtractionFieldPanel(ParserConfigDialog parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new ServiceResponseExtractionFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public void createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?> logParser) throws InterruptedException {
		serviceStatsTabs = new ServiceStatsTabs(control, configuration, (ServiceResponseLogParser) logParser);
	}

	@Override
	public String toString() {
		return "Service Calls and Response Times";
	}

	@Override
	public ConfiguredLogParser<RawStatsDataPoint> createParser(String string) {
		return new ServiceResponseLogParser(string);
	}

	@Override
	public ConfiguredLogParser<RawStatsDataPoint> convertLogParser(ConfiguredLogParser<?> other) {
		return new ServiceResponseLogParser(other);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?> logParser, Long obsStart) {
		serviceStatsTabs.setupDataPipeLines(logSource, (ConfiguredLogParser<RawStatsDataPoint>) logParser, obsStart);
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
