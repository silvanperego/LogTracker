package org.sper.logtracker.data;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractDataListener<RECEIVE,SEND> implements DataListener<RECEIVE> {
	
	private List<DataListener<SEND>> listenerList = new ArrayList<DataListener<SEND>>();
	
	public void addListener(DataListener<SEND> listener) {
		listenerList.add(listener);
	}
	
	protected void sendToListeners(SEND send) {
		for (DataListener<SEND> listener : listenerList) {
			listener.receiveData(send);
		}
	}

	@Override
	public void publishData() {
		for (DataListener<SEND> listener : listenerList) {
			listener.publishData();
		}
	}
	
	/**
	 * Entfernt alle Listener, mit einer spezifischen Klasse.
	 * @param cls
	 */
	public void removeListeners() {
		listenerList.clear();
	}

}
