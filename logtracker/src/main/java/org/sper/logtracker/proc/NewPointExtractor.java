package org.sper.logtracker.proc;

import java.util.ArrayList;
import java.util.List;

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
		data.add(dp);
		if (userOk(dp)) {
			sendToListeners(dp);
			newData = true;
		}
	}

	private boolean userOk(DataPoint dp) {
		return userFilter == null || (dp instanceof UserDataPoint && userFilter.contains(((UserDataPoint)dp).userIdx));
	}

	@Override
	public void addListener(DataListener<DataPoint> listener) {
		super.addListener(listener);
	}
	
	public void resendData() {
		for (DataPoint dp : data) {
			if (userOk(dp))
				sendToListeners(dp);
		}
		super.publishData();
	}

	@Override
	public void publishData() {
		if (newData) {
			super.publishData();
			newData = false;
		}
	}
	
}
