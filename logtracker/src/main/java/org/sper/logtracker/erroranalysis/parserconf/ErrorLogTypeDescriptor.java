package org.sper.logtracker.erroranalysis.parserconf;

import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import org.sper.logtracker.config.Configuration;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.erroranalysis.ErrorLogParser;
import org.sper.logtracker.erroranalysis.data.LogLineCatalog;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;
import org.sper.logtracker.erroranalysis.ui.LogLinePanel;
import org.sper.logtracker.erroranalysis.ui.LogLineTableModel;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.ExtractionFieldHandler;
import org.sper.logtracker.parserconf.FileTypeDescriptor;
import org.sper.logtracker.parserconf.ParserConfigDialog;
import org.sper.logtracker.proc.PipelineHelper;

public class ErrorLogTypeDescriptor implements FileTypeDescriptor {

	private ParserConfigDialog parserConfigDialog;
	private ErrorLogExtractionFields fields;
	private JTabbedPane parentPane;
	private KeepAliveElement keepAliveElement;
	private LogLineTableModel logLineTableModel;
	
	public ErrorLogTypeDescriptor() {
	}

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
		parentPane = tabbedPane;
		LogLinePanel logLinePanel = new LogLinePanel();
		logLineTableModel = logLinePanel.getTableModel();
		tabbedPane.addTab("Log Lines", logLinePanel);
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

	@SuppressWarnings("unchecked")
	@Override
	public void setupDataPipeLines(List<String> fname, ConfiguredLogParser<?> logParser, Long obsStart) {
		try {
			DataListener<RawErrorDataPoint> copyToTableListener = new LogLineCatalog(logLineTableModel);
			if (keepAliveElement != null) {
				keepAliveElement.endOfLife();
			}
			keepAliveElement = PipelineHelper.setupFileReaders(fname, (LogParser<RawErrorDataPoint>) logParser, obsStart, copyToTableListener);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(parentPane, e, "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

}
