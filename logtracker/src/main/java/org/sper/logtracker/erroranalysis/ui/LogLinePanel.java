package org.sper.logtracker.erroranalysis.ui;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.sper.logtracker.erroranalysis.data.ErrorCategory;

public class LogLinePanel extends JPanel {
	
	public LogLinePanel() {
		setLayout(new BorderLayout(0, 0));
		
		JLabel lblMostRelevantErrors = new JLabel("Most relevant Errors");
		add(lblMostRelevantErrors, BorderLayout.NORTH);
		
		logLineTable = new JTable();
		logLineTableModel = new LogLineTableModel();
		logLineTable.setModel(logLineTableModel);
		logLineTable.getColumnModel().getColumn(0).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(1).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(2).setPreferredWidth(98);
		logLineTable.getColumnModel().getColumn(3).setPreferredWidth(625);
		logLineTable.getColumnModel().getColumn(3).setCellRenderer(new LogMessageRenderer());		
		logLineTable.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (!e.getValueIsAdjusting()) {
					int selectedRow = logLineTable.getSelectedRow();
					if (selectedRow >= 0) {
						CategoryViewer viewer = new CategoryViewer(logLineTable, (ErrorCategory) logLineTableModel.getValueAt(selectedRow, 3));
						viewer.setVisible(true);
					}
				}
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
