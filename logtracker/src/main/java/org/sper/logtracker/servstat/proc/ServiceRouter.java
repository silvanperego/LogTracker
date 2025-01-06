package org.sper.logtracker.servstat.proc;

import org.sper.logtracker.data.DataListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Der Service-Distributor nimmt Datenpunkte entgegen und leitet diese an diejenigen Services, für welche sich die Registrierten Listener interessieren.
 * @author sper
 *
 */
public class ServiceRouter implements DataListener<DataPoint> {

	private List<DataListener<DataPoint>>[] listenerList;
	
	/**
	 * Konstruktor.
	 * @param nservice die gesamte Anzahl der vorhandenen Services.
	 */
	@SuppressWarnings("unchecked")
	public ServiceRouter(int nservice) {
		listenerList = new List[nservice];
		for (int i = 0; i < nservice; i++)
			listenerList[i] = new LinkedList<DataListener<DataPoint>>();
	}
	
	/**
	 * Registriert einen spezifischen Service. Registrierte Services werden an die registrieren Listeners
	 * weitergereicht.
	 * @param idx
	 */
	public void registerListener(CategoryCollection serviceCollection, DataListener<DataPoint> listener) {
		for (Integer idx : serviceCollection) {
			listenerList[idx].add(listener);
		}
	}
	
	@Override
	public void receiveData(DataPoint df) {
		if (df.svcIdx >= listenerList.length) {
			int oldlen = listenerList.length;
			listenerList = Arrays.copyOf(listenerList, df.svcIdx + 1);
			for (int i = oldlen; i < listenerList.length; i++)
				listenerList[i] = new ArrayList<DataListener<DataPoint>>();
		}
		for (DataListener<DataPoint> list : listenerList[df.svcIdx]) {
			list.receiveData(df);
		}
	}

	@Override
	public void publishData() {
		for (List<DataListener<DataPoint>> list : listenerList) {
			for (DataListener<DataPoint> listener : list) {
				listener.publishData();
			}
		}
	}

}
