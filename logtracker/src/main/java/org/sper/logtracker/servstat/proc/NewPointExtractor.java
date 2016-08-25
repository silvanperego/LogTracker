package org.sper.logtracker.servstat.proc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.sper.logtracker.data.AbstractDataListener;
import org.sper.logtracker.data.DataListener;

/**
 * Extrahiert die neuen Punkte in einem Data-Frame.
 * @author sper
 *
 */
public class NewPointExtractor extends AbstractDataListener<DataPoint, DataPoint> {

	private List<DataPoint> data = new ArrayList<DataPoint>();
	private CategoryCollection userFilter;
	private boolean newData = false;
	
	public void setUserFilter(CategoryCollection userFilter) {
		this.userFilter = userFilter;
	}

	@Override
	public void receiveData(DataPoint dp) {
		synchronized (this) {
			data.add(dp);
		}
		if (userOk(dp)) {
			sendToListeners(dp);
			newData = true;
		}
	}

	private boolean userOk(DataPoint dp) {
		return userFilter == null || userFilter.contains(dp.user);
	}

	@Override
	public void addListener(DataListener<DataPoint> listener) {
		super.addListener(listener);
	}
	
	public void resendData() {
		synchronized (this) {
			for (DataPoint dp : data) {
				if (userOk(dp))
					sendToListeners(dp);
			}
		}
		super.publishData();
	}
	
	public Stream<DataPoint> stream() {
		return data.stream();
	}

	@Override
	public void publishData() {
		if (newData) {
			super.publishData();
			newData = false;
		}
	}
	
}
