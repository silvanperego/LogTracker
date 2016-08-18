package org.sper.logtracker.servstat.scatter;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeriesCollection;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.servstat.proc.DataPoint;

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
		if (item.logSource != null) {
			sb.append(item.logSource);
			sb.append("-> ");
		}
		if (item.user != null) {
			sb.append(users.getLabel(item.user));
			sb.append(": ");
		}
		sb.append(item.svcIdx != null ? services.getLabel(item.svcIdx) : "Others");
		sb.append(" | ");
		sb.append(sdf.format(new Date(item.getX().longValue())));
		sb.append(" | ");
		sb.append(item.getY());
		sb.append("sec");
		if (item.returnCode != null) {
			sb.append(", RetCode: ");
			sb.append(item.returnCode);
		}
		return sb.toString();
	}
}
