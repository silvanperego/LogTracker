package org.sper.logtracker.correlation.parserconf;

import bibliothek.gui.dock.common.CControl;
import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.correlation.data.CorrelatedDataPoint;
import org.sper.logtracker.correlation.data.CorrelationCatalog;
import org.sper.logtracker.correlation.data.CorrelationFactors;
import org.sper.logtracker.correlation.data.RawCorrelatedDataPoint;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.logreader.ActivityMonitor;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.LogSource;
import org.sper.logtracker.parserconf.ConfiguredLogParser;
import org.sper.logtracker.parserconf.TrackingDockables;
import org.sper.logtracker.proc.PipelineHelper;

import javax.swing.*;
import java.util.List;

public class CorrelationDataTypeDockables implements TrackingDockables {

	private KeepAliveElement keepAliveElement;

	@Override
	public void setupDataPipeLines(List<LogSource> logSource, ConfiguredLogParser<?, ?> logParser, Long obsStart, ActivityMonitor activityMonitor, GlobalConfig globalConfig) {
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
							factors.getUser().addString(data.user), data.logSource, 
							data.correlationId, data.fileSnippet, 
							factors));
				}

				@Override
				public void publishData() {
				}
			};
			keepAliveElement = PipelineHelper.setupFileReaders(logSource, logParser, obsStart, activityMonitor, categoryListener);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e, "Error", JOptionPane.ERROR_MESSAGE);
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
