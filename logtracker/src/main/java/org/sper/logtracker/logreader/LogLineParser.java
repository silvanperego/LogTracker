package org.sper.logtracker.logreader;

import org.sper.logtracker.data.DataListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Verwendet einen Log-Parser zum einlesen einzelner Log-Zeilen. Leitet jede gelesene Zeile dann an alle
 * Registrierten DataListeners weiter. 
 * @author silvan.perego
 */
public class LogLineParser<T> implements DataListener<T> {

	private List<DataListener<T>> dataListeners = new ArrayList<DataListener<T>>();
	private LogParser<T> logParser;
	private Long obsStart;
	private LogSource logSource;

	public LogLineParser(LogParser<T> logParser, Long obsStart, LogSource logSource) {
		this.logParser = logParser;
		this.obsStart = obsStart;
		this.logSource = logSource;
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

	public void scanLine(FileSnippet lineInFile) {
		logParser.scanLine(lineInFile, this, obsStart);
	}

	public LogSource getLogSource() {
		return logSource;
	}

}
