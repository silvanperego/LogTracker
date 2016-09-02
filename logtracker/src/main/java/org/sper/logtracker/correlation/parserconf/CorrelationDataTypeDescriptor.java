package org.sper.logtracker.correlation.parserconf;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.CorrelationLogParser;
import org.sper.logtracker.correlation.data.RawCorrelatedDataPoint;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigPanel;

import bibliothek.gui.dock.common.CControl;

/**
 * Beschreibt einen File-Typ mit Daten, welche nur über Correlation geladen
 * werden. Sie bringen Zusatzinfos zu Abläufen, wie z.B. Message-Content, haben
 * aber keinen Service-Namen, keine typische Meldung und keine Response-Time.
 * 
 * @author silvan.perego
 *
 */
public class CorrelationDataTypeDescriptor implements FileTypeDescriptor<CorrelationLogParser, RawCorrelatedDataPoint> {

	private ParserConfigPanel parserConfigDialog;
	private CorrelationDataFields fields;

	public CorrelationDataTypeDescriptor() {
	}

	@Override
	public ExtractionFieldHandler<CorrelationLogParser, RawCorrelatedDataPoint> createExtractionFieldPanel(ParserConfigPanel parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new CorrelationDataFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public CorrelationDataTypeDockables createAndRegisterDockables(CControl control, Configuration configuration,
			ConfiguredLogParser<?, ?> logParser, GlobalConfig globalConfig) throws InterruptedException {
		return new CorrelationDataTypeDockables();
	}

	@Override
	public String toString() {
		return "Correlated Log File";
	}

	@Override
	public CorrelationLogParser createParser(String name) {
		return new CorrelationLogParser(name);
	}

	@Override
	public CorrelationLogParser convertLogParser(ConfiguredLogParser<?,?> other) {
		return new CorrelationLogParser(other);
	}

}
