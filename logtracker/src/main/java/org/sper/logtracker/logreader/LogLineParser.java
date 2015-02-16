package org.sper.logtracker.logreader;

import java.util.ArrayList;
import java.util.List;

import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.data.RawDataPoint;

/**
 * Leitet gelesene Zeilen an den {@link LogParser} weiter welcher sie dann 
 * @author silvan.perego
 */
public class LogLineParser implements DataListener<RawDataPoint>{

	private List<DataListener<RawDataPoint>> dataListeners = new ArrayList<DataListener<RawDataPoint>>();
	private LogParser logParser;
	private Long obsStart;

	public LogLineParser(LogParser logParser, Long obsStart) {
		this.logParser = logParser;
		this.obsStart = obsStart;
	}

	@Override
	public synchronized void publishData() {
		for (DataListener<RawDataPoint> dataListener : dataListeners) {
			dataListener.publishData();
		}
	}

	public void registerListener(DataListener<RawDataPoint> listener) {
		dataListeners.add(listener);
	}

	public void removeListeners() {
		dataListeners.clear();
	}
	
	public void receiveData(RawDataPoint dataPoint) {
		for (DataListener<RawDataPoint> listener : dataListeners) {
			listener.receiveData(dataPoint);
		}
	}

	public void scanLine(String readLine) {
		logParser.scanLine(readLine, this, obsStart);
	}

}
