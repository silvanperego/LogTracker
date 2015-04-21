package org.sper.logtracker.erroranalysis.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.sper.logtracker.data.DataListener;

/**
 * Ein Katalog aller gefundenen Log-Line-Kategorien.
 * @author silvan.perego
 */
public class LogLineCatalog implements DataListener<RawErrorDataPoint> {

	private List<ErrorCategory> catalog = new ArrayList<ErrorCategory>();
	private DefaultTableModel tableModel;
	private boolean needsRefresh = false;
	
	public LogLineCatalog(DefaultTableModel tableModel) {
		this.tableModel = tableModel;
	}
	
	@Override
	public void receiveData(RawErrorDataPoint data) {
		// Find best score
		int bestScore = 0;
		ErrorCategory bestCategory = null;
		for (ErrorCategory cat : catalog) {
			int score = cat.score(data);
			if (score >= 50 && score > bestScore) {
				bestScore = score;
				bestCategory = cat;
			}
		}
		if (bestCategory != null)
			bestCategory.addDataPoint();
		else {
			ErrorCategory cat = new ErrorCategory(data);
			catalog.add(cat);
		}
		needsRefresh = true;
	}

	@Override
	public void publishData() {
		if (needsRefresh) {
			tableModel.setRowCount(0);
			for (ErrorCategory cat : catalog) {
				RawErrorDataPoint latestMessage = cat.getLatestMessage();
				tableModel.addRow(new Object[] {
						latestMessage.severity,
						new Date(latestMessage.occTime),
						cat.getNumMessages(),
						latestMessage.msg
				});
			}
		}
		needsRefresh = false;
	}
	
}
