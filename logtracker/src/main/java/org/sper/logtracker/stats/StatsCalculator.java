package org.sper.logtracker.stats;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JTable;

import org.sper.logtracker.data.DataListener;
import org.sper.logtracker.data.Factor;
import org.sper.logtracker.data.NewCategoryListener;
import org.sper.logtracker.proc.DataPoint;
import org.sper.logtracker.proc.PublishingSemaphore;

/**
 * Klassifiziert alle Services eines Data-Frames und erstellt Statistiken für alle Services.
 * @author silvan.perego
 */
public class StatsCalculator<T extends DataPoint> implements DataListener<T>, NewCategoryListener {
	
	private List<ServiceStats> groupStats;
	private ServiceStats totals;
	private CategoryExtractor<T> extractor;
	private boolean withTotals;
	private AbstractTableModel tableModel;
	private JTable jtable;
	private PublishingSemaphore semaphore;
	private JComponent enableComp;
	
	public static interface CategoryExtractor<T> {
		Integer cat(T dp);
	}

	public StatsCalculator(Factor services, CategoryExtractor<T> extractor, JTable jtable, 
			boolean withTotals, PublishingSemaphore semaphore, JComponent enableComp) {
		groupStats = new ArrayList<ServiceStats>();
		totals = new ServiceStats("Total");
		this.withTotals = withTotals;
		services.addCategoryListener(this);
		this.extractor = extractor;
		this.tableModel = (AbstractTableModel) jtable.getModel();
		this.jtable = jtable;
		this.semaphore = semaphore;
		this.enableComp = enableComp;
	}

	@Override
	public void receiveData(T dpt) {
		totals.addDataPoint(dpt);
		groupStats.get(extractor.cat(dpt)).addDataPoint(dpt);
	}

	@Override
	public void publishData() {
		if (semaphore == null || semaphore.publish()) {
			totals.calcStatistics();
			if (withTotals) {
				tableModel.createOrReplaceTableRow(Boolean.TRUE, totals);
			}
			int nservice = groupStats.size();
			for (int i = 0; i < nservice; i++) {
				ServiceStats serviceStats = groupStats.get(i);
				serviceStats.calcStatistics(totals);
				tableModel.createOrReplaceTableRow(Boolean.FALSE, serviceStats);
			}
			int selcol = jtable.getSelectedColumn();
			int selrow = jtable.getSelectedRow();
			tableModel.fireTableDataChanged();
			jtable.changeSelection(selrow, selcol, false, false);
			if (enableComp != null)
				enableComp.setEnabled(true);
		}
	}

	@Override
	public void newCategory(String name, int idx) {
		groupStats.add(new ServiceStats(name));
	}

	public double getTotalMean() {
		return totals.getMean();
	}
	
}
