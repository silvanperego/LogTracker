package org.sper.logtracker.erroranalysis.parserconf;

import javax.swing.table.DefaultTableModel;

import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.erroranalysis.data.RawErrorDataPoint;

class CopyToTableListener implements DataListener<RawErrorDataPoint> {

	private DefaultTableModel targetTable;

	public CopyToTableListener(DefaultTableModel targetTable) {
		this.targetTable = targetTable;
	}
	
	@Override
	public void receiveData(RawErrorDataPoint data) {
		targetTable.addRow(new String[] {data.severity, data.msg});
	}

	@Override
	public void publishData() {
	}

}
