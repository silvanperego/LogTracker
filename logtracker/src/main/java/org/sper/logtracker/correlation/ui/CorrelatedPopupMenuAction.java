package org.sper.logtracker.correlation.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.correlation.data.CorrelatedMessage;

public class CorrelatedPopupMenuAction extends MouseAdapter {

	private JTable table;
	private CorrelatedPopupMenuAction.DataPointProvider dataPointProvider;
	private GlobalConfig globalConfig;

	public CorrelatedPopupMenuAction(JTable table, CorrelatedPopupMenuAction.DataPointProvider dataPointProvider,
			GlobalConfig globalConfig) {
		this.table = table;
		this.dataPointProvider = dataPointProvider;
		this.globalConfig = globalConfig;
	}

	public interface DataPointProvider {
		Object getDataPointForRow(int r);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		processPopUp(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		processPopUp(e);
	}

	private void processPopUp(MouseEvent e) {
		if (e.isPopupTrigger()) {
			int r = table.rowAtPoint(e.getPoint());
			if (r >= 0 && r < table.getRowCount()) {
				Object dataPoint = dataPointProvider.getDataPointForRow(r);
				if (dataPoint instanceof CorrelatedMessage) {
					JPopupMenu popup = new JPopupMenu();
					final JMenuItem menuItem = new JMenuItem("Show correlated Messages");
					menuItem.addActionListener(aev -> showCorrelationTableForId((CorrelatedMessage) dataPoint));
					popup.add(menuItem);
					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		}
	}

	private void showCorrelationTableForId(CorrelatedMessage cdp) {
		new CorrelatedMessagesViewer(table, cdp.getCorrelationId(), globalConfig).setVisible(true);
	}

}