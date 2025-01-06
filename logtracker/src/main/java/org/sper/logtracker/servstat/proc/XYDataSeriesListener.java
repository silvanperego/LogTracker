package org.sper.logtracker.servstat.proc;

import org.jfree.data.xy.XYSeries;
import org.sper.logtracker.data.DataListener;

public class XYDataSeriesListener implements DataListener<DataPoint> {

	private XYSeries series;

	public XYDataSeriesListener(XYSeries series) {
		this.series = series;
	}
	
	@Override
	public void receiveData(DataPoint df) {
		series.add(df);
	}

	@Override
	public void publishData() {
		series.setNotify(true);
		series.fireSeriesChanged();
		series.setNotify(false);
	}

}
