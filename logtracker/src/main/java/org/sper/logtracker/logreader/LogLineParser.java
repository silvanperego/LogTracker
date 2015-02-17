package org.sper.logtracker.logreader;

import java.util.ArrayList;
import java.util.List;

import org.sper.logtracker.data.DataListener;

/**
 * Leitet gelesene Zeilen an den {@link LogParser} weiter welcher sie dann 
 * @author silvan.perego
 */
public class LogLineParser<T> implements DataListener<T> {

	private List<DataListener<T>> dataListeners = new ArrayList<DataListener<T>>();
	private LogParser<T> logParser;
	private Long obsStart;

	public LogLineParser(LogParser<T> logParser, Long obsStart) {
		this.logParser = logParser;
		this.obsStart = obsStart;
	}

	@Override
	public synchronized void publishData() {
		for (DataListener<T> dataListener : dataListeners) {
			dataListener.publishData();
		}
	}

	public void registerListener(DataListener<T> listener) {
		dataListeners.add(listener);
	}

	public void removeListeners() {
		dataListeners.clear();
	}
	
	@Override
	public void receiveData(T dataPoint) {
		for (DataListener<T> listener : dataListeners) {
			listener.receiveData(dataPoint);
		}
	}

	public void scanLine(String readLine) {
		logParser.scanLine(readLine, this, obsStart);
	}

}
