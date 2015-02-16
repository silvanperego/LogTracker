package org.sper.logtracker.servstat;

import java.util.List;

import org.sper.logtracker.logreader.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.ui.LogTracker;

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
	public void createAndRegisterTabs(LogTracker logTracker, ConfiguredLogParser logParser) throws InterruptedException {
		serviceStatsTabs = new ServiceStatsTabs(logTracker, (ServiceResponseLogParser) logParser);
	}

	@Override
	public String toString() {
		return "Service Calls and Response Times";
	}

	@Override
	public ConfiguredLogParser createParser(String string) {
		return new ServiceResponseLogParser(string, this);
	}

	@Override
	public ConfiguredLogParser convertLogParser(ConfiguredLogParser other) {
		return new ServiceResponseLogParser(other, this);
	}

	@Override
	public void setupDataPipeLines(List<String> fname, ConfiguredLogParser logParser) {
		serviceStatsTabs.setupDataPipeLines(fname, logParser);
	}

}
