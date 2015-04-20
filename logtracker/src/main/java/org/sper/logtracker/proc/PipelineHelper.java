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

public class PipelineHelper {

	/**
	 * Erstelle die Log-File-nahe Basisstruktur der der Pipeline. Überwache Files, interpretiere deren Inhalt.
	 * und leite die Raw-Daten an einen Listener weiter.
	 * @param fname eine Liste der Filenamen, welche überwacht werden sollen.
	 * @param logLineInterpreter interpretiert die gelesenen Zeilen und erstellt Raw-Datensätze.
	 * @param obsStart Daten ab diesem Zeitpunkt sollen interpretiert, ansonsten ignoriert werden.
	 * @param rawDataListener der Daten-Listener, an welche die interpretierten und verarbeiteten Daten gesendet werden sollen.
	 * @return ein KeepAliveElement. Mit diesem kann das Ende eine Lese-Operation signalisiert werden.
	 * @throws FileNotFoundException
	 */
	public static <T extends RawDataPoint> KeepAliveElement setupFileReaders(List<String> fname,
			LogParser<T> logLineInterpreter, Long obsStart, DataListener<T> rawDataListener)
			throws FileNotFoundException {
		KeepAliveElement terminationPointer = null;
		if (fname.size() == 1) {
			LogLineParser<T> logLineParser = new LogLineParser<T>(logLineInterpreter, obsStart);
			logLineParser.registerListener(rawDataListener);
			KeepAliveLogReader keepAliveLogReader = new KeepAliveLogReader(new File(fname.get(0)), logLineParser);
			terminationPointer = keepAliveLogReader;
			keepAliveLogReader.start();
		} else {
			// Bei mehreren Input-Files müssen die Daten durch einen MutliPipeCollector zusammengefasst werden.
			MultiPipeCollector<T> pipeCollector = new MultiPipeCollector<T>();
			pipeCollector.addListener(rawDataListener);
			for (String fn : fname) {
				LogLineParser<T> logLineParser = new LogLineParser<T>(logLineInterpreter, obsStart);
				KeepAliveLogReader keepAliveElement = new KeepAliveLogReader(new File(fn), logLineParser);
				pipeCollector.addFeeder(logLineParser, keepAliveElement);
			}
			pipeCollector.run();
			terminationPointer = pipeCollector;
		}
		return terminationPointer;
	}

}
