package org.sper.logtracker.erroranalysis.parserconf;

import java.util.List;

import javax.swing.JTabbedPane;

import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;

public class ErrorLogTypeDescriptor implements FileTypeDescriptor {

	private ParserConfigDialog parserConfigDialog;
	private ErrorLogExtractionFields fields;

	@Override
	public ExtractionFieldHandler createExtractionFieldPanel(ParserConfigDialog parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new ErrorLogExtractionFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public void createAndRegisterTabs(JTabbedPane tabbedPane, Configuration configuration, ConfiguredLogParser<?> logParser)
			throws InterruptedException {
	}

	@Override
	public String toString() {
		return "Error Log File";
	}

	@Override
	public ConfiguredLogParser<?> createParser(String name) {
		return new ErrorLogParser(name, this);
	}

	@Override
	public ConfiguredLogParser<?> convertLogParser(ConfiguredLogParser<?> other) {
		return new ErrorLogParser(other, this);
	}

	@Override
	public void setupDataPipeLines(List<String> fname, ConfiguredLogParser<?> logParser, Long obsStart) {
	}

}
