package org.sper.logtracker.erroranalysis.parserconf;

import java.util.List;

import javax.swing.JOptionPane;

import org.sper.logtracker.config.compat.Configuration;
import org.sper.logtracker.correlation.data.CorrelationCatalog;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.erroranalysis.data.LogLineCatalog;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.erroranalysis.ui.LogLinePanel;
import org.sper.logtracker.erroranalysis.ui.LogLineTableModel;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.proc.PipelineHelper;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CLocation;
import bibliothek.gui.dock.common.DefaultMultipleCDockable;

public class ErrorLogTypeDescriptor implements FileTypeDescriptor<ErrorLogParser, RawErrorDataPoint> {

	private ParserConfigDialog parserConfigDialog;
	private ErrorLogExtractionFields fields;
	private KeepAliveElement keepAliveElement;
	private LogLineTableModel logLineTableModel;
	private LogLinePanel logLinePanel;
	private DefaultMultipleCDockable logLineDockable;
	
	public ErrorLogTypeDescriptor() {
	}

	@Override
	public ExtractionFieldHandler<ErrorLogParser, RawErrorDataPoint> createExtractionFieldPanel(ParserConfigDialog parserConfigDialog) {
		if (this.parserConfigDialog != parserConfigDialog)
			fields = new ErrorLogExtractionFields(parserConfigDialog);
		this.parserConfigDialog = parserConfigDialog;
		return fields;
	}

	@Override
	public void createAndRegisterDockables(CControl control, Configuration configuration, ConfiguredLogParser<?,?> logParser)
			throws InterruptedException {
		logLinePanel = new LogLinePanel();
		logLineTableModel = logLinePanel.getTableModel();
		logLineDockable = new DefaultMultipleCDockable(null, "Error Log-Messages", logLinePanel);
		control.addDockable(logLineDockable);
		logLineDockable.setLocation(CLocation.base().normalEast(0.6));
		logLineDockable.setVisible(true);
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

	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?, ?> logParser, Long obsStart, ActivityMonitor activityMonitor) {
		try {
			DataListener<RawErrorDataPoint> logLineCatalog = new LogLineCatalog(logLineTableModel);
			if (keepAliveElement != null) {
				keepAliveElement.endOfLife();
			}
			if (logParser.getCorrelationIdIdx() != null)
				keepAliveElement = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, activityMonitor, logLineCatalog, CorrelationCatalog.getInstance());
			else
				keepAliveElement = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, activityMonitor, logLineCatalog);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(logLinePanel, e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	public void removeDockables(CControl control) {
		control.removeDockable(logLineDockable);
		logLineDockable = null;
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
