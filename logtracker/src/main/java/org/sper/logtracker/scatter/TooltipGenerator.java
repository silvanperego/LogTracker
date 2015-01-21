package org.sper.logtracker.scatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;

import org.sper.logtracker.data.Factor;
import org.sper.logtracker.proc.DataPoint;
import org.sper.logtracker.proc.UserDataPoint;

public class TooltipGenerator implements XYToolTipGenerator {

	private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
	private Factor services;
	private Factor users;
	
	public TooltipGenerator(Factor services, Factor users) {
		this.services = services;
		this.users = users;
	}
	
	@Override
	public String generateToolTip(XYDataset dataset, int seriesIdx, int itemIdx) {
		DataPoint item = (DataPoint) ((XYSeriesCollection)dataset).getSeries(seriesIdx).getDataItem(itemIdx);
		StringBuilder sb = new StringBuilder();
		if (item instanceof UserDataPoint) {
			UserDataPoint useritem = (UserDataPoint) item;
			if (useritem.userIdx != null) {
				sb.append(useritem.userIdx != null ? users.getLabel(useritem.userIdx) : "");
				sb.append(": ");
			}
		}
		sb.append(item.svcIdx != null ? services.getLabel(item.svcIdx) : "Others");
		sb.append(" | ");
		sb.append(sdf.format(new Date(item.getX().longValue())));
		sb.append(" | ");
		sb.append(item.getY());
		sb.append("sec");
		return sb.toString();
	}
}
