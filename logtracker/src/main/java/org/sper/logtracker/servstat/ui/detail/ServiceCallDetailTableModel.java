package org.sper.logtracker.servstat.ui.detail;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.servstat.proc.DataPoint;
import org.sper.logtracker.servstat.proc.StatsDataPointFactorizer;

public class ServiceCallDetailTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;
	private static final String[] COL_NAMES = {"Occurrence Time", "Service Name", "User", "Response Time", "Return Code"};
	private static final Class<?> [] COL_CLASSES = {String.class, String.class, String.class, Double.class, Integer.class};
	
	private List<DataPoint> dataPointList;
	private StatsDataPointFactorizer<DataPoint> factorizer;
	private SimpleDateFormat sdf;
	
	public ServiceCallDetailTableModel(List<DataPoint> dataPointList, StatsDataPointFactorizer<DataPoint> factorizer, GlobalConfig globalConfig) {
		this.dataPointList = dataPointList;
		this.factorizer = factorizer;
		sdf = new SimpleDateFormat(globalConfig.getTimestampFormatStr());
	}

	@Override
	public int getRowCount() {
		return dataPointList.size();
	}

	@Override
	public int getColumnCount() {
		return COL_NAMES.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		DataPoint dp = dataPointList.get(rowIndex);
		switch (columnIndex) {
		case 0 : return sdf.format(new Date(dp.occTime));
		case 1 : return factorizer.getService().getLabel(dp.svcIdx);
		case 2 : return dp.user != null ? factorizer.getUser().getLabel(dp.user) :  null;
		case 3 : return dp.responseTime;
		case 4 : return dp.returnCode;
		}
		return null;
	}
	
	DataPoint getDataPointAt(int rowIndex) {
	  return dataPointList.get(rowIndex);
	}

	@Override
	public String getColumnName(int column) {
		return COL_NAMES[column];
	}

	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return COL_CLASSES[columnIndex];
	}

}
