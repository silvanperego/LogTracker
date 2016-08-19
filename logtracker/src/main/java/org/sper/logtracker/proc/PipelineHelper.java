package org.sper.logtracker.proc;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.KeepAliveLogReader;
import org.sper.logtracker.logreader.LogLineParser;
import org.sper.logtracker.logreader.LogParser;
import org.sper.logtracker.logreader.LogSource;

public class PipelineHelper {

	/**
	 * Erstelle die Log-File-nahe Basisstruktur der der Pipeline. Überwache Files, interpretiere deren Inhalt.
	 * und leite die Raw-Daten an einen Listener weiter.
	 * @param fname eine Liste der Filenamen, welche überwacht werden sollen.
	 * @param logLineInterpreter interpretiert die gelesenen Zeilen und erstellt Raw-Datensätze.
	 * @param obsStart Daten ab diesem Zeitpunkt sollen interpretiert, ansonsten ignoriert werden.
	 * @param rawDataListener die Daten-Listener, an welche die interpretierten und verarbeiteten Daten gesendet werden sollen.
	 * @return ein KeepAliveElement. Mit diesem kann das Ende eine Lese-Operation signalisiert werden.
	 * @throws FileNotFoundException
	 */
	@SafeVarargs
	public static <T extends RawDataPoint> KeepAliveElement setupFileReaders(List<LogSource> logSource,
			LogParser<?> logLineInterpreter, Long obsStart, DataListener<T>... rawDataListener)
			throws FileNotFoundException {
		KeepAliveElement terminationPointer = null;
		if (logSource.size() == 1) {
			@SuppressWarnings("unchecked")
			LogLineParser<T> logLineParser = new LogLineParser<T>((LogParser<T>) logLineInterpreter, obsStart, logSource.get(0).getSourceName());
			for (DataListener<T> dataListener : rawDataListener) {
				logLineParser.registerListener(dataListener);
			}
			KeepAliveLogReader keepAliveLogReader = new KeepAliveLogReader(new File(logSource.get(0).getFileName()), logLineParser, logLineInterpreter.getEncoding());
			terminationPointer = keepAliveLogReader;
			keepAliveLogReader.start();
		} else {
			// Bei mehreren Input-Files müssen die Daten durch einen MultiPipeCollector zusammengefasst werden.
			MultiPipeCollector<T> pipeCollector = new MultiPipeCollector<T>();
			for (DataListener<T> dataListener : rawDataListener) {
				pipeCollector.addListener(dataListener);
			}
			for (LogSource ls : logSource) {
				@SuppressWarnings("unchecked")
				LogLineParser<T> logLineParser = new LogLineParser<T>((LogParser<T>) logLineInterpreter, obsStart, ls.getSourceName());
				KeepAliveLogReader keepAliveElement = new KeepAliveLogReader(new File(ls.getFileName()), logLineParser, logLineInterpreter.getEncoding());
				pipeCollector.addFeeder(logLineParser, keepAliveElement);
			}
			pipeCollector.run();
			terminationPointer = pipeCollector;
		}
		return terminationPointer;
	}

}
