package org.sper.logtracker.logreader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Die Klasse öffnet ein Log-File, liest es Zeile für Zeile und übergibt den
 * Inhalt der Zeilen an einen registrierten Listener. Das Log-File wird offen
 * gehalten, und immer wieder auf Erweiterungen überprüft. Falls solche
 * auftreten, werden sie wiederum dem Listener gemeldet.
 * 
 * @author silvan.perego
 */
public class KeepAliveLogReader extends Thread implements KeepAliveElement {

	private LogLineParser<?> listener;
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
	public KeepAliveLogReader(File logFile, LogLineParser<?> listener)
			throws FileNotFoundException {
		this.logFile = logFile;
		this.listener = listener;
	}
	
	private class CountingInputStream extends BufferedInputStream {

		private long pos;
		private ByteArrayOutputStream scannedLine = new ByteArrayOutputStream(10240);
		private long startPos;

		CountingInputStream(InputStream in, long startpos) {
			super(in);
			pos = startpos;
		}
		
		String readLine() throws IOException {
			scannedLine.reset();
			while (scannedLine.size() == 0) {	// Ignoriere Leere Zeilen
				startPos = pos;
				while (true) {
					int c = read();
					pos++;
					if (c < 0 && scannedLine.size() == 0) {
						return null;
					}
					if (c == '\n' || c == '\r' || c < 0)
						break;
					// Zeichen, welche keine neue Zeile bedeuten, werden der aktuellen Zeile hinzugefügt.
					scannedLine.write(c);
				}
			}
			return new String(scannedLine.toByteArray());
		}

		/**
		 * Liefere die Start-Position der letzten gelesenen Zeile.
		 * @return die Start-Position.
		 */
		long lineStart() {
			return startPos;
		}
		
	}
	
	@Override
	public void run() {
		CountingInputStream cis = null;
		try {
			do {
				FileInputStream fis = new FileInputStream(logFile);
				cis = new CountingInputStream(fis, lastPos);
				fis.getChannel().position(lastPos);
				String readLine = cis.readLine();
				while (readLine != null) {
					listener.scanLine(new FileSnippet(logFile, cis.lineStart(), readLine));
					readLine = cis.readLine();
				}
				listener.publishData();
				lastPos = fis.getChannel().position();
				if (cis != null)
					cis.close();
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
			if (cis != null)
				try {
					cis.close();
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
