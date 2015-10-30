package org.sper.logtracker.servstat.parserconf;

import java.util.List;

import javax.swing.JTabbedPane;

import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;
import org.sper.logtracker.servstat.ui.ServiceStatsTabs;

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
	public void createAndRegisterTabs(JTabbedPane tabbedPane, Configuration configuration, ConfiguredLogParser<?> logParser) throws InterruptedException {
		serviceStatsTabs = new ServiceStatsTabs(tabbedPane, configuration, (ServiceResponseLogParser) logParser);
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
		serviceStatsTabs.setupDataPipeLines(logSource, (LogParser<RawStatsDataPoint>) logParser, obsStart);
	}

}
