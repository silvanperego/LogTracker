package org.sper.logtracker.logreader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Die Klasse öffnet ein Log-File, liest es Zeile für Zeile und übergibt den
 * Inhalt der Zeilen an einen registrierten Listener. Das Log-File wird offen
 * gehalten, und immer wieder auf Erweiterungen überprüft. Falls solche
 * auftreten, werden sie wiederum dem Listener gemeldet.
 * 
 * @author silvan.perego
 */
public class KeepAliveLogReader extends Thread implements KeepAliveElement {

	private LogLineParser listener;
	private boolean keepAlive = true;
	private File logFile;
	private long lastPos = 0;

	/**
	 * Konstruktor.
	 * 
	 * @param logFile
	 *            das Log-File, das überwacht werden soll.
	 * @param listener
	 *            der Listener, dem die gefundenen Inhalte mitgeteilt werden.
	 * @throws FileNotFoundException
	 *             falls das gewünschte File nicht gefunden werden kann.
	 */
	public KeepAliveLogReader(File logFile, LogLineParser listener)
			throws FileNotFoundException {
		this.logFile = logFile;
		this.listener = listener;
	}

	@Override
	public void run() {
		BufferedReader reader = null;
		try {
			do {
				FileInputStream fis = new FileInputStream(logFile);
				fis.getChannel().position(lastPos);
				reader = new BufferedReader(new InputStreamReader(fis));
				String readLine = reader.readLine();
				while (readLine != null) {
					if (readLine.length() > 0) {
						listener.scanLine(readLine);
					}
					readLine = reader.readLine();
				}
				listener.publishData();
				lastPos = fis.getChannel().position();
				if (reader != null)
					reader.close();
				if (keepAlive)
					try {
						Thread.sleep(15000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
			} while (keepAlive);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.sper.logtracker.logreader.KeepAliveElement#endOfLife()
	 */
	@Override
	public void endOfLife() {
		keepAlive = false;
	}

}
