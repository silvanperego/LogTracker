package org.sper.logtracker.erroranalysis.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import org.sper.logtracker.config.GlobalConfig;
import org.sper.logtracker.erroranalysis.data.ErrorCategory;

public class LogLinePanel extends JPanel {
	
	public LogLinePanel(GlobalConfig globalConfig) {
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblMostRelevantErrors = new JLabel("Most relevant Errors");
		add(lblMostRelevantErrors, BorderLayout.NORTH);
		
		logLineTable = new JTable() {

			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				final Component comp = super.prepareRenderer(renderer, row, column);
				if (row != logLineTable.getSelectedRow())
				  logLineTableModel.setRowColor(comp, row);
				return comp;
			}
			
		};
		logLineTableModel = new LogLineTableModel();
		logLineTable.setModel(logLineTableModel);
		logLineTable.getColumnModel().getColumn(0).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(1).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(2).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(3).setPreferredWidth(625);
		logLineTable.getColumnModel().getColumn(3).setCellRenderer(new LogMessageRenderer());		
		logLineTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int rowAtPoint = logLineTable.rowAtPoint(e.getPoint());
				CategoryViewer viewer = new CategoryViewer(logLineTable, (ErrorCategory) logLineTableModel.getValueAt(rowAtPoint, 3), globalConfig);
				viewer.setVisible(true);
			}
		});
		JScrollPane scrollPane = new JScrollPane(logLineTable);
		add(scrollPane, BorderLayout.CENTER);
	}

	private static final long serialVersionUID = 1L;
	private JTable logLineTable;
	private LogLineTableModel logLineTableModel;
	
	public LogLineTableModel getTableModel() {
		return logLineTableModel;
	}

}
