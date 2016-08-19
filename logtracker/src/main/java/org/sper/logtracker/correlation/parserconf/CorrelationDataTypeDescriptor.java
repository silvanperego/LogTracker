package org.sper.logtracker.correlation.parserconf;

import java.util.List;

import javax.swing.JOptionPane;

import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.CorrelationLogParser;
import org.sper.logtracker.correlation.data.CorrelatedDataPoint;
import org.sper.logtracker.correlation.data.CorrelationCatalog;
import org.sper.logtracker.correlation.data.CorrelationFactors;
import org.sper.logtracker.correlation.data.RawCorrelatedDataPoint;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.erroranalysis.ui.LogLinePanel;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.proc.PipelineHelper;

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

	private ParserConfigDialog parserConfigDialog;
	private CorrelationDataFields fields;
	private KeepAliveElement keepAliveElement;
	private LogLinePanel logLinePanel;

	public CorrelationDataTypeDescriptor() {
	}

	@Override
	public ExtractionFieldHandler<CorrelationLogParser, RawCorrelatedDataPoint> createExtractionFieldPanel(ParserConfigDialog parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new CorrelationDataFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public void createAndRegisterDockables(CControl control, Configuration configuration,
			ConfiguredLogParser<?, ?> logParser) throws InterruptedException {
		// Es werden keine Dockables erzeugt, sondern der User wird informiert,
		// dass der Prozess gestartet wurde.
		JOptionPane.showMessageDialog(control.getContentArea(), "Log files are being monitored", "Information",
				JOptionPane.INFORMATION_MESSAGE);
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

	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?, ?> logParser, Long obsStart) {
		try {
			if (keepAliveElement != null) {
				keepAliveElement.endOfLife();
			}
			DataListener<RawCorrelatedDataPoint> categoryListener = new DataListener<RawCorrelatedDataPoint>() {

				private CorrelationFactors factors = new CorrelationFactors();
				private CorrelationCatalog<CorrelatedDataPoint> catalog = CorrelationCatalog.getInstance();

				@Override
				public void receiveData(RawCorrelatedDataPoint data) {
					catalog.receiveData(new CorrelatedDataPoint(data.occTime,
							factors.getServiceName().addString(data.serviceName), 
							factors.getUser().addString(data.user), factors.getLogSource().addString(data.logSource), 
							data.correlationId, data.fileSnippet, 
							factors));
				}

				@Override
				public void publishData() {
				}
			};
			keepAliveElement = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, categoryListener);
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
		// Es gibt keine spezifische Konfiguration für diesen File-Typ. (Die
		// Methode sollte eigentlich gar nie aufgerufen werden).
	}

}
