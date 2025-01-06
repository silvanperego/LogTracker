package org.sper.logtracker.erroranalysis.data;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.data.DataListener;

import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Ein Katalog aller gefundenen Log-Line-Kategorien.
 * @author silvan.perego
 */
public class LogLineCatalog implements DataListener<RawErrorDataPoint> {

	private List<ErrorCategory> catalog = new ArrayList<ErrorCategory>();
	private DefaultTableModel tableModel;
	private boolean needsRefresh = false;
	private SimpleDateFormat sdf;
	
	public LogLineCatalog(DefaultTableModel tableModel, GlobalConfig config) {
		this.tableModel = tableModel;
		sdf = new SimpleDateFormat(config.getTimestampFormatStr());
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
			ErrorCategory cat;
			try {
				cat = new ErrorCategory(data);
				catalog.add(cat);
			} catch (NoContextException e) {
			}
		}
		needsRefresh = true;
	}

	@Override
	public void publishData() {
		if (needsRefresh) {
			tableModel.setRowCount(0);
			Collections.sort(catalog);
			for (ErrorCategory cat : catalog) {
				RawErrorDataPoint latestMessage = cat.getLatestMessage();
				tableModel.addRow(new Object[] {
						latestMessage.severity,
						sdf.format(new Date(latestMessage.occTime)),
						cat.getNumMessages(),
						cat
				});
			}
		}
		needsRefresh = false;
	}
	
}
