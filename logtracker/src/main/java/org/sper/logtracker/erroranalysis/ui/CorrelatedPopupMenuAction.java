package org.sper.logtracker.erroranalysis.ui;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

import org.sper.logtracker.correlation.data.CorrelatedMessage;
import org.sper.logtracker.correlation.ui.CorrelatedMessagesViewer;

public class CorrelatedPopupMenuAction extends MouseAdapter {
  
  private JTable table;
  private CorrelatedPopupMenuAction.DataPointProvider dataPointProvider;
  
  public CorrelatedPopupMenuAction(JTable table, CorrelatedPopupMenuAction.DataPointProvider dataPointProvider) {
    this.table = table;
    this.dataPointProvider = dataPointProvider;
  }

  public interface DataPointProvider {
    Object getDataPointForRow(int r);
  }
  
  @Override
  public void mouseReleased(MouseEvent e) {
    if (e.isPopupTrigger()) {
      int r = table.rowAtPoint(e.getPoint());
      if (r >= 0 && r < table.getRowCount()) {
        Object dataPoint = dataPointProvider.getDataPointForRow(r);
        if (dataPoint instanceof CorrelatedMessage) {
          JPopupMenu popup = new JPopupMenu();
          final JMenuItem menuItem = new JMenuItem("Search for correlated Messages");
          menuItem.addActionListener(aev -> showCorrelationTableForId((CorrelatedMessage) dataPoint));
          popup.add(menuItem);
          popup.show(e.getComponent(), e.getX(), e.getY());
        }
      }
    }
  }

  private void showCorrelationTableForId(CorrelatedMessage cdp) {
    new CorrelatedMessagesViewer(table, cdp.getCorrelationId()).setVisible(true);
  }
  
}