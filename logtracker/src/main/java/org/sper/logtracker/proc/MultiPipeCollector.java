package org.sper.logtracker.proc;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.data.RawDataPoint;
import org.sper.logtracker.logreader.KeepAliveElement;
import org.sper.logtracker.logreader.KeepAliveLogReader;
import org.sper.logtracker.logreader.LogLineParser;

/**
 * Fügt Ereignisse aus mehreren "Pipes" zu einer einzelnen Pipe zusammen. Der MultiPipeCollector geht davon aus,
 * dass eingespiesene Ereignisse innerhalb einer einzelnen Pipe bereits sortiert sind. Die Output des Collectors wird
 * ebenfalls sortiert.
 * @author silvan.perego
 */
public class MultiPipeCollector<T extends RawDataPoint> extends AbstractDataListener<T, T> implements KeepAliveElement {

	private static int fifoSeqNo = 0;

	private class CollectorFifo extends LinkedList<T> implements DataListener<T> {

		private static final long serialVersionUID = 1L;
		private KeepAliveLogReader keepAliveElement;
		private long lastFifoTime = 0;
		private int myId;
		private boolean published;
		
		CollectorFifo(KeepAliveLogReader keepAliveElement) {
			this.keepAliveElement = keepAliveElement;
			myId = fifoSeqNo++;
		}

		@Override
		public void receiveData(T data) {
			synchronized (MultiPipeCollector.this) {
				add(data);
				if (data.occTime > latestOccTime)
					latestOccTime = data.occTime;
				if (data.occTime > lastFifoTime)
					lastFifoTime = data.occTime;
				sortAndSend();
			}
		}

		@Override
		public void publishData() {
			published = true;
			markPublish(this);
		}

		@Override
		public boolean equals(Object o) {
			return myId == ((CollectorFifo)o).myId;
		}

		@Override
		public int hashCode() {
			return myId;
		}

	}
	
	private List<CollectorFifo> fifoList = new ArrayList<CollectorFifo>();
	private Set<CollectorFifo> fifoSet = new HashSet<CollectorFifo>();
	private long latestOccTime = 0;
	
	public void addFeeder(LogLineParser<T> parser, KeepAliveLogReader keepAliveElement) {
		CollectorFifo fifo = new CollectorFifo(keepAliveElement);
		parser.registerListener(fifo);
		fifoList.add(fifo);
	}

	private void sortAndSend() {
		boolean missing_elem = false;
		while (true) {
			long minTime = Long.MAX_VALUE;
			CollectorFifo minFifo = null;
			for (CollectorFifo fifo : fifoList) {
				RawDataPoint dp = fifo.peek();
				if (dp == null) {
					// Von einer Queue fehlen noch Punkte.
					if (!fifo.published)
						return;
					missing_elem = true;
				} else if (dp.occTime < minTime) {
					minTime = dp.occTime;
					minFifo = fifo;
				}
			}
			// Wir nehmen an, dass Datenpunkte nie mit einer Verspätung von mehr als 5 Minuten geschrieben werden.
			// Auch bei fehlenden Daten in einem der Log-Files werden Punkte bis 5 Minuten vor dem letzten geloggten Punkt geschrieben.
			if (minFifo != null && (!missing_elem || latestOccTime - minTime > 300000))
				sendToListeners(minFifo.pop());
			else
				return;
		}
	}
	
	private synchronized void markPublish(CollectorFifo fifo) {
		fifoSet.add(fifo);
		if (fifoSet.size() == fifoList.size()) {
			boolean removed = false;
			do {
				removed = false;
				sortAndSend();
				publishData();
				fifoSet.clear();
				for (int i = fifoList.size(); --i >= 0; ) {
					CollectorFifo other = fifoList.get(i);
					// Prüfe, ob diese Fifo schon seit geraumer Zeit keine Daten mehr erhalten haben.
					if (other.isEmpty() && latestOccTime - other.lastFifoTime > 43200000) {
						// Falls ja, beende den Lese-Thread und lösche die Fifo aus der Registrierung.
						other.keepAliveElement.endOfLife();
						fifoList.remove(other);
						removed = true;
					} else
						other.published = false;
				}
			} while (removed);
		}
	}

	@Override
	public void receiveData(T data) {
		throw new UnsupportedOperationException("Diese Methode kann nicht direkt aufgerufen werden. Verbinden sie Ihre Log-Reader via #addFeeder");
	}

	@Override
	public synchronized void endOfLife() {
		// Terminiere alle Keep-Alive-Readers.
		for (CollectorFifo fifo : fifoList) {
			fifo.keepAliveElement.endOfLife();
		}
	}

	/**
	 * Starte alle registrierten Log-Reader.
	 */
	public synchronized void run() {
		for (CollectorFifo fifo : fifoList) {
			fifo.keepAliveElement.start();
		}
	}

}
