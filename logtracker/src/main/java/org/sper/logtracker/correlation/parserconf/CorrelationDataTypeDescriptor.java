package org.sper.logtracker.correlation.parserconf;

import java.util.List;

import javax.swing.JOptionPane;

import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.data.CorrelationCatalog;
import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.erroranalysis.ui.LogLinePanel;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.proc.PipelineHelper;

import bibliothek.gui.dock.common.CControl;

/**
 * Beschreibt einen File-Typ mit Daten, welche nur über Correlation geladen werden. Sie bringen Zusatzinfos zu Abläufen, wie z.B. 
 * Message-Content, haben aber keinen Service-Namen, keine typische Meldung und keine Response-Time.
 * @author silvan.perego
 *
 */
public class CorrelationDataTypeDescriptor implements FileTypeDescriptor {

	private ParserConfigDialog parserConfigDialog;
	private CorrelationDataFields fields;
	private KeepAliveElement keepAliveElement;
	private LogLinePanel logLinePanel;
	
	public CorrelationDataTypeDescriptor() {
	}

	@Override
	public ExtractionFieldHandler createExtractionFieldPanel(ParserConfigDialog parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new CorrelationDataFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public void createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?> logParser)
			throws InterruptedException {
		// Es werden keine Dockables erzeugt, sondern der User wird informiert, dass der Prozess gestartet wurde.
		JOptionPane.showMessageDialog(control.getContentArea(), "Log files are being monitored", "Information", JOptionPane.INFORMATION_MESSAGE);
	}

	@Override
	public String toString() {
		return "Correlated Log File";
	}

	@Override
	public ConfiguredLogParser<?> createParser(String name) {
		return new ErrorLogParser(name);
	}

	@Override
	public ConfiguredLogParser<?> convertLogParser(ConfiguredLogParser<?> other) {
		return new ErrorLogParser(other);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?> logParser, Long obsStart) {
		try {
			if (keepAliveElement != null) {
				keepAliveElement.endOfLife();
			}
			keepAliveElement = PipelineHelper.setupFileReaders(logSource, (LogParser<RawErrorDataPoint>) logParser, obsStart, CorrelationCatalog.getInstance());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(logLinePanel, e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void removeDockables(CControl control) {
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