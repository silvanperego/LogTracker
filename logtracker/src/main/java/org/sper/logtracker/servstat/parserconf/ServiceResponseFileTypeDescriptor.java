package org.sper.logtracker.servstat.parserconf;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigPanel;
import org.sper.logtracker.servstat.ServiceResponseLogParser;
import org.sper.logtracker.servstat.data.RawStatsDataPoint;
import org.sper.logtracker.servstat.ui.ServiceStatsDockables;

public class ServiceResponseFileTypeDescriptor implements FileTypeDescriptor<ServiceResponseLogParser, RawStatsDataPoint> {

	private ParserConfigPanel parserConfigDialog;
	private ServiceResponseExtractionFields fields;

	@Override
	public ExtractionFieldHandler<ServiceResponseLogParser, RawStatsDataPoint> createExtractionFieldPanel(ParserConfigPanel parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new ServiceResponseExtractionFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public ServiceStatsDockables createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?,?> logParser, GlobalConfig globalConfig, CLocation parentLocation) throws InterruptedException {
		return new ServiceStatsDockables(control, configuration, (ServiceResponseLogParser) logParser, globalConfig, parentLocation);
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

}
