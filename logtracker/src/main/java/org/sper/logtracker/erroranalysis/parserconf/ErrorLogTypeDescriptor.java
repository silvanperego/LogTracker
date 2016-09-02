package org.sper.logtracker.erroranalysis.parserconf;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigPanel;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;

public class ErrorLogTypeDescriptor implements FileTypeDescriptor<ErrorLogParser, RawErrorDataPoint> {

	private ParserConfigPanel parserConfigDialog;
	private ErrorLogExtractionFields fields;
	
	public ErrorLogTypeDescriptor() {
	}

	@Override
	public ExtractionFieldHandler<ErrorLogParser, RawErrorDataPoint> createExtractionFieldPanel(ParserConfigPanel parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new ErrorLogExtractionFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public ErrorLogTypeDockables createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?,?> logParser, GlobalConfig globalConfig, CLocation parentLocation)
			throws InterruptedException {
		return new ErrorLogTypeDockables(control, configuration, logParser, globalConfig, parentLocation);
	}

	@Override
	public String toString() {
		return "Error Log File";
	}

	@Override
	public ErrorLogParser createParser(String name) {
		return new ErrorLogParser(name);
	}

	@Override
	public ErrorLogParser convertLogParser(ConfiguredLogParser<?,?> other) {
		return new ErrorLogParser(other);
	}

}
